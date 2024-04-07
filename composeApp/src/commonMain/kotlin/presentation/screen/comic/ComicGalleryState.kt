package presentation.screen.comic

import domain.model.Comic

data class ComicGalleryState(
    val comics: List<Comic> = emptyList(),
    val previousButtonVisible: Boolean = false,
    val nextButtonVisible: Boolean = false
)