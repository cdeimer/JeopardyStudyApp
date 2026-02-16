package com.cdeimer.jeopardystudyapp.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cdeimer.jeopardystudyapp.views.StatsViewModel.DailyScore

@Composable
fun LineGraph(
    data: List<DailyScore>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF4ECB71) // Green
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier.padding(16.dp)) {
        if (data.isEmpty()) return@Canvas

        // 1. Setup Dimensions
        val width = size.width
        val height = size.height
        val spacePerPoint = width / (data.size - 1)
        val maxVal = 100f // Y-Axis goes up to 100%

        // 2. Build the Path
        val path = Path()
        data.forEachIndexed { i, point ->
            val x = i * spacePerPoint
            val y = height - (point.accuracy / maxVal * height) // Invert Y (0 is top)

            if (i == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }

        // 3. Draw the Line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // 4. Draw Dots & Labels
        data.forEachIndexed { i, point ->
            val x = i * spacePerPoint
            val y = height - (point.accuracy / maxVal * height)

            // Dot
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = Offset(x, y)
            )

            // Label (Day Name) - Drawn below the graph
            val textLayoutResult = textMeasurer.measure(
                text = point.dayLabel,
                style = TextStyle(color = Color.Gray, fontSize = 10.sp)
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = x - (textLayoutResult.size.width / 2),
                    y = height + 10.dp.toPx()
                )
            )

            // C. The Value Label (Top - NEW!)
            // Only show if accuracy > 0 to keep it clean
            if (point.accuracy > 0) {
                val valueLabelResult = textMeasurer.measure(
                    text = "${point.accuracy.toInt()}%",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                drawText(
                    textLayoutResult = valueLabelResult,
                    topLeft = Offset(
                        x = x - (valueLabelResult.size.width / 2),
                        y = y - 20.dp.toPx() // Float slightly above the dot
                    )
                )
            }
        }
    }
}