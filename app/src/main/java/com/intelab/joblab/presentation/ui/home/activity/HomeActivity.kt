package com.intelab.joblab.presentation.ui.home.activity

import android.os.Bundle
import com.intelab.joblab.R
import com.intelab.joblab.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()
    }
}
