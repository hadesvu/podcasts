package com.example.podcast.data

import kotlinx.coroutines.flow.Flow
import com.example.podcast.data.Result.Success
import com.example.podcast.data.Result.Error

class DefaultPodcastRepository(
    private val podcastRemoteDataSource: PodcastDataSource,
    private val podcastLocalDataSource: PodcastDataSource,
) : PodcastRepository {
    override fun getLivePodcasts(): Flow<Result<List<Podcast>>> {
        return podcastLocalDataSource.getLivePodcasts()
    }

    override fun getPodcasts(forceUpdate: Boolean): Result<List<Podcast>> {
        if (forceUpdate) {
            try {
                updatePodcastFromRemoteDataSource()
            } catch (ex: Exception) {
                return Error(ex)
            }
        }
        return podcastLocalDataSource.getPodcasts()
    }

    override fun refreshPodcasts() {
        updatePodcastFromRemoteDataSource()
    }

    override fun savePodcast(podcast: Podcast) {
        podcastLocalDataSource.savePodcast(podcast)
        podcastRemoteDataSource.savePodcast(podcast)
    }

    override fun deleteAllPodcasts() {
        podcastLocalDataSource.deleteAllPodcasts()
        podcastRemoteDataSource.deleteAllPodcasts()
    }

    private fun updatePodcastFromRemoteDataSource() {
        val remotePodcast = podcastRemoteDataSource.getPodcasts()

        if (remotePodcast is Success) {
            // Real apps might want to do a proper sync, deleting, modifying or adding each task.
            podcastLocalDataSource.deleteAllPodcasts()
            remotePodcast.data.forEach { podcast ->
                podcastLocalDataSource.savePodcast(podcast)
            }
        } else if (remotePodcast is Error) {
            throw remotePodcast.exception
        }
    }


}