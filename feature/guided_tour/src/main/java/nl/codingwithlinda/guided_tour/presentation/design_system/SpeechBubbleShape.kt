package nl.codingwithlinda.guided_tour.presentation.design_system

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.rotationMatrix

class SpeechBubbleShape(
    private val tipOffset: Offset,
    private val tipRotation: Float = 0f,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val tipSize = Size(24f, 24f)
        val tipAtBottom = tipRotation != 0f

        // Rectangle occupies (size.height - tipSize.height) so the tip fits within bounds.
        // Tip-at-top: rect starts below the tip  (y = tipSize.height)
        // Tip-at-bottom: rect starts at top       (y = 0)
        val rectStartY = if (tipAtBottom) 0f else tipSize.height
        val path = Path().apply {
            moveTo(x = 0f, y = rectStartY)
            relativeLineTo(dx = size.width, dy = 0f)
            relativeLineTo(dx = 0f, dy = size.height - tipSize.height)
            relativeLineTo(dx = -size.width, dy = 0f)
            close()
        }

        val p2 = Path().apply {
            moveTo(x = 0f, y = tipSize.height)
            relativeLineTo(dx = tipSize.width / 2, dy = -tipSize.height)
            relativeLineTo(dx = tipSize.width / 2, dy = tipSize.height)
        }
        p2.transform(Matrix().apply { rotateZ(tipRotation) })

        path.addPath(p2, offset = tipOffset)

        return Outline.Generic(path)
    }
}