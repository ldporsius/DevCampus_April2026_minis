# Guided Tour — Feature Gist

A first-launch walkthrough for the Task Manager screen. A dialog asks the user
to start or skip; if they start, the screen is dimmed with a scrim and each
target element (search, filter tabs, task list, FAB) is highlighted in turn
with a speech-bubble tooltip. Once finished or skipped, the tour does not run
again until the user explicitly resets it.

The implementation is split into the usual layers — a Koin-wired ViewModel
drives MVI state, DataStore persists the "done" flag, and the overlay is
pure Compose on top of the existing screen (no new Activity, no Dialog
window).

---

## 1. State model

Standard MVI surface. `TourStep` drives both which element is highlighted and
the copy inside the tooltip.

```kotlin
data class TourState(
    val showDialog: Boolean = false,
    val currentStep: TourStep? = null,
)

sealed interface TourAction {
    data object Skip       : TourAction
    data object StartTour  : TourAction
    data object NextStep   : TourAction
    data object Finish     : TourAction
    data object Reset      : TourAction
}

enum class TourStep(val stepNumber: Int, val description: String, val isLast: Boolean) {
    SEARCH    (1, "Use search to quickly find your tasks", false),
    FILTERS   (2, "Filter your tasks by status",           false),
    TASK_LIST (3, "Your tasks will appear here",           false),
    ADD_BUTTON(4, "Tap here to create a new task",         true);

    val total: Int get() = entries.size
}
```

---

## 2. Persistence & lifecycle

Two sources of truth combined into one `StateFlow<TourState>`:

- **`SavedStateHandle`** — the in-progress step index. Survives config changes
  and app backgrounding, but is cleared on process death / swipe-from-recents,
  which is exactly what we want: a partially-completed tour shouldn't resume
  on a cold start — the user sees the dialog again.
- **DataStore (`TourPreferencesDataSource`)** — the `tourDone` boolean. Written
  on Skip and Finish; reset via the hidden `Reset` action (tapping the title).

```kotlin
class TourViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val dataSource: TourPreferencesDataSource,
    private val appScope: CoroutineScope,
) : ViewModel() {

    private val stepFlow: StateFlow<Int> = savedStateHandle.getStateFlow(KEY_STEP, -1)

    val state: StateFlow<TourState> = combine(dataSource.tourDone, stepFlow) { done, step ->
        TourState(
            showDialog  = !done && step == -1,
            currentStep = if (!done && step >= 0) TourStep.entries[step] else null,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TourState())

    fun onAction(action: TourAction) { /* updates SavedStateHandle / DataStore */ }
}
```

`appScope` is used for `Reset`/`Finish` writes so a DataStore write in flight
isn't cancelled if the ViewModel is cleared during the action.

---

## 3. Capturing target bounds

Every highlightable element writes its own rect into the screen's state on
every layout pass — one line per target, no manual coordinate math:

```kotlin
IconButton(
    onClick = { },
    modifier = Modifier.onGloballyPositioned { coords ->
        searchBounds = coords.boundsInRoot()
    },
) { /* … */ }
```

When a `TourStep` becomes active, `TaskManagerScreen` looks up the matching
`Rect?` and hands it to `TourOverlay`. If bounds aren't known yet (first
frame), the overlay simply renders the scrim without a cutout — no crash.

---

## 4. The overlay: scrim + cutout

The overlay is a plain `Canvas` rendering a full-screen scrim, with a
rounded rectangle "punched out" for the currently highlighted element using
`BlendMode.Clear`. The `graphicsLayer { compositingStrategy = Offscreen }`
is required for `Clear` to produce a real hole instead of the scrim's color.

```kotlin
Canvas(
    modifier = Modifier
        .fillMaxSize()
        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
) {
    drawRect(scrimColor)
    if (rootBounds != null && highlightBounds != null) {
        drawHighlightCutout(highlightPadding, highlightEdgeMargin, rootBounds, highlightBounds)
    }
}
```

