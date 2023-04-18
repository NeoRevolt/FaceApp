package com.dartmedia.faceappsdk.remote

import com.dartmedia.faceappsdk.remote.response.VerifyResponseModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FaceVerificationService {

    @Multipart
    @POST("verify")
    fun verifyImage(
        @Part file1: MultipartBody.Part,
        @Part file2: MultipartBody.Part
    ): Call<VerifyResponseModel>
}