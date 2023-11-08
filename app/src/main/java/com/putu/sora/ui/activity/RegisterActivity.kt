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
import com.putu.sora.databinding.ActivityRegisterBinding
import com.putu.sora.extra.Helper
import com.putu.sora.extra.ResultResponse
import com.putu.sora.extra.UserPreferences
import com.putu.sora.viewmodel.RegisterViewModel
import com.putu.sora.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private var _registerBind : ActivityRegisterBinding? = null
    private val registerBind get() = _registerBind

    private lateinit var registerViewModel: RegisterViewModel

    private val helper = Helper()

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _registerBind = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBind?.root)

        supportActionBar?.hide()

        setUpAction()
        setUpAnimation()
        setUpViewModel()

        registerViewModel.isLoading.observe(this) {
            registerBind?.let { it1 ->
                helper.isLoading(
                    it,
                    it1.registerProgressBar
                )
            }
        }

        onBackPressedDispatcher.addCallback(this@RegisterActivity, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
            }
        })
    }

    private fun setUpAction() {
        registerBind?.closeButton?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
        }

        registerBind?.signupButton?.setOnClickListener {
            val name = registerBind?.nameEditText?.text.toString()
            val email = registerBind?.emailEditText?.text.toString()
            val password = registerBind?.passwordEditText?.text.toString()

            when {
                name.isEmpty() ->
                    registerBind?.nameEditText?.error = getString(R.string.error_name_empty)

                email.isEmpty() ->
                    registerBind?.emailEditText?.error = getString(R.string.error_email_empty)

                password.isEmpty() ->
                    registerBind?.passwordEditText?.error = getString(R.string.error_password_empty)

                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                    registerBind?.emailEditText?.error = getString(R.string.error_invalid_email_format)

                password.length < 8 ->
                    registerBind?.passwordEditText?.error = getString(R.string.error_wrong_password_format)

                else -> registerViewModel.register(name, email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultResponse.Loading -> {

                            }

                            is ResultResponse.Success -> {
                                AlertDialog.Builder(this).apply {
                                    setTitle(R.string.success_alert_title)
                                    setMessage(R.string.register_alert_message)
                                    setPositiveButton(R.string.continue_alert_button) { _, _ ->
                                        val intent = Intent(context, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }

                            is ResultResponse.Error -> {
                                when (result.error) {
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
                                            .setTitle(R.string.register_failed_alert_title)
                                            .setMessage(R.string.register_failed_alert_message)
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
        ObjectAnimator.ofFloat(registerBind?.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(registerBind?.titleSignup, View.ALPHA, 1f).setDuration(500)
        val nameEditText = ObjectAnimator.ofFloat(registerBind?.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(registerBind?.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(registerBind?.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(registerBind?.signupButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, nameEditText, emailEditTextLayout, passwordEditTextLayout, register)
            startDelay = 500
        }.start()
    }

    private fun setUpViewModel() {
        registerViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferences.getInstance(dataStore), this))[RegisterViewModel::class.java]
    }
}