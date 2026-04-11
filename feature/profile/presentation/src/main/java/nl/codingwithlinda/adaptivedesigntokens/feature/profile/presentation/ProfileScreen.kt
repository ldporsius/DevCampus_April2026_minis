package nl.codingwithlinda.adaptivedesigntokens.feature.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.UiMode
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.AdaptiveDesignTokensTheme
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.LocalSpacing
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.LocalTypography
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.toAvatarSize
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.toLocalTypography
import nl.codingwithlinda.adaptivedesigntokens.core.designsystem.theme.toSpacing
import nl.codingwithlinda.adaptivedesigntokens.feature.profile.domain.model.UserProfile
import org.koin.androidx.compose.koinViewModel

// ── State ────────────────────────────────────────────────────────────────────

data class ProfileState(
    val selectedMode: UiMode = UiMode.Compact,
    val profile: UserProfile = UserProfile(
        name = "Alex Morgan",
        role = "Android Developer",
        followersCount = "1.2K",
        postsCount = "120",
        avatarAsset = "avatar.png",
    ),
    val avatar: ImageBitmap? = null,
    val isLoading: Boolean = false,
)

// ── Action ───────────────────────────────────────────────────────────────────

sealed interface ProfileAction {
    data class OnModeSelected(val mode: UiMode) : ProfileAction
    data object OnFollowClick : ProfileAction
}

// ── Root ─────────────────────────────────────────────────────────────────────

@Composable
fun ProfileRoot(
    viewModel: ProfileViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

// ── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .safeContentPadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            UiModeSelector(
                selectedMode = state.selectedMode,
                onModeSelected = { onAction(ProfileAction.OnModeSelected(it)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
            CompactProfileCard(
                selectedMode = state.selectedMode,
                profile = state.profile,
                avatar = state.avatar,
                onFollow = { onAction(ProfileAction.OnFollowClick) },
            )
        }
    }

}

// ── Internal composables ──────────────────────────────────────────────────────

@Composable
private fun UiModeSelector(
    selectedMode: UiMode,
    onModeSelected: (UiMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalSpacing provides selectedMode.toSpacing()) {
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = modifier,
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                UiMode.entries.forEach { mode ->
                    val selected = mode == selectedMode
                    Surface(
                        onClick = { onModeSelected(mode) },
                        shape = RoundedCornerShape(50),
                        color = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = mode.name,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactProfileCard(
    selectedMode: UiMode,
    profile: UserProfile,
    avatar: ImageBitmap?,
    onFollow: () -> Unit,
    modifier: Modifier = Modifier,
) {

    CompositionLocalProvider(
        LocalSpacing provides selectedMode.toSpacing(),
        LocalTypography provides selectedMode.toLocalTypography()
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (avatar != null) {
                        Image(
                            bitmap = avatar,
                            contentDescription = "Avatar of ${profile.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(selectedMode.toAvatarSize())
                                .clip(CircleShape),
                        )
                    } else {
                        Surface(
                            modifier = Modifier.size(selectedMode.toAvatarSize()),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {}
                    }
                    Spacer(modifier = Modifier.width(LocalSpacing.current.medium))
                    Column {
                        Text(
                            text = profile.name,
                            style = LocalTypography.current.captionTextStyle,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = profile.role,
                            style = LocalTypography.current.bodyTextStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(LocalSpacing.current.large))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(LocalSpacing.current.large))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatColumn(uiMode = selectedMode, label = "FOLLOWERS", value = profile.followersCount)
                    VerticalDivider(
                        modifier = Modifier.height(40.dp + LocalSpacing.current.large),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    StatColumn(uiMode = selectedMode, label = "POSTS", value = profile.postsCount)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onFollow,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(
                        text = "Follow",
                        style = LocalTypography.current.bodyTextStyle,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatColumn(
    uiMode: UiMode,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalSpacing provides uiMode.toSpacing(),
        LocalTypography provides uiMode.toLocalTypography()) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = LocalTypography.current.labelTextStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(LocalSpacing.current.small))
            Text(
                text = value,
                style = LocalTypography.current.titleTextStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    AdaptiveDesignTokensTheme {
        ProfileScreen(
            state = ProfileState(),
            onAction = {},
        )
    }
}