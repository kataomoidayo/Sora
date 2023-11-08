package com.putu.sora.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.putu.sora.R
import com.putu.sora.databinding.ActivitySettingsBinding
import com.putu.sora.extra.Helper
import com.putu.sora.extra.UserPreferences
import com.putu.sora.viewmodel.SettingsViewModel
import com.putu.sora.viewmodel.ViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private var _settingsBind: ActivitySettingsBinding? = null
    private val settingsBind get() = _settingsBind

    private lateinit var settingsViewModel: SettingsViewModel

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val helper = Helper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _settingsBind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBind?.root)

        supportActionBar?.title = getString(R.string.settings_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpAction()
        setUpViewModel()

        settingsViewModel.isLoading.observe(this) {
            settingsBind?.let { it1 ->
                helper.isLoading(
                    it,
                    it1.settingsProgressBar
                )
            }
        }
    }

    private fun setUpAction() {
        settingsBind?.cvLanguageSetting?.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        settingsBind?.cvLogout?.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val alert = builder.create()
            builder
                .setTitle(R.string.logout_alert_title)
                .setMessage(R.string.logout_alert_message)
                .setPositiveButton(R.string.logout_alert_positive_button) { _, _ ->
                    settingsViewModel.logout()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }

                .setNegativeButton(R.string.logout_alert_negative_button) {_, _ ->
                    alert.cancel()
                }
                .show()
        }
    }

    private fun setUpViewModel() {
        settingsViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore), this))[SettingsViewModel::class.java]
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}