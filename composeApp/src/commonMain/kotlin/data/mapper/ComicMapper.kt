package data.mapper

import data.dto.ComicDto
import domain.model.Comic
import util.Mapper

class ComicMapper : Mapper<ComicDto, Comic> {

    override fun ComicDto.map() = Comic(
        number = number,
        title = title,
        date = date
    )

}