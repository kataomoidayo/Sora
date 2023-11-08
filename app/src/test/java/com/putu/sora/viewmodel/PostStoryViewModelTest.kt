package com.putu.sora.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.putu.sora.extra.DataDummy
import com.putu.sora.extra.MainDispatcherRule
import com.putu.sora.data.model.UserModel
import com.putu.sora.data.repository.Repository
import com.putu.sora.data.response.UploadResponse
import com.putu.sora.extra.ResultResponse
import com.putu.sora.extra.UserPreferences
import com.putu.sora.extra.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PostStoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var repository: Repository

    private lateinit var postStoryViewModel: PostStoryViewModel
    private val file = DataDummy.multipartFile()
    private val description = DataDummy.description()
    private val token = "token"
    private val lat = 35.4526124
    private val lon = 139.2651147

    @Before
    fun setUp() {
        postStoryViewModel = PostStoryViewModel(userPreferences, repository)
    }

    @Test
    fun `when User Posting a Story Should Not Null and Return Success`() {
        val dummyPost = DataDummy.generateDummyPostStorySuccess()
        val expectedPostStory = MutableLiveData<ResultResponse<UploadResponse>>()
        expectedPostStory.value = ResultResponse.Success(dummyPost)

        `when`(repository.postStory(token, file, description, lat, lon)).thenReturn(expectedPostStory)

        val actualPostStory = postStoryViewModel.postStory(token, file, description, lat, lon).getOrAwaitValue()
        Mockito.verify(repository).postStory(token, file, description, lat, lon)
        assertNotNull(actualPostStory)
        assertTrue(actualPostStory is ResultResponse.Success)
    }

    @Test
    fun `when User Posting a Story Should Null and Return Error`() {
        val dummyPost = DataDummy.generateDummyPostStoryError()
        val expectedPostStory = MutableLiveData<ResultResponse<UploadResponse>>()
        expectedPostStory.value = ResultResponse.Error(dummyPost.message)

        `when`(repository.postStory(token, file, description, lat, lon)).thenReturn(expectedPostStory)

        val actualPostStory = postStoryViewModel.postStory(token, file, description, lat, lon).getOrAwaitValue()
        Mockito.verify(repository).postStory(token, file, description, lat, lon)
        assertNotNull(actualPostStory)
        assertTrue(actualPostStory is ResultResponse.Error)
    }

    @Test
    fun `when Get User Not Null and Return Success`() = runTest {
        val dummyUser = DataDummy.generateDummyUserData()
        val expectedUser = MutableLiveData<UserModel>()
        expectedUser.value = dummyUser

        `when`(userPreferences.getUser()).thenReturn(expectedUser.asFlow())

        val actualUser = postStoryViewModel.getUser().getOrAwaitValue()
        Mockito.verify(userPreferences).getUser()
        assertNotNull(actualUser)
        assertEquals(dummyUser, actualUser)
    }
}