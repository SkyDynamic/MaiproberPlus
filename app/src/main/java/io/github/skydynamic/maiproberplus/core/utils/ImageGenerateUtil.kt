package io.github.skydynamic.maiproberplus.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import io.github.skydynamic.maiproberplus.Application.Companion.application

enum class TextAlign(val align: Paint.Align) {
    Left(Paint.Align.LEFT),
    Center(Paint.Align.CENTER),
    Right(Paint.Align.RIGHT)
}

class TextOutline(
    val color: Int,
    val weight: Float,
)

fun Canvas.drawImage(bitmap: Bitmap, x: Float, y: Float) {
    this.drawBitmap(bitmap, x, y, null)
}

fun Canvas.drawText(
    text: String,
    textSize: Float,
    x: Float,
    y: Float,
    maxText: Int = Int.MAX_VALUE,
    font: Int? = null,
    color: Int? = null,
    outline: TextOutline? = null,
    align: TextAlign = TextAlign.Left
) {
    val paint = Paint()

    paint.color = color ?: Color.BLACK
    paint.textAlign = align.align
    paint.textSize = textSize
    paint.typeface = if (font != null) {
        application.getFont(font)
    } else {
        Typeface.DEFAULT_BOLD
    }

    outline?.let {
        val outlinePaint = Paint(paint)
        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.strokeWidth = it.weight
        outlinePaint.color = it.color

        this.drawText(text, x, y, outlinePaint)
    }

    var truncatedText = if (text.length > maxText) {
        "${text.substring(0, maxText - 3)}..."
    } else {
        text
    }

    this.drawText(truncatedText, x, y, paint)
}