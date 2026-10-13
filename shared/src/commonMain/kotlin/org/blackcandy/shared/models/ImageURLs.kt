package org.blackcandy.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class ImageURLs(
    val small: String,
    val medium: String,
    val large: String,
)
