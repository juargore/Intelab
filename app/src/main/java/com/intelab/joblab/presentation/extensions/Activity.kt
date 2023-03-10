package com.intelab.joblab.presentation.extensions

import android.app.Activity
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.intelab.joblab.R
import com.intelab.joblab.presentation.ui.views.JoblabDialog
import com.intelab.joblab.presentation.ui.views.TYPES

inline fun Activity.showJoblabDialog(func: JoblabDialog.() -> Unit): AlertDialog =
    JoblabDialog(this).apply { func() }.create()

fun Fragment.toast(@StringRes msg: Int, duration : Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), getString(msg), duration).show()
}

fun FragmentActivity.backConfirmation(fr: Fragment, accutest: Boolean) {
    this.onBackPressedDispatcher.addCallback(fr.viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showJoblabDialog {
                    setTypeDialog(TYPES.DOUBLE)
                    title.text = getString(if (accutest) R.string.dialog_title_accutest else R.string.app_name_release)
                    message.text = getString(if (accutest) R.string.dialog_accutest_loss_progress else R.string.dialog_message_leaving_app)
                    if (!accutest) {
                        acceptButton.text = getString(R.string.bn_text_yes)
                        cancelButton.text = getString(R.string.bn_text_no)
                    }
                    cancelClickListener { }
                    acceptClickListener {
                        if (accutest) {
                            fr.findNavController().navigateUp()
                        } else {
                            this@backConfirmation.finish()
                        }
                    }
                }.show()
            }
        }
    )
}
