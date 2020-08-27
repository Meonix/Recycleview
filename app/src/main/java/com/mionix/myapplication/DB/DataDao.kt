package com.mionix.myapplication.DB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {
    @Insert
    fun saveData(movie : DataTable)
    @Query("select * from DataTable WHERE dataID = :dataID")
    fun readData(dataID : Int) :DataTable
    @Query("select * from DataTable")
    fun readAllData() :List<DataTable>


//    @Insert
//    fun saveWatchListTable(movie : WatchListTable)
//    @Query("select * from WatchListTable WHERE colMovieID = :colMovieID")
//    fun readWatchListTable(colMovieID : Int) :List<WatchListTable>
//    @Query("select * from WatchListTable")
//    fun readAllWatchListTable() :List<WatchListTable>

}