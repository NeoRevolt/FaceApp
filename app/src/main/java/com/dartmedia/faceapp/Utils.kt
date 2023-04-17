package com.dartmedia.faceapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.ImageProxy
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer

object Utils {
    fun tempFileImage(context: Context, bitmap: Bitmap, name: String): String? {
        val outputDir = context.cacheDir
        val imageFile = File(outputDir, "$name.jpg")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG,15, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(context.javaClass.simpleName, "Error Save Photo", e)
        }
        return imageFile.absolutePath
    }

    fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}