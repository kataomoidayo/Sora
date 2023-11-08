package com.putu.sora.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.putu.sora.R
import com.putu.sora.adapter.LoadingStateAdapter
import com.putu.sora.adapter.StoryAdapter
import com.putu.sora.databinding.FragmentHomeBinding
import com.putu.sora.extra.UserPreferences
import com.putu.sora.ui.activity.SettingsActivity
import com.putu.sora.viewmodel.HomeViewModel
import com.putu.sora.viewmodel.ViewModelFactory

class HomeFragment : Fragment(), MenuProvider {

    private var _homeBind: FragmentHomeBinding? = null
    private val homeBind get() = _homeBind

    private lateinit var homeViewModel: HomeViewModel

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _homeBind = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.sora)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setUpViewModel()

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun setUpViewModel() {
        val pref = UserPreferences.getInstance(requireContext().dataStore)
        homeViewModel = ViewModelProvider(this, ViewModelFactory(pref, requireActivity()))[HomeViewModel::class.java]

        homeViewModel.getUser().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                getData(user.token)
            }
        }
    }

    private fun getData(token: String) {
        homeBind?.rvStoryList?.layoutManager = LinearLayoutManager(activity)

        val storyAdapter = StoryAdapter()
        homeBind?.rvStoryList?.adapter = storyAdapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter {
                storyAdapter.retry()
            },

            footer = LoadingStateAdapter {
                storyAdapter.retry()
            }
        )

        homeViewModel.getAllStories(token).observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_menu_list, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.settingsBtn -> {
                startActivity(Intent(requireActivity(), SettingsActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _homeBind = null
    }
}