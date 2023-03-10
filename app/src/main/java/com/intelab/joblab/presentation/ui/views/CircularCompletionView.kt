package com.intelab.joblab.presentation.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView

class CircularCompletionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ShapeableImageView(context, attrs, defStyle) {
    private var completionPercent = 0f
    private var mustShowStroke = true
    private val paint: Paint = Paint()
    private var radius = 100.0
    private var strokeSize = 20.0
    private var diameter = radius * 2

    fun setCompletionPercentage(completion: Float) {
        completionPercent = completion
        invalidate()
    }

    fun setMustShowStroke(show: Boolean) {
        mustShowStroke = show
        invalidate()
    }

    @Suppress("UNUSED_VALUE")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = MeasureSpec.getSize(heightMeasureSpec)
        if (width > height) {
            width = height
        } else {
            height = width
        }
        diameter = width.toDouble()
        radius = diameter / 2
        val newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.parseColor("#dedede") // circle stroke color- grey
        paint.strokeWidth = strokeSize.toFloat()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(radius.toFloat(), radius.toFloat(), radius.toFloat() - 10, paint)
        if (mustShowStroke) {
            paint.color = if (completionPercent == 100f) {
                Color.parseColor("#01FF00")
            } else {
                Color.parseColor("#E2E735")
            }
        } else {
            paint.color = Color.parseColor("#dedede")
        }
        paint.strokeWidth = strokeSize.toFloat()
        paint.style = Paint.Style.FILL
        val oval = RectF()
        paint.style = Paint.Style.STROKE
        oval[10f, 10f, (diameter - 10).toFloat()] = (diameter - 10).toFloat()
        canvas.drawArc(oval, 270.0f, completionPercent * 360 / 100f, false, paint)
    }
}
