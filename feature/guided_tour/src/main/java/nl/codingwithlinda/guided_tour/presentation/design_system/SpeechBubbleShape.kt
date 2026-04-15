package nl.codingwithlinda.guided_tour.presentation.design_system

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import nl.codingwithlinda.guided_tour.presentation.TooltipPlacement

/**
 * A card shape with a triangular speech-bubble tip on one edge.
 *
 * The tip always points toward the highlighted element.
 * Which edge carries the tip is determined by [placement]:
 *
 *  [TooltipPlacement.Below]  → tip on the **top**   edge  (tooltip is below the highlight)
 *  [TooltipPlacement.Above]  → tip on the **bottom** edge  (tooltip is above the highlight)
 *  [TooltipPlacement.End]    → tip on the **start**  edge  (tooltip is to the right)
 *  [TooltipPlacement.Start]  → tip on the **end**    edge  (tooltip is to the left)
 *
 * @param placement       Which edge carries the tip.
 * @param tipEdgeFraction 0..1 — where the tip center sits along that edge.
 */
class SpeechBubbleShape(
    private val placement: TooltipPlacement,
    private val tipEdgeFraction: Float = 0.5f,
    val tipLength: Float,  // how far the tip protrudes from the card edge
    val tipBase: Float ,  // width of the tip's base along the edge
    val cornerRadius: Float

) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {

        val path = when (placement) {
            TooltipPlacement.Below -> topTipPath(
                size, tipLength, tipBase,
                tipCenterX = tipEdgeFraction * size.width,
            )
            TooltipPlacement.Above -> bottomTipPath(
                size, tipLength, tipBase,
                tipCenterX = tipEdgeFraction * size.width,
            )
            TooltipPlacement.End -> tipPointLeftPath(
                size, tipLength, tipBase,
                tipCenterY = tipEdgeFraction * size.height,
                tipAtStart = true,
            )
            TooltipPlacement.Start -> tipPointRightPath(
                size, tipLength, tipBase,
                tipCenterY = tipEdgeFraction * size.height,
                cornerRadius = cornerRadius
            )
        }

        return Outline.Generic(path)
    }
}

// ── Path builders ─────────────────────────────────────────────────────────────

/** Single path: rounded rect with tip pointing down, all corners via quadraticBezierTo. */
private fun bottomTipPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterX: Float,
): Path = Path().apply {
    val r          = 50f
    val rectTop    = 0f
    val rectBottom = size.height
    val apexY      = size.height + tipLength
    val tipLeft    = tipCenterX - tipBase / 2
    val tipRight   = tipCenterX + tipBase / 2

    // Start just below the top-left corner
    moveTo(0f, rectTop + r)

    // Top-left corner
    quadraticTo(0f, rectTop, r, rectTop)

    // Top-right corner
    lineTo(size.width - r, rectTop)
    quadraticTo(size.width, rectTop, size.width, rectTop + r)

    // Bottom-right corner
    lineTo(size.width, rectBottom - r)
    quadraticTo(size.width, rectBottom, tipRight, rectBottom)

    // Right foot → apex
    quadraticTo(tipCenterX , rectBottom, tipCenterX, apexY)
    // Apex → left foot
    quadraticTo(tipCenterX , rectBottom, tipLeft, rectBottom)


    // Bottom-left corner
    lineTo(r, rectBottom)
    quadraticTo(0f, rectBottom, 0f, rectBottom - r)

    close()
}

private fun topTipPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterX: Float,
): Path = Path().apply {
    val r          = 50f
    val rectTop    = 0f
    val rectBottom = size.height
    val apexY      = -tipLength
    val tipLeft    = tipCenterX - tipBase / 2f
    val tipRight   = tipCenterX + tipBase / 2f

    // Start just below the top-left corner
    moveTo(0f, rectTop + r)

    // Top-left corner
    quadraticTo(0f, rectTop, r, rectTop)

    lineTo(tipLeft, rectTop)

    // Left foot → apex (control at base-centre pulls the curve smooth)
    quadraticTo(tipCenterX, rectTop, tipCenterX, apexY)
    // Apex → right foot
    quadraticTo(tipCenterX, rectTop, tipRight, rectTop)


    // Top-right corner
    lineTo(size.width - r, rectTop)
    quadraticTo(size.width, rectTop, size.width, rectTop + r)

    // Bottom-right corner
    lineTo(size.width, rectBottom - r)
    quadraticTo(size.width, rectBottom, size.width - r, rectBottom)

    // Bottom-left corner
    lineTo(r, rectBottom)
    quadraticTo(0f, rectBottom, 0f, rectBottom - r)

    close()
}

/** Card rect + tip pointing left from the rect. */
private fun tipPointLeftPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterY: Float,
    tipAtStart: Boolean,
): Path = Path().apply {
    val rectRight = if (tipAtStart) size.width else size.width
    addRoundRect(RoundRect(left = 0f, top = 0f, right = rectRight, bottom = size.height, cornerRadius = CornerRadius(50f, 50f)))

    val apexX = if (tipAtStart) 0f        else size.width + tipLength
    val baseX = if (tipAtStart) tipLength else size.width
    addTriangle(
        a    = Offset(baseX, tipCenterY - tipBase / 2f),
        apex = Offset(apexX, tipCenterY),
        b    = Offset(baseX, tipCenterY + tipBase / 2f),
    )
}

/** Card rect + tip pointing right from the rect. */
private fun tipPointRightPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterY: Float,
    cornerRadius: Float,
): Path = Path().apply {
    val r = cornerRadius
    val halfTipBase = tipBase / 2f
    val clampedTipCenterY = tipCenterY.coerceIn(r+halfTipBase, size.height - r - halfTipBase)

    val tipTop    = (clampedTipCenterY - tipBase / 2f)
    val tipBottom = (clampedTipCenterY + tipBase / 2f)

    moveTo(0f, r)
    // top-left corner
    quadraticTo(0f, 0f, r, 0f)
    lineTo(size.width - r, 0f)
    quadraticTo(size.width , 0f, size.width, r)
    lineTo(size.width, tipTop)

    lineTo(size.width+tipLength, clampedTipCenterY)
    lineTo(size.width, tipBottom)
    lineTo(size.width, size.height-r)
    quadraticTo(size.width, size.height, size.width-r, size.height)

    lineTo(r, size.height)
    // bottom-left corner
    quadraticTo(0f, size.height, 0f, size.height - r)
    lineTo(0f, r)

    close()
}

private fun Path.addTriangle(a: Offset, apex: Offset, b: Offset) {
    moveTo(a.x, a.y)
    lineTo(apex.x, apex.y)
    lineTo(b.x, b.y)
    close()
}