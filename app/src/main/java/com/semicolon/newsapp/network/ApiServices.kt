package com.semicolon.newsapp.network

import com.semicolon.newsapp.helper.Config
import com.semicolon.newsapp.model.ActionModel
import com.semicolon.newsapp.model.LoginModel
import com.semicolon.newsapp.model.ResponseNews
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @FormUrlEncoded
    @POST(Config.DELETENEWS)
    fun deleteNews(
        @Field("id") id:String
    ):Call<ActionModel>

    @Multipart
    @POST(Config.ADDNEWS)
    fun addNews(
        @Part("judul") judul:RequestBody,
        @Part("deskripsi") deskripsi:RequestBody,
        @Part("userinput") user:RequestBody,
        @Part photo: MultipartBody.Part
    ):Call<ActionModel>
}