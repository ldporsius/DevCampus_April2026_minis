package nl.codingwithlinda.guided_tour.presentation.design_system

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val tipLength = 24f   // how far the tip protrudes from the card edge
        val tipBase   = 36f   // width of the tip's base along the edge

        val path = when (placement) {
            TooltipPlacement.Below -> verticalTipPath(
                size, tipLength, tipBase,
                tipCenterX = tipEdgeFraction * size.width,
                tipAtTop   = true,
            )
            TooltipPlacement.Above -> verticalTipPath(
                size, tipLength, tipBase,
                tipCenterX = tipEdgeFraction * size.width,
                tipAtTop   = false,
            )
            TooltipPlacement.End -> horizontalTipPath(
                size, tipLength, tipBase,
                tipCenterY = tipEdgeFraction * size.height,
                tipAtStart = true,
            )
            TooltipPlacement.Start -> horizontalTipPath(
                size, tipLength, tipBase,
                tipCenterY = tipEdgeFraction * size.height,
                tipAtStart = false,
            )
        }

        return Outline.Generic(path)
    }
}

// ── Path builders ─────────────────────────────────────────────────────────────

/** Card rect + tip pointing up (tipAtTop=true) or down from the rect. */
private fun verticalTipPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterX: Float,
    tipAtTop: Boolean,
): Path = Path().apply {
    // Rectangle occupies full width; tip reserves [tipLength] on one vertical end.
    val rectTop    = if (tipAtTop) tipLength else 0f
    val rectBottom = if (tipAtTop) size.height else size.height - tipLength
    addRoundRect(RoundRect(left = 0f, top = rectTop, right = size.width, bottom = rectBottom, cornerRadius = CornerRadius(50f, 50f)))
    //addRect(Rect(left = 0f, top = rectTop, right = size.width, bottom = rectBottom))

    val apexY = if (tipAtTop) 0f        else size.height
    val baseY = if (tipAtTop) tipLength else size.height - tipLength
    addTriangle(
        a    = Offset(tipCenterX - tipBase / 2f, baseY),
        apex = Offset(tipCenterX,                apexY),
        b    = Offset(tipCenterX + tipBase / 2f, baseY),
    )
}

/** Card rect + tip pointing left (tipAtStart=true) or right from the rect. */
private fun horizontalTipPath(
    size: Size,
    tipLength: Float,
    tipBase: Float,
    tipCenterY: Float,
    tipAtStart: Boolean,
): Path = Path().apply {
    // Rectangle occupies full height; tip reserves [tipLength] on one horizontal side.
    val rectLeft  = if (tipAtStart) tipLength else 0f
    val rectRight = if (tipAtStart) size.width else size.width - tipLength
    addRoundRect(RoundRect(left = rectLeft, top = 0f, right = rectRight, bottom = size.height, cornerRadius = CornerRadius(50f, 50f)))

    val apexX = if (tipAtStart) 0f        else size.width
    val baseX = if (tipAtStart) tipLength else size.width - tipLength
    addTriangle(
        a    = Offset(baseX, tipCenterY - tipBase / 2f),
        apex = Offset(apexX, tipCenterY),
        b    = Offset(baseX, tipCenterY + tipBase / 2f),
    )
}

private fun Path.addTriangle(a: Offset, apex: Offset, b: Offset) {
    moveTo(a.x, a.y)
    lineTo(apex.x, apex.y)
    lineTo(b.x, b.y)
    close()
}