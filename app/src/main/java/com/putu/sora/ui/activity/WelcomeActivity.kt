package com.putu.sora.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.putu.sora.R
import com.putu.sora.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private var _welcomeBind : ActivityWelcomeBinding? = null
    private val welcomeBind get() = _welcomeBind


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _welcomeBind = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(welcomeBind?.root)

        supportActionBar?.hide()

        setUpAction()
        setUpAnimation()
    }

    private fun setUpAction() {
        welcomeBind?.loginButton?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }

        welcomeBind?.signupButton?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }
    }

    private fun setUpAnimation() {
        ObjectAnimator.ofFloat(welcomeBind?.logo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(welcomeBind?.loginButton, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(welcomeBind?.signupButton, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(welcomeBind?.welcome, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(welcomeBind?.welcomeIntro, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, login, signup)
            start()
        }
    }
}