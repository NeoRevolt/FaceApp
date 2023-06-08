package com.dartmedia.faceappsdk

import android.util.Log
import com.dartmedia.faceappsdk.remote.ApiConfig
import com.dartmedia.faceappsdk.remote.Status
import com.dartmedia.faceappsdk.remote.response.VerifyResponseModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private var isValid: Status = Status.DEFAULT

class FaceVerification {

    fun verify(image1: File, image2: File): Status {
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
        service.enqueue(object : Callback<VerifyResponseModel> {
            override fun onResponse(
                call: Call<VerifyResponseModel>,
                response: Response<VerifyResponseModel>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val distance = responseBody?.result?.distance as Double
                    if (distance < 0.3) {
                        isValid = Status.VALID
                        Log.d("FACE SDK", responseBody.result.toString())
                    } else {
                        isValid = Status.INVALID
                        Log.d("FACE SDK", "BEDA")
                    }
                } else {
                    isValid = Status.INVALID
                    Log.d("FACE SDK", "INVALID")
                }
            }
            override fun onFailure(call: Call<VerifyResponseModel>, t: Throwable) {
                isValid = Status.FAILED
                Log.d("FACE SDK", "$t")
            }
        })
        return isValid
    }


    // Warning: Synchronous (handled with try-catch)
    fun verifySync(image1: File, image2: File): Status {
        isValid = Status.LOADING
        Log.d("SDK SYNCHRONOUS", "$isValid" )
        val requestImageFile1 = image1.asRequestBody("image/*".toMediaTypeOrNull())
        val requestImageFile2 = image2.asRequestBody("image/*".toMediaTypeOrNull())
        val imageMultipart1: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file1",
            image1.name,
            requestImageFile1
        )
        val imageMultipart2: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file2",
            image2.name,
            requestImageFile2
        )
        val service = ApiConfig.getApiService().verifyImage(imageMultipart1, imageMultipart2)
        try {
            val response = service.execute()
            isValid = Status.LOADING
            if (response.isSuccessful) {
                Log.d("SDK", "Synchronous Call ")
                val responseBody = response.body()
                val verified = responseBody?.result?.verified
                if (verified == true) {
                    isValid = Status.VALID
                } else {
                    isValid = Status.INVALID
                }
                Log.d("SDK", "Distance : ${responseBody?.result?.distance}")

            } else {
                isValid = Status.INVALID
            }
        } catch (e: Exception) {
            Log.d("SDK", "$e")
            isValid = Status.FAILED
        }
        Log.d("SDK SYNCHRONOUS", "$isValid" )
        return isValid
    }
}