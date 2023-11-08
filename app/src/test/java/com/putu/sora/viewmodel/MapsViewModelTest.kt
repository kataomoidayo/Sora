package com.putu.sora.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.putu.sora.data.database.StoryEntity
import com.putu.sora.data.model.UserModel
import com.putu.sora.data.repository.Repository
import com.putu.sora.extra.*
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
class MapsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val  mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var repository: Repository

    private lateinit var mapsViewModel: MapsViewModel
    private val token = "token"

    @Before
    fun setUp() {
        mapsViewModel = MapsViewModel(userPreferences, repository)
    }

    @Test
    fun `when Get Story Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val expectedStory = MutableLiveData<ResultResponse<List<StoryEntity>>>()
        expectedStory.value = ResultResponse.Success(dummyStory)

        `when`(repository.getStoryLocation(token)).thenReturn(expectedStory)

        val actualStory = mapsViewModel.getStoryLocation(token).getOrAwaitValue()
        Mockito.verify(repository).getStoryLocation(token)
        assertNotNull(actualStory)
        assertTrue(actualStory is ResultResponse.Success)
    }

    @Test
    fun `when Get Story Should Null and Return Error`() = runTest {
        val expectedStory = MutableLiveData<ResultResponse<List<StoryEntity>>>()
        expectedStory.value = ResultResponse.Error("get story error")

        `when`(repository.getStoryLocation(token)).thenReturn(expectedStory)

        val actualStory = mapsViewModel.getStoryLocation(token).getOrAwaitValue()
        Mockito.verify(repository).getStoryLocation(token)
        assertNotNull(actualStory)
        assertTrue(actualStory is ResultResponse.Error)
    }

    @Test
    fun `when Get User Not Null and Return Success`() = runTest {
        val dummyUser = DataDummy.generateDummyUserData()
        val expectedUser = MutableLiveData<UserModel>()
        expectedUser.value = dummyUser

        `when`(userPreferences.getUser()).thenReturn(expectedUser.asFlow())

        val actualUser = mapsViewModel.getUser().getOrAwaitValue()
        Mockito.verify(userPreferences).getUser()
        assertNotNull(actualUser)
        assertEquals(dummyUser, actualUser)
    }
}