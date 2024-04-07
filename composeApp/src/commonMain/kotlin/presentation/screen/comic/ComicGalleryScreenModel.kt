package presentation.screen.comic

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.model.Comic
import domain.model.ComicPanels
import domain.operation.GetComicPanelsOperation
import domain.operation.GetComicsOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class ComicGalleryScreenModel : StateScreenModel<ComicGalleryState>(ComicGalleryState()) {

    val scrollToComicTrigger = MutableSharedFlow<Int>()

    fun downloadComics(initialComicIndex: Int) = CoroutineScope(Dispatchers.IO).launch {
        val comics = GetComicsOperation(refresh = false).execute()
        val scrollIndex = if (initialComicIndex < 0) comics.lastIndex else initialComicIndex

        mutableState.emit(
            ComicGalleryState(
                comics = comics,
                previousButtonVisible = scrollIndex > 0,
                nextButtonVisible = scrollIndex < comics.lastIndex
            )
        )

        scrollToComicTrigger.emit(scrollIndex)
    }

    fun onPageChanged(index: Int) {
        val previousState = state.value
        val previousButtonVisible = index > 0
        val nextButtonVisible = index < previousState.comics.lastIndex
        if (previousButtonVisible != previousState.previousButtonVisible || nextButtonVisible != previousState.nextButtonVisible) {
            screenModelScope.launch {
                mutableState.emit(
                    ComicGalleryState(
                        comics = previousState.comics,
                        previousButtonVisible = previousButtonVisible,
                        nextButtonVisible = nextButtonVisible
                    )
                )
            }
        }
    }

    suspend fun getComicPanels(comic: Comic): ComicPanels {
        return GetComicPanelsOperation(comic).execute()
    }

}