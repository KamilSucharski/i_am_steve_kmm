package data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ComicDto(
    val number: Int,
    val title: String,
    val date: String
)