package com.intelab.joblab.presentation.extensions

import androidx.annotation.IdRes
import androidx.navigation.NavController

fun NavController.isOnBackStack(@IdRes id: Int): Boolean = try {
    getBackStackEntry(id); true
} catch (e: Throwable) {
    false
}