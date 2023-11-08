package com.putu.sora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.putu.sora.extra.UserPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val pref: UserPreferences): ViewModel() {

    val isLoading : LiveData<Boolean> = pref.loading


    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}