package com.mionix.myapplication

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
import com.mionix.myapplication.DB.DataTable
import com.mionix.myapplication.DB.LocalDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    //load more
    private var isLoading = true
    private lateinit var layoutManager: LinearLayoutManager
    //
    private lateinit var mAdapter : Adapter
    private var currentListData = mutableListOf<Data>()
    private val dBViewModel : DBViewModel by viewModel()
    private var sizeOfDB = 0
    private var observerList : Deferred<MutableList<Data>> ?=null
    private var flagFilter = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        GlobalScope.launch(Dispatchers.IO) {
            initListData()
        }
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
        try {
            if(dBViewModel.getSize() == 0){
                var i = 0
                while (i<100){
                    dBViewModel.saveData("data $i")
                    i += 1
                }
                observerData()

            }
            else{
                observerData()
            }
            // define the size of list data in DB
            sizeOfDB = dBViewModel.getSize()
        } catch (e: Exception) {
            Log.i("DUY","Error ${e.message} ")
        }

    }
    //this function to hearing data form observerList every time when page is changed
    private fun observerData(){
        observerList = GlobalScope.async {
            getMoreData()
        }
    }

    private fun addingDataToDB(data:String){
        dBViewModel.saveData(data)
        if(flagFilter){
            GlobalScope.launch(Dispatchers.Main) {
                currentListData.clear()
                observerData()
                observerList?.let {currentListData =  it.await()}
                mAdapter.upDateAdapter(currentListData)
            }
        }
        sizeOfDB = dBViewModel.getSize()
    }
    //Load more//
    private fun initLoadMore() {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = mAdapter.itemCount
                    if (isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total && mAdapter.itemCount < sizeOfDB) {
                         //   page += 1

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
            GlobalScope.launch(Dispatchers.Main) {
                observerData()
                observerList?.let{currentListData.addAll(it.await())}
                mAdapter.upDateAdapter(currentListData)
            }
            popularProgressBar.visibility = View.GONE

            isLoading = true
        },1200)
    }
    private fun getMoreData():MutableList<Data>{
        if(flagFilter){
            return dBViewModel.getListDataSorted(currentListData.size)
        }
        return dBViewModel.getMoreData(currentListData.size)
    }

    //Load more//
    private fun deleteOnClick(){
        GlobalScope.launch(Dispatchers.IO){
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    currentListData.filter { it.isSelect }.forEachIndexed { index, data ->
                        if(data.isSelect){
                            dBViewModel.deleteData(data.id,data.string)
                        }
                    }
                    currentListData.removeIf { data: Data ->  data.isSelect}
                }
                GlobalScope.launch(Dispatchers.Main) {
                    mAdapter.upDateAdapter(currentListData)
                }
            } catch (e: Exception) {
                Log.i("DUY","Error ${e.message} ")
            }
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
            deleteOnClick()
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
                flagFilter = false
                if(s.toString() == ""){
                    GlobalScope.launch(Dispatchers.Main) {
                        currentListData.clear()
                        observerData()
                        observerList?.let {currentListData =  it.await()}
                        mAdapter.upDateAdapter(currentListData)
                    }
                }
                else{
                    GlobalScope.launch(Dispatchers.IO) {
                        currentListData = dBViewModel.getAllData().filter { it.string.contains(s)}.toMutableList()
                        GlobalScope.launch(Dispatchers.Main) {
                            mAdapter.upDateAdapter(currentListData)
                        }
                    }
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
                    flagFilter = true
                    GlobalScope.launch(Dispatchers.Main) {
                        currentListData.clear()
                        observerData()
                        observerList?.let {currentListData =  it.await()}
                        mAdapter.upDateAdapter(currentListData)
                    }
                }
                else if(spFilter.selectedItem.toString()=="no Filter"){
                    flagFilter = false
                    GlobalScope.launch(Dispatchers.Main) {
                        currentListData.clear()
                        observerData()
                        observerList?.let {currentListData =  it.await()}
                        mAdapter.upDateAdapter(currentListData)
                    }
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
                GlobalScope.launch(Dispatchers.IO){
                    addingDataToDB(data)
                }
                dialog.dismiss()
            }
        dialog.show()
    }
    private fun initRecycleView() {
        GlobalScope.launch(Dispatchers.Main) {
            observerList?.let {currentListData =  it.await()}
            mAdapter = Adapter(currentListData)
            rv.adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            rv.layoutManager = layoutManager
            (rv.adapter as Adapter).notifyDataSetChanged()
        }
    }
}