package org.blackcandy.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: Long,
    val name: String,
    val year: Int? = null,
    val genre: String? = null,
    val artistId: Long,
    val artistName: String,
    val imageUrls: ImageURLs,
)
