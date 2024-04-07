package data.dataSource

import data.dto.ComicDto
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import util.Consts

interface ApiDataSource {

    @GET(Consts.COMIC_METADATA_FILE_NAME)
    suspend fun getComics(): List<ComicDto>

    @GET("assets/comic/{fileName}")
    suspend fun getComicPanel(@Path("fileName") fileName: String): ByteArray

}