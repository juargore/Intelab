package com.intelab.joblab.presentation.base

import android.app.Dialog
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import com.intelab.joblab.R
import com.intelab.joblab.domain.common.Connectivity
import com.intelab.joblab.presentation.base.utils._connectivityAction
import com.intelab.joblab.presentation.base.utils._delay200
import com.intelab.joblab.presentation.extensions.hide
import com.intelab.joblab.presentation.extensions.progressDialog
import com.intelab.joblab.presentation.extensions.show
import com.intelab.joblab.presentation.receiver.ConnectivityReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register receiver for network changes through the Application
        val intentFilter = IntentFilter()
        intentFilter.addAction(_connectivityAction)
        registerReceiver(ConnectivityReceiver(), intentFilter)

        dialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_no_internet_connection)
            setCancelable(false)
        }

        dialog?.let { d ->
            d.findViewById<ImageView>(R.id.imgRetryConnectivity)?.setOnClickListener {
                if (Connectivity(this).isNetworkAvailable()) {
                    d.dismiss()
                    d.hide()
                } else {
                    with(d.findViewById<AppCompatTextView>(R.id.txtNoInternetYet)) {
                        this.show()
                        lifecycleScope.launch {
                            delay(_delay200)
                            this@with.hide()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    /** Callback will be called when a change exists */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }

    private fun showMessage(isConnected: Boolean) {
        try {
            dialog?.let { d ->
                if (!isConnected) {
                    if(!d.isShowing) d.show()
                } else {
                    d.dismiss()
                    d.hide()
                }
            }
        } catch (e: Exception) {
            Log.e("Exception", "Dialog: ${e.message}")
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog?.let {
            it.dismiss()
            progressDialog = null
        }
    }
}