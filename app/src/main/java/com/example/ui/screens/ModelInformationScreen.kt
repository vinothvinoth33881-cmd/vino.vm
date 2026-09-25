package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ModelMetadataEntity
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.MedicalCyanAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ModelInformationScreen(
    metadata: ModelMetadataEntity?
) {
    val meta = metadata ?: ModelMetadataEntity()
    val lastUpdatedStr = SimpleDateFormat("MMM dd, yyyy • HH:mm:ss z", Locale.getDefault()).format(Date(meta.lastUpdated))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("model_information_screen_container")
    ) {
        Text(
            text = "Model Specifications & Architecture",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Technical specification of the radiomics feature extraction, Support Vector Machine (SVM), and Decision Tree algorithms.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // System Model Inventory Status Table
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "DEPLOYED MODEL ARTIFACT STATUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                StatusCheckRow("SVM Model", meta.svmStatus, "models/svm_model.pkl")
                StatusCheckRow("Decision Tree", meta.dtStatus, "models/decision_tree_model.pkl")
                StatusCheckRow("Standard Scaler", meta.scalerStatus, "models/scaler.pkl")
                StatusCheckRow("Feature Extractor", meta.featureExtractorStatus, "models/feature_extractor.pkl")
                StatusCheckRow("Label Encoder", meta.labelEncoderStatus, "models/label_encoder.pkl")

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Model Version: ${meta.version}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("Updated: $lastUpdatedStr", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SVM Technical Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. SUPPORT VECTOR MACHINE (SVM)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                SpecItem("Algorithm", "Support Vector Classification (One-vs-Rest OvR)")
                SpecItem("Kernel", meta.svmKernel)
                SpecItem("Regularization (C)", "1.0 (L2 Penalty)")
                SpecItem("Kernel Coefficient (gamma)", "scale (1 / (n_features * X.var()))")
                SpecItem("Feature Extraction", "29 Radiomics features (Moments + Histogram + Sobel + GLCM)")
                SpecItem("Training Status", "Trained on CT Benchmark Dataset")
                SpecItem("Training Dataset", "${meta.trainingDatasetSize} Axial CT slices across 4 classes")

                Spacer(modifier = Modifier.height(10.dp))
                Text("EVALUATED TEST METRICS:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricBadge("Accuracy", meta.svmAccuracy?.let { "${(it * 100).toInt()}%" } ?: "Not evaluated yet", Modifier.weight(1f))
                    MetricBadge("Precision", meta.svmPrecision?.let { "${(it * 100).toInt()}%" } ?: "Not evaluated yet", Modifier.weight(1f))
                    MetricBadge("Recall", meta.svmRecall?.let { "${(it * 100).toInt()}%" } ?: "Not evaluated yet", Modifier.weight(1f))
                    MetricBadge("F1-Score", meta.svmF1?.toString() ?: "Not evaluated yet", Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Decision Tree Technical Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "2. DECISION TREE CLASSIFIER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                SpecItem("Algorithm", "Classification and Regression Trees (CART)")
                SpecItem("Splitting Criterion", meta.dtCriterion)
                SpecItem("Maximum Depth", "12 Levels (Pruned to prevent overfitting)")
                SpecItem("Min Samples Split", "4 Samples")
                SpecItem("Feature Extraction", "Same 29 Radiomics features as SVM")
                SpecItem("Training Status", "Trained on CT Benchmark Dataset")
                SpecItem("Training Dataset", "${meta.trainingDatasetSize} Axial CT slices")

                Spacer(modifier = Modifier.height(10.dp))
                Text("EVALUATED TEST METRICS:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricBadge("Accuracy", meta.dtAccuracy?.let { "${(it * 100).toInt()}%" } ?: "Not evaluated yet", Modifier.weight(1f))
                    MetricBadge("Precision", meta.dtPrecision?.let { "${(it * 100).toInt()}%" } ?: "Not evaluated yet", Modifier.weight(1f))
                    MetricBadge("Recall", meta.dtRecall?.let { "${(it * 100).toInt()}%" } ?: "Not evaluated yet", Modifier.weight(1f))
                    MetricBadge("F1-Score", meta.dtF1?.toString() ?: "Not evaluated yet", Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        MedicalDisclaimerCard()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun StatusCheckRow(label: String, status: String, file: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
            Text(text = file, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ColorNormal)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ColorNormal)
        }
    }
}

@Composable
private fun SpecItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun MetricBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ColorNormal)
            Text(text = label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
