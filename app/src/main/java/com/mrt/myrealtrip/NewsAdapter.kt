package com.mrt.myrealtrip

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(val context: Context, val newsList: ArrayList<News>) : RecyclerView.Adapter<NewsAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.recyclerview_news, parent, false)

        return Holder(view)
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemId(position: Int): Long {
        return newsList[position].title.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(newsList[position])
        holder.view.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
        holder.newsContent.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    inner class Holder(newsView: View) : RecyclerView.ViewHolder(newsView) {
        val newsThumbnail = newsView.findViewById<ImageView>(R.id.imgv_news_thumbnail)
        val newsTitle = newsView.findViewById<TextView>(R.id.tv_news_title)
        val newsContent = newsView.findViewById<TextView>(R.id.tv_news_content)
        val newsKeyword_1 = newsView.findViewById<TextView>(R.id.tv_keyword_1)
        val newsKeyword_2 = newsView.findViewById<TextView>(R.id.tv_keyword_2)
        val newsKeyword_3 = newsView.findViewById<TextView>(R.id.tv_keyword_3)
        val view: View = newsView

        fun bind (news: News) {
            if (news.img_link != "") {
                Glide.with(view)
                    .load(news.img_link)
                    .override(100)
                    .centerCrop()
                    .into(newsThumbnail)
            } else {
                newsThumbnail?.setImageResource(R.mipmap.ic_launcher)
            }
            newsTitle?.text = news.title
            newsContent?.text = news.content
            newsKeyword_1?.text = news.keyword_1
            newsKeyword_2?.text = news.keyword_2
            newsKeyword_3?.text = news.keyword_3
        }
    }

}