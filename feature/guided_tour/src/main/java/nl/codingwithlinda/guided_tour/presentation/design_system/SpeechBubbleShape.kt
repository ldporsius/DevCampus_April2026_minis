package nl.codingwithlinda.guided_tour.presentation.design_system

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
                cornerRadius = cornerRadius,
            )
            TooltipPlacement.Above -> bottomTipPath(
                size, tipLength, tipBase,
                tipCenterX = tipEdgeFraction * size.width,
                cornerRadius = cornerRadius,
            )
            TooltipPlacement.End -> tipPointLeftPath(
                size, tipLength, tipBase,
                tipCenterY = tipEdgeFraction * size.height,
                cornerRadius = cornerRadius,
            )
            TooltipPlacement.Start -> tipPointRightPath(
                size, tipLength, tipBase,
                tipCenterY = tipEdgeFraction * size.height,
                cornerRadius = cornerRadius,
            )
        }

        return Outline.Generic(path)
    }
}

// ── Path builders ─────────────────────────────────────────────────────────────

/** Card rect + tip pointing down from the bottom edge. */
private fun bottomTipPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterX: Float,
    cornerRadius: Float,
): Path = Path().apply {
    val r = cornerRadius
    val halfTipBase = tipBase / 2f
    val clampedTipCenterX = tipCenterX.coerceIn(r + halfTipBase, size.width - r - halfTipBase)

    val tipLeft  = clampedTipCenterX - halfTipBase
    val tipRight = clampedTipCenterX + halfTipBase

    moveTo(0f, r)
    // top-left corner
    quadraticTo(0f, 0f, r, 0f)
    lineTo(size.width - r, 0f)
    // top-right corner
    quadraticTo(size.width, 0f, size.width, r)
    lineTo(size.width, size.height - r)
    // bottom-right corner
    quadraticTo(size.width, size.height, size.width - r, size.height)

    // bottom edge → tip
    lineTo(tipRight, size.height)
    lineTo(clampedTipCenterX, size.height + tipLength)
    lineTo(tipLeft, size.height)

    lineTo(r, size.height)
    // bottom-left corner
    quadraticTo(0f, size.height, 0f, size.height - r)
    lineTo(0f, r)

    close()
}

/** Card rect + tip pointing up from the top edge. */
private fun topTipPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterX: Float,
    cornerRadius: Float,
): Path = Path().apply {
    val r = cornerRadius
    val halfTipBase = tipBase / 2f
    val clampedTipCenterX = tipCenterX.coerceIn(r + halfTipBase, size.width - r - halfTipBase)

    val tipLeft  = clampedTipCenterX - halfTipBase
    val tipRight = clampedTipCenterX + halfTipBase

    moveTo(0f, r)
    // top-left corner
    quadraticTo(0f, 0f, r, 0f)

    // top edge → tip
    lineTo(tipLeft, 0f)
    lineTo(clampedTipCenterX, -tipLength)
    lineTo(tipRight, 0f)

    lineTo(size.width - r, 0f)
    // top-right corner
    quadraticTo(size.width, 0f, size.width, r)
    lineTo(size.width, size.height - r)
    // bottom-right corner
    quadraticTo(size.width, size.height, size.width - r, size.height)
    lineTo(r, size.height)
    // bottom-left corner
    quadraticTo(0f, size.height, 0f, size.height - r)
    lineTo(0f, r)

    close()
}

/** Card rect + tip pointing left from the left edge. */
private fun tipPointLeftPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterY: Float,
    cornerRadius: Float,
): Path = Path().apply {
    val r = cornerRadius
    val halfTipBase = tipBase / 2f
    val clampedTipCenterY = tipCenterY.coerceIn(r + halfTipBase, size.height - r - halfTipBase)

    val tipTop    = clampedTipCenterY - halfTipBase
    val tipBottom = clampedTipCenterY + halfTipBase

    moveTo(r, 0f)
    lineTo(size.width - r, 0f)
    // top-right corner
    quadraticTo(size.width, 0f, size.width, r)
    lineTo(size.width, size.height - r)
    // bottom-right corner
    quadraticTo(size.width, size.height, size.width - r, size.height)
    lineTo(r, size.height)
    // bottom-left corner
    quadraticTo(0f, size.height, 0f, size.height - r)

    // left edge → tip
    lineTo(0f, tipBottom)
    lineTo(-tipLength, clampedTipCenterY)
    lineTo(0f, tipTop)

    lineTo(0f, r)
    // top-left corner
    quadraticTo(0f, 0f, r, 0f)

    close()
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
