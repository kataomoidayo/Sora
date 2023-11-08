package com.putu.sora.extra

import com.putu.sora.data.database.StoryEntity
import com.putu.sora.data.model.UserModel
import com.putu.sora.data.response.LoginResponse
import com.putu.sora.data.response.RegisterResponse
import com.putu.sora.data.response.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {

    fun generateDummyRegisterSuccess(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "register success"
        )
    }

    fun generateDummyRegisterError(): RegisterResponse {
        return RegisterResponse(
            error = true,
            message = "register error"
        )
    }

    fun generateDummyLoginSuccess(): LoginResponse {
        return LoginResponse(
            error = false,
            message = "login success",
            loginResult = UserModel(
                userId = "userId",
                name = "name",
                token = "token",
                isLogin = true
            )
        )
    }

    fun generateDummyLoginError(): LoginResponse {
        return LoginResponse(
            error = true,
            message = "login error",
            loginResult = UserModel(
                userId = "userId",
                name = "name",
                token = "token",
                isLogin = false
            )
        )
    }

    fun generateDummyUserData(): UserModel {
        return UserModel(
            userId = "userId",
            name = "name",
            token = "token",
            isLogin = true
        )
    }

    fun generateDummyPostStorySuccess(): UploadResponse {
        return UploadResponse(
            error = false,
            message = "post story success"
        )
    }

    fun generateDummyPostStoryError(): UploadResponse {
        return UploadResponse(
            error = true,
            message = "post story error"
        )
    }

    fun generateDummyStoryEntity(): List<StoryEntity> {
        val storyList: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..10) {
            val story = StoryEntity (
                "$i",
                "片思い",
                "生麦生米生卵",
                "https://avatars.githubusercontent.com/u/114056087?v=4",
                "2023-05-07T15:31:00Z",
                35.4526124,
                139.2651147
            )
            storyList.add(story)
        }
        return storyList
    }

    fun multipartFile() = MultipartBody.Part.create("dummyFile".toRequestBody())

    fun description() = "description".toRequestBody("text/plain".toMediaTypeOrNull())
}