package org.blackcandy.shared.api

import io.ktor.http.Headers

data class PageInfo(
    val currentPage: Int,
    val totalPages: Int,
    val totalCount: Int,
    val limit: Int,
) {
    val hasNextPage: Boolean get() = currentPage < totalPages

    val nextPage: Int? get() = if (hasNextPage) currentPage + 1 else null

    companion object {
        const val CURRENT_PAGE_HEADER = "Current-Page"
        const val LIMIT_HEADER = "Page-Items"
        const val TOTAL_COUNT_HEADER = "Total-Count"
        const val TOTAL_PAGES_HEADER = "Total-Pages"

        fun from(
            headers: Headers,
            itemCount: Int,
        ): PageInfo =
            PageInfo(
                currentPage = headers[CURRENT_PAGE_HEADER]?.toIntOrNull() ?: 1,
                totalPages = headers[TOTAL_PAGES_HEADER]?.toIntOrNull() ?: 1,
                totalCount = headers[TOTAL_COUNT_HEADER]?.toIntOrNull() ?: itemCount,
                limit = headers[LIMIT_HEADER]?.toIntOrNull() ?: itemCount,
            )
    }
}
