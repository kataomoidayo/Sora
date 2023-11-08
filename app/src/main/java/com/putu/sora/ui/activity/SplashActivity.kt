package com.putu.sora.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.putu.sora.databinding.ActivitySplashBinding
import com.putu.sora.extra.UserPreferences
import com.putu.sora.viewmodel.HomeViewModel
import com.putu.sora.viewmodel.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var _splashBind: ActivitySplashBinding? = null
    private val splashBind get() = _splashBind

    private lateinit var homeViewModel: HomeViewModel

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _splashBind = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBind?.root)

        supportActionBar?.hide()

        loginCheck()
    }

    private fun loginCheck() {
        homeViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore), this))[HomeViewModel::class.java]

        homeViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                val intent = Intent(this, MainActivity::class.java)
                splashScreen(intent)
            } else {
                val intent = Intent(this, WelcomeActivity::class.java)
                splashScreen(intent)
            }
        }
    }

    private fun splashScreen(intent: Intent) {
        Handler(Looper.getMainLooper()).postDelayed ({
            startActivity(intent)
            finish()
        }, SPLASH_DELAY.toLong())
    }

    companion object {
        const val SPLASH_DELAY = 3000
    }
}