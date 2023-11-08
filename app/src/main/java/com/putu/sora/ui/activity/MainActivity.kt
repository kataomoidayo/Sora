package com.putu.sora.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.putu.sora.R
import com.putu.sora.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _mainBind: ActivityMainBinding? = null
    private val mainBind get() = _mainBind


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _mainBind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBind?.root)

        val navView: BottomNavigationView? = mainBind?.bottomNavigation

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_postStory,
                R.id.navigation_maps
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView?.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_postStory) {
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
                navView?.visibility = View.GONE
            } else {
                navView?.visibility = View.VISIBLE
            }
        }
    }
}