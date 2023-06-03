package com.semicolon.newsapp.model

data class LoginModel(
    var error:Boolean ? = null,
    var message:String ? = null,
    var user:User ? = null
)

data class User(
    var nama:String? = null,
    var alamat:String? = null,
    var role:String? = null,
)