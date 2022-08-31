package com.example.podcast.data

import kotlinx.coroutines.flow.Flow

interface PodcastRepository {
    fun getLivePodcasts(): Flow<Result<List<Podcast>>>

    fun getPodcasts(forceUpdate: Boolean = false): Result<List<Podcast>>

    fun refreshPodcasts()

    fun savePodcast(podcast: Podcast)

    fun deleteAllPodcasts()
}