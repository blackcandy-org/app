package org.blackcandy.shared.api

import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PageInfoTest {
    @Test
    fun readsPagyHeaders() {
        val headers =
            headersOf(
                PageInfo.CURRENT_PAGE_HEADER to listOf("2"),
                PageInfo.LIMIT_HEADER to listOf("30"),
                PageInfo.TOTAL_COUNT_HEADER to listOf("95"),
                PageInfo.TOTAL_PAGES_HEADER to listOf("4"),
            )

        val pageInfo = PageInfo.from(headers, itemCount = 30)

        assertEquals(2, pageInfo.currentPage)
        assertEquals(30, pageInfo.limit)
        assertEquals(95, pageInfo.totalCount)
        assertEquals(4, pageInfo.totalPages)
    }

    @Test
    fun reportsNextPageWhilePagesRemain() {
        val headers =
            headersOf(
                PageInfo.CURRENT_PAGE_HEADER to listOf("2"),
                PageInfo.TOTAL_PAGES_HEADER to listOf("4"),
            )

        val pageInfo = PageInfo.from(headers, itemCount = 30)

        assertTrue(pageInfo.hasNextPage)
        assertEquals(3, pageInfo.nextPage)
    }

    @Test
    fun reportsNoNextPageOnLastPage() {
        val headers =
            headersOf(
                PageInfo.CURRENT_PAGE_HEADER to listOf("4"),
                PageInfo.TOTAL_PAGES_HEADER to listOf("4"),
            )

        val pageInfo = PageInfo.from(headers, itemCount = 5)

        assertFalse(pageInfo.hasNextPage)
        assertNull(pageInfo.nextPage)
    }

    @Test
    fun treatsResponseWithoutHeadersAsSinglePage() {
        val pageInfo = PageInfo.from(headersOf(), itemCount = 12)

        assertEquals(1, pageInfo.currentPage)
        assertEquals(1, pageInfo.totalPages)
        assertEquals(12, pageInfo.totalCount)
        assertEquals(12, pageInfo.limit)
        assertFalse(pageInfo.hasNextPage)
    }

    @Test
    fun treatsUnreadableHeadersAsAbsent() {
        val headers = headersOf(PageInfo.TOTAL_PAGES_HEADER to listOf("not-a-number"))

        val pageInfo = PageInfo.from(headers, itemCount = 3)

        assertEquals(1, pageInfo.totalPages)
    }
}
