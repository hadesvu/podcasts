package com.example.podcast.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Episodes")
data class Episode @JvmOverloads constructor(
    @PrimaryKey @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "code") var code: String = "",
    @ColumnInfo(name = "podcastId") var podcastId: Int = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "duration") var duration: Long = 0 ,
    @ColumnInfo(name = "audioCacheId") var audioCacheId: Int = 0,
    @ColumnInfo(name = "releaseDate") var releaseDate: Date,
    @ColumnInfo(name = "isCompleted") var isCompleted: Boolean = false,
    @ColumnInfo(name = "downloadStatus") var downloadStatus: DownloadStatus = DownloadStatus.Requested,
    @ColumnInfo(name = "downloadProgress") var downloadProgress: Byte = 0,
    @ColumnInfo(name = "playbackPosition") var playbackPosition: Long = 0,
    @ColumnInfo(name = "isProhibited") var isProhibited: Boolean = false,
){

}

enum class DownloadStatus {
    Requested,
    Downloaded,
    InProgress
}