package org.blackcandy.shared.models

object AppVersion {
    const val MAJOR = 2
    const val MINOR = 0
    const val PATCH = 0

    fun isSupported(minVersion: SystemInfo.Version): Boolean {
        if (MAJOR != minVersion.major) return MAJOR > minVersion.major
        if (MINOR != minVersion.minor) return MINOR > minVersion.minor
        return PATCH >= minVersion.patch
    }
}
