package com.medhelp.callmed2.utils.WorkTofFile.convert_Base64


import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64OutputStream
import android.webkit.MimeTypeMap
import com.medhelp.callmed2.utils.timber_log.LoggingTree.Companion.getMessageForError
import timber.log.Timber
import java.io.*
import java.util.*

class ProcessingFileB64AndImg {
    fun base64ToFile(context: Context, b64: String, uriFTo: Uri ) : Boolean {
        try{
            val decodedString = Base64.decode(b64, Base64.DEFAULT)
            context.contentResolver.openOutputStream(uriFTo).use { out ->
                if (out == null)
                    return false

                out.write(decodedString)
            }
        } catch (e: FileNotFoundException) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$base64ToFile1 " + e.message))
        } catch (e: IOException) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$base64ToFile2 " + e.message))
        }catch (e: Exception) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$base64ToFile3 " + e.message))
        }

        return true
    }

//    private fun getFile(context: Context): String? {
//        //File pathToDownloadFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        val pathToDownloadFolder = context.cacheDir
//        val pathToFolderApp = File(pathToDownloadFolder, LoadFile.NAME_FOLDER_APP)
//        if (!pathToFolderApp.exists()) {
//            pathToFolderApp.mkdirs()
//        }
//        val pathToFolder = File(pathToFolderApp, LoadFile.NAME_FOLDER_CHAT)
//        if (!pathToFolder.exists()) {
//            pathToFolder.mkdirs()
//        }
//        val file =
//            File(pathToFolder, LoadFile.NAME_FOLDER_CHAT + System.currentTimeMillis() + ".JPEG")
//        try {
//            file.createNewFile()
//        } catch (e: IOException) {
//            Timber.tag("my").e(getMessageForError(null, "ConvertBase64\$getFile " + e.message))
//        }
//        return if (file.exists()) file.absolutePath else null
//    }

    suspend fun fileToBase64String(context:Context, fileUri: Uri): String {

//        if(img.length() > 1024*1024*9)
//            return Single.error(new Throwable("Ограничение на размер изображения 9 мегабайт"));

        var inputStream: InputStream? = null //You can get an inputStream using any IO API
        try {
            //inputStream = FileInputStream(fileUri)
            inputStream = context.contentResolver.openInputStream(fileUri)
        } catch (e: FileNotFoundException) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$fileToBase64$1 " + e.message))
        }
        val buffer = ByteArray(1024)
        var bytesRead: Int
        val output : ByteArrayOutputStream = ByteArrayOutputStream()
        val output64 = Base64OutputStream(output, Base64.DEFAULT)
        try {
            while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                output64.write(buffer, 0, bytesRead)
            }
        } catch (e: IOException) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$fileToBase64$2 " + e.message))
        }
        try {
            output64.close()
            inputStream?.close()
        } catch (e: IOException) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$fileToBase64$3 " + e.message))
        }
        val attachedFile = output.toString()

        return attachedFile
    }
    fun fileToBase64ByteArray(context:Context, fileUri: Uri): ByteArray {

        var inputStream: InputStream? = null //You can get an inputStream using any IO API
        try {
            //inputStream = FileInputStream(fileUri)
            inputStream = context.contentResolver.openInputStream(fileUri)
        } catch (e: FileNotFoundException) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$fileToBase64$1 " + e.message))
        }
        val buffer = ByteArray(1024)
        var bytesRead: Int
        val output : ByteArrayOutputStream = ByteArrayOutputStream()
        val output64 = Base64OutputStream(output, Base64.DEFAULT)
        try {
            while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                output64.write(buffer, 0, bytesRead)
            }
        } catch (e: IOException) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$fileToBase64$2 " + e.message))
        }
        try {
            output64.close()
            inputStream?.close()
        } catch (e: IOException) {
            Timber.tag("my")
                .e(getMessageForError(null, "ConvertBase64\$fileToBase64$3 " + e.message))
        }
        val attachedFile = output.toByteArray()

        return attachedFile
    }

    fun compressImage(context:Context, uriF: Uri) {
        var bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uriF))
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uriF)
        }

        val sizeList = calculationOfProportionsBitmap(
            bitmap!!.width,
            bitmap!!.height,
            1080,
            1920
        )

        sizeList?.let {
            bitmap = Bitmap.createScaledBitmap(bitmap, it.get(0), it.get(1), false)
            context.getContentResolver().openOutputStream(uriF)
                .use { outputStream ->
                    bitmap!!.compress(Bitmap.CompressFormat.PNG, 30, outputStream!!)
                    outputStream?.flush()
                }
        }
    }

    fun calculationOfProportionsBitmap(wBitmap: Int, hBitmap: Int, wView: Int, hView: Int): List<Int>? {
        var newWith: Int
        var newHeight: Int
        if (wBitmap > hBitmap) {
            newWith = wView
            newHeight = newWith * hBitmap / wBitmap
        } else {
            newHeight = hView
            newWith = newHeight * wBitmap / hBitmap
        }
        if (newWith <= wView && newHeight <= hView) return Arrays.asList(newWith, newHeight)
        if (wBitmap <= hBitmap) {
            newWith = wView
            newHeight = newWith * hBitmap / wBitmap
        } else {
            newHeight = hView
            newWith = newHeight * wBitmap / hBitmap
        }
        return if (newWith <= wView && newHeight <= hView) Arrays.asList(
            newWith,
            newHeight
        ) else null
    }

    fun copyFileByUri(context: Context, pathFrom: Uri, pathTo: Uri) {
        context.contentResolver.openInputStream(pathFrom).use { inputStream: InputStream? ->
            if (pathTo == null || inputStream == null) return
            context.contentResolver.openOutputStream(pathTo).use { out ->
                if (out == null) return
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            }
        }
    }

    fun getExtensionByUri(context: Context, uri: Uri): String? {
        val extension: String?

        //Check uri format to avoid null
        extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

}