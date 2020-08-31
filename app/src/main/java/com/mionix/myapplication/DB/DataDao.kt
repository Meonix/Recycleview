package com.mionix.myapplication.DB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {
    @Insert
    fun saveData(data : DataTable)
    @Query("select * from DataTable WHERE dataID = :dataID")
    fun readData(dataID : Int) :DataTable
    @Query("select * from DataTable")
    fun readAllData() :List<DataTable>
    @Delete
    fun deleteData(data : DataTable)


//    @Insert
//    fun saveWatchListTable(movie : WatchListTable)
//    @Query("select * from WatchListTable WHERE colMovieID = :colMovieID")
//    fun readWatchListTable(colMovieID : Int) :List<WatchListTable>
//    @Query("select * from WatchListTable")
//    fun readAllWatchListTable() :List<WatchListTable>

}