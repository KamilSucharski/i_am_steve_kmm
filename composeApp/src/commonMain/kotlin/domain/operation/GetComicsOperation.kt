package domain.operation

import data.repository.ComicRepository
import domain.model.Comic
import org.koin.core.component.inject
import util.Operation

class GetComicsOperation(
    private val refresh: Boolean = false
) : Operation<List<Comic>> {

    private val comicRepository by inject<ComicRepository>()

    override suspend fun execute() = comicRepository.getComics(refresh)

}