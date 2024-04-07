package domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ComicPanels(
    val panel1: ByteArray,
    val panel2: ByteArray,
    val panel3: ByteArray,
    val panel4: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ComicPanels

        if (!panel1.contentEquals(other.panel1)) return false
        if (!panel2.contentEquals(other.panel2)) return false
        if (!panel3.contentEquals(other.panel3)) return false
        if (!panel4.contentEquals(other.panel4)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = panel1.contentHashCode()
        result = 31 * result + panel2.contentHashCode()
        result = 31 * result + panel3.contentHashCode()
        result = 31 * result + panel4.contentHashCode()
        return result
    }
}