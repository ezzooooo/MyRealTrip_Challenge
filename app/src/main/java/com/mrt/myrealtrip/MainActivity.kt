package com.mrt.myrealtrip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.jsoup.*
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private val url = "https://news.google.com/rss?hl=ko&gl=KR&ceid=KR:ko"
    private val NEWS_TITLE = "newsTitle"
    private val NEWS_KEYWORD1 = "newsKeyword1"
    private val NEWS_KEYWORD2 = "newsKeyword2"
    private val NEWS_KEYWORD3 = "newsKeyword3"
    private val NEWS_URL = "newsUrl"
    private var lastpubDate = ""

    private var newsList = arrayListOf<News>()
    private val mAdapter = NewsAdapter(this, newsList)
    private lateinit var backPressCloseHandler:BackPressCloseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backPressCloseHandler = BackPressCloseHandler(this)

        val mSwipeRefreshLayout =  findViewById<SwipeRefreshLayout>(R.id.news_swipe_layout)
        mSwipeRefreshLayout.setOnRefreshListener {
            GlobalScope.launch {
                val updateNews = loadNews()
                updateNews.join()
                mSwipeRefreshLayout.isRefreshing = false
            }
        }

        mAdapter.setHasStableIds(true)
        mAdapter.setItemClickListener( object: NewsAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                val intent = Intent(applicationContext, NewsWebviewActivity::class.java)
                intent.putExtra(NEWS_TITLE, newsList[position].title)
                intent.putExtra(NEWS_KEYWORD1, newsList[position].keyword_1)
                intent.putExtra(NEWS_KEYWORD2, newsList[position].keyword_2)
                intent.putExtra(NEWS_KEYWORD3, newsList[position].keyword_3)
                intent.putExtra(NEWS_URL, newsList[position].link)
                startActivity(intent)
            }
        })

        news_list_view.adapter = mAdapter

        val lm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        lm.initialPrefetchItemCount = 30
        news_list_view.layoutManager = lm
        news_list_view.setHasFixedSize(true)
        news_list_view.setItemViewCacheSize(40)
        news_list_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        news_list_progressBar.visibility = View.VISIBLE

        loadNews()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        backPressCloseHandler.onBackPressed()
    }

    fun loadNews() = CoroutineScope(Dispatchers.Main).launch {
        val task = launch(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(url).parser(Parser.xmlParser()).ignoreHttpErrors(true).get()
                if( lastpubDate.equals(doc.select("item").first().select("pubDate").text()) ) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationContext, "새로고침 할 내용이 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    newsList.clear()
                    lastpubDate = doc.select("item").first().select("pubDate").text()
                    val elements = doc.select("item")
                    for (element in elements) {
                        val link = element.select("link").first().text()
                        var newsDoc: Document? = null
                        try {
                            newsDoc = Jsoup.connect(link).userAgent("Mozilla").timeout(15000).get()
                        } catch (e:IOException) {
                            newsDoc = null
                        }

                        if (newsDoc != null) {
                            var content = ""
                            var imgLink = ""

                            val imgLinkElement =
                                newsDoc.select("meta[property=og:image]").first()

                            if (imgLinkElement != null) {
                                imgLink = imgLinkElement.attr("content")
                            }

                            val contentElement =
                                newsDoc.select("meta[property=og:description]").first()
                            if (contentElement != null) {
                                content = contentElement.attr("content")
                                content = content.trim()
                            } else {
                                content = "신문사의 오류로 읽어오지 못했습니다."
                            }

                            if (imgLink != "" && content != "") {
                                val title = element.select("title").first().text()
                                val keyword = WordCount.sort(content)

                                newsList.add(
                                    News(
                                        imgLink,
                                        title,
                                        content,
                                        link,
                                        keyword[0],
                                        keyword[1],
                                        keyword[2]
                                    )
                                )

                                if(newsList.size < 8) {
                                    if(newsList.size % 2 == 0) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            mAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                                else if(newsList.size % 8 == 0) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        mAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                println("parse 오류")
                e.printStackTrace()
            }
        }
        task.join()
        Toast.makeText(applicationContext, "최신 뉴스 상태입니다!", Toast.LENGTH_SHORT)
            .show()
        news_list_progressBar.visibility = View.GONE
        mAdapter.notifyDataSetChanged()
    }
}
