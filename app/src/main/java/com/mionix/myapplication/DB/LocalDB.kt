package com.mionix.myapplication.DB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DataTable::class/*,WatchListTable::class*/],version = 1)
abstract class LocalDB : RoomDatabase() {
    abstract fun dataDAO(): DataDao
}