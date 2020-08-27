package com.mionix.myapplication

import android.app.ActionBar
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.mionix.myapplication.DB.DataTable
import com.mionix.myapplication.DB.LocalDB
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    //load more
    private var page = 1
    private var isLoading = true
    private lateinit var layoutManager: LinearLayoutManager
    //
    private lateinit var mAdapter : Adapter
    private var listData = mutableListOf<Data>()
    private var currentListData = mutableListOf<Data>()
    private var tempListData = mutableListOf<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListData()
        initRecycleView()
        handleOnClick()
        initLoadMore()
    }

    private fun initView() {
        val spinnerLeft = arrayOf("no Filter","A-z")
        val arrayAdapterLeft = ArrayAdapter(this@MainActivity,R.layout.support_simple_spinner_dropdown_item,spinnerLeft)
        spFilter.adapter = arrayAdapterLeft
    }

    private fun initListData() {
        val db = Room.databaseBuilder(applicationContext
            , LocalDB::class.java
            ,"MyMovieDB")
            .fallbackToDestructiveMigration()
            .build()
        val timestampLong = System.currentTimeMillis()/60000
        val timestamp = timestampLong.toString()
        Thread{

            if(db.dataDAO().readAllData().isEmpty()){
                var i = 0
                while (i<100){
                    val favouritesTable = DataTable(i, "data $i", false)
                    db.dataDAO().saveData(favouritesTable)
                    i += 1
                }
            }
            else{
                db.dataDAO().readAllData().forEach {
//                    Log.i("DUY","""" Id id: ${it.data} """")
                    if(it.data !=null && it.isSelect !=null){
                        listData.add(Data(it.data,it.isSelect))
                    }
                    Log.i("DUY","""" Id id: ${it.dataID} """")
                }
                getListData(1)
                page += 1
            }
        }.start()
    }
    private fun addingDataToDB(id:Int,data:String){
                val db = Room.databaseBuilder(applicationContext
            , LocalDB::class.java
            ,"MyMovieDB")
            .fallbackToDestructiveMigration()
            .build()
        val timestampLong = System.currentTimeMillis()/60000
        val timestamp = timestampLong.toString()
        Thread{

            if(db.dataDAO().readAllData().isNotEmpty()){
                val favouritesTable = DataTable(id, data, false)
                db.dataDAO().saveData(favouritesTable)
            }
            else{
                db.dataDAO().readAllData().forEach {
                    Log.i("DUY","""" Id id: ${it.data} """")
                }

            }
        }.start()
    }
    private fun initLoadMore() {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = mAdapter.itemCount
                    if (isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total && page*25<listData.size) {
                            page += 1

                            getMorePage()
                            isLoading = false
                        }
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }

        })
    }
    private fun getMorePage(){
        isLoading = true
        popularProgressBar.visibility = View.VISIBLE
        Handler().postDelayed({
            getListData(page)
            mAdapter.upDateAdapter(currentListData)
            popularProgressBar.visibility = View.GONE

            isLoading = true
        },1200)
    }

    private fun getListData(page: Int) {
            val a = 25* page
            var startAt = currentListData.size
            while(startAt<a){
                if(listData.size==startAt){
                    break
                }
                currentListData.add(listData[startAt])
                startAt += 1
            }
    }

    private fun handleOnClick() {
        tvClear.setOnClickListener {
            currentListData.forEachIndexed { index, data ->
                data.isSelect = false
            }
            mAdapter.upDateAdapter(currentListData)
        }
        tvDelete.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                currentListData.removeIf { data: Data ->  data.isSelect}
                mAdapter.upDateAdapter(currentListData)
            }

        }
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if(s==""){
                    mAdapter.upDateAdapter(currentListData)
                }
                else{
                    tempListData = currentListData.filter { it.string.contains(s)}.toMutableList()
                    mAdapter.upDateAdapter(tempListData)
                }

            }
        })
        spFilter.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                Log.d("DUY",spFilter.selectedItem.toString())
                if(spFilter.selectedItem.toString()=="A-z"){
                    tempListData = currentListData.sortedBy { it.string}.toMutableList()
                    mAdapter.upDateAdapter(tempListData)
                }
                else if(spFilter.selectedItem.toString()=="no Filter"){
                    mAdapter.upDateAdapter(currentListData)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }
        fab.setOnClickListener {
            showCustomDialog()
        }
    }
    private fun showCustomDialog() {
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setContentView(R.layout.dialog_custom_layout)
        val btAdd = dialog.findViewById(R.id.btAdd) as Button
        val etAddingData = dialog.findViewById(R.id.etAddingData) as EditText
            btAdd.setOnClickListener {
                val data = etAddingData.text.toString()
                addingDataToDB(listData.size,data)
            }
        dialog.show()

    }
    private fun initRecycleView() {
        mAdapter = Adapter(currentListData)
        rv.adapter = mAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
        rv.layoutManager = layoutManager
        (rv.adapter as Adapter).notifyDataSetChanged()
    }
}