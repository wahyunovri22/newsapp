package com.semicolon.newsapp.model

import com.google.gson.annotations.SerializedName

data class ActionModel(

	@field:SerializedName("pesan")
	val pesan: String? = null,

	@field:SerializedName("kode")
	val kode: Int? = null
)
