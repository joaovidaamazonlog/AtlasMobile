package im.manus.atlas.util

inline fun <T, R> Result<T>.mapCatching(transform: (value: T) -> R): Result<R> {
    return fold(
        onSuccess = { value -> Result.success(transform(value)) },
        onFailure = { exception -> Result.failure(exception) }
    )
}

inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (isSuccess) {
        action(getOrNull()!!)
    }
    return this
}

inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    if (isFailure) {
        action(exceptionOrNull()!!)
    }
    return this
}
