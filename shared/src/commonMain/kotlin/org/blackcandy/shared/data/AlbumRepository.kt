package org.blackcandy.shared.data

import org.blackcandy.shared.api.BlackCandyService
import org.blackcandy.shared.api.Paged
import org.blackcandy.shared.models.Album
import org.blackcandy.shared.utils.TaskResult

class AlbumRepository(
    private val service: BlackCandyService,
) {
    suspend fun getAlbums(page: Int): TaskResult<Paged<Album>> = service.getAlbums(page).asResult()
}
