package nl.codingwithlinda.guided_tour.presentation.design_system

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class SpeechBubbleShape(
    private val tipOffset: Offset
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val tipSize = Size(24f, 24f)
        val halfWidth = size.width / 2f - tipSize.width / 2f

        val path = Path().apply {
            moveTo(x = 0f, y = tipSize.height)
            relativeLineTo(dx = size.width, dy = 0f)
            relativeLineTo(dx = 0f, dy = size.height)
            relativeLineTo(dx = -size.width, dy = 0f)
            close()
        }
        path.addPath(
            Path().apply {
                moveTo(x = 0f, y = tipSize.height)
                relativeLineTo(dx = tipSize.width / 2, dy = -tipSize.height)
                relativeLineTo(dx = tipSize.width / 2, dy = tipSize.height)
            },
            offset = tipOffset
        )


        return Outline.Generic(path)
    }
}