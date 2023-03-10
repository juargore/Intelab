@file:Suppress("SameParameterValue")

package com.intelab.joblab.presentation.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.intelab.joblab.R

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var missing = 0
    private val barHeight = 50

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ProgressView,
            0, 0
        ).apply {
            try {
                missing = 100 - getInt(R.styleable.ProgressView_value, 0)
            } finally {
                recycle()
            }
        }
    }

    fun setValue(value: Int) {
        missing = 100 - value
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val shader: Shader = LinearGradient(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            ContextCompat.getColor(context, R.color.red),
            ContextCompat.getColor(context, R.color.blue),
            Shader.TileMode.CLAMP
        )

        val gradientPaint = Paint().apply {
            setShader(shader)
        }

        val whitePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        if (missing == 100) {

            drawView(
                barHeight / 2f,
                (height - barHeight) / 2f,
                canvas,
                barHeight / 2,
                width - barHeight,
                whitePaint, false
            )
        } else {

            drawView(
                barHeight / 2f,
                (height - barHeight) / 2f,
                canvas,
                barHeight / 2,
                width - barHeight - (width - barHeight) * missing / 100,
                gradientPaint, false
            )

            drawView(
                barHeight / 2 + (width - barHeight) * (100 - missing) / 100f,
                (height - barHeight) / 2f,
                canvas,
                barHeight / 2,
                (width - barHeight) * missing / 100,
                whitePaint, true
            )
        }
    }


    private fun drawView(
        x: Float, y: Float,
        canvas: Canvas?,
        radius: Int,
        barWidth: Int,
        pathPaint: Paint,
        onDrawArc: Boolean
    ) {

        val path = Path()

        path.moveTo(x, y)

        val leftRect =
            RectF(
                x - radius,
                y,
                x + radius,
                height - y
            )

        if (onDrawArc)
            path.addArc(leftRect, 270f, 180f)
        else {
            path.addArc(leftRect, 270f, -180f)
        }


        path.lineTo(barWidth + x, height - y)

        val rightRect = RectF(
            x + barWidth - radius,
            y,
            x + barWidth + radius,
            height - y
        )
        path.addArc(rightRect, 90f, -180f)

        path.lineTo(x, y)

        canvas?.drawPath(path, pathPaint)
    }
}