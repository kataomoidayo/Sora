package com.putu.sora.data.response

import com.google.gson.annotations.SerializedName
import com.putu.sora.data.model.StoryModel

data class StoryResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val stories: List<StoryModel>

)
