package com.mrt.myrealtrip

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recyclerview_news.view.*

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
        private val newsThumbnail = newsView.imgv_news_thumbnail
        private val newsTitle = newsView.tv_news_title
        internal val newsContent = newsView.tv_news_content
        private val newsKeyword1 = newsView.tv_keyword_1
        private val newsKeyword2 = newsView.tv_keyword_2
        private val newsKeyword3 = newsView.tv_keyword_3
        internal val view: View = newsView

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
            newsTitle.text = news.title
            newsContent.text = news.content
            newsKeyword1.text = news.keyword_1
            newsKeyword2.text = news.keyword_2
            newsKeyword3.text = news.keyword_3
        }
    }

}