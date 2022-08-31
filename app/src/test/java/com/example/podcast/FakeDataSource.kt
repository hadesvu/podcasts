package com.example.podcast

import com.example.podcast.data.Podcast
import com.example.podcast.data.PodcastDataSource
import com.example.podcast.data.Result
import com.example.podcast.data.Result.Success
import com.example.podcast.data.Result.Error
import kotlinx.coroutines.flow.Flow

class FakeDataSource(var podcasts: MutableList<Podcast>? = mutableListOf()) : PodcastDataSource {
    override fun getLivePodcasts(): Flow<Result<List<Podcast>>> {
        TODO("Not yet implemented")
    }

    override fun getPodcasts(): Result<List<Podcast>> {
        podcasts?.let { return Success(ArrayList(it)) }
        return Error(
            Exception("Tasks not found")
        )
    }

    override fun refreshPodcasts() {
        TODO("Not yet implemented")
    }

    override fun savePodcast(podcast: Podcast) {
        podcasts?.add(podcast)
    }

    override fun deleteAllPodcasts() {
        podcasts?.clear()
    }


}