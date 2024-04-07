package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Comic(
    val number: Int,
    val title: String,
    val date: String
)