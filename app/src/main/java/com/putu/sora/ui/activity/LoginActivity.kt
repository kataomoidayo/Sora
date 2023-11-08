package com.putu.sora.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.putu.sora.R
import com.putu.sora.databinding.ActivityLoginBinding
import com.putu.sora.extra.Helper
import com.putu.sora.extra.ResultResponse
import com.putu.sora.extra.UserPreferences
import com.putu.sora.viewmodel.LoginViewModel
import com.putu.sora.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private var _loginBind: ActivityLoginBinding? = null
    private val loginBind get() = _loginBind

    private lateinit var loginViewModel: LoginViewModel

    private val helper = Helper()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _loginBind = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBind?.root)

        supportActionBar?.hide()

        setUpAction()
        setUpAnimation()
        setUpViewModel()

        loginViewModel.isLoading.observe(this) {
            loginBind?.let { it1 ->
                helper.isLoading(
                    it,
                    it1.loginProgressBar
                )
            }
        }

        onBackPressedDispatcher.addCallback(this@LoginActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
            }
        })
    }

    private fun setUpAction() {
        loginBind?.closeButton?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        }

        loginBind?.loginButton?.setOnClickListener {
            val email = loginBind?.emailEditText?.text.toString()
            val password = loginBind?.passwordEditText?.text.toString()

            when {
                email.isEmpty() ->
                    loginBind?.emailEditText?.error = getString(R.string.error_email_empty)

                password.isEmpty() ->
                    loginBind?.passwordEditText?.error = getString(R.string.error_password_empty)

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    loginBind?.emailEditText?.error = getString(R.string.error_invalid_email_format)

                password.length < 8 ->
                    loginBind?.passwordEditText?.error = getString(R.string.error_wrong_password_format)

                else -> loginViewModel.login(email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultResponse.Loading -> {

                            }

                            is ResultResponse.Success -> {
                                AlertDialog.Builder(this).apply {
                                    setTitle(R.string.success_alert_title)
                                    setMessage(R.string.login_alert_message)
                                    setPositiveButton(R.string.continue_alert_button) { _, _ ->
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }

                            is ResultResponse.Error -> {
                                when(result.error) {
                                    "Unable to resolve host \"story-api.dicoding.dev\": No address associated with hostname" -> {
                                        val builder = AlertDialog.Builder(this)
                                        val alert = builder.create()
                                        builder
                                            .setTitle(R.string.failed_response_alert_title)
                                            .setMessage(R.string.failed_response_alert_message)
                                            .setPositiveButton(R.string.back_alert_button) {_, _ ->
                                                alert.cancel()
                                            }.show()
                                    }

                                    else -> {
                                        val builder = AlertDialog.Builder(this)
                                        val alert = builder.create()
                                        builder
                                            .setTitle(R.string.login_failed_alert_title)
                                            .setMessage(R.string.login_failed_alert_message)
                                            .setPositiveButton(R.string.back_alert_button) {_, _ ->
                                                alert.cancel()
                                            }.show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setUpAnimation() {
        ObjectAnimator.ofFloat(loginBind?.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(loginBind?.titleLogin, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(loginBind?.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(loginBind?.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(loginBind?.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, emailEditTextLayout, passwordEditTextLayout, login)
            startDelay = 500
        }.start()
    }

    private fun setUpViewModel() {
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore), this))[LoginViewModel::class.java]
    }
}