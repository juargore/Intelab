package com.intelab.joblab.presentation.base

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intelab.joblab.BR
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty

/**
 * From Android example
 * [[https://developer.android.com/topic/libraries/data-binding/architecture]]
 */
abstract class ObservableViewModel : ViewModel(), Observable, CoroutineScope {

    override val coroutineContext: CoroutineContext = viewModelScope.coroutineContext

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    @Suppress("unused")
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}

fun <T> ObservableViewModel.bindDelegate(
    value: T,
    expression: ((oldValue: T, newValue: T) -> Unit)? = null
): DelegatedBindable<T> = DelegatedBindable(value, this, expression)


/**
 * From Android example
 * [[https://stablekernel.com/article/reducing-data-binding-boilerplate-with-kotlin/]]
 */
class DelegatedBindable<T>(
    private var value: T,
    private val observer: ObservableViewModel,
    private val expression: ((oldValue: T, newValue: T) -> Unit)? = null
) {
    private var bindingTarget: Int = -1

    operator fun getValue(thisRef: Any?, p: KProperty<*>) = value

    operator fun setValue(thisRef: Any?, p: KProperty<*>, v: T) {

        val oldValue = value
        value = v
        if (bindingTarget == -1) {
            bindingTarget = BR::class.java.fields.filter {
                it.name == p.name
            }[0].getInt(null)
        }
        observer.notifyPropertyChanged(bindingTarget)
        expression?.invoke(oldValue, value)
    }
}