package com.putu.sora.extra

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.putu.sora.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading


    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[USER_ID] ?: "",
                preferences[NAME] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN] ?: false
            )
        }
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGIN] = true
        }
    }

    suspend fun logout() {
        _loading.value = true
        dataStore.edit { preferences ->
            preferences.clear()
            preferences[IS_LOGIN] = false
        }
        _loading.value = false
    }

    companion object {

        private val USER_ID = stringPreferencesKey("userId")
        private val NAME = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN = booleanPreferencesKey("isLogin")

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}