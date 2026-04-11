package nl.codingwithlinda.cloud_photo_upload.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoAction
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoBackupState
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.PhotoBackupUiState
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.toButtonColors
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.toButtonText
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.toDescription
import nl.codingwithlinda.cloud_photo_upload.presentation.interaction.toStatusText
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CloudPhotoTheme
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CpBg
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CpSkyBlue
import nl.codingwithlinda.cloud_photo_upload.presentation.theme.CpSurface
import org.koin.androidx.compose.koinViewModel


@Composable
fun PhotoBackupRoot(
    viewModel: PhotoBackupViewModel = koinViewModel(),
){
    CloudPhotoTheme {
        PhotoBackupScreen(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
            uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun PhotoBackupScreen(
    modifier: Modifier = Modifier,
    uiState: PhotoBackupUiState,
    onAction: (PhotoAction) -> Unit
) {
    Surface(
        modifier = modifier,
        color = CpBg,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Cloud Photo Backup",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            PhotoBackupStatusCard(
                modifier = Modifier.fillMaxWidth(),
                uiState = uiState,
            )
            Button(
                onClick = {
                    if (uiState.state == PhotoBackupState.FINISHED)
                        onAction(PhotoAction.BackupCompleted)
                    else
                        onAction(PhotoAction.StartBackup)
                },
                enabled = uiState.isButtonEnabled(),
                colors = uiState.state.toButtonColors(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = uiState.state.toButtonText())
            }
        }
    }
}

@Composable
fun PhotoBackupStatusCard(
    modifier: Modifier = Modifier,
    uiState: PhotoBackupUiState,
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = CpSurface),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(uiState.state.toDescription())
            Text(
                uiState.state.toStatusText(uiState.numberUploaded, uiState.total),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
            LinearProgressIndicator(
                progress = { uiState.progress() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                color = CpSkyBlue,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    }
}


