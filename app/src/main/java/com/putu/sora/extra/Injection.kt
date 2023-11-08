package com.putu.sora.extra

import android.content.Context
import com.putu.sora.data.database.StoryDatabase
import com.putu.sora.data.repository.Repository
import com.putu.sora.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return Repository(database, apiService)
    }
}