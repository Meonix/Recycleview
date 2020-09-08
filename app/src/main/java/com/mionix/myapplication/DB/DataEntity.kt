package com.mionix.myapplication.DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DataTable (@PrimaryKey
                       val dataID: String,
                       @ColumnInfo(name = "data")
                       val data: String?,
                       @ColumnInfo(name = "isSelect")
                       val isSelect: Boolean
)