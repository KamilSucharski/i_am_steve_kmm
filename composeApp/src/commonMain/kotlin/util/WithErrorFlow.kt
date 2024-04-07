package util

import kotlinx.coroutines.flow.Flow

interface WithErrorFlow {
    val errorFlow: Flow<Throwable?>
}