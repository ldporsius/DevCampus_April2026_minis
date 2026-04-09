package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation.theme.ReadyToTypeTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReadyToTypeRoot(
    modifier: Modifier = Modifier,
    viewModel: ReadyToTypeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReadyToTypeScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun ReadyToTypeScreen(
    state: ReadyToTypeState,
    onAction: (ReadyToTypeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequesters = remember { List(4) { FocusRequester() } }
    val pins = listOf(state.pin1, state.pin2, state.pin3, state.pin4)

    LaunchedEffect(true) {
        focusRequesters[0].requestFocus()
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Secure Vault",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Text(
                text = "Unlock your vault using your 4-digit PIN",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )


            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pins.forEachIndexed { index, digit ->
                    PinInputCell(
                        digit = digit,
                        focusRequester = focusRequesters[index],
                        onDigitEntered = { enteredDigit ->
                            onAction(ReadyToTypeAction.OnPinDigitEntered(index + 1, enteredDigit))
                            if (index < 3) focusRequesters[index + 1].requestFocus()
                        },
                        onDeletePress = {
                            println("deleting digit: $digit at index: $index")
                            when(digit){
                                null -> {
                                    if (index > 0) focusRequesters[index - 1].requestFocus()
                                }
                                else -> {
                                    onAction(ReadyToTypeAction.OnPinDigitEntered(index + 1, null))
                                }
                            }

                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PinActionArea(
                pinStatus = state.pinStatus,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun PinActionArea(
    pinStatus: PinStatus,
    onAction: (ReadyToTypeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (pinStatus) {
        PinStatus.Idle -> TextButton(
            onClick = { onAction(ReadyToTypeAction.OnConfirmPin) },
            modifier = modifier,
        ) {
            Text(text = "Enter pin")
        }
        PinStatus.Unlocked -> Text(
            text = "Unlocked!",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier,
        )
        PinStatus.WrongPin -> Text(
            text = "Wrong PIN, try again",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier,
        )
    }
}

// A regular space kept as the first character so every IME has a real character to
// delete, making backspace reliable even when no digit has been entered yet.
// VisualTransformation below strips it from display.
private const val SENTINEL = " "

private val SentinelVisualTransformation = VisualTransformation { annotated ->
    val stripped = annotated.text.removePrefix(SENTINEL)
    TransformedText(
        text = AnnotatedString(stripped),
        offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = (offset - 1).coerceAtLeast(0)
            override fun transformedToOriginal(offset: Int): Int =
                (offset + 1).coerceAtMost(annotated.text.length)
        }
    )
}

@Composable
private fun PinInputCell(
    digit: Int?,
    focusRequester: FocusRequester,
    onDigitEntered: (Int) -> Unit,
    onDeletePress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val value = if (digit != null) "$SENTINEL$digit" else SENTINEL

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            when {
                newValue.isEmpty() || newValue == SENTINEL -> onDeletePress()
                else -> newValue.replace(SENTINEL, "")
                    .lastOrNull()
                    ?.digitToIntOrNull()
                    ?.let { onDigitEntered(it) }
            }
        },
        modifier = modifier
            .size(56.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused }
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Backspace && keyEvent.type == KeyEventType.KeyUp) {
                    onDeletePress()
                    true
                } else false
            },
        visualTransformation = SentinelVisualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                innerTextField()
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun ReadyToTypeScreenPreview() {
    ReadyToTypeTheme {
        ReadyToTypeScreen(
            state = ReadyToTypeState(pin1 = 2, pin2 = 5, pin3 = null, pin4 = null),
            onAction = {},
        )
    }
}