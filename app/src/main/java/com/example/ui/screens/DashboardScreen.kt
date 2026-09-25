package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AnalysisEntity
import com.example.data.local.entity.ModelMetadataEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.ClassStatusPill
import com.example.ui.components.DevModeBanner
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.navigation.KidneyAiScreen
import com.example.ui.theme.ColorAgreement
import com.example.ui.theme.ColorCyst
import com.example.ui.theme.ColorDisagreement
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.ColorStone
import com.example.ui.theme.ColorTumor
import com.example.ui.theme.MedicalCyanAccent
import com.example.ui.theme.MedicalNavy700
import com.example.ui.theme.MedicalNavy800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    user: UserEntity?,
    analyses: List<AnalysisEntity>,
    metadata: ModelMetadataEntity?,
    isDevMode: Boolean,
    onNavigate: (KidneyAiScreen) -> Unit,
    onSelectAnalysis: (AnalysisEntity) -> Unit
) {
    val totalCount = analyses.size
    val normalCount = analyses.count { it.finalPrediction.uppercase() == "NORMAL" }
    val cystCount = analyses.count { it.finalPrediction.uppercase() == "CYST" }
    val tumorCount = analyses.count { it.finalPrediction.uppercase() == "TUMOR" }
    val stoneCount = analyses.count { it.finalPrediction.uppercase() == "STONE" }
    val disagreementCount = analyses.count { !it.isAgreement }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen_container")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            if (isDevMode) {
                DevModeBanner()
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Welcome Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CLINICAL RESEARCH WORKBENCH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalCyanAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Welcome back, ${user?.fullName ?: "Researcher"}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${user?.role ?: "AI Medical Researcher"} • ${user?.institution ?: "Radiology Lab"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(KidneyAiScreen.NEW_ANALYSIS) },
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("dashboard_start_new_analysis_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF070E17), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Analysis", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF070E17))
                        }

                        OutlinedButton(
                            onClick = { onNavigate(KidneyAiScreen.HISTORY) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("dashboard_view_history_button")
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MedicalCyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("History (${analyses.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Summary Statistics Cards
        item {
            Text(
                text = "DIAGNOSTIC CASE SUMMARY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MedicalCyanAccent,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard("Total", totalCount.toString(), MaterialTheme.colorScheme.onBackground, Modifier.weight(1f))
                StatCard("Normal", normalCount.toString(), ColorNormal, Modifier.weight(1f))
                StatCard("Cyst", cystCount.toString(), ColorCyst, Modifier.weight(1f))
                StatCard("Tumor", tumorCount.toString(), ColorTumor, Modifier.weight(1f))
                StatCard("Stone", stoneCount.toString(), ColorStone, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Model Status Card (SVM & Decision Tree Loaded / Not Loaded)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI MODEL ENGINE STATUS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalCyanAccent,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = metadata?.version ?: "v1.0.0",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ModelStatusItem("SVM Model", metadata?.svmStatus ?: "LOADED", "RBF/Linear Kernel")
                        ModelStatusItem("Decision Tree", metadata?.dtStatus ?: "LOADED", "Gini Impurity")
                        ModelStatusItem("Radiomics Scaler", metadata?.scalerStatus ?: "LOADED", "29 Features")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Dual-model cross-verification active.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "View Architecture Specs →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalCyanAccent,
                            modifier = Modifier.clickable { onNavigate(KidneyAiScreen.MODEL_INFORMATION) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Recent Analyses Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ANALYSES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 0.5.sp
                )
                if (analyses.isNotEmpty()) {
                    Text(
                        text = "View All (${analyses.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalCyanAccent,
                        modifier = Modifier.clickable { onNavigate(KidneyAiScreen.HISTORY) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (analyses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Biotech,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No analyses recorded yet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Upload a kidney CT or MRI scan to start dual ML classification.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onNavigate(KidneyAiScreen.NEW_ANALYSIS) },
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Start Analysis", color = Color(0xFF070E17), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(analyses.take(5)) { item ->
                RecentAnalysisCard(
                    analysis = item,
                    onClick = {
                        onSelectAnalysis(item)
                        onNavigate(KidneyAiScreen.ANALYSIS_RESULT)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            MedicalDisclaimerCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ModelStatusItem(name: String, status: String, sub: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ColorNormal)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorNormal)
        }
        Text(text = sub, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun RecentAnalysisCard(
    analysis: AnalysisEntity,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(analysis.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("analysis_card_${analysis.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = analysis.id,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalCyanAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ClassStatusPill(label = analysis.finalPrediction)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${analysis.imageName} (${analysis.imageDimensions})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "SVM: ${analysis.svmPrediction}  |  DT: ${analysis.decisionTreePrediction}  •  ${analysis.processingTimeSec}s",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                if (analysis.confidence != null) {
                    Text(
                        text = "${analysis.confidence}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorNormal
                    )
                    Text(
                        text = "Confidence",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Confidence N/A",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MedicalCyanAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
