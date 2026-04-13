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
 * and the tip aligns with the center of the overlap between tooltip and highlight.
 *
 * All coordinates are **overlay-local px** (origin = top-left corner of the overlay).
 *
 * Priority: Below → Above → End (right) → Start (left).
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
): TooltipLayout {
    val hRight  = hLeft + hWidth
    val hBottom = hTop  + hHeight

    // Available space outside the (padded) highlight on each side
    val spaceBelow = screenHeightPx - hBottom - highlightGapPx
    val spaceAbove = hTop - highlightGapPx
    val spaceEnd   = screenWidthPx  - hRight  - highlightGapPx
    val spaceStart = hLeft          - highlightGapPx

    val placement = when {
        spaceStart >= tooltipWidthPx  + marginPx -> TooltipPlacement.Start
        spaceBelow >= tooltipHeightPx + marginPx -> TooltipPlacement.Below
        spaceAbove >= tooltipHeightPx + marginPx -> TooltipPlacement.Above
        spaceEnd   >= tooltipWidthPx  + marginPx -> TooltipPlacement.End
        else                                      -> TooltipPlacement.Start
    }

    return when (placement) {
        TooltipPlacement.Below, TooltipPlacement.Above -> {
            val tooltipY = if (placement == TooltipPlacement.Below) {
                hBottom + highlightGapPx
            } else {
                (hTop - highlightGapPx - tooltipHeightPx).coerceAtLeast(marginPx)
            }
            val tooltipX = (hLeft + hWidth / 2f - tooltipWidthPx / 2f)
                .coerceIn(marginPx, screenWidthPx - tooltipWidthPx - marginPx)

            TooltipLayout(
                offset          = Offset(tooltipX, tooltipY),
                placement       = placement,
                tipEdgeFraction = xOverlapFraction(hLeft, hRight, tooltipX, tooltipWidthPx),
            )
        }

        TooltipPlacement.End, TooltipPlacement.Start -> {
            val tooltipX = if (placement == TooltipPlacement.End) {
                hRight + highlightGapPx
            } else {
                (hLeft - highlightGapPx - tooltipWidthPx).coerceAtLeast(marginPx)
            }
            val tooltipY = (hTop + hHeight / 2f - tooltipHeightPx / 2f)
                .coerceIn(marginPx, screenHeightPx - tooltipHeightPx - marginPx)

            TooltipLayout(
                offset          = Offset(tooltipX, tooltipY),
                placement       = placement,
                tipEdgeFraction = yOverlapFraction(hTop, hBottom, tooltipY, tooltipHeightPx),
            )
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

/** 0..1 fraction where the X-overlap center sits within the tooltip's width. */
private fun xOverlapFraction(
    hLeft: Float, hRight: Float,
    tooltipX: Float, tooltipWidth: Float,
): Float {
    val overlapCenter = (maxOf(hLeft, tooltipX) + minOf(hRight, tooltipX + tooltipWidth)) / 2f
    return ((overlapCenter - tooltipX) / tooltipWidth).coerceIn(0f, 1f)
}

/** 0..1 fraction where the Y-overlap center sits within the tooltip's height. */
private fun yOverlapFraction(
    hTop: Float, hBottom: Float,
    tooltipY: Float, tooltipHeight: Float,
): Float {
    val overlapCenter = (maxOf(hTop, tooltipY) + minOf(hBottom, tooltipY + tooltipHeight)) / 2f
    return ((overlapCenter - tooltipY) / tooltipHeight).coerceIn(0f, 1f)
}