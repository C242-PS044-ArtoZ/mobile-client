package com.c242_ps044.artoz.data.remote

import okhttp3.MultipartBody

class OcrRepository(private val ocrService: OcrService) {

  fun uploadImage(image: MultipartBody.Part) = ocrService.uploadImage(image)
}
