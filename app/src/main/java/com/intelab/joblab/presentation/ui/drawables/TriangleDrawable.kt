package com.intelab.joblab.presentation.ui.drawables

import android.graphics.*
import android.graphics.drawable.Drawable

class TriangleDrawable : Drawable() {

    var paint: Paint = Paint().apply {
        setARGB(255, 220, 220, 220)
    }

    @Suppress("UNUSED_VARIABLE")
    override fun draw(canvas: Canvas) {
        val width: Int = bounds.width()
        val height: Int = bounds.height()
        val slope = 3.59f
        val space = height / slope

        val vectorPath = Path()
        vectorPath.moveTo(0f, 0f)
        vectorPath.lineTo(space, height.toFloat())
        vectorPath.lineTo(0f, height.toFloat())
        vectorPath.close()

        canvas.drawPath(vectorPath, paint)
    }

    override fun setAlpha(p0: Int) {
        paint.alpha = p0
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity(): Int = PixelFormat.OPAQUE
}