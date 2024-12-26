package io.github.skydynamic.maiproberplus.core.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
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

private fun truncateTextToFitWidth(text: String, maxWidth: Float, paint: Paint): String {
    val ellipsis = "..."
    val ellipsisWidth = paint.measureText(ellipsis)

    if (ellipsisWidth >= maxWidth) {
        return if (ellipsisWidth == maxWidth) ellipsis else ""
    }

    var truncatedText = text
    var textWidth = paint.measureText(truncatedText)

    while (textWidth + ellipsisWidth > maxWidth && truncatedText.isNotEmpty()) {
        truncatedText = truncatedText.dropLast(1)
        textWidth = paint.measureText(truncatedText)
    }

    if (truncatedText.length < text.length) {
        truncatedText += ellipsis
    }

    return truncatedText
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

    val truncatedText = truncateTextToFitWidth(text, maxText.toFloat(), paint)

    outline?.let {
        val outlinePaint = Paint(paint)
        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.strokeWidth = it.weight
        outlinePaint.color = it.color

        this.drawText(truncatedText, x, y, outlinePaint)
    }

    this.drawText(truncatedText, x, y, paint)
}

fun createScaledBitmapHighQuality(src: Bitmap, dstWidth: Int, dstHeight: Int): Bitmap {
    val dst = Bitmap.createBitmap(dstWidth, dstHeight, src.config)
    val canvas = Canvas(dst)
    val paint = Paint()
    paint.isAntiAlias = true
    paint.isFilterBitmap = true
    paint.isDither = true
    canvas.drawBitmap(src,
        Rect(0, 0, src.width, src.height), Rect(0, 0, dstWidth, dstHeight), paint)
    return dst
}

fun String.toHalfWidth(): String {
    val sb = StringBuilder()
    for (char in this) {
        var num = char.code
        if (num == 0x3000) {
            num = 32
        } else if (num in 0xFF01..0xFF5E) {
            num -= 0xFEE0
        }
        sb.append(num.toChar())
    }
    return sb.toString()
}