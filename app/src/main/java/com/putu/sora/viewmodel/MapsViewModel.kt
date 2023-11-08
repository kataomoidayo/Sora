package com.putu.sora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.putu.sora.data.model.UserModel
import com.putu.sora.data.repository.Repository
import com.putu.sora.extra.UserPreferences

class MapsViewModel(private val pref: UserPreferences, private val repository: Repository): ViewModel() {

    val isLoading : LiveData<Boolean> = repository.loading


    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getStoryLocation(token: String) = repository.getStoryLocation(token)
}
