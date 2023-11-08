package com.putu.sora.data.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
class StoryEntity (

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val id: String,

    @ColumnInfo("name")
    val name: String,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("photoUrl")
    val photoUrl: String,

    @ColumnInfo("createdAt")
    val createdAt: String,

    @ColumnInfo("lat")
    val lat: Double?,

    @ColumnInfo("lon")
    val lon: Double?

): Parcelable