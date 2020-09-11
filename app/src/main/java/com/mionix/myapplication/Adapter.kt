package com.mionix.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item.view.*
import java.util.*

class Adapter(private var data:MutableList<Data> = mutableListOf()): RecyclerView.Adapter<Adapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun onBindData(position: Int){
            setColor(data[position])
            itemView.tvItem.text =  data[position].data + " "+data[position].isSelect.toString()
            itemView.setOnLongClickListener {
           //         model.isSelect = true
                data[position].isSelect = !data[position].isSelect
                setColor(data[position])
                return@setOnLongClickListener true
            }
            itemView.setOnClickListener {
                val filterList = data.filter { data ->
                    data.isSelect
                }
                if(filterList.isNotEmpty()&& data[position].isSelect){
                    data[position].isSelect = false
                    setColor(data[position])
                }
                else if(filterList.isNotEmpty() && !data[position].isSelect){
                    data[position].isSelect = true
                    setColor(data[position])
                }
                else{
                    data[position].isSelect = false
                    setColor(data[position])
                }
            }
        }
        private fun setColor(model: Data){
            itemView.setBackgroundColor(
                if (!model.isSelect){
                    Color.TRANSPARENT
                }
                else{
                    ContextCompat.getColor(itemView.context,R.color.colorAccent)
                }
            )
        }
    }
    fun upDateAdapter(data: MutableList<Data>){
        this.data = data
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindData(position)
    }
}
data class Data(var id :String ,var data :String , var isSelect:Boolean)
