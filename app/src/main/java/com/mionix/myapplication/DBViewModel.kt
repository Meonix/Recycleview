package com.mionix.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mionix.myapplication.DB.DataTable
import com.mionix.myapplication.DB.LocalDB

class DBViewModel(private val mDB: LocalDB) : ViewModel() {
    fun getMoreData(firstIndex :Int) : MutableList<Data>{
        val list = mutableListOf<Data>()
        mDB.dataDAO().readMoreData(firstIndex).forEachIndexed { index, dataTable ->
            if(dataTable.data!=null){
                list.add(Data(dataTable.dataID,dataTable.data,dataTable.isSelect))
            }
        }
        return list
    }
//    fun getData(id: Int): Data? {
//            var mData : Data? = null
//            mDB.dataDAO().readData(id).let {
//                if(it.data !=null && it.isSelect !=null){
//                    mData =  Data(it.dataID,it.data, it.isSelect)
//                    return mData
//                }
//                return mData
//            }
//    }
    fun getListDataSorted(firstIndex: Int):MutableList<Data>{
        val list = mutableListOf<Data>()
        mDB.dataDAO().getListSorted(firstIndex).forEachIndexed { index, dataTable ->
            if(dataTable.data!=null){
                list.add(Data(dataTable.dataID,dataTable.data,dataTable.isSelect))
            }
        }
        return list
    }
    fun deleteData(id:Int,data:String){
        mDB.dataDAO().readData(id).let {
            mDB.dataDAO().deleteData(DataTable(id,data,false))
        }
    }
    fun saveData(data:String){
        val timestampLong = System.currentTimeMillis()/60000
        val timestamp = timestampLong.toInt()
        mDB.dataDAO().saveData(DataTable(timestamp, data, false))
    }
    fun getSize():Int{
        return mDB.dataDAO().getSizeOfDB()
    }
    fun getAllData():MutableList<Data>{
        val mListData = mutableListOf<Data>()
        mDB.dataDAO().readAllData().forEachIndexed { index, dataTable ->
            if(dataTable.data!=null){
                mListData.add(Data(dataTable.dataID,dataTable.data,dataTable.isSelect))
            }
        }
        return mListData
    }
}