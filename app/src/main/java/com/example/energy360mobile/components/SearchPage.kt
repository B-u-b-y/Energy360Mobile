package com.example.energy360mobile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.energy360mobile.R

@Composable
fun SearchPage() {
    var searchQuery by remember { mutableStateOf("") }
    
    val categories = listOf("Yoga", "Crossfit", "Nuoto", "Pilates", "Pesi", "Cardio")

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        //top-box (big-one)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = stringResource(id = R.string.what_doin),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(id = R.string.explore),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-28).dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                placeholder = { Text(stringResource(id = R.string.find_by)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }

        Text(
            text = stringResource(id = R.string.most_wanted),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(4).forEach { category ->
                SuggestionChip(
                    onClick = { searchQuery = category },
                    label = { Text(category) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        labelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.suggested_result),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            val results = listOf(
                SearchResult("Hatha Yoga", "Lunedì e Mercoledì - 18:30", "Yoga", Icons.Default.SelfImprovement),
                SearchResult("Total Body Workout", "Ogni giorno - 09:00", "Pesi", Icons.Default.FitnessCenter),
                SearchResult("Pilates Matwork", "Martedì e Giovedì - 17:00", "Pilates", Icons.Default.AccessibilityNew)
            )

            items(results) { item ->
                SearchItemCard(item)
            }
        }
    }
}

data class SearchResult(val title: String, val time: String, val tag: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun SearchItemCard(result: SearchResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(result.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
            }
            
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = result.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = result.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
