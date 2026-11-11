package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Playlist
import com.example.data.model.Track
import com.example.ui.theme.FRCyan
import com.example.ui.theme.FRSurface
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String) -> Unit,
    initialTitle: String = "",
    initialDescription: String = "",
    isEditing: Boolean = false
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FRSurface,
        title = {
            Text(
                text = if (isEditing) "Edit Playlist" else "Create New Playlist",
                color = FRTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Playlist Title (e.g. Chill Hindi)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FRTextPrimary,
                        unfocusedTextColor = FRTextPrimary,
                        focusedBorderColor = FRCyan,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = FRCyan,
                        unfocusedLabelColor = FRTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playlist_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FRTextPrimary,
                        unfocusedTextColor = FRTextPrimary,
                        focusedBorderColor = FRCyan,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = FRCyan,
                        unfocusedLabelColor = FRTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playlist_desc_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), description.trim())
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FRCyan,
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("save_playlist_button")
            ) {
                Text(if (isEditing) "Save" else "Create", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = FRTextSecondary)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddToPlaylistDialog(
    track: Track,
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (Playlist) -> Unit,
    onCreateNewPlaylist: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FRSurface,
        title = {
            Text(
                text = "Add to Playlist",
                color = FRTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Select a playlist for '${track.title}'",
                    color = FRTextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(FRSurfaceVariant)
                        .clickable(onClick = onCreateNewPlaylist)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Playlist",
                        tint = FRCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Create new playlist",
                        color = FRCyan,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (playlists.isEmpty()) {
                    Text(
                        text = "No custom playlists yet.",
                        color = FRTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(playlists) { pl ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onPlaylistSelected(pl) }
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = FRTextSecondary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = pl.title,
                                    color = FRTextPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = FRTextSecondary)
            ) {
                Text("Close")
            }
        }
    )
}
