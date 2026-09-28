package org.blackcandy.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: Long,
    val name: String,
    val isVarious: Boolean,
    val imageUrls: ImageURLs,
)
