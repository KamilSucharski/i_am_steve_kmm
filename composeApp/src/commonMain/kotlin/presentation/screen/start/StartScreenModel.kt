package presentation.screen.start

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import domain.operation.GetComicPanelsOperation
import domain.operation.GetComicsOperation
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import util.WithErrorFlow

class StartScreenModel : StateScreenModel<StartState>(StartState()), WithErrorFlow, KoinComponent {

    override val errorFlow = MutableSharedFlow<Throwable>()
    val navigateToComicGalleryScreenTrigger = MutableSharedFlow<Unit>()
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Napier.e("Error downloading comics", exception)
        screenModelScope.launch {
            errorFlow.emit(exception)
        }
    }

    fun downloadComics() = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
        val comics = GetComicsOperation(refresh = true).execute()
        mutableState.value = StartState(0, comics.size)
        comics.forEach { comic ->
            GetComicPanelsOperation(comic = comic).execute()
            mutableState.value = StartState(comic.number, comics.size)
        }
        navigateToComicGalleryScreenTrigger.emit(Unit)
    }

}