package com.putu.sora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.putu.sora.data.model.UserModel
import com.putu.sora.data.repository.Repository
import com.putu.sora.extra.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostStoryViewModel(private val pref: UserPreferences, private val repository: Repository) : ViewModel() {

    val isLoading : LiveData<Boolean> = repository.loading


    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun postStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?) = repository.postStory(token, file, description, lat, lon)
}