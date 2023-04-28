package com.dartmedia.faceapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.dartmedia.faceapp.databinding.ActivityCameraBinding
import com.dartmedia.faceappsdk.FaceVerification
import com.dartmedia.faceappsdk.remote.Status
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "CAMERA ACTIVITY"
    }

    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var faceVerification: FaceVerification

    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        cameraExecutor = Executors.newSingleThreadExecutor()
        faceVerification = FaceVerification()

        startCamera()
        binding.imgCaptureBtn.setOnClickListener { capturePhoto() }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Select front camera
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            imageCapture = ImageCapture.Builder().build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this@CameraActivity))

//        getOrientationCamera()
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                //convert img proxy to bitmap
                val filename = System.currentTimeMillis().toString()
                val imgBitmap = Utils.imageProxyToBitmap(image)

                //rotate image from mirror to normal
                val matrix = Matrix()
                matrix.postScale(-1f, 1f)
                val rotatedImgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.width, imgBitmap.height, matrix, true)
//                if(BuildConfig.VERSION_CODE <)
                //save normal image bitmap to file
                val filePath = File(Utils.tempFileImage(this@CameraActivity, rotatedImgBitmap, filename))
                val filePath2 = File(intent.getStringExtra("image1"))

                    //Panggil API
                when(faceVerification.verifySync(filePath, filePath2)){
                    Status.VALID -> {
//                        Toast.makeText(this@CameraActivity, "Valid", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        intent.putExtra("path", filePath.absolutePath)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    Status.LOADING -> {
//                        Toast.makeText(this@CameraActivity, "Loading...", Toast.LENGTH_SHORT).show()
                        Log.d("CAMERA ACTIVITY","LOADING....")
                    }
                    Status.FAILED -> {
                        startCamera()
                    }
                    Status.INVALID -> {
                        startCamera()
                    }
                    else -> {
//                        Toast.makeText(this@CameraActivity, "Loading...", Toast.LENGTH_SHORT).show()
                        Log.d("CAMERA ACTIVITY","LOADING....")
                    }
                }

//                val intent = Intent()
//                intent.putExtra("path", filePath.absolutePath)
//                setResult(Activity.RESULT_OK, intent)
//                finish()
                    //If condition result true will intent to the main activity

                    //If condition result false will retake photo



                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)

                Toast.makeText(this@CameraActivity, "Something went wrong, try again!", Toast.LENGTH_SHORT).show()
                recreate()
            }
        })
    }

    private fun getOrientationCamera() {
        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation : Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation : Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture?.targetRotation = rotation
            }
        }

        orientationEventListener.enable()
    }
}