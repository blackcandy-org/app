package org.blackcandy.shared.api

data class Paged<T>(
    val items: List<T>,
    val pageInfo: PageInfo,
) {
    fun <R> map(transform: (T) -> R): Paged<R> = Paged(items.map(transform), pageInfo)
}
