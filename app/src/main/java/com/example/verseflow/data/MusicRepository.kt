package com.example.verseflow.data

import com.example.verseflow.model.Album
import com.example.verseflow.model.Artist
import com.example.verseflow.model.Playlist
import com.example.verseflow.model.Song
import com.example.verseflow.model.UserProfile

interface MusicRepository {
    fun profile(): UserProfile
    fun songs(): List<Song>
    fun albums(): List<Album>
    fun artists(): List<Artist>
    fun playlists(): List<Playlist>
    fun featuredAlbumIds(): List<String>
    fun recentlyPlayedIds(): List<String>
    fun trendingSongIds(): List<String>
    fun favoritePlaylistIds(): List<String>
    fun recentSearches(): List<String>
    fun trendingCategories(): List<String>
}
