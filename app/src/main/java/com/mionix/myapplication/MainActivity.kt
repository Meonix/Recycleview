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
import androidx.lifecycle.Observer
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
    private val dBViewModel : DBViewModel by viewModel()
    private var isAToZFilter = false

    companion object{
        const val FILTER_FROM_A_TO_Z ="A-z"
        const val NO_FILTER = "no Filter"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        GlobalScope.launch(Dispatchers.IO) {
            dBViewModel.initListData()
        }
        initRecycleView()
        handleOnClick()
        initLoadMore()
    }
    override fun onDestroy() {
        super.onDestroy()
        isLoading = true
        isAToZFilter = false
    }
    private fun initView() {
        val spinnerLeft = arrayOf("no Filter","A-z")
        val arrayAdapterLeft = ArrayAdapter(this@MainActivity,R.layout.support_simple_spinner_dropdown_item,spinnerLeft)
        spFilter.adapter = arrayAdapterLeft
    }


    private fun addDataToDB(data:String){
        dBViewModel.saveData(data)
        if(isAToZFilter){
                GlobalScope.launch(Dispatchers.IO) {
                    dBViewModel.getMoreDataWithFilter(isAToZFilter,mAdapter.itemCount)
                }
        }
    }
    //Load more//
    private fun initLoadMore() {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = mAdapter.itemCount
                    GlobalScope.launch(Dispatchers.IO) {
                        val sizeOfDb= dBViewModel.getSize()
                        val sizeOfListSearch = dBViewModel.getSizeDataOfSearch(etSearch.text.toString())
                        if (isLoading) {
                            if((visibleItemCount + pastVisibleItem) >= total && mAdapter.itemCount < sizeOfListSearch){
                                getMorePage()
                                isLoading = false
                            }
                            else if ((visibleItemCount + pastVisibleItem) >= total && mAdapter.itemCount < sizeOfDb) {
                                //   page += 1
                                getMorePage()
                                isLoading = false
                            }
                        }
                    }

                }
                super.onScrolled(recyclerView, dx, dy)
            }

        })
    }

    private fun getMorePage(){
        isLoading = true
        GlobalScope.launch(Dispatchers.Main) {
            popularProgressBar.visibility = View.VISIBLE
            Handler().postDelayed({
                GlobalScope.launch(Dispatchers.Main) {
                    GlobalScope.launch(Dispatchers.IO) {
                        if(etSearch.text.toString().isNotEmpty()){
                            dBViewModel.search(etSearch.text.toString(),false)
                        }
                        else{
                            dBViewModel.getMoreDataWithFilter(isAToZFilter,mAdapter.itemCount)
                        }
                    }
                    popularProgressBar.visibility = View.GONE
                }
                isLoading = true
            },1200)
        }

    }


    //Load more//
    private fun deleteOnClick(){
                dBViewModel.getListData.observe(this@MainActivity, Observer { listData ->
                    listData.filter { it.isSelect }.forEach{
                        if(it.isSelect){
                            GlobalScope.launch(Dispatchers.IO) {
                                dBViewModel.deleteData(it.id,it.data)
                            }
                        }
                    }
                    mAdapter.upDateAdapter(listData)
                })
    }

    private fun handleOnClick() {
        tvClear.setOnClickListener {
            dBViewModel.getListData.observe(this@MainActivity, Observer {
                it.forEach {data ->
                    data.isSelect = false
                }
                mAdapter.upDateAdapter(it)
            })
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
                isAToZFilter = false
                dBViewModel.search(s.toString(),true)
            }
        })
        spFilter.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if(spFilter.selectedItem.toString() == FILTER_FROM_A_TO_Z){
                    isAToZFilter = true
                }
                else if(spFilter.selectedItem.toString() == NO_FILTER){
                    isAToZFilter = false
                }
                refreshData()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {

            }
        }
        fab.setOnClickListener {
            showCustomDialog()
        }
    }

    private fun refreshData(){
            GlobalScope.launch(Dispatchers.IO) {
                dBViewModel.getMoreDataWithFilter(isAToZFilter,0)
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
                    addDataToDB(data)
                }
                dialog.dismiss()
            }
        dialog.show()
    }
    private fun initRecycleView() {
        GlobalScope.launch(Dispatchers.Main) {
            mAdapter = Adapter()
            rv.adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            rv.layoutManager = layoutManager
            GlobalScope.launch(Dispatchers.IO) {
                dBViewModel.getMoreDataWithFilter(isAToZFilter,mAdapter.itemCount)
            }
            dBViewModel.getListData.observe(this@MainActivity, Observer {
                mAdapter.upDateAdapter(it)
            })
        }
    }
}