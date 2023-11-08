package com.putu.sora.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.putu.sora.extra.DataDummy
import com.putu.sora.extra.MainDispatcherRule
import com.putu.sora.data.repository.Repository
import com.putu.sora.data.response.LoginResponse
import com.putu.sora.extra.ResultResponse
import com.putu.sora.extra.UserPreferences
import com.putu.sora.extra.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var repository: Repository

    private lateinit var loginViewModel: LoginViewModel
    private val email = "email@email.com"
    private val password = "qwerty"

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(userPreferences, repository)
    }

    @Test
    fun `when Login Should Not Null and Return Success`() {
        val dummyLogin = DataDummy.generateDummyLoginSuccess()
        val expectedLogin = MutableLiveData<ResultResponse<LoginResponse>>()
        expectedLogin.value = ResultResponse.Success(dummyLogin)

        `when`(repository.login(email, password, userPreferences)).thenReturn(expectedLogin)

        val actualLogin = loginViewModel.login(email, password).getOrAwaitValue()
        Mockito.verify(repository).login(email, password, userPreferences)
        assertNotNull(actualLogin)
        assertTrue(actualLogin is ResultResponse.Success)
    }

    @Test
    fun `when Login Should Null and Return Error`() {
        val dummyLogin = DataDummy.generateDummyLoginError()
        val expectedLogin = MutableLiveData<ResultResponse<LoginResponse>>()
        expectedLogin.value = ResultResponse.Error(dummyLogin.message)

        `when`(repository.login(email, password, userPreferences)).thenReturn(expectedLogin)

        val actualLogin = loginViewModel.login(email, password).getOrAwaitValue()
        Mockito.verify(repository).login(email, password, userPreferences)
        assertNotNull(actualLogin)
        assertTrue(actualLogin is ResultResponse.Error)
    }
}