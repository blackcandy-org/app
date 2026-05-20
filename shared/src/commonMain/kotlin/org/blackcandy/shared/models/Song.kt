package org.blackcandy.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: Long,
    val name: String,
    val duration: Double,
    val albumId: Long,
    val artistId: Long,
    val url: String,
    val albumName: String,
    val artistName: String,
    val format: String,
    val albumImageUrls: ImageURLs,
    var isFavorited: Boolean,
) {
    @Serializable
    data class ImageURLs(
        val small: String,
        val medium: String,
        val large: String,
    )
}
