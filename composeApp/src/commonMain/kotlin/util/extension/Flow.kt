package util.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

inline fun <T> Flow<T?>.filterNotNull(): Flow<T> {
    return filter { it != null }
        .map { it!! }
}