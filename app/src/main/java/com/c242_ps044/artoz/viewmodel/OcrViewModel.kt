package com.c242_ps044.artoz.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c242_ps044.artoz.data.remote.OcrRepository
import com.c242_ps044.artoz.data.remote.OcrResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OcrViewModel(private val repository: OcrRepository) : ViewModel() {

  private val _ocrResult = MutableLiveData<OcrResponse?>()
  val ocrResult: LiveData<OcrResponse?> get() = _ocrResult

  private val _errorMessage = MutableLiveData<String?>()
  val errorMessage: LiveData<String?> get() = _errorMessage

  fun uploadImage(image: MultipartBody.Part) {
    repository.uploadImage(image).enqueue(object : Callback<OcrResponse> {
      override fun onResponse(call: Call<OcrResponse>, response: Response<OcrResponse>) {
        if (response.isSuccessful) {
          _ocrResult.postValue(response.body())
        } else {
          _errorMessage.postValue("Response Error: ${response.errorBody()?.string()}")
        }
      }

      override fun onFailure(call: Call<OcrResponse>, t: Throwable) {
        _errorMessage.postValue("Request Failed: ${t.message}")
      }
    })
  }
}
