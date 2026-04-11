package nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.codingwithlinda.adaptivedesigntokens.feature.editing_status.presentation.theme.EditingStatusTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun NoteRoot(
    modifier: Modifier = Modifier,
    viewModel: EditingStatusViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    NoteScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun NoteScreen(
    state: NoteState,
    onAction: (NoteAction) -> Unit,
    modifier: Modifier = Modifier,
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
        ) {
            Text(
                text = "My Note",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(16.dp))

            NoteTextField(
                text = state.noteText,
                onTextChange = { onAction(NoteAction.OnNoteChanged(it)) },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                ,
            )

            Spacer(modifier = Modifier.height(12.dp))

            EditingStatusChip(
                status = state.status,
                modifier = Modifier.align(Alignment.Start)
                    .testTag("editing_status_chip"),
            )
        }
    }
}

@Composable
private fun EditingStatusChip(
    status: EditingStatus,
    modifier: Modifier = Modifier,
) {
    if (status == EditingStatus.Idle) return


    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = status.statusText(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun NoteTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .fillMaxWidth()
            .testTag("note_text_field"),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
            ) {

                innerTextField()
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun NoteScreenEditingPreview() {
    EditingStatusTheme {
        NoteScreen(
            state = NoteState(noteText = "Hello world", status = EditingStatus.Editing),
            onAction = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoteScreenSavedPreview() {
    EditingStatusTheme {
        NoteScreen(
            state = NoteState(noteText = "Hello world", status = EditingStatus.Saved),
            onAction = {},
        )
    }
}