package com.semicolon.newsapp.network

import com.semicolon.newsapp.helper.Config
import com.semicolon.newsapp.model.LoginModel
import com.semicolon.newsapp.model.ResponseNews
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {

    @FormUrlEncoded
    @POST(Config.LOGIN)
    fun login(
        @Field("username") u:String,
        @Field("password") p:String,
    ):Call<LoginModel>

    @GET(Config.NEWS)
    fun news():Call<ResponseNews>
}