package com.intelab.joblab.presentation.ui.helpers

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getMonthsAsStringList
import com.intelab.joblab.presentation.base.utils._appPdfType
import com.intelab.joblab.presentation.base.utils._fileNameType
import com.intelab.joblab.presentation.base.utils._fileType
import com.intelab.joblab.presentation.base.utils._imageJpgType
import com.intelab.joblab.presentation.base.utils._imageLower
import com.intelab.joblab.presentation.base.utils._patternDecimal
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.DecimalFormat

fun generateDaysForYearAndMonth(
    selectYear: Int,
    selectMonth: Int,
    actualMonth: Int,
    actualDay: Int,
    actualYear: Int
): List<String> {
    var days = mutableListOf<String>()
    val format = DecimalFormat(_patternDecimal)

    if (selectMonth == actualMonth && selectYear == actualYear) {
        days = MutableList(actualDay) {
            format.format(it + 1)
        }
        return days
    }

    when (selectMonth) {
        1, 3, 5, 7, 8, 10, 12 -> {
            days = MutableList(31) {
                format.format(it + 1)
            }
        }

        4, 6, 9, 11 -> {
            days = MutableList(30) { format.format(it + 1) }
        }

        2 -> {
            days = if (selectYear % 4 == 0 && selectYear % 100 != 0) {
                MutableList(29) { format.format(it + 1) }
            } else {
                MutableList(28) { format.format(it + 1) }
            }
        }
    }
    return days
}

fun getMonthsAfterNumber(n: Int): Array<String> {
    val sMonths = mutableListOf<String>()
    getMonthsAsStringList().forEachIndexed { i, v ->
        if (i >= n) {
            sMonths.add(v)
        }
    }
    return sMonths.toTypedArray()
}

fun getMonthsBeforeNumber(n: Int): Array<String> {
    val sMonths = mutableListOf<String>()
    getMonthsAsStringList().forEachIndexed { i, v ->
        if (i <= n) {
            sMonths.add(v)
        }
    }
    return sMonths.toTypedArray()
}

fun getMonthsInRange(init: Int, end: Int): Array<String> {
    val sMonths = mutableListOf<String>()
    getMonthsAsStringList().forEachIndexed { i, v ->
        if (i in init..end) {
            sMonths.add(v)
        }
    }
    return sMonths.toTypedArray()
}

fun toMultiPartFile(fileName: String, byteArray: ByteArray): MultipartBody.Part {
    val reqFile = byteArray.toRequestBody(_imageJpgType.toMediaTypeOrNull(), 0, byteArray.size)
    return MultipartBody.Part.createFormData(_fileType, fileName, reqFile)
}

fun toPDFMultiPartFile(fileName: String, byteArray: ByteArray): MultipartBody.Part {
    val reqFile = byteArray.toRequestBody(_appPdfType.toMediaTypeOrNull(), 0, byteArray.size)
    return MultipartBody.Part.createFormData(_fileType, fileName, reqFile)
}

@RequiresApi(Build.VERSION_CODES.Q)
fun saveImageInQ(context: Context, bitmap: Bitmap): Uri? {
    val filename = "IMG_${System.currentTimeMillis()}.jpg"
    var fos: OutputStream?
    var imageUri: Uri?
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, _imageJpgType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        put(MediaStore.Video.Media.IS_PENDING, 1)
    }

    val contentResolver = context.contentResolver

    contentResolver.also { resolver ->
        imageUri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = imageUri?.let { resolver?.openOutputStream(it) }
    }

    fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }

    contentValues.clear()
    contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
    imageUri?.let {
        contentResolver?.update(it, contentValues, null, null)
    }
    return imageUri
}

fun saveTheImageLegacyStyle(context: Context, bitmap: Bitmap): Uri {
    val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File(imagesDir, _fileNameType)
    val fos = FileOutputStream(image)
    fos.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }
    return Uri.fromFile(image)
}

fun saveBitmapOnDevice(context: Context, bitmap: Bitmap): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        saveImageInQ(context, bitmap)
    else saveTheImageLegacyStyle(context, bitmap)
}

fun createPartFromString(descriptionString: String): RequestBody {
    return descriptionString
        .toRequestBody(MultipartBody.FORM)
}

fun flipBitmap(src: Bitmap): Bitmap {
    val matrix = Matrix()
    matrix.preScale(-1.0f, 1.0f)
    return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
}

fun imageToBitmap(image: Image): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
}

fun convertBitmapToFile(context: Context, bitmap: Bitmap): File {
    val dirPath = context.getExternalFilesDir(null)!!.absolutePath
    val filePath = "$dirPath/$_imageLower"
    val file = File(filePath)
    val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
    os.close()
    return file
}

fun getDeviceId(context: Context?): String {
    return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
}