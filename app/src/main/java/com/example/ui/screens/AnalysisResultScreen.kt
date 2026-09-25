package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AnalysisEntity
import com.example.ui.components.ClassStatusPill
import com.example.ui.components.DevModeBanner
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.components.ModelAgreementBadge
import com.example.ui.navigation.KidneyAiScreen
import com.example.ui.theme.ColorAgreement
import com.example.ui.theme.ColorDisagreement
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.MedicalCyanAccent

@Composable
fun AnalysisResultScreen(
    analysis: AnalysisEntity?,
    bitmap: Bitmap?,
    onNavigate: (KidneyAiScreen) -> Unit
) {
    if (analysis == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No analysis selected.", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { onNavigate(KidneyAiScreen.NEW_ANALYSIS) }) {
                Text("Start Analysis")
            }
        }
        return
    }

    val isDevMode = analysis.modelStatus == "DEVELOPMENT_MODE"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("analysis_result_screen_container")
    ) {
        if (isDevMode) {
            DevModeBanner()
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI ANALYSIS RESULT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Analysis ID: ${analysis.id}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent
                )
            }
            ModelAgreementBadge(isAgreement = analysis.isAgreement)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Image Preview & Specs
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MedicalCyanAccent, RoundedCornerShape(12.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Analyzed Kidney Scan",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize().testTag("result_image_preview")
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = analysis.imageName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${analysis.imageDimensions} • ${analysis.fileSizeFormatted}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Primary Classification Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "FINAL CLASSIFICATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClassStatusPill(label = analysis.finalPrediction, large = true)

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Model Status",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = analysis.modelStatus,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDevMode) ColorDisagreement else ColorNormal
                        )
                    }
                }

                if (!analysis.isAgreement) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = ColorDisagreement.copy(alpha = 0.12f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = ColorDisagreement, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MODEL DISAGREEMENT DETECTED: SVM classified scan as '${analysis.svmPrediction}' while Decision Tree classified scan as '${analysis.decisionTreePrediction}'. Radiologists and clinical specialists must perform secondary manual review.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = ColorDisagreement
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                // Dual Model Diagnostics Table
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DiagnosticResultCol(
                        title = "SVM Model",
                        prediction = analysis.svmPrediction,
                        sub = "RBF/Linear Hyperplane",
                        modifier = Modifier.weight(1f)
                    )
                    DiagnosticResultCol(
                        title = "Decision Tree",
                        prediction = analysis.decisionTreePrediction,
                        sub = "Orthogonal Gini Split",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                // Confidence & Processing Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Confidence", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (analysis.confidence != null) {
                            Text(
                                text = "${analysis.confidence}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorNormal,
                                modifier = Modifier.testTag("result_confidence_value")
                            )
                        } else {
                            Text(
                                text = "Not available",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("result_confidence_value")
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Processing Time", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${analysis.processingTimeSec} seconds",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Explainability Notice Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "EXPLAINABILITY VISUALIZATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = analysis.explainabilityNote,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons
        Button(
            onClick = { onNavigate(KidneyAiScreen.REPORTS) },
            colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_report_button")
        ) {
            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF070E17), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "GENERATE CLINICAL REPORT",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF070E17)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { onNavigate(KidneyAiScreen.NEW_ANALYSIS) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(46.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Scan", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { onNavigate(KidneyAiScreen.HISTORY) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).height(46.dp)
            ) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("All Cases", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        MedicalDisclaimerCard()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DiagnosticResultCol(title: String, prediction: String, sub: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(text = sub, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        ClassStatusPill(label = prediction)
    }
}
