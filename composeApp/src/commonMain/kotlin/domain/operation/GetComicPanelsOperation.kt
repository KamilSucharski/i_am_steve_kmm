package domain.operation

import data.repository.ComicRepository
import domain.model.Comic
import domain.model.ComicPanels
import org.koin.core.component.inject
import util.Operation

class GetComicPanelsOperation(
    private val comic: Comic
) : Operation<ComicPanels> {

    private val comicRepository by inject<ComicRepository>()

    override suspend fun execute() = comicRepository.getComicPanels(comic.number)

}