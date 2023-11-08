package com.putu.sora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.putu.sora.data.database.StoryEntity
import com.putu.sora.data.model.UserModel
import com.putu.sora.data.repository.Repository
import com.putu.sora.extra.UserPreferences

class HomeViewModel(private val pref: UserPreferences, private val repository: Repository): ViewModel() {

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getAllStories(token: String): LiveData<PagingData<StoryEntity>> {
        return repository.getAllStories(token).cachedIn(viewModelScope)
    }
}
