package com.dartmedia.faceapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.dartmedia.faceapp.databinding.ActivityMainBinding
import com.dartmedia.faceappsdk.FaceVerification
import com.dartmedia.faceappsdk.remote.Status
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.net.URI

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    companion object {
        private const val TAG = "MAIN ACTIVITY"

        private const val RC_STORAGE = 901
        private const val RC_CAMERA = 902
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var faceVerification: FaceVerification

    private var imgFile1: File? = null

    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val uriImage = result.data?.data

            binding.image.setImageURI(uriImage)

            if (uriImage != null) {
                //Compress Image
                val bitmapImage = BitmapFactory.decodeFile(getRealPathFromUri(uriImage))
                val newFile = Utils.tempFileImage(this, bitmapImage, System.currentTimeMillis().toString())
                imgFile1 = File(newFile!!)
            }
        }
    }

    private val galleryResultLauncher2 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val uriImage = result.data?.data

            binding.image2.setImageURI(uriImage)

            if (uriImage != null) {
                //Compress Image
                val bitmapImage = BitmapFactory.decodeFile(getRealPathFromUri(uriImage))
                val newFile = Utils.tempFileImage(this, bitmapImage, System.currentTimeMillis().toString())
                val imgFile2 = File(newFile!!)
                when(faceVerification.verifySync(imgFile1!!, imgFile2)){
                    Status.VALID -> {
                        Toast.makeText(this@MainActivity, "Valid", Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        Toast.makeText(this@MainActivity, "Loading...", Toast.LENGTH_SHORT).show()
                    }
                    Status.FAILED -> {
                        Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }
                    Status.INVALID -> {
                        Toast.makeText(this@MainActivity, "Invalid", Toast.LENGTH_SHORT).show()
                    }
                }

                Log.d("SIZE 1", imgFile1!!.length().toString())
                Log.d("SIZE 2", imgFile2.length().toString())
            }
        }
    }

    private val cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo berhasil!", Toast.LENGTH_SHORT).show()
            val file = File(result.data!!.getStringExtra("path"))
            binding.image2.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        faceVerification = FaceVerification()
        selectImage()
        openCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this@MainActivity)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            RC_STORAGE -> intentToGalleryImage()
            RC_CAMERA -> intentToCameraActivity()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun selectImage() {
        binding.image.setOnClickListener {
            if (EasyPermissions.hasPermissions(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                intentToGalleryImage()
            } else {
                EasyPermissions.requestPermissions(this@MainActivity, "Izinkan aplikasi mengakses penyimpanan?", RC_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openCamera() {
        binding.image2.setOnClickListener {
            val permissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                arrayOf(Manifest.permission.CAMERA)
            }

            if (EasyPermissions.hasPermissions(this@MainActivity, *permissions)) {
                intentToCameraActivity()
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                //galleryResultLauncher2.launch(intent)
            } else {
                EasyPermissions.requestPermissions(this@MainActivity, "Izinkan aplikasi mengakses camera?", RC_CAMERA, *permissions)
            }
        }
    }

    private fun intentToGalleryImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.type = "image/*"
        galleryResultLauncher.launch(intent)
    }

    private fun intentToCameraActivity() {
        if (imgFile1 == null) {
            Snackbar.make(binding.root, "First image can't be empty!", Snackbar.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this@MainActivity, CameraActivity::class.java)
        intent.putExtra("image1",imgFile1!!.absolutePath)
        cameraResultLauncher.launch(intent)
    }

    private fun getRealPathFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)

        return if (cursor == null) {
            uri.path!!
        } else {
            cursor.moveToFirst()
            val id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(id)
        }
    }
}