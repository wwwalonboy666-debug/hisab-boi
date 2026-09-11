package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.ui.theme.pressClickable
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TransactionEntity
import com.example.model.AppLanguage
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.EmptyStateNatureView
import com.example.ui.components.MonthSelector
import com.example.ui.components.TransactionItemRow
import com.example.ui.theme.financialExpenseColor
import com.example.ui.theme.financialIncomeColor
import com.example.util.CurrencyFormatter
import com.example.util.DateUtils
import com.example.viewmodel.HisabBoiViewModel

@Composable
fun TransactionsScreen(
    viewModel: HisabBoiViewModel,
    onOpenEditTransaction: (TransactionEntity) -> Unit,
    onOpenCalendar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val strings by viewModel.strings.collectAsState()

    val year by viewModel.selectedYear.collectAsState()
    val monthIndex by viewModel.selectedMonthIndex.collectAsState()

    val monthIncome by viewModel.selectedMonthIncome.collectAsState()
    val monthExpense by viewModel.selectedMonthExpense.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val customCategories by viewModel.customCategories.collectAsState()

    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    val netTotal = monthIncome - monthExpense
    val incomeColor = financialIncomeColor()
    val expenseColor = financialExpenseColor()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("transactions_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Title with Calendar Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.allTransactionsTitle,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .pressClickable { onOpenCalendar() }
                        .testTag("transactions_calendar_btn"),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = strings.calendarTitle,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.calendarTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Month Selector
        item {
            MonthSelector(
                year = year,
                monthIndexZeroBased = monthIndex,
                language = language,
                onPreviousMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() }
            )
        }

        // Month Summary Row
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = strings.filterIncome,
                            style = MaterialTheme.typography.labelSmall,
                            color = incomeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = CurrencyFormatter.format(monthIncome, language),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = strings.filterExpense,
                            style = MaterialTheme.typography.labelSmall,
                            color = expenseColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = CurrencyFormatter.format(monthExpense, language),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column {
                        Text(
                            text = strings.netTotalLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = CurrencyFormatter.format(netTotal, language),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (netTotal >= 0) incomeColor else expenseColor
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transactions_search_input"),
                placeholder = { Text(strings.searchHint, style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        // Filter Chips Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All
                FilterChip(
                    selected = filterType == "ALL",
                    onClick = { viewModel.filterType.value = "ALL" },
                    label = { Text(strings.filterAll) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("filter_chip_all")
                )

                // Expense
                FilterChip(
                    selected = filterType == "EXPENSE",
                    onClick = { viewModel.filterType.value = "EXPENSE" },
                    label = { Text(strings.filterExpense) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = expenseColor.copy(alpha = 0.16f),
                        selectedLabelColor = expenseColor
                    ),
                    modifier = Modifier.testTag("filter_chip_expense")
                )

                // Income
                FilterChip(
                    selected = filterType == "INCOME",
                    onClick = { viewModel.filterType.value = "INCOME" },
                    label = { Text(strings.filterIncome) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = incomeColor.copy(alpha = 0.16f),
                        selectedLabelColor = incomeColor
                    ),
                    modifier = Modifier.testTag("filter_chip_income")
                )
            }
        }

        // Transactions grouped by date
        if (filteredTransactions.isEmpty()) {
            item {
                EmptyStateNatureView(
                    title = strings.emptyTransactionsTitle,
                    subtitle = strings.emptyTransactionsSub
                )
            }
        } else {
            val groupedByDate = filteredTransactions.groupBy {
                DateUtils.formatDateGroup(it.dateMillis, language)
            }

            groupedByDate.forEach { (dateHeader, txList) ->
                item {
                    Text(
                        text = dateHeader,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }

                items(txList, key = { it.id }) { tx ->
                    TransactionItemRow(
                        transaction = tx,
                        language = language,
                        customCategories = customCategories,
                        onEdit = onOpenEditTransaction,
                        onDelete = { transactionToDelete = it },
                        showActions = true
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Confirmation dialog for deletion
    if (transactionToDelete != null) {
        ConfirmationDialog(
            title = strings.deleteConfirmTitle,
            message = strings.deleteConfirmMsg,
            confirmButtonText = strings.deleteBtn,
            dismissButtonText = strings.cancelBtn,
            onConfirm = {
                transactionToDelete?.let { viewModel.deleteTransaction(it) }
                transactionToDelete = null
            },
            onDismiss = { transactionToDelete = null }
        )
    }
}
