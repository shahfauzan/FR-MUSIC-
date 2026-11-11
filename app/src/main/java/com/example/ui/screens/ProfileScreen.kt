package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.ProviderType
import com.example.ui.MainViewModel
import com.example.ui.theme.FRCharcoal
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRMagenta
import com.example.ui.theme.FRSurface
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary
import com.example.ui.theme.FRViolet

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val favoriteTracks by viewModel.favoriteTracks.collectAsState()
    val userPlaylists by viewModel.userPlaylists.collectAsState()
    val recentlyPlayed by viewModel.recentlyPlayed.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val currentProviderType by viewModel.repository.currentProviderType.collectAsState()

    val name = userProfile?.name?.ifBlank { "Fauzan Shah" } ?: "Fauzan Shah"
    val email = userProfile?.email?.ifBlank { "shahfauzan49@gmail.com" } ?: "shahfauzan49@gmail.com"
    val languages = userProfile?.preferredLanguages?.ifBlank { "Hindi, English, Punjabi" } ?: "Hindi, English, Punjabi"

    var hindiPref by remember { mutableStateOf(languages.contains("Hindi")) }
    var englishPref by remember { mutableStateOf(languages.contains("English")) }
    var punjabiPref by remember { mutableStateOf(languages.contains("Punjabi")) }
    var highQualityAudio by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // User Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FRSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = FRTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FRTextPrimary,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = FRTextSecondary,
                                    fontSize = 13.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Saved Locally on Device",
                                    color = FRTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .clickable { viewModel.setShowLoginDialog(true) }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Edit Profile Details",
                                color = FRTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .clickable { viewModel.logoutUser() }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Switch Account",
                                color = FRTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Liked Songs",
                    value = "${favoriteTracks.size}",
                    icon = Icons.Default.Favorite,
                    iconTint = FRMagenta,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Playlists",
                    value = "${userPlaylists.size}",
                    icon = Icons.Default.QueueMusic,
                    iconTint = FRCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "History",
                    value = "${recentlyPlayed.size}",
                    icon = Icons.Default.Audiotrack,
                    iconTint = FRViolet,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Music Provider Configuration (Demo Mode vs Live Legal API)
        item {
            Text(
                text = "Music Provider & Engine",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = FRTextPrimary,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FRSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setProviderType(ProviderType.YOUTUBE_DATA_API) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentProviderType == ProviderType.YOUTUBE_DATA_API,
                            onClick = { viewModel.setProviderType(ProviderType.YOUTUBE_DATA_API) },
                            colors = RadioButtonDefaults.colors(selectedColor = FRCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "YouTube Data API v3 (Active Key)",
                                color = FRTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Official YouTube music search, video metadata, and top music channels.",
                                color = FRTextSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setProviderType(ProviderType.LEGAL_API_PREVIEWS) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentProviderType == ProviderType.LEGAL_API_PREVIEWS,
                            onClick = { viewModel.setProviderType(ProviderType.LEGAL_API_PREVIEWS) },
                            colors = RadioButtonDefaults.colors(selectedColor = FRCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Authorized Legal Preview API (iTunes)",
                                color = FRTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Legal 30-sec official audio previews & high-res artwork without DRM scraping.",
                                color = FRTextSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setProviderType(ProviderType.CURATED_DEMO_OFFLINE) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentProviderType == ProviderType.CURATED_DEMO_OFFLINE,
                            onClick = { viewModel.setProviderType(ProviderType.CURATED_DEMO_OFFLINE) },
                            colors = RadioButtonDefaults.colors(selectedColor = FRCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Curated Demo Mode (Offline Friendly)",
                                color = FRTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Royalty-free verified streams across Hindi, English, Punjabi, and Lo-fi.",
                                color = FRTextSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Language Preferences
        item {
            Text(
                text = "Language & Music Preferences",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = FRTextPrimary,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FRSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    PreferenceToggleRow("Hindi (Bollywood & Indie)", hindiPref) { hindiPref = it }
                    PreferenceToggleRow("English (Pop, Hip-Hop & R&B)", englishPref) { englishPref = it }
                    PreferenceToggleRow("Punjabi (Bhangra & Pop)", punjabiPref) { punjabiPref = it }
                    PreferenceToggleRow("High Quality Streaming (256 kbps)", highQualityAudio) { highQualityAudio = it }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Legal & Copyright Notice
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FRSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Legal Notice",
                        tint = FRCharcoal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Legal & Copyright Policy",
                            fontWeight = FontWeight.Bold,
                            color = FRTextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "FR Music strictly adheres to authorized API usage. Song previews are legally licensed 30-second clips provided by official open endpoints, and demo tracks use verified royalty-free audio licenses. No DRM is bypassed, and no unauthorized MP3 files are stored or distributed.",
                            color = FRTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "FR Music v1.0 • Built with Jetpack Compose & Kotlin",
                color = FRTextMuted,
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = FRSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = FRTextPrimary,
                fontSize = 18.sp
            )
            Text(
                text = title,
                color = FRTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PreferenceToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = FRTextPrimary,
            fontSize = 13.5.sp
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = FRCharcoal,
                uncheckedTrackColor = Color(0xFFE2E8F0),
                uncheckedThumbColor = Color(0xFF94A3B8)
            )
        )
    }
}
