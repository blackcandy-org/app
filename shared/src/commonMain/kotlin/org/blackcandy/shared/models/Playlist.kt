package org.blackcandy.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: Long,
    val name: String,
    val isFavorite: Boolean,
)
