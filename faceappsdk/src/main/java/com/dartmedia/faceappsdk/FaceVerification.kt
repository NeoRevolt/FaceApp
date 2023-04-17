package com.dartmedia.faceappsdk

import android.util.Log
import com.dartmedia.faceappsdk.remote.ApiConfig
import com.dartmedia.faceappsdk.remote.response.VerifyResponse
import com.dartmedia.faceappsdk.remote.Status
import com.dartmedia.faceappsdk.remote.response.VerifyResponse2
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private var isValid: Status = Status.DEFAULT

class FaceVerification {

    fun verify(image1: File, image2: File) : Status {
        isValid = Status.LOADING
        val requestImageFile1 = image1.asRequestBody("image/*".toMediaTypeOrNull())
        val requestImageFile2 = image2.asRequestBody("image/*".toMediaTypeOrNull())

        val imageMultipart1: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file1",
            image1.name,
            requestImageFile1
        )
        val imageMultipart2: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file2",
            image1.name,
            requestImageFile2
        )

        val service = ApiConfig.getApiService().verifyImage(imageMultipart1, imageMultipart2)

        service.enqueue(object : Callback<VerifyResponse2> {
            override fun onResponse(
                call: Call<VerifyResponse2>,
                response: Response<VerifyResponse2>
            ) {
                if (response.isSuccessful){
                    val responseBody = response.body()
                    val distance = responseBody?.result?.distance as Double
                    if (distance < 0.3){
                        isValid = Status.VALID
                        Log.d("FACE SDK", responseBody.result.toString())
                    }
                    else {
                        isValid = Status.INVALID
                        Log.d("FACE SDK", "BEDA")
                    }
                }
                else {
                    isValid = Status.INVALID
                    Log.d("FACE SDK", "INVALID")

                }
            }

            override fun onFailure(call: Call<VerifyResponse2>, t: Throwable) {
                isValid = Status.FAILED
                Log.d("FACE SDK", "$t")
            }
        })
        return isValid
    }
}