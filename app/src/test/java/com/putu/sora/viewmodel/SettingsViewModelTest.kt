package com.putu.sora.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.putu.sora.extra.MainDispatcherRule
import com.putu.sora.extra.UserPreferences
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
class SettingsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences

    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setUp() {
        settingsViewModel = SettingsViewModel(userPreferences)
    }

    @Test
    fun `when User Logout Clear User Login Data`() = runTest {
        settingsViewModel.logout()
        Mockito.verify(userPreferences).logout()
    }
}