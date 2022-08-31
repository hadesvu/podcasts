package com.example.podcast.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM Episodes")
    fun getLiveEpisodes(): Flow<List<Episode>>

    @Query("SELECT * FROM Episodes WHERE id = :episodeId")
    fun getLiveEpisodeById(episodeId: Int): Flow<Episode>

    @Query("SELECT * FROM Episodes")
    fun getEpisodes(): List<Episode>

    @Query("SELECT * FROM Episodes WHERE id = :episodeId")
    fun getEpisodeById(episodeId: Int): Episode?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisode(episode: Episode)

    @Update
    fun updateEpisode(episode: Episode): Int

    @Query("DELETE FROM Episodes")
    fun deletePodCasts()
}