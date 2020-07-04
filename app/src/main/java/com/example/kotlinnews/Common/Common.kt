package com.example.kotlinnews.Common

import com.example.kotlinnews.Interface.NewsService
import com.example.kotlinnews.Remote.RetrofitClient
import java.lang.StringBuilder

object Common{
    val BASE_URl ="https://newsapi.org/"
    val API_KEY ="d8a943665b0c4c86a6cf0075e7cf1569"

    val newsService:NewsService
            get()=RetrofitClient.getClient(BASE_URl).create(NewsService::class.java)

    fun getNewsAPI(source:String):String{
        val apiUrl = StringBuilder("https://newsapi.org/v2/top-headlines?sources=")
            .append(source)
            .append("&apiKey=")
            .append(API_KEY)
            .toString()

        return apiUrl
    }
}