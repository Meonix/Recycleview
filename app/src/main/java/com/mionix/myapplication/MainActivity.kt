package com.mionix.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        var i = 0
        while (i<10){
            listData.add(Data("Adsad",false))
            listData.add(Data("gdsad",false))
            listData.add(Data("Cdsad",false))
            listData.add(Data("tdsad",false))
            listData.add(Data("ydsad",false))
            listData.add(Data("wdsad",false))
            listData.add(Data("qdsad",false))
            listData.add(Data("uafwq",false))
            listData.add(Data("ajk",false))
            listData.add(Data("badsv",false))
            listData.add(Data("Basd",false))
            listData.add(Data("csaeq",false))
            listData.add(Data("QWT",false))
            listData.add(Data("Adsad",false))
            listData.add(Data("Adsad",false))
            listData.add(Data("rqwqwd",false))
            i += 1
        }

    }

    private fun initLoadMore() {
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val pastVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
                    val total = mAdapter.itemCount
                    if (isLoading) {
                        if ((visibleItemCount + pastVisibleItem) >= total) {
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
    }

    private fun initRecycleView() {
        getListData(page)
        mAdapter = Adapter(currentListData)
        rv.adapter = mAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
        rv.layoutManager = layoutManager
        (rv.adapter as Adapter).notifyDataSetChanged()
    }
}