package com.example.kotlinnews

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ThemedSpinnerAdapter
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinnews.Adapter.ViewHolder.ListSourceAdapter
import com.example.kotlinnews.Common.Common
import com.example.kotlinnews.Interface.NewsService
import com.example.kotlinnews.Model.WebSite
import com.google.gson.Gson
import dmax.dialog.SpotsDialog
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var newsService: NewsService
    lateinit var adapter: ListSourceAdapter
    lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize cache DB
        Paper.init(this)

        //Initialize service
        newsService = Common.newsService

        //Initialize view
        swipe_to_refresh.setOnRefreshListener {
            loadWebSiteSource(true)
        }
        recycler_view_source_news.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recycler_view_source_news.layoutManager = layoutManager

        dialog = SpotsDialog(this)
        loadWebSiteSource(false)
    }

    private fun loadWebSiteSource(isRefresh: Boolean) {

        if(!isRefresh){
            val cache = Paper.book().read<String>("cache")
            if(cache !=null && !cache.isBlank() && cache !="null"){
                //read cache
                val webSite = Gson().fromJson<WebSite>(cache,WebSite::class.java)
                adapter = ListSourceAdapter(baseContext, webSite)
                adapter.notifyDataSetChanged()
                recycler_view_source_news.adapter = adapter
            }else{
                //Load website and write cache
                dialog.show()
                //fetch new data
                newsService.sources.enqueue(object :retrofit2.Callback<WebSite>{
                    override fun onResponse(call: Call<WebSite>, response: Response<WebSite>) {
                        adapter = ListSourceAdapter(baseContext,response!!.body()!!)
                        adapter.notifyDataSetChanged()
                        recycler_view_source_news.adapter = adapter

                        //save to cache
                        Paper.book().write("cache",Gson().toJson(response!!.body()!!))

                        dialog.dismiss()
                    }

                    override fun onFailure(call: Call<WebSite>, t: Throwable) {
                        Toast.makeText(baseContext,"Failed",Toast.LENGTH_LONG)
                            .show()
                    }
                })
            }
        }else{
            swipe_to_refresh.isRefreshing = true
            //fetch new data
            newsService.sources.enqueue(object :retrofit2.Callback<WebSite>{
                override fun onResponse(call: Call<WebSite>, response: Response<WebSite>) {
                    adapter = ListSourceAdapter(baseContext,response!!.body()!!)
                    adapter.notifyDataSetChanged()
                    recycler_view_source_news.adapter = adapter

                    //save to cache
                    Paper.book().write("cache",Gson().toJson(response!!.body()!!))

                    swipe_to_refresh.isRefreshing = false
                }

                override fun onFailure(call: Call<WebSite>, t: Throwable) {
                    Toast.makeText(baseContext,"Failed",Toast.LENGTH_LONG)
                        .show()
                }
            })

        }
    }
}