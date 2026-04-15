package nl.codingwithlinda.guided_tour.presentation.design_system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.codingwithlinda.guided_tour.presentation.TooltipPlacement
import nl.codingwithlinda.guided_tour.presentation.design_system.theme.TaskManagerTheme

/**
 * Preview harness for [SpeechBubbleShape] — renders all four [TooltipPlacement] cases,
 * each with a few [tipEdgeFraction] values including the edge-of-corner case that used
 * to produce a gap.
 */
@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun SpeechBubbleShapeGalleryPreview() {
    TaskManagerTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                PlacementSection(
                    title = "Below (tip on top edge)",
                    placement = TooltipPlacement.Below,
                )
                PlacementSection(
                    title = "Above (tip on bottom edge)",
                    placement = TooltipPlacement.Above,
                )
                PlacementSection(
                    title = "End (tip on start edge)",
                    placement = TooltipPlacement.End,
                )
                PlacementSection(
                    title = "Start (tip on end edge)",
                    placement = TooltipPlacement.Start,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 600, name = "Small screen — Start placement corner-merge")
@Composable
private fun SpeechBubbleSmallScreenPreview() {
    TaskManagerTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Start placement — fractions 0.0, 0.05, 0.5, 0.95, 1.0")
                listOf(0f, 0.05f, 0.5f, 0.95f, 1f).forEach { frac ->
                    Bubble(
                        placement = TooltipPlacement.Start,
                        tipEdgeFraction = frac,
                        label = "frac=$frac",
                    )
                }
            }
        }
    }
}

@Composable
private fun PlacementSection(
    title: String,
    placement: TooltipPlacement,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            listOf(0.1f, 0.5f, 0.9f).forEach { frac ->
                Box(modifier = Modifier.weight(1f)) {
                    Bubble(
                        placement = placement,
                        tipEdgeFraction = frac,
                        label = "$frac",
                    )
                }
            }
        }
    }
}

@Composable
private fun Bubble(
    placement: TooltipPlacement,
    tipEdgeFraction: Float,
    label: String,
) {
    val density = LocalDensity.current
    val shape: Shape = with(density) {
        SpeechBubbleShape(
            placement = placement,
            tipEdgeFraction = tipEdgeFraction,
            tipLength = 16.dp.toPx(),
            tipBase = 24.dp.toPx(),
            cornerRadius = 16.dp.toPx(),
        )
    }

    // Extra outer padding so the tip (which overflows the card bounds) is visible.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = if (placement == TooltipPlacement.End) 20.dp else 0.dp,
                end = if (placement == TooltipPlacement.Start) 20.dp else 0.dp,
                top = if (placement == TooltipPlacement.Below) 20.dp else 0.dp,
                bottom = if (placement == TooltipPlacement.Above) 20.dp else 0.dp,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .background(MaterialTheme.colorScheme.surface, shape),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}