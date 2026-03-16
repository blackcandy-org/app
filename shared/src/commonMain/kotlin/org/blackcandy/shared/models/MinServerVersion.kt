package org.blackcandy.shared.models

object MinServerVersion {
    const val MAJOR = 3
    const val MINOR = 2
    const val PATCH = 0

    fun isSupported(version: SystemInfo.Version): Boolean {
        if (version.major != MAJOR) return version.major > MAJOR
        if (version.minor != MINOR) return version.minor > MINOR
        return version.patch >= PATCH
    }
}
