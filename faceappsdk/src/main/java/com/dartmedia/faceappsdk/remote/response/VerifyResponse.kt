package com.dartmedia.faceappsdk.remote.response

import com.google.gson.annotations.SerializedName

data class VerifyResponse(

    @field:SerializedName("result")
    val result: String,

    @field:SerializedName("status")
    val status: Int
)
