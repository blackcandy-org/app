package org.blackcandy.shared.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.blackcandy.shared.models.AuthenticationResponse
import org.blackcandy.shared.models.Song
import org.blackcandy.shared.models.SystemInfo
import org.blackcandy.shared.models.User

interface BlackCandyService {
    suspend fun getSystemInfo(): ApiResponse<SystemInfo>

    suspend fun createAuthentication(
        email: String,
        password: String,
    ): ApiResponse<AuthenticationResponse>

    suspend fun removeAuthentication(): ApiResponse<Unit>

    suspend fun getSongsFromCurrentPlaylist(): ApiResponse<List<Song>>

    suspend fun addSongToFavorite(songId: Long): ApiResponse<Song>

    suspend fun removeSongFromFavorite(songId: Long): ApiResponse<Song>

    suspend fun removeAllSongsFromCurrentPlaylist(): ApiResponse<Unit>

    suspend fun removeSongFromCurrentPlaylist(songId: Long): ApiResponse<Unit>

    suspend fun moveSongInCurrentPlaylist(
        songId: Long,
        destinationSongId: Long,
    ): ApiResponse<Unit>

    suspend fun replaceCurrentPlaylistWithAlbumSongs(albumId: Long): ApiResponse<List<Song>>

    suspend fun replaceCurrentPlaylistWithPlaylistSongs(playlistId: Long): ApiResponse<List<Song>>

    suspend fun addSongToCurrentPlaylist(
        songId: Long,
        currentSongId: Long?,
        location: String?,
    ): ApiResponse<Song>
}

class BlackCandyServiceImpl(
    private val client: HttpClient,
) : BlackCandyService {
    override suspend fun getSystemInfo(): ApiResponse<SystemInfo> =
        handleResponse {
            val response = client.get("system")
            val responseUrl = response.request.url
            val systemInfo: SystemInfo = response.body()

            systemInfo.serverAddress =
                URLBuilder(
                    protocol = responseUrl.protocol,
                    host = responseUrl.host,
                    port = responseUrl.port,
                ).buildString()

            systemInfo
        }

    override suspend fun createAuthentication(
        email: String,
        password: String,
    ): ApiResponse<AuthenticationResponse> =
        handleResponse {
            val response: HttpResponse =
                client.post("sessions") {
                    setBody(
                        buildJsonObject {
                            putJsonObject("session") {
                                put("email", email)
                                put("password", password)
                            }
                        },
                    )
                }

            val userElement = Json.parseToJsonElement(response.bodyAsText()).jsonObject["user"]!!

            val token = userElement.jsonObject["api_token"]?.jsonPrimitive.toString()
            val id = userElement.jsonObject["id"]?.jsonPrimitive?.long!!
            val userEmail = userElement.jsonObject["email"]?.jsonPrimitive.toString()
            val isAdmin = userElement.jsonObject["is_admin"]?.jsonPrimitive?.boolean!!
            val cookies = response.headers.getAll(HttpHeaders.SetCookie) ?: emptyList()

            AuthenticationResponse(
                token = token,
                user =
                    User(
                        id = id,
                        email = userEmail,
                        isAdmin = isAdmin,
                    ),
                cookies = cookies,
            )
        }

    override suspend fun removeAuthentication(): ApiResponse<Unit> =
        handleResponse {
            client.delete("my/session").body()
        }

    override suspend fun getSongsFromCurrentPlaylist(): ApiResponse<List<Song>> =
        handleResponse {
            client.get("current_playlist/songs").body()
        }

    override suspend fun addSongToFavorite(songId: Long): ApiResponse<Song> =
        handleResponse {
            client
                .post("favorite_playlist/songs") {
                    setBody(
                        buildJsonObject {
                            put("song_id", songId)
                        },
                    )
                }.body()
        }

    override suspend fun removeSongFromFavorite(songId: Long): ApiResponse<Song> =
        handleResponse {
            client.delete("favorite_playlist/songs/$songId").body()
        }

    override suspend fun removeAllSongsFromCurrentPlaylist(): ApiResponse<Unit> =
        handleResponse {
            client.delete("current_playlist/songs").body()
        }

    override suspend fun removeSongFromCurrentPlaylist(songId: Long): ApiResponse<Unit> =
        handleResponse {
            client.delete("current_playlist/songs/$songId").body()
        }

    override suspend fun moveSongInCurrentPlaylist(
        songId: Long,
        destinationSongId: Long,
    ): ApiResponse<Unit> =
        handleResponse {
            client
                .put("current_playlist/songs/$songId/move") {
                    setBody(
                        buildJsonObject {
                            put("destination_song_id", destinationSongId)
                        },
                    )
                }.body()
        }

    override suspend fun replaceCurrentPlaylistWithAlbumSongs(albumId: Long): ApiResponse<List<Song>> =
        handleResponse {
            client.put("current_playlist/songs/albums/$albumId").body()
        }

    override suspend fun replaceCurrentPlaylistWithPlaylistSongs(playlistId: Long): ApiResponse<List<Song>> =
        handleResponse {
            client.put("current_playlist/songs/playlists/$playlistId").body()
        }

    override suspend fun addSongToCurrentPlaylist(
        songId: Long,
        currentSongId: Long?,
        location: String?,
    ): ApiResponse<Song> =
        handleResponse {
            client
                .post("current_playlist/songs") {
                    setBody(
                        buildJsonObject {
                            put("song_id", songId)

                            if (currentSongId != null) {
                                put("current_song_id", currentSongId)
                            }

                            if (location != null) {
                                put("location", location)
                            }
                        },
                    )
                }.body()
        }

    private suspend fun <T> handleResponse(request: suspend () -> T): ApiResponse<T> =
        try {
            ApiResponse.Success(request())
        } catch (e: ApiException) {
            ApiResponse.Failure(e)
        }
}
