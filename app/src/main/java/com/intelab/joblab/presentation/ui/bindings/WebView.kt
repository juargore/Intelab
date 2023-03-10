package com.intelab.joblab.presentation.ui.bindings

import android.webkit.WebView
import androidx.databinding.BindingAdapter

@BindingAdapter("content")
fun WebView.setHtmlContent(html: String) {
    loadDataWithBaseURL(null, html, "text/html; charset=utf-8", "UTF-8", null)
}