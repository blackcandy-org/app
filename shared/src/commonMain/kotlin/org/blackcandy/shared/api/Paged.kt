package org.blackcandy.shared.api

import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.blackcandy.shared.utils.TaskResult

class Paged<T> internal constructor(
    val items: List<T>,
    private val nextUrl: String?,
    private val loadPage: suspend (String) -> ApiResponse<Paged<T>>,
) {
    val hasNextPage: Boolean get() = nextUrl != null

    suspend fun next(): TaskResult<Paged<T>> {
        val url = nextUrl ?: return TaskResult.Success(this)

        return when (val response = loadPage(url)) {
            is ApiResponse.Success -> {
                TaskResult.Success(
                    Paged(items + response.data.items, response.data.nextUrl, response.data.loadPage),
                )
            }

            is ApiResponse.Failure -> {
                TaskResult.Failure(response.exception.message)
            }
        }
    }

    companion object {
        internal fun <T> from(
            items: List<T>,
            headers: Headers,
            loadPage: suspend (String) -> ApiResponse<Paged<T>>,
        ): Paged<T> = Paged(items, LinkHeader.parse(headers[HttpHeaders.Link])["next"], loadPage)
    }
}
