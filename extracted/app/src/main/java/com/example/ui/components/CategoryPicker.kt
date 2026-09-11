package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomCategoryEntity
import com.example.model.AppLanguage
import com.example.model.Category
import com.example.model.CategoryGroup
import com.example.model.CategoryRegistry
import com.example.model.TransactionType
import com.example.util.Strings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryPicker(
    transactionType: TransactionType,
    selectedCategoryKey: String,
    customCategories: List<CustomCategoryEntity>,
    language: AppLanguage,
    strings: Strings,
    onCategorySelected: (Category) -> Unit,
    onAddNewCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (transactionType == TransactionType.INCOME) {
        // Income Categories: Built-in + Custom Income (excluding archived unless currently selected)
        val customIncomeCategories = customCategories
            .filter { it.type == "INCOME" && (!it.isArchived || it.key == selectedCategoryKey) }
            .map { CategoryRegistry.customEntityToCategory(it) }
        val allIncomeCategories = CategoryRegistry.incomeCategories + customIncomeCategories

        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allIncomeCategories.forEach { category ->
                    val isSelected = category.key == selectedCategoryKey
                    val categoryName = if (language == AppLanguage.BN) category.nameBn else category.nameEn

                    CategoryChip(
                        name = categoryName,
                        icon = category.icon,
                        color = category.color,
                        isSelected = isSelected,
                        testTag = "category_option_${category.key}",
                        onClick = { onCategorySelected(category) }
                    )
                }
            }

            // Add Custom Category Button
            OutlinedButton(
                onClick = onAddNewCategoryClick,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("add_custom_category_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.addCustomCategoryBtn,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        // Expense: 2-Tier Grouped Category Picker (excluding archived unless currently selected)
        val customExpenseCategories = customCategories
            .filter { it.type == "EXPENSE" && (!it.isArchived || it.key == selectedCategoryKey) }
            .map { CategoryRegistry.customEntityToCategory(it) }

        // Determine initial active group based on currently selectedCategoryKey
        val activeGroupKeyFromSelection = remember(selectedCategoryKey, customExpenseCategories) {
            if (customExpenseCategories.any { it.key == selectedCategoryKey }) {
                "GRP_CUSTOM"
            } else {
                CategoryRegistry.expenseGroups.firstOrNull { group ->
                    group.categories.any { it.key == selectedCategoryKey }
                }?.key ?: CategoryRegistry.expenseGroups.first().key
            }
        }

        var activeGroupKey by remember(selectedCategoryKey) {
            mutableStateOf(activeGroupKeyFromSelection)
        }

        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Active Custom Expense Categories (always visible immediately in Add Expense)
            if (customExpenseCategories.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.BN) "কাস্টম খাত" else "Custom Categories",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        customExpenseCategories.forEach { category ->
                            val isSelected = category.key == selectedCategoryKey
                            val categoryName = if (language == AppLanguage.BN) category.nameBn else category.nameEn

                            CategoryChip(
                                name = categoryName,
                                icon = category.icon,
                                color = category.color,
                                isSelected = isSelected,
                                testTag = "category_option_${category.key}",
                                onClick = { onCategorySelected(category) }
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
            }

            // Horizontal Group Selector Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryRegistry.expenseGroups.forEach { group ->
                    val isGroupActive = group.key == activeGroupKey
                    val groupName = if (language == AppLanguage.BN) group.nameBn else group.nameEn

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { activeGroupKey = group.key }
                            .border(
                                width = if (isGroupActive) 1.5.dp else 1.dp,
                                color = if (isGroupActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("category_group_${group.key}"),
                        color = if (isGroupActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        tonalElevation = if (isGroupActive) 3.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = group.emoji, fontSize = 17.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = groupName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isGroupActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isGroupActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Custom Categories Group Tab if any custom expense categories exist
                if (customExpenseCategories.isNotEmpty()) {
                    val isCustomActive = activeGroupKey == "GRP_CUSTOM"
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { activeGroupKey = "GRP_CUSTOM" }
                            .border(
                                width = if (isCustomActive) 1.5.dp else 1.dp,
                                color = if (isCustomActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("category_group_GRP_CUSTOM"),
                        color = if (isCustomActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⭐", fontSize = 17.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == AppLanguage.BN) "কাস্টম" else "Custom",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isCustomActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCustomActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Subcategories in Active Group
            val currentCategories = if (activeGroupKey == "GRP_CUSTOM") {
                customExpenseCategories
            } else {
                CategoryRegistry.expenseGroups.find { it.key == activeGroupKey }?.categories ?: emptyList()
            }

            AnimatedVisibility(
                visible = currentCategories.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentCategories.forEach { category ->
                        val isSelected = category.key == selectedCategoryKey
                        val categoryName = if (language == AppLanguage.BN) category.nameBn else category.nameEn

                        CategoryChip(
                            name = categoryName,
                            icon = category.icon,
                            color = category.color,
                            isSelected = isSelected,
                            testTag = "category_option_${category.key}",
                            onClick = { onCategorySelected(category) }
                        )
                    }
                }
            }

            // Add Custom Category Button
            OutlinedButton(
                onClick = onAddNewCategoryClick,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("add_custom_category_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.addCustomCategoryBtn,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    name: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(14.dp)
            )
            .testTag(testTag),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f) else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