The cutout enforces a 12.dp minimum margin from the screen edges — but only
on axes where the highlight is already centered on the root. Asymmetric
clamping would move the cutout off the target; keeping the cutout on the
target is more important than the margin, so the margin is skipped on axes
where it would break centering (e.g. the `TabRow` is full-width → margin
applies; the FAB is in the corner → margin skipped).

---

## 5. Adaptive tooltip placement

`computeTooltipLayout` is a pure function (no Compose, no Density) that picks
one of four placements and returns a top-left offset plus a `tipEdgeFraction`
for the speech-bubble tip. Each candidate constrains only one axis; the first
candidate whose constrained axis fits the screen wins.

```kotlin
fun computeTooltipLayout(
    screenWidthPx: Float, screenHeightPx: Float,
    hLeft: Float, hTop: Float, hWidth: Float, hHeight: Float,
    tooltipWidthPx: Float, tooltipHeightPx: Float,
    marginPx: Float, highlightGapPx: Float,
    apexY: Float,
): TooltipLayout

data class TooltipLayout(
    val offset: Offset,
    val placement: TooltipPlacement,   // Above | Below | Start | End
    val tipEdgeFraction: Float,        // 0..1 along the tip-bearing edge
)
```

Priority: `Start → Below → Above → End`, with a clamped `Above` fallback for
pathological cases (tiny screens, oversized tooltips). Because the logic is
pure and driven by `Float`s only, it's trivially unit-testable without a
Compose rule.

---

## 6. Speech-bubble shape

Custom `Shape` with four path builders — one per `TooltipPlacement`. The tip
is positioned via `tipEdgeFraction` (0..1 along the tip-bearing edge). Each
builder clamps the tip so its feet never enter the rounded-corner region,
which removes a visible gap that appeared on small screens when the tip
landed near a corner.

```kotlin
private fun tipPointRightPath(
    size: Size, tipLength: Float, tipBase: Float,
    tipCenterY: Float, cornerRadius: Float,
): Path = Path().apply {
    val r = cornerRadius
    val halfTipBase = tipBase / 2f
    val clampedTipCenterY = tipCenterY.coerceIn(
        r + halfTipBase,
        size.height - r - halfTipBase,
    )
    val tipTop    = clampedTipCenterY - halfTipBase
    val tipBottom = clampedTipCenterY + halfTipBase

    moveTo(0f, r)
    quadraticTo(0f, 0f, r, 0f)                                  // top-left
    lineTo(size.width - r, 0f)
    quadraticTo(size.width, 0f, size.width, r)                  // top-right
    lineTo(size.width, tipTop)
    lineTo(size.width + tipLength, clampedTipCenterY)           // apex
    lineTo(size.width, tipBottom)
    lineTo(size.width, size.height - r)
    quadraticTo(size.width, size.height, size.width - r, size.height)  // bottom-right
    lineTo(r, size.height)
    quadraticTo(0f, size.height, 0f, size.height - r)           // bottom-left
    lineTo(0f, r)
    close()
}
```

A preview file (`SpeechBubbleShapePreview.kt`) renders all four placements
across `tipEdgeFraction` values 0.0 → 1.0 so the corner-edge behaviour is
visible in the IDE's Design view — useful for regression-checking any future
change to the path builders.

---

## 7. File map

```
feature/guided_tour/src/main/java/nl/codingwithlinda/guided_tour/
├── data/
│   └── TourPreferencesDataSource.kt     DataStore: tourDone flag
├── di/
│   └── GuidedTourModule.kt              Koin module
└── presentation/
    ├── TaskManagerScreen.kt             Root + Screen + TourOverlay + StartTourDialog + TourTooltip
    ├── TourViewModel.kt                 MVI ViewModel
    ├── TourState.kt, TourAction.kt, TourStep.kt
    ├── TooltipLayout.kt                 Pure placement function
    └── design_system/
        ├── SpeechBubbleShape.kt         Custom Shape with four path builders
        ├── SpeechBubbleShapePreview.kt  @Preview harness covering all placements
        ├── components/TmButtons.kt      TmButton / TmOutlinedButton — 16.dp rounded corners
        └── theme/                       Color scheme, TaskManagerTheme
```