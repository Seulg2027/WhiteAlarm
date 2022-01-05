package com.seulgi.whitealarm.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.seulgi.whitealarm.R

class VisitRVAdapter (
        val context: Context,
        val items : ArrayList<VisitModel>
) : RecyclerView.Adapter<VisitRVAdapter.Viewholder>(){
    private var TAG = VisitRVAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.main_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: VisitRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems (item: VisitModel){

        }
    }
}