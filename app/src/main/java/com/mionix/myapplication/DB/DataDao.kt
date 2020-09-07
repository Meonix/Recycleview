package com.mionix.myapplication.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {
    @Insert
    fun saveData(data : DataTable)
    @Query("select * from DataTable WHERE dataID = :dataID")
    fun readData(dataID : Int) :DataTable?
    @Query("select * from DataTable")
    fun readAllData() :List<DataTable>
    @Query("select * from DataTable LIMIT 25 OFFSET :startAt")
    fun readMoreData(startAt:Int):MutableList<DataTable>
    @Delete
    fun deleteData(data : DataTable)
    @Query("select count(data) from DataTable")
    fun getSizeOfDB(): Int
    @Query("select * from DataTable Order by data LIMIT 25 OFFSET :startAt")
    fun getListSorted(startAt: Int):MutableList<DataTable>

//    @Insert
//    fun saveWatchListTable(movie : WatchListTable)
//    @Query("select * from WatchListTable WHERE colMovieID = :colMovieID")
//    fun readWatchListTable(colMovieID : Int) :List<WatchListTable>
//    @Query("select * from WatchListTable")
//    fun readAllWatchListTable() :List<WatchListTable>

}