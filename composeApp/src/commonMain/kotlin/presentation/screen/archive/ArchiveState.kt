package presentation.screen.archive

import domain.model.Comic

data class ArchiveState(
    val comics: List<Comic> = emptyList()
)
