package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.theme.FRBackground
import com.example.ui.theme.FRCharcoal
import com.example.ui.theme.FRDivider
import com.example.ui.theme.FREmerald
import com.example.ui.theme.FRSurface
import com.example.ui.theme.FRSurfaceElevated
import com.example.ui.theme.FRSurfaceVariant
import com.example.ui.theme.FRTextMuted
import com.example.ui.theme.FRTextPrimary
import com.example.ui.theme.FRTextSecondary

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onSaveProfile: (name: String, email: String, languages: String) -> Unit,
    initialName: String = "Fauzan Shah",
    initialEmail: String = "shahfauzan49@gmail.com",
    initialLanguages: String = "Hindi, English, Punjabi",
    canDismiss: Boolean = true
) {
    var name by remember { mutableStateOf(initialName) }
    var email by remember { mutableStateOf(initialEmail) }

    val allLanguages = listOf("Hindi", "English", "Punjabi", "Bollywood", "Lo-fi")
    val selectedLanguages = remember {
        mutableStateListOf<String>().apply {
            addAll(initialLanguages.split(", ").filter { it.isNotBlank() })
        }
    }

    AlertDialog(
        onDismissRequest = { if (canDismiss) onDismiss() },
        containerColor = FRBackground,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(FRCharcoal),
                    contentAlignment = Alignment.Center
                ) {
                    coil.compose.AsyncImage(
                        model = com.example.R.drawable.fr_brand_logo,
                        contentDescription = "FR Music Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Welcome to FR Music",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = FRTextPrimary,
                        fontSize = 20.sp
                    )
                )

                Text(
                    text = "Save details locally to personalize your experience",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = FRTextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FRTextPrimary,
                        unfocusedTextColor = FRTextPrimary,
                        focusedBorderColor = FRCharcoal,
                        unfocusedBorderColor = FRDivider,
                        focusedLabelColor = FRCharcoal,
                        unfocusedLabelColor = FRTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_name_input")
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Your Email") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = FRTextPrimary,
                        unfocusedTextColor = FRTextPrimary,
                        focusedBorderColor = FRCharcoal,
                        unfocusedBorderColor = FRDivider,
                        focusedLabelColor = FRCharcoal,
                        unfocusedLabelColor = FRTextSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("login_email_input")
                )

                Text(
                    text = "Preferred Languages & Moods",
                    color = FRTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allLanguages.take(3).forEach { lang ->
                        val isSelected = selectedLanguages.contains(lang)
                        Surface(
                            color = if (isSelected) FRCharcoal else FRSurfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable {
                                if (isSelected) selectedLanguages.remove(lang) else selectedLanguages.add(lang)
                            }
                        ) {
                            Text(
                                text = lang,
                                color = if (isSelected) Color.White else FRTextPrimary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allLanguages.drop(3).forEach { lang ->
                        val isSelected = selectedLanguages.contains(lang)
                        Surface(
                            color = if (isSelected) FRCharcoal else FRSurfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable {
                                if (isSelected) selectedLanguages.remove(lang) else selectedLanguages.add(lang)
                            }
                        ) {
                            Text(
                                text = lang,
                                color = if (isSelected) Color.White else FRTextPrimary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = FRSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = FREmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Stored 100% on this device locally (No cloud login needed)",
                            color = FRTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val langString = selectedLanguages.joinToString(", ")
                    onSaveProfile(name.trim(), email.trim(), langString)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FRCharcoal,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("save_login_button")
            ) {
                Text("Save & Continue", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            if (canDismiss) {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = FRTextSecondary)
                ) {
                    Text("Skip for Now")
                }
            }
        }
    )
}
