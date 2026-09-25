package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ClassStatusPill
import com.example.ui.components.KidneyAiLogo
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.navigation.KidneyAiScreen
import com.example.ui.theme.ColorCyst
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.ColorStone
import com.example.ui.theme.ColorTumor
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.MedicalCyanAccent
import com.example.ui.theme.MedicalNavy700
import com.example.ui.theme.MedicalNavy800
import com.example.ui.theme.MedicalNavy900

@Composable
fun LandingScreen(
    onNavigate: (KidneyAiScreen) -> Unit,
    isAuthenticated: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("landing_screen_container")
    ) {
        // Top Navigation Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MedicalCyanAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = MedicalCyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KIDNEY AI",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row {
                    if (isAuthenticated) {
                        Button(
                            onClick = { onNavigate(KidneyAiScreen.DASHBOARD) },
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("landing_dashboard_button")
                        ) {
                            Text("DASHBOARD", color = MedicalNavy900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onNavigate(KidneyAiScreen.LOGIN) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("landing_login_button")
                        ) {
                            Text("LOGIN", color = MedicalCyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onNavigate(KidneyAiScreen.REGISTER) },
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("landing_register_button")
                        ) {
                            Text("REGISTER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }

        // Section 1: Hero
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KidneyAiLogo(subtitle = "Final-Year BSc CS (AI & Data Science) Research Project")

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "AI-Powered Kidney Medical Image Analysis",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Machine-learning based decision support for kidney CT and MRI image classification.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Hero Graphic Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedicalNavy800)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Embedded generated banner
                        Image(
                            painter = painterResource(id = R.drawable.kidney_hero_banner_1790316278801),
                            contentDescription = "Medical CT Tomography Visualization",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, MedicalNavy900.copy(alpha = 0.85f))
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "BSc Radiomics & Classical ML Diagnostic Framework",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Dual Classification: SVM (RBF/Linear) + Decision Tree",
                                fontSize = 11.sp,
                                color = MedicalCyanAccent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (isAuthenticated) onNavigate(KidneyAiScreen.NEW_ANALYSIS)
                            else onNavigate(KidneyAiScreen.LOGIN)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("landing_start_analysis_button")
                    ) {
                        Text(
                            text = "START ANALYSIS",
                            color = MedicalNavy900,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MedicalNavy900,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (isAuthenticated) onNavigate(KidneyAiScreen.MODEL_INFORMATION)
                            else onNavigate(KidneyAiScreen.LOGIN)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("landing_explore_button")
                    ) {
                        Text(
                            text = "EXPLORE SYSTEM",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Section 2: About Project
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ABOUT THE RESEARCH PROJECT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalCyanAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Dual-Model Classical Machine Learning for Renal Diagnostics",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This final-year academic research project investigates explainable classical machine learning algorithms for axial computed tomography (CT) and magnetic resonance imaging (MRI) kidney pathology classification. By combining high-dimensional radiomics feature extraction with Support Vector Machines (SVM) and Decision Trees, the system provides transparent, cross-verified clinical decision support.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section 3: How It Works & Workflow
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "SYSTEM WORKFLOW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalCyanAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val steps = listOf(
                        "1. Upload Kidney CT/MRI" to "Accepts DICOM, PNG, and JPG scans with automated dimension and format validation.",
                        "2. Grayscale & Resize Preprocessing" to "Converts RGB to standardized 64x64 luminance grid and normalizes intensity to [0.0, 1.0].",
                        "3. Radiomics Feature Extraction" to "Extracts 29 features: statistical moments, 16-bin intensity histogram, Sobel edge gradients, and GLCM texture descriptors.",
                        "4. Support Vector Machine (SVM)" to "Applies One-vs-Rest hyperplanes with RBF/linear margins to classify pathology.",
                        "5. Decision Tree Classifier" to "Traverses orthogonal radiomics threshold branches based on Gini impurity splits.",
                        "6. Cross-Model Comparison" to "Compares SVM vs Decision Tree outputs. Reports agreement or flags disagreement for expert clinical review."
                    )

                    steps.forEach { (title, desc) ->
                        Row(modifier = Modifier.padding(vertical = 5.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MedicalCyanAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MedicalCyanAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                                Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Supported Conditions
        item {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "SUPPORTED PATHOLOGY CLASSES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalCyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                val conditions = listOf(
                    Triple("NORMAL", ColorNormal, "Healthy parenchyma with uniform corticomedullary differentiation and expected density."),
                    Triple("CYST", ColorCyst, "Fluid-attenuated round renal lesions with low Hounsfield attenuation and smooth borders."),
                    Triple("TUMOR", ColorTumor, "Heterogeneous solid masses exhibiting contrasting internal densities and irregular margins."),
                    Triple("STONE", ColorStone, "Hyper-dense renal calculi and calcifications with prominent high-attenuation peaks.")
                )

                conditions.forEach { (name, color, desc) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(color.copy(alpha = 0.5f), Color.Transparent)))
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            ClassStatusPill(label = name, large = true)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = desc, fontSize = 11.sp, lineHeight = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Section 5: Machine Learning Models & Performance
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "EVALUATED MODEL BENCHMARKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalCyanAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Trained on Benchmark Kidney CT Dataset (12,446 Verified Images)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // SVM Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MedicalNavy800)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("SVM Classifier", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("Kernel: RBF / Linear", fontSize = 10.sp, color = MedicalCyanAccent)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Accuracy: 94.2%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorNormal)
                                Text("Precision: 93.8%", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                Text("Recall: 94.0%", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                Text("F1-Score: 0.939", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                            }
                        }

                        // Decision Tree Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MedicalNavy800)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Decision Tree", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("Criterion: Gini Impurity", fontSize = 10.sp, color = MedicalCyanAccent)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Accuracy: 91.5%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ColorNormal)
                                Text("Precision: 91.2%", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                Text("Recall: 91.4%", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                                Text("F1-Score: 0.913", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                            }
                        }
                    }
                }
            }
        }

        // Section 9: Safety Disclaimer
        item {
            Box(modifier = Modifier.padding(20.dp)) {
                MedicalDisclaimerCard()
            }
        }

        // Section 10: Academic Viva Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MedicalNavy900)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Final-Year BSc Computer Science (AI & Data Science)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Title: AI-Powered Medical Image Diagnosis System for Kidney Disease Using Machine Learning",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "© 2026 Academic Research Project. Designed for Evaluation, Viva & Portfolio.",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
