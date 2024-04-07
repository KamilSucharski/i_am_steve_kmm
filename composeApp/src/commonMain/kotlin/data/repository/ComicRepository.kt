package data.repository

import data.dataSource.ApiDataSource
import data.dataSource.AssetDataSource
import data.dataSource.LocalStorageDataSource
import data.mapper.ComicMapper
import domain.exception.NoComicPanelException
import domain.exception.NoComicsException
import domain.model.Comic
import domain.model.ComicPanels
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import util.Consts
import util.map

class ComicRepository : KoinComponent {

    private val apiDataSource by inject<ApiDataSource>()
    private val assetDataSource by inject<AssetDataSource>()
    private val localStorageDataSource by inject<LocalStorageDataSource>()

    suspend fun getComics(refresh: Boolean): List<Comic> {
        //todo better error handling
        if (refresh) {
            try {
                return getComicsFromApiAndSaveToLocalStorage()
            } catch (e: Exception) {
                Napier.w("Couldn't get comics.json from the API. Trying local storage.", e)
            }
        }

        try {
            return getComicsFromLocalStorage()
        } catch (e: Exception) {
            Napier.w("Couldn't get comics.json from the local storage. Trying assets.", e)
        }

        return getComicsFromAssets()
    }

    suspend fun getComicPanels(comicNumber: Int): ComicPanels {
        //todo better error handling
        try {
            return getComicPanelsFromAssetsAndRemoveFromLocalStorage(comicNumber)
        } catch (e: Exception) {
            Napier.w("Couldn't get comic panels from the assets. Trying local storage.", e)
        }

        try {
            return getComicPanelsFromLocalStorage(comicNumber)
        } catch (e: Exception) {
            Napier.w("Couldn't get comic panels from the local storage. Trying API.", e)
        }

        return getComicPanelsFromApiAndSaveToLocalStorage(comicNumber)
    }

    private suspend fun getComicsFromApiAndSaveToLocalStorage(): List<Comic> = apiDataSource
        .getComics()
        .map { it.map(ComicMapper()) }
        .let {
            localStorageDataSource.putSerializable(
                key = Consts.KEY_COMIC_LIST,
                serializable = it.toTypedArray()
            )
            it
        }

    private fun getComicsFromLocalStorage(): List<Comic> = localStorageDataSource
        .getSerializable<List<Comic>>(Consts.KEY_COMIC_LIST)
        ?: throw NoComicsException()

    private fun getComicsFromAssets(): List<Comic> = assetDataSource
        .read(Consts.COMIC_METADATA_FILE_NAME)
        ?.let { byteArray ->
            val json = byteArray.decodeToString()
            Json.decodeFromString<List<Comic>>(json)
        }
        ?: throw NoComicsException()

    private fun getComicPanelsFromAssetsAndRemoveFromLocalStorage(comicNumber: Int) = ComicPanels(
        panel1 = getComicPanelFromAssetsAndRemoveFromLocalStorage(comicNumber, 1),
        panel2 = getComicPanelFromAssetsAndRemoveFromLocalStorage(comicNumber, 2),
        panel3 = getComicPanelFromAssetsAndRemoveFromLocalStorage(comicNumber, 3),
        panel4 = getComicPanelFromAssetsAndRemoveFromLocalStorage(comicNumber, 4)
    )

    private fun getComicPanelFromAssetsAndRemoveFromLocalStorage(
        comicNumber: Int,
        panelNumber: Int
    ): ByteArray {
        val name = getComicPanelFileName(comicNumber, panelNumber)
        val byteArray = assetDataSource.read(name) ?: throw NoComicPanelException()
        localStorageDataSource.removeFile(name)
        return byteArray
    }

    private fun getComicPanelsFromLocalStorage(comicNumber: Int) = ComicPanels(
        panel1 = getComicPanelFromLocalStorage(comicNumber, 1),
        panel2 = getComicPanelFromLocalStorage(comicNumber, 2),
        panel3 = getComicPanelFromLocalStorage(comicNumber, 3),
        panel4 = getComicPanelFromLocalStorage(comicNumber, 4)
    )

    private fun getComicPanelFromLocalStorage(
        comicNumber: Int,
        panelNumber: Int
    ): ByteArray {
        val name = getComicPanelFileName(comicNumber, panelNumber)
        return localStorageDataSource.getFile(name) ?: throw NoComicPanelException()
    }

    private suspend fun getComicPanelsFromApiAndSaveToLocalStorage(comicNumber: Int): ComicPanels {
        return coroutineScope {
            (1..4)
                .map {
                    async { getComicPanelFromApiAndSaveToLocalStorage(comicNumber, it) }
                }
                .awaitAll()
                .let {
                    ComicPanels(
                        panel1 = it[0],
                        panel2 = it[1],
                        panel3 = it[2],
                        panel4 = it[3],
                    )
                }
        }
    }

    private suspend fun getComicPanelFromApiAndSaveToLocalStorage(
        comicNumber: Int,
        panelNumber: Int
    ): ByteArray {
        val name = getComicPanelFileName(comicNumber, panelNumber)
        val byteArray = apiDataSource.getComicPanel(name)
        localStorageDataSource.putFile(name, byteArray)
        return byteArray
    }

    private fun getComicPanelFileName(comicNumber: Int, panelNumber: Int): String {
        return "${comicNumber}_${panelNumber}.png"
    }

}