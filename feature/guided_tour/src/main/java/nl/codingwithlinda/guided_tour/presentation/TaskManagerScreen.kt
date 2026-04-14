package nl.codingwithlinda.guided_tour.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.codingwithlinda.guided_tour.presentation.design_system.SpeechBubbleShape
import nl.codingwithlinda.guided_tour.presentation.design_system.theme.TaskManagerTheme
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

    val filters = listOf("All", "Active", "Completed")
    var selectedFilterIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                rootBounds = it.boundsInRoot()
                println("--- TASK MANAGER SCREEN --- ROOT BOUNDS 1 --- $rootBounds")
            }
        ,
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
            ,
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "My Tasks",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier.clickable{
                                onAction(TourAction.Reset)
                            }
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
                    .consumeWindowInsets(innerPadding)
                ,
            ) {
                // Filter tabs
                TabRow(
                    selectedTabIndex = selectedFilterIndex,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            filterRowBounds = coords.boundsInRoot()
                        },
                ) {
                    filters.forEachIndexed { index, label ->
                        Tab(
                            selected = selectedFilterIndex == index,
                            onClick = { selectedFilterIndex = index },
                            text = { Text(label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground)
                            },
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
                    ,
                    contentAlignment = Alignment.TopCenter,
                ) {
                    Column(
                        modifier = Modifier
                            .onGloballyPositioned { coords ->
                                taskListBounds = coords.boundsInRoot()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
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
                modifier = Modifier.align(Alignment.BottomCenter)
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

// ── Start tour dialog ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartTourDialog(
    onSkip: () -> Unit,
    onStartTour: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val containerHeight = with(density) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val isCompactHeight = containerHeight < 480.dp
    val sizeModifier = if (isCompactHeight) Modifier.fillMaxSize() else Modifier.fillMaxWidth()


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.15f))
        ,
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = sizeModifier.align(Alignment.BottomCenter)
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
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        modifier = Modifier.weight(1f)
                    ) {
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
                        modifier = Modifier.weight(1f)
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
    val highlightPadding = 80.dp

    BoxWithConstraints(modifier = Modifier
        .fillMaxSize()
        .drawWithContent(){
            drawRect(scrimColor)

            if (rootBounds != null && highlightBounds != null) {
                drawHighlightCutout(highlightPadding, rootBounds, highlightBounds)
            }
            drawContent()
        }
        
    ) {
        val density = LocalDensity.current

       /* androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {

        }*/

        if (rootBounds != null && highlightBounds != null) {
            val tooltipWidthPx  = with(density) { 240.dp.toPx() }
            val tooltipHeightPx = with(density) { 140.dp.toPx() }

            val navBarPx = WindowInsets.navigationBars.getBottom(density).toFloat()
            val sysBarPx = WindowInsets.systemBars.getTop(density).toFloat()

            val tipLength = with(density) { 16.dp.toPx() }
            val tipBase   = with(density) { 24.dp.toPx() }
            val cornerRadius = with(density) { 12.dp.toPx() }

            val layout = computeTooltipLayout(
                screenWidthPx   = with(density) { maxWidth.toPx() },
                screenHeightPx  = with(density) { maxHeight.toPx()},
                hLeft           = highlightBounds.left - rootBounds.left,
                hTop            = with(density){
                    highlightBounds.top.toDp().toPx()  - rootBounds.top.toDp().toPx()
                                               },
                hWidth          = highlightBounds.width,
                hHeight         = with(density){highlightBounds.height},
                tooltipWidthPx  = tooltipWidthPx,
                tooltipHeightPx = tooltipHeightPx ,
                marginPx        = with(density) { 12.dp.toPx() },
                highlightGapPx  = with(density) { highlightPadding.toPx() },
                apexY           = tipLength,
            )

            Box(
                modifier = Modifier
                    .offset { IntOffset(layout.offset.x.toInt(), layout.offset.y.toInt()) }
                    .width(with(density) { tooltipWidthPx.toDp()})

            ) {
                TourTooltip(
                    step            = step,
                    onAction        = onAction,
                    placement       = layout.placement,
                    tipEdgeFraction = layout.tipEdgeFraction,
                    tipLength       = tipLength,
                    tipBase         = tipBase,
                    cornerRadius    = cornerRadius,
                )
            }
        }
    }
}

private fun DrawScope.drawHighlightCutout(
    padding: Dp,
    rootBounds: Rect,
    highlightBounds: Rect,
) {
    val padding = padding.toPx()
    val left   = highlightBounds.left  - rootBounds.left - padding
    val top    = highlightBounds.top   - rootBounds.top  - padding
    val width  = highlightBounds.width  + padding * 2
    val height = highlightBounds.height + padding * 2

    drawRoundRect(
        color = Color.White,
        alpha = .5f,
        topLeft = Offset(left, top),
        size = androidx.compose.ui.geometry.Size(width, height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
        //blendMode = BlendMode.Clear,
    )
}

// ── Tooltip ──────────────────────────────────────────────────────────────────

@Composable
private fun TourTooltip(
    step: TourStep,
    onAction: (TourAction) -> Unit,
    placement: TooltipPlacement,
    tipEdgeFraction: Float,
    tipLength: Float,
    tipBase: Float,
    cornerRadius: Float,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = SpeechBubbleShape(
            placement       = placement,
            tipEdgeFraction = tipEdgeFraction,
            tipLength = tipLength,
            tipBase = tipBase,
            cornerRadius
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                val buttonColors = if (step.isLast) ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                )
                else{
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Button(
                    onClick = {
                        onAction(if (step.isLast) TourAction.Finish else TourAction.NextStep)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = buttonColors,
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
private fun TaskManagerScreenPreviewSearch() {
    TaskManagerTheme {
        TaskManagerScreen(
            state = TourState(showDialog = false, currentStep = TourStep.SEARCH),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskManagerScreenPreviewFilters() {
    TaskManagerTheme {
        TaskManagerScreen(
            state = TourState(showDialog = false, currentStep = TourStep.FILTERS),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskManagerScreenPreviewTaskList() {
    TaskManagerTheme {
        TaskManagerScreen(
            state = TourState(showDialog = false, currentStep = TourStep.TASK_LIST),
            onAction = {},
        )
    }
}


@Preview(showBackground = true, showSystemUi = true,
    device = "spec:parent=pixel_5,navigation=buttons"
)
@Composable
private fun TaskManagerScreenPreviewAddButton() {
    TaskManagerTheme {
        TaskManagerScreen(
            state = TourState(showDialog = false, currentStep = TourStep.ADD_BUTTON),
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