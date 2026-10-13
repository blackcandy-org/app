package org.blackcandy.shared.data

import org.blackcandy.shared.api.BlackCandyService
import org.blackcandy.shared.api.Paged
import org.blackcandy.shared.models.Artist
import org.blackcandy.shared.utils.TaskResult

class ArtistRepository(
    private val service: BlackCandyService,
) {
    suspend fun getArtists(): TaskResult<Paged<Artist>> = service.getArtists().asResult()
}
