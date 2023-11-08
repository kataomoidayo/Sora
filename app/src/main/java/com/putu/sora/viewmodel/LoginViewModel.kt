package com.putu.sora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.putu.sora.data.repository.Repository
import com.putu.sora.extra.UserPreferences

class LoginViewModel(private val pref: UserPreferences, private val repository: Repository) : ViewModel() {

    val isLoading : LiveData<Boolean> = repository.loading


    fun login(email: String, password: String) = repository.login(email, password, pref)
}
