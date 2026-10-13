package org.blackcandy.shared.data

import org.blackcandy.shared.api.BlackCandyService
import org.blackcandy.shared.api.Paged
import org.blackcandy.shared.models.Playlist
import org.blackcandy.shared.utils.TaskResult

class PlaylistRepository(
    private val service: BlackCandyService,
) {
    suspend fun getPlaylists(): TaskResult<Paged<Playlist>> = service.getPlaylists().asResult()
}
