package com.intelab.joblab.presentation.ui.bindings

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.intelab.joblab.R
import com.intelab.joblab.presentation.base.utils._delay100
import com.intelab.joblab.presentation.base.utils._percentageCompleted
import com.intelab.joblab.presentation.base.utils._smallSizeTech

@BindingAdapter("animate")
fun ImageView.animateImage(animate: Boolean) {
    if (animate) {
        alpha = 0f
        animate().alpha(1f).duration = _delay100
    }
}

@BindingAdapter("loadFromUri")
fun ImageView.loadFromUri(uri: Uri?) {
    uri?.let {
        setupGlide(
            view = this,
            toLoad = uri,
            skipMemory = true,
            cacheStrategy = true,
            placeHolder = R.drawable.ic_photo_user,
            image = this
        )
    }
}

@BindingAdapter("url")
fun ImageView.loadFromUrl(url: String?) {
    if (url.isNullOrBlank() || url == _smallSizeTech) {
        setImageResource(R.drawable.ic_photo_user)
    } else {
        setupGlide(
            view = this,
            toLoad = url,
            skipMemory = false,
            cacheStrategy = false,
            placeHolder = R.color.gray_300,
            image = this
        )
    }
}

@BindingAdapter("url_no_cache")
fun ImageView.loadFromUrlNoCache(url: String?) {
    if (url.isNullOrBlank() || url == _smallSizeTech) {
        setImageResource(R.drawable.ic_photo_user)
    } else {
        setupGlide(
            view = this,
            toLoad = url,
            skipMemory = true,
            cacheStrategy = true,
            placeHolder = R.drawable.ic_photo_user,
            image = this
        )
    }
}

@BindingAdapter("radioNational", "foreignChecked")
fun RadioButton.foreignChecked(radioNational: Boolean, foreignChecked: Boolean) {
    if (radioNational) {
        this.isChecked = !foreignChecked
    }
    if (!radioNational) {
        this.isChecked = foreignChecked
    }
}

@BindingAdapter("profileCompleted")
fun ImageView.setCustomTint(progress: Int) {
    if (progress == _percentageCompleted) {
        setColorFilter(
            ContextCompat.getColor(context, R.color.green_800),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    } else {
        setColorFilter(
            ContextCompat.getColor(context, R.color.gray_400),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }
}

private fun <T>setupGlide(
    view: View,
    toLoad: T,
    skipMemory: Boolean,
    cacheStrategy: Boolean,
    placeHolder: Int,
    image: ImageView
) {
    Glide.with(view)
        .load(toLoad)
        .placeholder(placeHolder)
        .diskCacheStrategy(
            if (cacheStrategy) DiskCacheStrategy.NONE
            else DiskCacheStrategy.AUTOMATIC
        )
        .skipMemoryCache(skipMemory)
        .dontAnimate()
        .let { request ->
            if (image.drawable != null) {
                request.placeholder(image.drawable.constantState?.newDrawable()?.mutate())
            } else request
        }
        .into(image)
}
