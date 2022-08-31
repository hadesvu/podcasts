package com.example.podcast.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Podcast::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PodcastDatabase : RoomDatabase() {

    abstract fun podcastDao(): PodcastDao
}