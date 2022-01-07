package com.seulgi.whitealarm.hospital
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPI {
    @GET("v2/local/search/keyword.json")
    fun getSearchKeyword(
            @Header("Authorization") key: String, // 인증키
            @Query("y") y: String,
            @Query("x") x: String,
            @Query("query") query: String, // 질의어
    ) : Call<ResultSearchKeyword>
}