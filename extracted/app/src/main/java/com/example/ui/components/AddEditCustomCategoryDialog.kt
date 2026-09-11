package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.entity.CustomCategoryEntity
import com.example.model.AppLanguage
import com.example.model.CategoryRegistry
import com.example.model.TransactionType
import com.example.util.Strings

@Composable
fun AddEditCustomCategoryDialog(
    initialType: TransactionType = TransactionType.EXPENSE,
    existingCategory: CustomCategoryEntity? = null,
    strings: Strings,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (nameBn: String, nameEn: String, type: TransactionType, iconName: String, colorHex: String) -> Unit
) {
    var nameBn by remember { mutableStateOf(existingCategory?.nameBn ?: "") }
    var nameEn by remember { mutableStateOf(existingCategory?.nameEn ?: "") }
    var selectedType by remember {
        mutableStateOf(
            if (existingCategory != null) {
                if (existingCategory.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
            } else {
                initialType
            }
        )
    }

    var selectedIconName by remember {
        mutableStateOf(existingCategory?.iconName ?: CategoryRegistry.availableCustomIcons.first().first)
    }

    val availableColors = listOf(
        "#2E7D32" to Color(0xFF2E7D32), // Forest Green
        "#00897B" to Color(0xFF00897B), // Teal
        "#1E88E5" to Color(0xFF1E88E5), // Ocean Blue
        "#7E57C2" to Color(0xFF7E57C2), // Purple
        "#FB8C00" to Color(0xFFFB8C00), // Amber Orange
        "#E53935" to Color(0xFFE53935), // Rose
        "#6D4C41" to Color(0xFF6D4C41)  // Earth Brown
    )

    var selectedColorHex by remember {
        mutableStateOf(existingCategory?.colorHex ?: availableColors.first().first)
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("add_custom_category_dialog"),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingCategory != null) strings.editCustomCategoryTitle else strings.newCustomCategoryTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = strings.cancelBtn)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Type Selector (if creating new)
                if (existingCategory == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = selectedType == TransactionType.EXPENSE,
                            onClick = { selectedType = TransactionType.EXPENSE },
                            label = { Text(strings.filterExpense) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_cat_type_expense"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        FilterChip(
                            selected = selectedType == TransactionType.INCOME,
                            onClick = { selectedType = TransactionType.INCOME },
                            label = { Text(strings.filterIncome) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_cat_type_income"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                // Name Bangla / primary
                OutlinedTextField(
                    value = nameBn,
                    onValueChange = {
                        nameBn = it
                        if (nameEn.isEmpty() || nameEn == nameBn) {
                            nameEn = it
                        }
                        errorMessage = null
                    },
                    label = { Text(strings.categoryNameLabel) },
                    placeholder = { Text(strings.categoryNameHint) },
                    isError = errorMessage != null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_cat_name_input")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Choose Icon
                Text(
                    text = strings.selectIconLabel,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryRegistry.availableCustomIcons.forEach { (iconKey, iconVec) ->
                        val isSelected = iconKey == selectedIconName
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable { selectedIconName = iconKey }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                ),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = iconVec,
                                    contentDescription = iconKey,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // Choose Color
                Text(
                    text = if (language == AppLanguage.BN) "রং নির্বাচন করুন" else "Select Color",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    availableColors.forEach { (hex, col) ->
                        val isSelected = hex == selectedColorHex
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameBn.isBlank()) {
                        errorMessage = if (language == AppLanguage.BN) "দয়া করে ক্যাটাগরির নাম লিখুন" else "Please enter category name"
                    } else {
                        onSave(
                            nameBn.trim(),
                            nameEn.trim().ifEmpty { nameBn.trim() },
                            selectedType,
                            selectedIconName,
                            selectedColorHex
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_custom_cat_btn")
            ) {
                Text(
                    text = if (existingCategory != null) strings.updateTransactionBtn else strings.createCategoryBtn,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("cancel_custom_cat_btn")
            ) {
                Text(strings.cancelBtn)
            }
        }
    )
}
