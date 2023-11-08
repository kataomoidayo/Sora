package com.putu.sora.data.response

import com.google.gson.annotations.SerializedName
import com.putu.sora.data.model.UserModel

data class LoginResponse (

    @field:SerializedName("error")
    var error: Boolean,

    @field:SerializedName("message")
    var message: String,

    @field:SerializedName("loginResult")
    var loginResult: UserModel

)
