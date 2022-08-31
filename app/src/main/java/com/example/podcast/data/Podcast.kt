package com.example.podcast.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Podcasts")
data class Podcast @JvmOverloads constructor(
    @PrimaryKey @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "author") var author: String = "",
    @ColumnInfo(name = "imageCacheId") var imageCacheId: Int = 0,
    @ColumnInfo(name = "followedDate") var followedDate: Date = Calendar.getInstance().time
){

}