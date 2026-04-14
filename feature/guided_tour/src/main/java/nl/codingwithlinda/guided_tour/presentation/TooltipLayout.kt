package nl.codingwithlinda.guided_tour.presentation

import androidx.compose.ui.geometry.Offset

// ── Placement ─────────────────────────────────────────────────────────────────

/**
 * Where the tooltip box is placed relative to the highlighted element.
 * The speech-bubble tip always points back toward the highlight.
 *
 *  [Below]  → tooltip is below  the highlight, tip on its top edge
 *  [Above]  → tooltip is above  the highlight, tip on its bottom edge
 *  [End]    → tooltip is to the right, tip on its start (left) edge
 *  [Start]  → tooltip is to the left,  tip on its end (right) edge
 */
enum class TooltipPlacement { Above, Below, Start, End }

// ── Layout result ─────────────────────────────────────────────────────────────

/**
 * Everything [TourOverlay] needs to position the tooltip and point its tip.
 *
 * @param offset          Top-left corner of the tooltip box in overlay-local px.
 * @param placement       Which side of the highlight the tooltip occupies.
 * @param tipEdgeFraction 0..1 — where the tip center sits along the edge it appears on.
 */
data class TooltipLayout(
    val offset: Offset,
    val placement: TooltipPlacement,
    val tipEdgeFraction: Float,
)

// ── Pure layout function ───────────────────────────────────────────────────────

/**
 * Picks the best placement and computes the tooltip position so it stays on screen
 * and the tip aligns with the center of the highlighted element.
 *
 * All coordinates are **overlay-local px** (origin = top-left corner of the overlay).
 *
 * Each placement candidate clamps only the axis that is free to move, then checks
 * the single axis that is truly constrained for that direction.  The first candidate
 * whose constrained axis fits wins — no cascading conditions, no safety guard needed.
 *
 * Priority: Start → Below → Above → End → fallback Above (clamped).
 *
 * @param marginPx       Minimum distance the tooltip must keep from any screen edge.
 * @param highlightGapPx Gap between the highlight's edge and the nearest tooltip edge.
 */
fun computeTooltipLayout(
    screenWidthPx: Float,
    screenHeightPx: Float,
    hLeft: Float,
    hTop: Float,
    hWidth: Float,
    hHeight: Float,
    tooltipWidthPx: Float,
    tooltipHeightPx: Float,
    marginPx: Float,
    highlightGapPx: Float,
    apexY: Float,
): TooltipLayout {
    val hRight   = hLeft  + hWidth
    val hBottom  = hTop   + hHeight
    val hCenterX = hLeft  + hWidth  / 2f
    val hCenterY = hTop   + hHeight / 2f

    // ── Helpers ────────────────────────────────────────────────────────────────

    /** X that horizontally centres the tooltip on the highlight, clamped to screen. */
    fun clampedCenterX() = (hCenterX - tooltipWidthPx / 2f)
        .coerceIn(marginPx, screenWidthPx - tooltipWidthPx - marginPx)

    /** Y that vertically centres the tooltip on the highlight, clamped to screen. */
    fun clampedCenterY() = (hCenterY - tooltipHeightPx / 2f)
        .coerceIn(marginPx, screenHeightPx - tooltipHeightPx - marginPx)

    /** Tip fraction when the tooltip sits left or right of the highlight (vertical edge). */
    fun vTipFraction(tooltipY: Float) = ((hCenterY - tooltipY) / tooltipHeightPx).coerceIn(0f, 1f)

    /** Tip fraction when the tooltip sits above or below the highlight (horizontal edge). */
    fun hTipFraction(tooltipX: Float) = ((hCenterX - tooltipX) / tooltipWidthPx).coerceIn(0f, 1f)

    // ── Candidate list ─────────────────────────────────────────────────────────
    //
    // Rule: each placement constrains ONLY ONE axis.
    //   • Start / End  → constrained axis is X;  Y is free (clamped).
    //   • Above / Below → constrained axis is Y;  X is free (clamped).
    //
    // The candidate returns a TooltipLayout when its constrained axis fits,
    // or null when it does not.  Order of the list = placement priority.

    val candidates: List<() -> TooltipLayout?> = listOf(

        // Start — tooltip to the LEFT of the highlight, tip on its right edge.
        // Only viable when hCenterY is inside the usable screen; otherwise the
        // clamped y would push the tip to an extreme fraction (0 or 1).
        {
            val x = hLeft - highlightGapPx - tooltipWidthPx - marginPx - apexY
            val y = clampedCenterY()
            if (x >= marginPx && y in marginPx..(screenHeightPx - tooltipHeightPx))
                TooltipLayout(Offset(x, y), TooltipPlacement.Start, vTipFraction(y))
            else null
        },

        // Below — tooltip BELOW the highlight, tip on its top edge
        {
            val x = clampedCenterX()
            val y = hBottom + highlightGapPx
            if (y + tooltipHeightPx <= screenHeightPx - marginPx)
                TooltipLayout(Offset(x, y), TooltipPlacement.Below, hTipFraction(x))
            else null
        },

        // Above — tooltip ABOVE the highlight, tip on its bottom edge
        {
            val x = clampedCenterX()
            val y = hTop - highlightGapPx - tooltipHeightPx - marginPx
            if (y >= marginPx)
                TooltipLayout(Offset(x, y), TooltipPlacement.Above, hTipFraction(x))
            else null
        },

        // End — tooltip to the RIGHT of the highlight, tip on its left edge.
        // Same vertical-center guard as Start.
        {
            val x = hRight + highlightGapPx
            val y = clampedCenterY()
            if (x + tooltipWidthPx <= screenWidthPx - marginPx && hCenterY in marginPx..(screenHeightPx - marginPx))
                TooltipLayout(Offset(x, y), TooltipPlacement.End, vTipFraction(y))
            else null
        },
    )

    // ── Selection ──────────────────────────────────────────────────────────────

    // First candidate whose constrained axis fits wins.
    // Fallback: Above, clamped — only reached when every side has less space than
    // the tooltip needs (e.g. a very small screen or an unusually large tooltip).

    println("--- TOOLTIP LAYOUT --- clampedCenterX() = ${clampedCenterX()}, clampedCenterY() = ${clampedCenterY()}, hCenterX = $hCenterX, hCenterY = $hCenterY")
    println("--- TOOLTIP LAYOUT --- hLeft = $hLeft, hTop = $hTop, hWidth = $hWidth, hHeight = $hHeight")
    println("--- TOOLTIP LAYOUT --- tooltipWidthPx = $tooltipWidthPx, tooltipHeightPx = $tooltipHeightPx")
    println("--- TOOLTIP LAYOUT --- marginPx = $marginPx, highlightGapPx = $highlightGapPx, screenWidthPx = $screenWidthPx, screenHeightPx = $screenHeightPx")
    println("--- TOOLTIP LAYOUT --- hTipFraction(x) = ${hTipFraction(clampedCenterX())}, vTipFraction(y) = ${vTipFraction(clampedCenterY())}")

    val result = candidates.firstNotNullOfOrNull { it() } ?: run {
        val x = clampedCenterX()
        val y = (hTop - highlightGapPx - tooltipHeightPx).coerceAtLeast(marginPx)
        TooltipLayout(Offset(x, y), TooltipPlacement.Above, hTipFraction(x))
    }
    println("--- TOOLTIP LAYOUT RESULT --- placement=${result.placement}, offset=${result.offset}, tipFraction=${result.tipEdgeFraction}")
    return result
}