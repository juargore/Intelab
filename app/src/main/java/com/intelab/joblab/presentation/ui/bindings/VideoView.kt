package com.intelab.joblab.presentation.ui.bindings

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.databinding.BindingAdapter

@BindingAdapter("playVideoId")
fun VideoView.showVideo(id: Int) {
    setVideoURI(Uri.parse("android.resource://${context.packageName}/$id"))
    val mediaController = MediaController(context)
    mediaController.setMediaPlayer(this)
    setMediaController(mediaController)
    setOnPreparedListener {
        start()
        mediaController.show()
    }
}