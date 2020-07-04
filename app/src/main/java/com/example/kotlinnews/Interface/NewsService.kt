package com.example.kotlinnews.Interface

import com.example.kotlinnews.Model.News
import com.example.kotlinnews.Model.WebSite
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface NewsService {

    @get:GET("v2/sources?apiKey=d8a943665b0c4c86a6cf0075e7cf1569")
    val sources: Call<WebSite>

    @GET
    fun getNewsFromSource(@Url url:String):Call<News>

}