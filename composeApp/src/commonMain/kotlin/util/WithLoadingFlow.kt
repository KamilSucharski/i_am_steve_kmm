package util

import kotlinx.coroutines.flow.Flow

interface WithLoadingFlow {
    val loadingFlow: Flow<Boolean>
}