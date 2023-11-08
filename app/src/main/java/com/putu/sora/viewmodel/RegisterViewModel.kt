package com.putu.sora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.putu.sora.data.repository.Repository

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    val isLoading : LiveData<Boolean> = repository.loading


    fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}