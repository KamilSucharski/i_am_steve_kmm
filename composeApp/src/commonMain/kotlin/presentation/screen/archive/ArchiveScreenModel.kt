package presentation.screen.archive

import cafe.adriel.voyager.core.model.StateScreenModel
import domain.operation.GetComicsOperation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class ArchiveScreenModel : StateScreenModel<ArchiveState>(ArchiveState()) {

    fun downloadComics() = CoroutineScope(Dispatchers.IO).launch {
        mutableState.value = ArchiveState(
            comics = GetComicsOperation(refresh = false).execute()
        )
    }

}