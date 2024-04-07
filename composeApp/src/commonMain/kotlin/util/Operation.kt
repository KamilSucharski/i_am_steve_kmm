package util

import org.koin.core.component.KoinComponent

interface Operation<OUT> : KoinComponent {
    suspend fun execute(): OUT
}