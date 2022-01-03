package com.seulgi.whitealarm.hospital

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.seulgi.whitealarm.R

class HospitalRVAdapter(
        val context: Context,
        val hospital : ArrayList<HospitalListLayout>
) : RecyclerView.Adapter<HospitalRVAdapter.Viewholder>(){
    private var TAG = HospitalRVAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.hospital_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: HospitalRVAdapter.Viewholder, position: Int) {
        holder.name.text = hospital[position].name
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return hospital.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name : Button = itemView.findViewById(R.id.hospitalArea)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}