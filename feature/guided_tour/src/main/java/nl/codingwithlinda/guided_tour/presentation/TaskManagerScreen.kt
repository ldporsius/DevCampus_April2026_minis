package nl.codingwithlinda.guided_tour.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.codingwithlinda.guided_tour.presentation.theme.TaskManagerTheme
import org.koin.androidx.compose.koinViewModel

// ── Root ─────────────────────────────────────────────────────────────────────

@Composable
fun TaskManagerRoot(
    viewModel: TourViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TaskManagerTheme {
        TaskManagerScreen(
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

// ── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(
    state: TourState,
    onAction: (TourAction) -> Unit,
) {
    var rootBounds      by remember { mutableStateOf<Rect?>(null) }
    var searchBounds    by remember { mutableStateOf<Rect?>(null) }
    var filterRowBounds by remember { mutableStateOf<Rect?>(null) }
    var taskListBounds  by remember { mutableStateOf<Rect?>(null) }
    var fabBounds       by remember { mutableStateOf<Rect?>(null) }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Completed")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootBounds = it.boundsInRoot() },
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "My Tasks",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.onGloballyPositioned { coords ->
                                searchBounds = coords.boundsInRoot()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {},
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.onGloballyPositioned { coords ->
                        fabBounds = coords.boundsInRoot()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add task",
                    )
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                // Filter row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .onGloballyPositioned { coords ->
                            filterRowBounds = coords.boundsInRoot()
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    filters.forEach { filter ->
                        FilterChip(
                            label = filter,
                            selected = filter == selectedFilter,
                            onClick = { selectedFilter = filter },
                        )
                    }
                }

                // Task list area
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .onGloballyPositioned { coords ->
                            taskListBounds = coords.boundsInRoot()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No tasks yet",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the + button to create your first task",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        // Start tour dialog
        if (state.showDialog) {
            StartTourDialog(
                onSkip = { onAction(TourAction.Skip) },
                onStartTour = { onAction(TourAction.StartTour) },
            )
        }

        // Tutorial overlay
        val step = state.currentStep
        if (step != null) {
            val highlightBounds = when (step) {
                TourStep.SEARCH     -> searchBounds
                TourStep.FILTERS    -> filterRowBounds
                TourStep.TASK_LIST  -> taskListBounds
                TourStep.ADD_BUTTON -> fabBounds
            }
            TourOverlay(
                step = step,
                rootBounds = rootBounds,
                highlightBounds = highlightBounds,
                onAction = onAction,
            )
        }
    }
}

// ── Filter chip ──────────────────────────────────────────────────────────────

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg   = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val text = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

// ── Start tour dialog ────────────────────────────────────────────────────────

@Composable
private fun StartTourDialog(
    onSkip: () -> Unit,
    onStartTour: () -> Unit,
) {
    Dialog(
        onDismissRequest = { /* blocked intentionally */ },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Take a quick tour",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Text(
                    text = "Learn how to manage your tasks in just a few steps",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onSkip) {
                        Text(
                            text = "Skip",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onStartTour,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(text = "Start Tour")
                    }
                }
            }
        }
    }
}

// ── Tutorial overlay ─────────────────────────────────────────────────────────

@Composable
private fun TourOverlay(
    step: TourStep,
    rootBounds: Rect?,
    highlightBounds: Rect?,
    onAction: (TourAction) -> Unit,
) {
    val scrimColor = MaterialTheme.colorScheme.scrim

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            drawRect(scrimColor)
            if (rootBounds != null && highlightBounds != null) {
                drawHighlightCutout(rootBounds, highlightBounds)
            }
        }

        if (rootBounds != null && highlightBounds != null) {
            val screenWidthPx  = with(density) { maxWidth.toPx() }
            val screenHeightPx = with(density) { maxHeight.toPx() }

            val hLeft   = highlightBounds.left   - rootBounds.left
            val hTop    = highlightBounds.top     - rootBounds.top
            val hWidth  = highlightBounds.width
            val hHeight = highlightBounds.height

            val tooltipWidthPx     = with(density) { 260.dp.toPx() }
            val tooltipHeightPx    = with(density) { 140.dp.toPx() }
            val marginPx           = with(density) { 12.dp.toPx() }
            val highlightPaddingPx = with(density) { 8.dp.toPx() }

            val elementBottom = hTop + hHeight + highlightPaddingPx
            val spaceBelow = screenHeightPx - elementBottom

            val tooltipY = if (spaceBelow >= tooltipHeightPx + marginPx) {
                elementBottom + marginPx
            } else {
                (hTop - highlightPaddingPx - marginPx - tooltipHeightPx).coerceAtLeast(marginPx)
            }

            val elementCenterX = hLeft + hWidth / 2f
            val tooltipX = (elementCenterX - tooltipWidthPx / 2f)
                .coerceIn(marginPx, screenWidthPx - tooltipWidthPx - marginPx)

            Box(
                modifier = Modifier
                    .offset { IntOffset(tooltipX.toInt(), tooltipY.toInt()) }
                    .width(with(density) { tooltipWidthPx.toDp() }),
            ) {
                TourTooltip(step = step, onAction = onAction)
            }
        }
    }
}

private fun DrawScope.drawHighlightCutout(
    rootBounds: Rect,
    highlightBounds: Rect,
) {
    val padding = 8.dp.toPx()
    val left   = highlightBounds.left  - rootBounds.left - padding
    val top    = highlightBounds.top   - rootBounds.top  - padding
    val width  = highlightBounds.width  + padding * 2
    val height = highlightBounds.height + padding * 2

    drawRoundRect(
        color = Color.Transparent,
        topLeft = androidx.compose.ui.geometry.Offset(left, top),
        size = androidx.compose.ui.geometry.Size(width, height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
        blendMode = BlendMode.Clear,
    )
}

// ── Tooltip ──────────────────────────────────────────────────────────────────

@Composable
private fun TourTooltip(
    step: TourStep,
    onAction: (TourAction) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Step ${step.stepNumber}/${step.total}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = step.description,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    onClick = {
                        onAction(if (step.isLast) TourAction.Finish else TourAction.NextStep)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(text = if (step.isLast) "Finish" else "Next")
                }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun TaskManagerScreenPreview() {
    TaskManagerTheme {
        TaskManagerScreen(
            state = TourState(showDialog = false, currentStep = null),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StartTourDialogPreview() {
    TaskManagerTheme {
        StartTourDialog(onSkip = {}, onStartTour = {})
    }
}