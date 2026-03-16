package org.blackcandy.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class SystemInfo(
    val version: Version,
    var serverAddress: String? = null,
    val minAppVersion: Version? = null,
) {
    val isServerSupported get() = MinServerVersion.isSupported(version)

    val isAppSupported: Boolean
        get() {
            val min = minAppVersion ?: return true
            return AppVersion.isSupported(min)
        }

    @Serializable
    data class Version(
        val major: Int,
        val minor: Int,
        val patch: Int,
        val pre: String = "",
    )
}
