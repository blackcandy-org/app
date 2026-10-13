package org.blackcandy.shared.data

import org.blackcandy.shared.api.BlackCandyService
import org.blackcandy.shared.api.Paged
import org.blackcandy.shared.models.Song
import org.blackcandy.shared.utils.TaskResult

class SongRepository(
    private val service: BlackCandyService,
) {
    suspend fun getSongs(): TaskResult<Paged<Song>> = service.getSongs().asResult()
}
