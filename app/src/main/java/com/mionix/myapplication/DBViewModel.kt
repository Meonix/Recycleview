package com.mionix.myapplication

import androidx.lifecycle.ViewModel
import com.mionix.myapplication.DB.DataTable
import com.mionix.myapplication.DB.LocalDB
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*

class DBViewModel(private val mDB: LocalDB) : ViewModel() {
    companion object{
        const val DATA_LIMIT_PER_CALL = 25
    }
    fun initListData() {
        if(getSize() == 0){
            initDataForTheFirstTime()
        }
    }
    private fun initDataForTheFirstTime(){
        var i = 0
        while (i<100){
            saveData("data $i")
            i += 1
        }

    }
    fun getMoreData(firstIndex :Int) : MutableList<Data>{
        val list = mutableListOf<Data>()
        mDB.dataDAO().readMoreData(firstIndex,DATA_LIMIT_PER_CALL).forEachIndexed { index, dataTable ->
            if(dataTable.data!=null){
                list.add(Data(dataTable.dataID,dataTable.data,dataTable.isSelect))
            }
        }
        //another way
//        mDB.dataDAO().readMoreData(firstIndex,DATA_LIMIT_PER_CALL).filter{ it.data != null }.map {
//           Data(it.dataID, it.data!!,it.isSelect)
//        }.toMutableList()
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
        mDB.dataDAO().getSortedList(firstIndex,DATA_LIMIT_PER_CALL).forEachIndexed { index, dataTable ->
            if(dataTable.data!=null){
                list.add(Data(dataTable.dataID,dataTable.data,dataTable.isSelect))
            }
        }
    //another way
//        mDB.dataDAO().getSortedList(firstIndex,DATA_LIMIT_PER_CALL).filter{ it.data != null }.map {
//           Data(it.dataID, it.data!!,it.isSelect)
//        }.toMutableList()
        return list
    }
    fun deleteData(id:String,data:String){
            mDB.dataDAO().deleteData(DataTable(id,data,false))
    }
    @Throws(Exception::class)
    fun createTransactionID(): String? {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase()
    }
    fun saveData(data:String){
        val id = createTransactionID()
        if(id != null){
            mDB.dataDAO().saveData(DataTable(id, data, false))
        }
    }
    fun getSize():Int{
        return mDB.dataDAO().getSizeOfDB()
    }
    fun getAllData():MutableList<Data>{
        val mListData = mutableListOf<Data>()
        mDB.dataDAO().readAllData().forEach { dataTable ->
            if(dataTable.data!=null){
                mListData.add(Data(dataTable.dataID,dataTable.data,dataTable.isSelect))
            }
        }
        //another way
//        return mDB.dataDAO().readAllData().filter{ it.data != null }.map {
//           Data(it.dataID, it.data!!,it.isSelect)
//        }.toMutableList()
        return mListData
    }
    //this function to hearing data form observerList every time when page is changed
    fun getData(isAToZFilter: Boolean,currentListData: MutableList<Data>): Deferred<MutableList<Data>> {
        return GlobalScope.async {
            getMoreData(isAToZFilter,currentListData)
        }
    }
    private fun getMoreData(isAToZFilter:Boolean, currentListData:MutableList<Data>):MutableList<Data>{
        if(isAToZFilter){
            return getListDataSorted(currentListData.size)
        }
        return getMoreData(currentListData.size)
    }
}