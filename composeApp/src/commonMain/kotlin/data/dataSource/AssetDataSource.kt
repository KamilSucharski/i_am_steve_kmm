package data.dataSource

import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes

class AssetDataSource {

    companion object {
        private const val ASSET_PREFIX = "files/"
    }

    @OptIn(InternalResourceApi::class)
    fun read(assetName: String): ByteArray? {
        return runBlocking(Dispatchers.IO) {
            try {
                readResourceBytes(ASSET_PREFIX + assetName)
            } catch (e: Exception) {
                Napier.e("Couldn't read asset: $assetName", e)
                null
            }
        }
    }

}