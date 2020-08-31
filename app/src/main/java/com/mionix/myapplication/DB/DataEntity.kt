package com.mionix.myapplication.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DataTable (@PrimaryKey
                       val dataID: Int,
                       @ColumnInfo(name = "data")
                       val data: String?,
                       @ColumnInfo(name = "isSelect")
                       val isSelect: Boolean?
)
//Adding more Table if needed
//@Entity
//class WatchListTable (
//    @PrimaryKey
//    val colMovieID: Int,
//    @ColumnInfo(name = "title")
//    val colTitle: String?,
//    @ColumnInfo(name = "posterPath")
//    val colPoster: String?,
//    @ColumnInfo(name = "overview")
//    val colOverview: String?,
//    @ColumnInfo(name = "dateSave")
//    val colDateSave: String?
//)