package com.putu.sora.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.putu.sora.extra.DataDummy
import com.putu.sora.extra.MainDispatcherRule
import com.putu.sora.adapter.StoryAdapter
import com.putu.sora.data.database.StoryEntity
import com.putu.sora.data.model.UserModel
import com.putu.sora.data.repository.Repository
import com.putu.sora.extra.UserPreferences
import com.putu.sora.extra.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
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
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var repository: Repository

    private lateinit var homeViewModel: HomeViewModel
    private val token = "token"

    @Before
    fun setup() {
        homeViewModel = HomeViewModel(userPreferences, repository)
    }

    @Test
    fun `when Get Story Paging Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryEntity()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data

        `when`(repository.getAllStories(token)).thenReturn(expectedStory)

        val actualStory: PagingData<StoryEntity> = homeViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer (
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Paging Data Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data

        `when`(repository.getAllStories(token)).thenReturn(expectedStory)

        val actualStory: PagingData<StoryEntity> = homeViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer (
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `when Get User Not Null and Return Success`() = runTest {
        val dummyUser = DataDummy.generateDummyUserData()
        val expectedUser = MutableLiveData<UserModel>()
        expectedUser.value = dummyUser

        `when`(userPreferences.getUser()).thenReturn(expectedUser.asFlow())

        val actualUser = homeViewModel.getUser().getOrAwaitValue()
        Mockito.verify(userPreferences).getUser()
        assertNotNull(actualUser)
        assertEquals(dummyUser, actualUser)
    }
}

class StoryPagingSource: PagingSource<Int, LiveData<List<StoryEntity>>>() {
    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
}