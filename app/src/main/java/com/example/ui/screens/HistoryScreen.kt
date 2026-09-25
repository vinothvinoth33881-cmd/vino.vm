package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AnalysisEntity
import com.example.ui.components.ClassStatusPill
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.navigation.KidneyAiScreen
import com.example.ui.theme.ColorCyst
import com.example.ui.theme.ColorDisagreement
import com.example.ui.theme.ColorNormal
import com.example.ui.theme.ColorStone
import com.example.ui.theme.ColorTumor
import com.example.ui.theme.MedicalCyanAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    analyses: List<AnalysisEntity>,
    onSelectAnalysis: (AnalysisEntity) -> Unit,
    onDeleteAnalysis: (String) -> Unit,
    onNavigate: (KidneyAiScreen) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var itemToDelete by remember { mutableStateOf<AnalysisEntity?>(null) }

    val filteredList = analyses.filter { item ->
        val matchesSearch = searchQuery.isBlank() ||
                item.id.contains(searchQuery, ignoreCase = true) ||
                item.imageName.contains(searchQuery, ignoreCase = true) ||
                item.finalPrediction.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "ALL" -> true
            "NORMAL" -> item.finalPrediction.equals("NORMAL", ignoreCase = true)
            "CYST" -> item.finalPrediction.equals("CYST", ignoreCase = true)
            "TUMOR" -> item.finalPrediction.equals("TUMOR", ignoreCase = true)
            "STONE" -> item.finalPrediction.equals("STONE", ignoreCase = true)
            "DISAGREEMENT" -> !item.isAgreement
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("history_screen_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Analysis History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${filteredList.size} logged diagnostic cases",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { onNavigate(KidneyAiScreen.NEW_ANALYSIS) },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyanAccent),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF070E17), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Scan", color = Color(0xFF070E17), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by ID, file name, or pathology...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MedicalCyanAccent) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips Horizontal Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("ALL", "NORMAL", "CYST", "TUMOR", "STONE", "DISAGREEMENT")
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MedicalCyanAccent.copy(alpha = 0.2f),
                        selectedLabelColor = MedicalCyanAccent
                    ),
                    modifier = Modifier.testTag("filter_chip_${filter.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (analyses.isEmpty()) "No diagnostic scans saved yet" else "No matching cases found",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Upload scans or adjust search filters.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(item.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectAnalysis(item)
                                onNavigate(KidneyAiScreen.ANALYSIS_RESULT)
                            }
                            .testTag("history_item_${item.id}"),
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
                                        text = item.id,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MedicalCyanAccent
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    ClassStatusPill(label = item.finalPrediction)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.imageName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Text(
                                    text = "SVM: ${item.svmPrediction}  |  DT: ${item.decisionTreePrediction}  •  ${item.processingTimeSec}s",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dateStr,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                    if (item.confidence != null) {
                                        Text(
                                            text = "  •  Confidence: ${item.confidence}%",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ColorNormal
                                        )
                                    }
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        onSelectAnalysis(item)
                                        onNavigate(KidneyAiScreen.REPORTS)
                                    },
                                    modifier = Modifier.testTag("report_button_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = "Report",
                                        tint = MedicalCyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { itemToDelete = item },
                                    modifier = Modifier.testTag("delete_button_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = ColorTumor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        itemToDelete?.let { target ->
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("Delete Analysis Record") },
                text = { Text("Are you sure you want to permanently delete case ${target.id}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteAnalysis(target.id)
                            itemToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorTumor)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        MedicalDisclaimerCard()
    }
}
