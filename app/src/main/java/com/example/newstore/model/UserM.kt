package com.example.newstore.model

import java.io.Serializable

data class UserM(

    var id: String ,
    var name: String = "",
    var avatar: String = "",
    var email: String = "",
    var password: String = ""
) : Serializable
