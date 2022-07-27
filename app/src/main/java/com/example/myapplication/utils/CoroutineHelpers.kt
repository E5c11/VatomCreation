package com.example.myapplication.utils

import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

typealias CallbackWrapper1<T> = (result: T?, error: Exception?) -> Unit
typealias CallbackWrapper2<T, X> = (result1: T?, result2: X?, error: Exception?) -> Unit

/** Wrap a BLOCKv-style callback with 1 return value */
suspend fun <T> wrap(block: (cb: CallbackWrapper1<T>) -> Unit): T {

    // Create coroutine
    val result = suspendCoroutine<T> { cont ->

        // Call function
        block { result: T?, error: Throwable? ->

            // Check for error
            when {
                error != null -> cont.resumeWithException(error)
                result == null -> cont.resumeWithException(Exception("No result was returned."))
                else -> cont.resume(result)
            }

        }

    }

    // Done
    return result

}

data class ResultTwoItems<T, X>(val item1: T?, val item2: X?)

/** Wrap a BLOCKv-style callback with 2 return values */
suspend fun <T, X> wrap2(block: (cb: CallbackWrapper2<T, X>) -> Unit): ResultTwoItems<T, X> {

    try {
        // Create coroutine
        return suspendCoroutine { cont ->
            // Call function
            block { result1: T?, result2: X?, error: Throwable? ->
                // Check for error
                if (error != null) cont.resumeWithException(error)
                else cont.resume(ResultTwoItems(result1, result2))
            }
        }
    } catch (e: Exception) {
        throw e
    }

}