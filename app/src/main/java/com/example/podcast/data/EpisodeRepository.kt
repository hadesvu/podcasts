package com.example.podcast.data

import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {
    fun getLiveEpisodes(): Flow<Result<List<Episode>>>

    fun getEpisodes(forceUpdate: Boolean = false): Result<List<Episode>>

    fun refreshEpisodes()

    fun saveEpisode(episode: Episode)

    fun deleteAllEpisodes()
}