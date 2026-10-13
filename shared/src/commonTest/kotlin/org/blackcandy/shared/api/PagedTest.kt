package org.blackcandy.shared.api

import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.blackcandy.shared.utils.TaskResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PagedTest {
    private fun page(
        items: List<String>,
        link: String?,
        headerName: String = "Link",
        loadPage: suspend (String) -> ApiResponse<Paged<String>> = { error("no further pages expected") },
    ) = Paged.from(
        items = items,
        headers = if (link == null) headersOf() else headersOf(headerName, listOf(link)),
        loadPage = loadPage,
    )

    @Test
    fun knowsWhenAnotherPageFollows() {
        val first = page(listOf("a"), "<https://bc.test/albums?page=2>; rel=\"next\"")
        val last = page(listOf("z"), "<https://bc.test/albums?page=1>; rel=\"prev\"")

        assertTrue(first.hasNextPage)
        assertFalse(last.hasNextPage)
    }

    @Test
    fun matchesTheLowercaseHeaderNameTheServerSends() {
        val first = page(listOf("a"), "<https://bc.test/albums?page=2>; rel=\"next\"", headerName = "link")

        assertTrue(first.hasNextPage)
    }

    @Test
    fun nextRequestsTheUrlTheServerGave() =
        runTest {
            var requested: String? = null

            val first =
                page(listOf("a"), "<https://bc.test/albums?filter%5Bgenre%5D=Jazz&page=2>; rel=\"next\"") { url ->
                    requested = url
                    ApiResponse.Success(page(listOf("b"), null))
                }

            first.next()

            assertEquals("https://bc.test/albums?filter%5Bgenre%5D=Jazz&page=2", requested)
        }

    @Test
    fun nextAccumulatesItemsAcrossPages() =
        runTest {
            val third = page(listOf("e"), null)
            val second = page(listOf("c", "d"), "<https://bc.test/albums?page=3>; rel=\"next\"") { ApiResponse.Success(third) }
            val first = page(listOf("a", "b"), "<https://bc.test/albums?page=2>; rel=\"next\"") { ApiResponse.Success(second) }

            val afterSecond = (first.next() as TaskResult.Success).data
            assertEquals(listOf("a", "b", "c", "d"), afterSecond.items)

            val afterThird = (afterSecond.next() as TaskResult.Success).data
            assertEquals(listOf("a", "b", "c", "d", "e"), afterThird.items)
            assertFalse(afterThird.hasNextPage)
        }

    @Test
    fun nextOnTheLastPageKeepsTheSameItems() =
        runTest {
            val last = page(listOf("a", "b"), null)

            val result = last.next() as TaskResult.Success

            assertEquals(listOf("a", "b"), result.data.items)
        }

    @Test
    fun nextReportsFailureWithoutLosingLoadedItems() =
        runTest {
            val first =
                page(listOf("a"), "<https://bc.test/albums?page=2>; rel=\"next\"") {
                    ApiResponse.Failure(ApiException(code = 500, message = "boom"))
                }

            val result = first.next()

            assertEquals("boom", (result as TaskResult.Failure).message)
            assertEquals(listOf("a"), first.items)
        }
}
