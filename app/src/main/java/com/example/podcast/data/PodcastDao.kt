package com.example.podcast.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PodcastDao {
    @Query("SELECT * FROM Podcasts")
    fun getLivePodcasts(): Flow<List<Podcast>>

    @Query("SELECT * FROM Podcasts WHERE id = :podcastId")
    fun getLivePodcastById(podcastId: Int): Flow<Podcast>

    @Query("SELECT * FROM Podcasts")
    fun getPodcasts(): List<Podcast>

    @Query("SELECT * FROM Podcasts WHERE id = :podcastId")
    fun getPodcastById(podcastId: Int): Podcast?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPodcast(podcast: Podcast)

    @Update
    fun updatePodcast(podcast: Podcast): Int

    @Query("DELETE FROM Podcasts")
    fun deletePodCasts()
}