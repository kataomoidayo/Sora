package com.putu.sora.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.putu.sora.extra.DataDummy
import com.putu.sora.extra.MainDispatcherRule
import com.putu.sora.data.repository.Repository
import com.putu.sora.data.response.RegisterResponse
import com.putu.sora.extra.ResultResponse
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
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: Repository

    private lateinit var registerViewModel: RegisterViewModel
    private val name = "name"
    private val email = "email@email.com"
    private val password = "qwerty"

    @Before
    fun setUp() {
        registerViewModel = RegisterViewModel(repository)
    }

    @Test
    fun `when Register Should Not Null and Return Success`() = runTest {
        val dummyRegister = DataDummy.generateDummyRegisterSuccess()
        val expectedRegister = MutableLiveData<ResultResponse<RegisterResponse>>()
        expectedRegister.value = ResultResponse.Success(dummyRegister)

        Mockito.`when`(repository.register(name, email, password)).thenReturn(expectedRegister)

        val actualRegister = registerViewModel.register(name, email, password).getOrAwaitValue()
        Mockito.verify(repository).register(name, email, password)
        assertNotNull(actualRegister)
        assertTrue(actualRegister is ResultResponse.Success)
    }

    @Test
    fun `when Register Should Null and Return Error`() {
        val dummyRegister = DataDummy.generateDummyRegisterError()
        val expectedRegister = MutableLiveData<ResultResponse<RegisterResponse>>()
        expectedRegister.value = ResultResponse.Error(dummyRegister.message)

        Mockito.`when`(repository.register(name, email, password)).thenReturn(expectedRegister)

        val actualRegister = registerViewModel.register(name, email, password).getOrAwaitValue()
        Mockito.verify(repository).register(name, email, password)
        assertNotNull(actualRegister)
        assertTrue(actualRegister is ResultResponse.Error)
    }
}