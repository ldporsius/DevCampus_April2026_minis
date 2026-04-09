package nl.codingwithlinda.adaptivedesigntokens.feature.ready_to_type.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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

    LaunchedEffect(state.activeCell) {
        focusRequesters[state.activeCell].requestFocus()
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
                repeat(4) { index ->
                    PinInputCell(
                        digit = state.pinAt(index),
                        isActive = index == state.activeCell,
                        focusRequester = focusRequesters[index],
                        onDigitEntered = { onAction(ReadyToTypeAction.OnPinDigitEntered(it)) },
                        onDeletePress = { onAction(ReadyToTypeAction.OnDeletePress) },
                        onCellClicked = { onAction(ReadyToTypeAction.OnCellClicked(index)) },
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

// Space sentinel ensures the IME always has a character to delete,
// making soft-keyboard backspace fire onValueChange reliably.
private const val SENTINEL = " "

@Composable
private fun PinInputCell(
    digit: Int?,
    isActive: Boolean,
    focusRequester: FocusRequester,
    onDigitEntered: (Int) -> Unit,
    onDeletePress: () -> Unit,
    onCellClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (isActive) {
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
            .clickable { onCellClicked() }
            .focusRequester(focusRequester)
            // Only handles hardware-keyboard backspace on an empty cell.
            // All other cases are covered by onValueChange via the sentinel.
            .onKeyEvent { keyEvent ->
                if (digit == null
                    && keyEvent.key == Key.Backspace
                    && keyEvent.type == KeyEventType.KeyUp
                ) {
                    onDeletePress()
                    true
                } else false
            },
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
            state = ReadyToTypeState(pin1 = 2, pin2 = 5),
            onAction = {},
        )
    }
}