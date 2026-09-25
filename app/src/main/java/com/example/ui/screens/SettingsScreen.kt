package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DevModeBanner
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.theme.ColorDevMode
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.ColorTumor
import com.example.ui.theme.MedicalCyanAccent
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    isDevMode: Boolean,
    isDarkTheme: Boolean,
    onToggleDevMode: (Boolean) -> Unit,
    onToggleTheme: (Boolean) -> Unit,
    onChangePassword: suspend (oldPass: String, newPass: String) -> Boolean,
    onLogout: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordChangeMessage by remember { mutableStateOf<String?>(null) }
    var isPasswordSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen_container")
    ) {
        if (isDevMode) {
            DevModeBanner()
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = "System Settings & Configuration",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Configure machine learning pipeline execution, interface preferences, and credential security.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Machine Learning Execution Mode Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "MACHINE LEARNING EXECUTION MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "USE_MOCK_MODEL (Development Mode)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (isDevMode)
                                "Mock prediction mode active. Displays prominent disclaimers."
                            else
                                "Production Mode active. Executes mathematical SVM and Decision Tree pipeline.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isDevMode,
                        onCheckedChange = onToggleDevMode,
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorDevMode, checkedTrackColor = ColorDevMode.copy(alpha = 0.4f)),
                        modifier = Modifier.testTag("dev_mode_toggle")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visual Theme Preference
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "APPEARANCE & THEME",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "High-Tech Medical Dark Mode",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Optimized for radiology workstation viewing and high contrast.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onToggleTheme,
                        colors = SwitchDefaults.colors(checkedThumbColor = MedicalCyanAccent),
                        modifier = Modifier.testTag("theme_toggle")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Security & Password Change
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECURITY & PASSWORD UPDATE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    placeholder = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("settings_current_password_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { Text("New Password (8+ chars, upper, lower, digit)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("settings_new_password_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    placeholder = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("settings_confirm_new_password_input")
                )

                if (passwordChangeMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = passwordChangeMessage ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPasswordSuccess) ColorNormal else ColorTumor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (newPassword != confirmNewPassword) {
                            passwordChangeMessage = "New passwords do not match."
                            isPasswordSuccess = false
                            return@Button
                        }
                        coroutineScope.launch {
                            val success = onChangePassword(oldPassword, newPassword)
                            if (success) {
                                passwordChangeMessage = "Password successfully changed."
                                isPasswordSuccess = true
                                oldPassword = ""
                                newPassword = ""
                                confirmNewPassword = ""
                            } else {
                                passwordChangeMessage = "Failed to change password. Please check current password and password complexity."
                                isPasswordSuccess = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_update_password_button")
                ) {
                    Text("UPDATE PASSWORD", color = Color(0xFF070E17), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sign Out
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = ColorTumor),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("settings_logout_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Sign Out Securely", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        MedicalDisclaimerCard()
        Spacer(modifier = Modifier.height(24.dp))
    }
}
