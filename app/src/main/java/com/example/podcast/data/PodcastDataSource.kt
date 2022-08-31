package com.example.podcast.data

import kotlinx.coroutines.flow.Flow

interface PodcastDataSource {
    fun getLivePodcasts(): Flow<Result<List<Podcast>>>

    fun getPodcasts(): Result<List<Podcast>>

    fun refreshPodcasts()

    fun savePodcast(podcast: Podcast)

    fun deleteAllPodcasts()
}