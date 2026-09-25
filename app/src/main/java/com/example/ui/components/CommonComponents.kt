package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.KidneyAiScreen
import com.example.ui.theme.ColorAgreement
import com.example.ui.theme.ColorCyst
import com.example.ui.theme.ColorDevMode
import com.example.ui.theme.ColorDisagreement
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.ColorStone
import com.example.ui.theme.ColorTumor
import com.example.ui.theme.MedicalCyanAccent
import com.example.ui.theme.MedicalNavy700
import com.example.ui.theme.MedicalNavy800
import com.example.ui.theme.MedicalNavy900

@Composable
fun KidneyAiLogo(modifier: Modifier = Modifier, subtitle: String = "AI Medical Image Analysis") {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF005082), Color(0xFF0A192F))
                    )
                )
                .border(1.5.dp, MedicalCyanAccent.copy(alpha = 0.6f), RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Biotech,
                contentDescription = "Kidney AI Logo",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "KIDNEY",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AI",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = MedicalCyanAccent
            )
        }
        Text(
            text = subtitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun MedicalDisclaimerCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("medical_disclaimer_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B).copy(alpha = 0.7f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(Color(0xFF64748B), Color(0xFF334155)))
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Medical AI Advisory",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "ACADEMIC RESEARCH DECISION-SUPPORT NOTICE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "“This application is an academic/research decision-support prototype. It is not a substitute for professional medical diagnosis, radiological interpretation, or clinical judgment. Predictions depend on the training dataset, preprocessing pipeline, model performance, and image quality.”",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFFCBD5E1)
                )
            }
        }
    }
}

@Composable
fun DevModeBanner(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dev_mode_banner"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorDevMode.copy(alpha = 0.15f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(ColorDevMode, Color(0xFFDC2626)))
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Development Mode Notice",
                tint = ColorDevMode,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Development Mode — Mock predictions are not medical predictions.",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorDevMode
            )
        }
    }
}

@Composable
fun ClassStatusPill(
    label: String,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    val (bgColor, textColor) = when (label.uppercase()) {
        "NORMAL" -> ColorNormal.copy(alpha = 0.15f) to ColorNormal
        "CYST" -> ColorCyst.copy(alpha = 0.15f) to ColorCyst
        "TUMOR" -> ColorTumor.copy(alpha = 0.15f) to ColorTumor
        "STONE" -> ColorStone.copy(alpha = 0.15f) to ColorStone
        "MODEL_DISAGREEMENT" -> ColorDisagreement.copy(alpha = 0.15f) to ColorDisagreement
        else -> Color.Gray.copy(alpha = 0.15f) to Color.LightGray
    }

    val displayLabel = if (label == "MODEL_DISAGREEMENT") "DISAGREEMENT" else label.uppercase()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = if (large) 14.dp else 8.dp, vertical = if (large) 6.dp else 3.dp)
    ) {
        Text(
            text = displayLabel,
            fontSize = if (large) 14.sp else 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun ModelAgreementBadge(isAgreement: Boolean, modifier: Modifier = Modifier) {
    val bgColor = if (isAgreement) ColorAgreement.copy(alpha = 0.15f) else ColorDisagreement.copy(alpha = 0.15f)
    val strokeColor = if (isAgreement) ColorAgreement else ColorDisagreement
    val icon = if (isAgreement) Icons.Default.CheckCircle else Icons.Default.Warning
    val text = if (isAgreement) "MODEL AGREEMENT: YES" else "MODEL DISAGREEMENT"

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, strokeColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = strokeColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = strokeColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeaderBar(
    title: String,
    onMenuClick: () -> Unit,
    isDevMode: Boolean = false,
    onActionClick: (() -> Unit)? = null,
    actionIcon: ImageVector? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (isDevMode) {
                    Text(
                        text = "DEV MODE ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColorDevMode,
                        letterSpacing = 1.sp
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick, modifier = Modifier.testTag("menu_drawer_button")) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            if (actionIcon != null && onActionClick != null) {
                IconButton(onClick = onActionClick, modifier = Modifier.testTag("app_bar_action_button")) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = "Action",
                        tint = MedicalCyanAccent
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun AppNavigationDrawer(
    currentScreen: KidneyAiScreen,
    userFullName: String?,
    userEmail: String?,
    isDevMode: Boolean,
    onSelectScreen: (KidneyAiScreen) -> Unit,
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = MedicalNavy800,
        drawerContentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MedicalNavy900)
                .padding(20.dp)
        ) {
            KidneyAiLogo(subtitle = "BSc AI & Data Science Project")
            Spacer(modifier = Modifier.height(16.dp))
            if (userFullName != null) {
                Text(
                    text = userFullName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = userEmail ?: "",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
        Divider(color = MedicalNavy700)
        Spacer(modifier = Modifier.height(8.dp))

        KidneyAiScreen.drawerDestinations.forEach { dest ->
            val icon = when (dest) {
                KidneyAiScreen.DASHBOARD -> Icons.Default.Assessment
                KidneyAiScreen.NEW_ANALYSIS -> Icons.Default.Biotech
                KidneyAiScreen.HISTORY -> Icons.Default.History
                KidneyAiScreen.REPORTS -> Icons.Default.Description
                KidneyAiScreen.MODEL_INFORMATION -> Icons.Default.Info
                KidneyAiScreen.PROFILE -> Icons.Default.Person
                KidneyAiScreen.SETTINGS -> Icons.Default.Settings
                else -> Icons.Default.Info
            }
            val isSelected = currentScreen == dest

            NavigationDrawerItem(
                label = {
                    Text(
                        text = dest.title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    onCloseDrawer()
                    onSelectScreen(dest)
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = dest.title,
                        tint = if (isSelected) MedicalCyanAccent else Color(0xFF94A3B8)
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .testTag("nav_item_${dest.name.lowercase()}"),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MedicalNavy700,
                    selectedTextColor = MedicalCyanAccent,
                    unselectedTextColor = Color(0xFFCBD5E1)
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Divider(color = MedicalNavy700)

        // Logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onCloseDrawer()
                    onLogout()
                }
                .padding(18.dp)
                .testTag("logout_drawer_button"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = ColorTumor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Sign Out",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorTumor
            )
        }
    }
}
