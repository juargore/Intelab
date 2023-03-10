package com.intelab.joblab.presentation.ui.home.main.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.navigation.NavigationView
import com.intelab.joblab.BuildConfig
import com.intelab.joblab.R
import com.intelab.joblab.databinding.FragmentHomeBinding
import com.intelab.joblab.databinding.NavHeaderMainBinding
import com.intelab.joblab.domain.entities.DataArray
import com.intelab.joblab.presentation.extensions.*
import com.intelab.joblab.presentation.services.PushFirebaseListenerService
import com.intelab.joblab.presentation.ui.home.main.adapter.HomeViewPagerAdapter
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeState
import com.intelab.joblab.presentation.ui.home.main.viewmodels.HomeViewModel
import com.intelab.joblab.presentation.base.utils.OPEN_ACCUTEST
import com.intelab.joblab.presentation.base.utils.OPEN_NOTIFICATIONS
import com.intelab.joblab.presentation.ui.views.TYPES
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    getArrayFromUri(uri)?.let {
                        viewModel.userPhoto = DataArray(it)
                        viewModel.sendPhotoToCloud()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.state.flow(this) { handleStateChange(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        binding.viewModel = viewModel
        viewModel.initialize(requireContext())
        setupViewPagerWithCustomImages()
        bindNavigationHeaderView()
        requireActivity().backConfirmation(this, false)
        addDrawerLayoutListener()
    }

    @SuppressLint("InflateParams")
    private fun setupViewPagerWithCustomImages() {
        val navIcons = intArrayOf(R.drawable.ic_user, R.drawable.ic_list_checked)
        val navigation = binding.tabs
        val viewPager = binding.viewPager

        viewPager.adapter = HomeViewPagerAdapter(childFragmentManager).also {
            it.addFragment(HomeTabOneFragment())
            it.addFragment(HomeTabTwoFragment())
        }; navigation.setupWithViewPager(viewPager)

        for (i in 0 until navigation.tabCount) {
            val tab = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_tab_home, null) as ConstraintLayout
            val icon = tab.findViewById<ImageView>(R.id.nav_icon)
            val background = tab.findViewById<ConstraintLayout>(R.id.nav_background)
            if (i == 0) {
                background.setBackgroundTint(R.color.green_300)
                icon?.setCustomColorFilter(R.color.black_800)
            }
            icon.setImageResource(navIcons[i])
            navigation.getTabAt(i)?.customView = tab
        }

        navigation.addCustomListener(viewPager)
        binding.navView.setNavigationItemSelectedListener(this)
        handleStateChange(HomeState.IsLoading(false))
    }

    fun updateUserName(name: String) {
        viewModel.updateUserName(name)
    }

    private fun bindNavigationHeaderView() {
        binding.footerVersion.text = BuildConfig.VERSION_NAME
        val viewHeader = binding.navView.getHeaderView(0)
        val binding: NavHeaderMainBinding = NavHeaderMainBinding.bind(viewHeader)
        binding.viewModel = viewModel
        binding.cpvAvatar.setOnClickListener {
            showBottomSheetDialog(viewModel) {
                selectImageFromGalleryResult.launch(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resetHomeState()
        viewModel.getTotalCounterNotifications()
        redirectOnNewNotification()
    }

    private fun resetHomeState() {
        viewModel.state.value = HomeState.Init
    }

    private fun handleStateChange(state: HomeState) {
        when (state) {
            is HomeState.Init -> Unit
            is HomeState.IsLoading -> updateProgressDialog(state.isLoading)
            is HomeState.OpenLoginScreen -> navigateSafe(state.directions)
            is HomeState.OpenNotificationsScreen -> navigateSafe(state.directions)
            is HomeState.CloseSideMenu -> {
                binding.drawerLayout.closeDrawer(Gravity.END)
                resetHomeState()
            }
            is HomeState.OpenSideMenu -> {
                binding.drawerLayout.openDrawer(Gravity.END)
                resetHomeState()
            }
            is HomeState.ErrorLogout -> {
                showJoblabDialog { errorDialog(state.rawResponse) }.show()
                resetHomeState()
            }
            is HomeState.ErrorState -> errorValidation(state.rawResponse) { resetHomeState() }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.init -> Unit
            R.id.logout -> {
                showJoblabDialog {
                    setTypeDialog(TYPES.DOUBLE)
                    title.text = getString(R.string.menu_item_close_session)
                    message.text = getString(R.string.menu_item_close_session_body)
                    acceptButton.text = getString(R.string.menu_item_close_session)
                    acceptClickListener { viewModel.logout() }
                    cancelClickListener { resetHomeState() }
                }.show()
            }
            R.id.delete_account -> {
                showJoblabDialog {
                    setTypeDialog(TYPES.DOUBLE)
                    title.text = getString(R.string.menu_item_delete_account)
                    message.text = getString(R.string.menu_delete_account_body)
                    acceptClickListener { viewModel.deleteAccount() }
                    cancelClickListener { resetHomeState() }
                }.show()
            }
            R.id.edit_profile -> {
                if (viewModel.complementaryRegisterFinished) {
                    val directions = HomeFragmentDirections.actionHomeFragmentToProfileNavigation()
                    navigateSafe(directions)
                } else {
                    simpleDialog(
                        R.string.tv_title_incomplete_register,
                        R.string.tv_message_incomplete_register_profile) {
                        resetHomeState()
                    }
                }
            }
        }
        binding.drawerLayout.closeDrawer(Gravity.END)
        return true
    }

    private fun redirectOnNewNotification() {
        try {
            when (PushFirebaseListenerService.action) {
                OPEN_NOTIFICATIONS -> navigateSafe(HomeFragmentDirections.actionHomeFragmentToNotifications())
                OPEN_ACCUTEST -> binding.viewPager.currentItem = 1
                else -> Unit // do nothing -> Home screen
            }
            PushFirebaseListenerService.action = ""
        } catch (e: Exception) {
            e.message?.let { Log.e("Error Notification", it) }
        }
    }

    private fun addDrawerLayoutListener() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                val bundle = bundleOf("newPhoto" to viewModel.newPhotoCapture)
                childFragmentManager.setFragmentResult("updatePhotoKey", bundle)
                viewModel.newPhotoCapture = false
            }
        })
    }
}
