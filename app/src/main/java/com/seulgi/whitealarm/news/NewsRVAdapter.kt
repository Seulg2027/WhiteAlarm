package com.seulgi.whitealarm.news

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seulgi.whitealarm.R

class NewsRVAdapter(
        val context: Context,
        val items : ArrayList<NewsModel>
    ) : RecyclerView.Adapter<NewsRVAdapter.Viewholder>(){
    private var TAG = NewsRVAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.news_rv_item, parent, false)

        Log.d(TAG, items.toString())
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: NewsRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bindItems(item : NewsModel) {
            itemView.setOnClickListener{
                Log.d(TAG, item.webUrl)
                val intent = Intent(context, NewsLinkActivity::class.java)
                intent.putExtra("url", item.webUrl)
                itemView.context.startActivity(intent)
            }

            val imageViewArea = itemView.findViewById<ImageView>(R.id.imageView)
            val titleViewArea = itemView.findViewById<TextView>(R.id.titleView)

            titleViewArea.text = item.title

            Glide.with(context)
                    .load(item.imageUrl)
                    .into(imageViewArea)
        }
    }

}