package com.intelab.joblab.presentation.ui.bindings

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter


@Suppress("UNUSED_PARAMETER")
@BindingAdapter("clickable")
fun TextView.clickable(clickable: Boolean) {
    movementMethod = LinkMovementMethod.getInstance()
}

@Suppress("UnnecessaryVariable")
@BindingAdapter("text", "drawable", requireAll = true)
fun TextView.setIconInText(text: String, @DrawableRes imgSrc: Int) {
    val modifiedText = text // you can use resource string here
    val span = SpannableStringBuilder(modifiedText)
    val drawable = ContextCompat.getDrawable(context, imgSrc) ?: return
    drawable.mutate()
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val image = ImageSpan(drawable)
    val startIndex = modifiedText.indexOf("[icon]")
    span.setSpan(image, startIndex, startIndex + 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    setText(span, TextView.BufferType.SPANNABLE)
}

@BindingAdapter("args", "customText")
fun TextView.setTextWithArgs(args: Int, @StringRes customText: Int) {
    text = context.resources.getString(customText, args)
}

@BindingAdapter("textFromInt")
fun TextView.setTextFromIntResource(@StringRes customText: Int?) {
    customText?.let {
        text = context.resources.getString(customText)
    }
}

@BindingAdapter("clickInit", "clickEnd", "onClick")
fun TextView.setPartialClickable(init: Int, start: Int, onClick: () -> Unit) {
    val ss = SpannableString(text)
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }
    ss.setSpan(clickableSpan, init, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    text = ss
    movementMethod = LinkMovementMethod.getInstance()
}
