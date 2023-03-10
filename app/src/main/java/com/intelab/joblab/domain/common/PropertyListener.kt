package com.intelab.joblab.domain.common

import androidx.databinding.Observable

/**
 * Helper functions that help us to test the bindables that are called in each ViewModel.
 * */
interface PropertyListener {
    fun onPropertyChanged(sender: Observable?, propertyId: Int)
}

@Suppress("unused")
fun propertyChangedCallback(listener: PropertyListener) =
    object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            listener.onPropertyChanged(sender, propertyId)
        }
    }

fun propertyChangedCallback(listener: (Observable?, Int) -> Unit) =
    object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            listener(sender, propertyId)
        }
    }