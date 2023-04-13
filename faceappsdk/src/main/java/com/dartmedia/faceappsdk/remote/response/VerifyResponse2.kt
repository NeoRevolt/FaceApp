package com.dartmedia.faceappsdk.remote.response

import com.google.gson.annotations.SerializedName

data class VerifyResponse2(

	@field:SerializedName("result")
	val result: Result,

	@field:SerializedName("status")
	val status: Int
)

data class Img2(

	@field:SerializedName("w")
	val w: Int,

	@field:SerializedName("x")
	val x: Int,

	@field:SerializedName("h")
	val h: Int,

	@field:SerializedName("y")
	val y: Int
)

data class Result(

	@field:SerializedName("facial_areas")
	val facialAreas: FacialAreas,

	@field:SerializedName("distance")
	val distance: Any,

	@field:SerializedName("detector_backend")
	val detectorBackend: String,

	@field:SerializedName("similarity_metric")
	val similarityMetric: String,

	@field:SerializedName("verified")
	val verified: Boolean,

	@field:SerializedName("threshold")
	val threshold: Any,

	@field:SerializedName("model")
	val model: String,

	@field:SerializedName("time")
	val time: Any
)

data class FacialAreas(

	@field:SerializedName("img2")
	val img2: Img2,

	@field:SerializedName("img1")
	val img1: Img1
)

data class Img1(

	@field:SerializedName("w")
	val w: Int,

	@field:SerializedName("x")
	val x: Int,

	@field:SerializedName("h")
	val h: Int,

	@field:SerializedName("y")
	val y: Int
)
