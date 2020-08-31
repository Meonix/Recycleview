package com.mionix.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mionix.myapplication.DB.LocalDB

class DBViewModel(private val mDB: LocalDB) : ViewModel() {
    fun getAllData( list: MutableList<Data>){
        mDB.dataDAO().readAllData().forEach {
//                    Log.i("DUY","""" Id id: ${it.data} """")
            if (it.data != null && it.isSelect != null) {
                list.add(Data(it.dataID,it.data, it.isSelect))
            }
            Log.i("DUY", """" Id id: ${it.dataID} """")
        }
    }
}