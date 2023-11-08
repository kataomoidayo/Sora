package com.putu.sora.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.putu.sora.data.database.StoryDatabase
import com.putu.sora.data.database.StoryEntity
import com.putu.sora.data.paging.StoryRemoteMediator
import com.putu.sora.data.response.LoginResponse
import com.putu.sora.data.response.RegisterResponse
import com.putu.sora.data.response.UploadResponse
import com.putu.sora.data.retrofit.ApiService
import com.putu.sora.extra.ResultResponse
import com.putu.sora.extra.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository (private val storyDatabase: StoryDatabase, private val apiService: ApiService) {

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading


    private suspend fun saveAuthToken(token: String, pref: UserPreferences?) {
        pref?.saveAuthToken(token)
    }

    fun register(name: String, email: String, password: String): LiveData<ResultResponse<RegisterResponse>> = liveData {
        _loading.value = true
        try {
            val response = apiService.register(name, email, password)
            emit(ResultResponse.Success(response))

        } catch (exception: Exception) {
            Log.d("Repository", "register: ${exception.message.toString()}")
            emit(ResultResponse.Error(exception.message.toString()))

        } finally {
            _loading.value = false
        }
    }

    fun login(email: String, password: String, pref: UserPreferences? = null): LiveData<ResultResponse<LoginResponse>> = liveData {
        _loading.value = true
        try {
            val response = apiService.login(email, password)
            saveAuthToken(response.loginResult.token, pref)
            emit(ResultResponse.Success(response))

        } catch (exception: Exception) {
            Log.d("Repository", "login: ${exception.message.toString()}")
            emit(ResultResponse.Error(exception.message.toString()))

        } finally {
            _loading.value = false
        }
    }

    fun getAllStories(token: String): LiveData<PagingData<StoryEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),

            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun postStory(token: String, file: MultipartBody.Part, description: RequestBody, lat: Double?, lon: Double?): LiveData<ResultResponse<UploadResponse>> = liveData {
        _loading.value = true
        try {
            val responseData = apiService.postStory("Bearer $token", file, description, lat, lon)
            emit(ResultResponse.Success(responseData))

        } catch (exception: Exception) {
            Log.d("Repository", "postStory: ${exception.message.toString()}")
            emit(ResultResponse.Error(exception.message.toString()))

        } finally {
            _loading.value = false
        }
    }

    fun getStoryLocation(token: String): LiveData<ResultResponse<List<StoryEntity>>> = liveData {
        _loading.value = true
        try {
            val responseData = apiService.getAllStories("Bearer $token", page = 1, size = 100, location = 1 )
            val responseDataList = responseData.stories
            val storyList = responseDataList.map { story ->
                StoryEntity(story.id, story.name, story.description, story.photoUrl, story.createdAt, story.lat, story.lon)
            }

            emit(ResultResponse.Success(storyList))
            storyDatabase.storyDao().deleteAll()
            storyDatabase.storyDao().insertStory(storyList)

        } catch (exception: Exception) {
            Log.d("Repository", "getStoryLocation: ${exception.message.toString()}")
            emit(ResultResponse.Error(exception.message.toString()))

        } finally {
            _loading.value = false
        }
    }
}