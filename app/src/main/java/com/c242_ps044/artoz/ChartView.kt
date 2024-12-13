package com.c242_ps044.artoz

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import java.text.DecimalFormat

class ChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val slicePaint = Paint().apply {
        style = Paint.Style.FILL
    }

    private val centerPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val percentageTextPaint = Paint().apply {
        color = Color.BLACK
        textSize = 44f
        textAlign = Paint.Align.LEFT
    }

    private var data: Map<String, Float> = emptyMap()
    private var animationProgress = 0f
    private val decimalFormat = DecimalFormat("#.##")

    fun setData(data: Map<String, Float>) {
        this.data = data
        startAnimation()
    }

    private fun startAnimation() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1000
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener {
            animationProgress = it.animatedValue as Float
            invalidate()
        }
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height
        val centerX = width / 2f
        val centerY = height / 2.5f // Geser diagram ke atas untuk memberi ruang teks di bawah
        val radius = Math.min(width, height) * 0.35f
        val innerRadius = radius * 0.6f

        if (data.isEmpty() || data.values.all { it == 0f }) {
            // Jika data kosong atau semua nilai 0, gambar lingkaran hitam
            slicePaint.color = Color.BLACK
            canvas.drawCircle(centerX, centerY, radius, slicePaint)
            canvas.drawCircle(centerX, centerY, innerRadius, centerPaint)
            canvas.drawText("Tidak ada data", centerX, centerY + radius + 120f, textPaint)
            return
        }

        val total = data.values.sum()

        if (data.values.count { it > 0 } == 1) {
            // Jika hanya satu jenis data yang ada
            val singleType = data.entries.first { it.value > 0 }
            slicePaint.color =
                if (singleType.key == "Pemasukan") Color.parseColor("#1976D2") else Color.parseColor(
                    "#CA0C00"
                )
            canvas.drawArc(
                centerX - radius, centerY - radius, centerX + radius, centerY + radius,
                0f, 360f * animationProgress, true, slicePaint
            )
            canvas.drawCircle(centerX, centerY, innerRadius, centerPaint)
            canvas.drawText(
                "${singleType.key}: ${decimalFormat.format(100f)}%",
                centerX,
                centerY + radius + 120f,
                textPaint
            )
            return
        }

        var startAngle = 0f
        val labels = data.keys.toList()
        val values = data.values.toList()

        // Gambar diagram
        for (i in values.indices) {
            val sweepAngle = (values[i] / total) * 360f * animationProgress

            slicePaint.color =
                if (labels[i] == "Pemasukan") Color.parseColor("#1976D2") else Color.parseColor("#CA0C00")

            canvas.drawArc(
                centerX - radius, centerY - radius, centerX + radius, centerY + radius,
                startAngle, sweepAngle, true, slicePaint
            )

            startAngle += sweepAngle
        }

        canvas.drawCircle(centerX, centerY, innerRadius, centerPaint)

        // Tampilkan persentase di bawah diagram
        var textY = centerY + radius + 120f
        values.forEachIndexed { index, value ->
            val percentage = (value / total) * 100
            percentageTextPaint.color =
                if (labels[index] == "Pemasukan") Color.parseColor("#003297") else Color.parseColor(
                    "#CA0C00"
                )
            canvas.drawText(
                "${labels[index]}: ${decimalFormat.format(percentage)}%",
                centerX,
                textY,
                percentageTextPaint
            )
            textY += 60f // Jarak antar teks
        }
    }
}
