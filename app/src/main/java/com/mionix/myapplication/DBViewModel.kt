package com.mionix.myapplication

import android.util.Log
import androidx.lifecycle.*
import com.mionix.myapplication.DB.DataTable
import com.mionix.myapplication.DB.LocalDB
import kotlinx.coroutines.*
import java.util.*

class DBViewModel(private val mDB: LocalDB) : ViewModel() {
    private val _getListData = MutableLiveData<MutableList<Data>>()
    var isLoading = true
    var isAToZFilter = false
    val getListData: LiveData<MutableList<Data>> get() = _getListData
    companion object{
        const val DATA_LIMIT_PER_CALL = 25
    }
    fun initListData() {
        if(getSize() == 0){
            initDataForTheFirstTime()
        }
    }
    fun search(searchString : String,firstTimeLoad:Boolean) = _getListData.value?.let {
        viewModelScope.launch {
            if(firstTimeLoad){
                it.clear()
            }
            val data = async(Dispatchers.IO) {
                mDB.dataDAO()
                    .searchData(searchString, DATA_LIMIT_PER_CALL, it.size)
                    ?.filter { it.data != null }?.map {
                        Log.d("TAG", "search: ${it.data}")
                    Data(it.dataID, it.data!!, it.isSelect)
                }?.toMutableList()
            }
            data.await()?.let { it1 -> it.addAll(it1) }
        }

    }

    private fun initDataForTheFirstTime(){
        var i = 0
        while (i<100){
            saveData("data $i")
            i += 1
        }

    }
    private fun getMoreData(){
        val list = mutableListOf<Data>()
        mDB.dataDAO().readMoreData(_getListData.value?.size ?: 0,DATA_LIMIT_PER_CALL).forEachIndexed { index, dataTable ->
            if(dataTable.data!=null){
                list.add(Data(dataTable.dataID,dataTable.data,dataTable.isSelect))
            }
        }
        //another way
//        mDB.dataDAO().readMoreData(firstIndex,DATA_LIMIT_PER_CALL).filter{ it.data != null }.map {
//           Data(it.dataID, it.data!!,it.isSelect)
//        }.toMutableList()
        viewModelScope.launch {
            _getListData.value =  ((_getListData.value ?: mutableListOf()) + list).toMutableList()
        }
    }
    private fun getListDataSorted(){
        val list = mutableListOf<Data>()
        mDB.dataDAO().getSortedList(_getListData.value?.size ?: 0,DATA_LIMIT_PER_CALL).forEachIndexed { index, dataTable ->
            if(dataTable.data!=null){
                list.add(Data(dataTable.dataID,dataTable.data,dataTable.isSelect))
            }
        }
        viewModelScope.launch {
            _getListData.value = ((_getListData.value ?: mutableListOf()) + list).toMutableList()
        }
    //another way
//        mDB.dataDAO().getSortedList(firstIndex,DATA_LIMIT_PER_CALL).filter{ it.data != null }.map {
//           Data(it.dataID, it.data!!,it.isSelect)
//        }.toMutableList()
    }
    fun deleteData(id:String,data:String){
            mDB.dataDAO().deleteData(DataTable(id,data,false))
            _getListData.value?.remove(Data(id,data,true))
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
    fun getSizeDataOfSearch(searchString: String):Int{
        return mDB.dataDAO().getSizeOfSearchListDB(searchString)
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

    fun getMoreDataWithFilter(isAToZFilter:Boolean, reset:Boolean){
        viewModelScope.launch  {
             if(reset){
                _getListData.value = mutableListOf()
             }
            withContext(Dispatchers.IO){
                if(isAToZFilter){
                    getListDataSorted()
                }
                else{
                    getMoreData()
                }
            }
        }



    }
}