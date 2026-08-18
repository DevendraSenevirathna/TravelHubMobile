package com.travelhub.mobileapp.data.api

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

fun uriToImagePart(context: Context, uri: Uri, partName: String = "image"): MultipartBody.Part? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return null
    val bytes = inputStream.use { it.readBytes() }

    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
    val extension = mimeType.substringAfter("/", "jpg")
    val fileName = "upload_${System.currentTimeMillis()}.$extension"

    val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, fileName, requestBody)
}