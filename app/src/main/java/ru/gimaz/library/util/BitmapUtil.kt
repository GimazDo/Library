package ru.gimaz.library.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID


suspend fun loadBitmapAsByteArray(
    uri: Uri,
    contentResolver: ContentResolver
): ByteArray? {
    return withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null
        try {
            inputStream = contentResolver.openInputStream(uri)
            inputStream?.let { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                bitmap.recycle()
                return@withContext byteArray
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
        }
    }
}
suspend fun loadBitmapToFile(
    uri: Uri,
    context: Context,
): String? {
    return withContext(Dispatchers.IO) {
        val outputStream = ByteArrayOutputStream()
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val fileName = UUID.randomUUID().toString()
                File(context.filesDir, fileName).writeBytes(outputStream.toByteArray())
                bitmap.recycle()
                return@withContext context.filesDir.absolutePath + File.separator + fileName
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
            outputStream.close()
        }
    }
}



suspend fun execute(    uri: Uri,
                        context: Context,): String? {
   return withContext(Dispatchers.IO) {
        try {
            println("Start saving image")
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))

            // Create a file to save the image to
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val file = File(context.filesDir, fileName)
            println("File created ${file.absolutePath}")
            // Save the bitmap to the file
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            println("Image compressed")
            // Add the image to the MediaStore
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                return@withContext Environment.DIRECTORY_PICTURES + File.separator + fileName
            } else {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.TITLE, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.DATA, file.absolutePath)
                }
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                return@withContext file.absolutePath
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}