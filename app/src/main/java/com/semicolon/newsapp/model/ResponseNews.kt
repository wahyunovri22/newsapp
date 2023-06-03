package com.semicolon.newsapp.model

import com.google.gson.annotations.SerializedName

data class ResponseNews(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("news")
	val news: ArrayList<NewsItem>? = null
)

data class NewsItem(

	@field:SerializedName("cover")
	val cover: String? = null,

	@field:SerializedName("userinput")
	val userinput: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("judul")
	val judul: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null
)
