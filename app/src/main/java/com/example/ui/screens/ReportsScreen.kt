package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AnalysisEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.ClassStatusPill
import com.example.ui.components.KidneyAiLogo
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.components.ModelAgreementBadge
import com.example.ui.navigation.KidneyAiScreen
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.MedicalCyanAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    analysis: AnalysisEntity?,
    user: UserEntity?,
    onNavigate: (KidneyAiScreen) -> Unit
) {
    val context = LocalContext.current

    if (analysis == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Analysis Selected for Report",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Run an analysis or select one from history to generate an official decision support report.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onNavigate(KidneyAiScreen.NEW_ANALYSIS) },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent)
            ) {
                Text("Start Analysis", color = Color(0xFF070E17), fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    val reportDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US).format(Date(analysis.timestamp))

    fun generateReportText(): String {
        return """
================================================================================
AI-POWERED MEDICAL IMAGE DIAGNOSIS SYSTEM FOR KIDNEY DISEASE USING MACHINE LEARNING
ACADEMIC CLINICAL DECISION-SUPPORT REPORT (BSc CS / AI & DATA SCIENCE)
================================================================================
Case Identification: ${analysis.id}
Date & Timestamp: ${reportDate}
Researcher / Investigator: ${user?.fullName ?: "Investigator"} (${user?.email ?: "researcher@lab.org"})
Institution: ${user?.institution ?: "Department of Computer Science & AI"}
--------------------------------------------------------------------------------
IMAGE SPECIMEN METADATA:
Image Identifier: ${analysis.imageName}
Dimensions: ${analysis.imageDimensions}
File Size: ${analysis.fileSizeFormatted}
Target Region: Renal Parenchyma (Axial CT / MRI Slice)

PREPROCESSING PIPELINE EXECUTION:
1. Resize: Rescaled to standardized 64x64 grid
2. Grayscale: Converted using standard luminance transform (0.299R + 0.587G + 0.114B)
3. Normalization: Pixel intensities scaled to [0.0, 1.0]
4. Radiomics Feature Extraction: 29 engineered features extracted (Moments, 16-bin histogram, Sobel gradients, GLCM texture descriptors)

MACHINE LEARNING DIAGNOSTIC OUTPUT:
Primary Model Classification: ${analysis.finalPrediction}
Support Vector Machine (SVM) Classification: ${analysis.svmPrediction}
Decision Tree Classification: ${analysis.decisionTreePrediction}
Dual-Model Agreement: ${if (analysis.isAgreement) "YES" else "MODEL DISAGREEMENT DETECTED"}
Statistical Confidence: ${if (analysis.confidence != null) "${analysis.confidence}%" else "Confidence not available"}
Engine Latency: ${analysis.processingTimeSec} seconds
Model Pipeline Status: ${analysis.modelStatus}
Explainability: ${analysis.explainabilityNote}
--------------------------------------------------------------------------------
MANDATORY RESEARCH & ETHICAL DISCLAIMER:
“This application is an academic/research decision-support prototype. It is not a substitute for professional medical diagnosis, radiological interpretation, or clinical judgment. Predictions depend on the training dataset, preprocessing pipeline, model performance, and image quality.”
================================================================================
        """.trimIndent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("reports_screen_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Clinical Decision Report",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Formatted for Viva Evaluation & Record Keeping",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Kidney AI Diagnostic Report", generateReportText())
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Full report copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("copy_report_button")
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF070E17), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy", color = Color(0xFF070E17), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Professional Dossier Paper Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("report_document_card"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1728)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "KIDNEY AI DIAGNOSTICS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = MedicalCyanAccent,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "BSc Computer Science (AI & Data Science)",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    ClassStatusPill(label = analysis.finalPrediction)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(12.dp))

                // Dossier Table
                ReportRow("Case ID", analysis.id)
                ReportRow("Timestamp", reportDate)
                ReportRow("Investigator", "${user?.fullName ?: "Researcher"} (${user?.email})")
                ReportRow("Institution", user?.institution ?: "AI & Data Science Lab")
                ReportRow("Specimen Name", analysis.imageName)
                ReportRow("Dimensions", analysis.imageDimensions)
                ReportRow("File Size", analysis.fileSizeFormatted)

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "MACHINE LEARNING CLASSIFICATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                ReportRow("Final Classification", "Model classification: ${analysis.finalPrediction}")
                ReportRow("SVM Classifier Result", analysis.svmPrediction)
                ReportRow("Decision Tree Result", analysis.decisionTreePrediction)
                ReportRow("Dual-Model Cross Agreement", if (analysis.isAgreement) "YES" else "MODEL DISAGREEMENT")
                ReportRow(
                    "Calibrated Confidence",
                    if (analysis.confidence != null) "${analysis.confidence}%" else "Confidence not available"
                )
                ReportRow("Execution Latency", "${analysis.processingTimeSec}s")
                ReportRow("Model Pipeline Status", analysis.modelStatus)

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "PREPROCESSING & FEATURE EXTRACTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Grayscale Conversion: Luminance formula 0.299R + 0.587G + 0.114B\n• Normalization: Min-Max range [0.0, 1.0]\n• Feature Engineering: 29 dimensional radiomics vector (Statistical moments, 16-bin intensity histogram, Sobel edge magnitude, GLCM texture contrast/homogeneity/energy/correlation)",
                    fontSize = 10.sp,
                    lineHeight = 15.sp,
                    color = Color(0xFFCBD5E1),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        MedicalDisclaimerCard()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ReportRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF94A3B8))
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF1F5F9))
    }
}
