package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TransactionEntity
import com.example.model.AppLanguage
import com.example.model.TransactionType
import com.example.ui.components.ConfirmationDialog
import com.example.ui.components.TransactionItemRow
import com.example.ui.theme.financialExpenseColor
import com.example.ui.theme.financialIncomeColor
import com.example.ui.theme.pressClickable
import com.example.ui.theme.pressScale
import com.example.util.CurrencyFormatter
import com.example.util.DateUtils
import com.example.util.Strings
import com.example.viewmodel.HisabBoiViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: HisabBoiViewModel,
    onNavigateBack: () -> Unit,
    onOpenAddTransaction: (Long, TransactionType) -> Unit,
    onOpenEditTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val strings by viewModel.strings.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val customCategories by viewModel.customCategories.collectAsState()

    // Real-time Today reference
    val todayCal = remember { Calendar.getInstance() }
    val currentRealYear = todayCal.get(Calendar.YEAR)
    val currentRealMonth = todayCal.get(Calendar.MONTH)
    val currentRealDay = todayCal.get(Calendar.DAY_OF_MONTH)

    // Calendar state
    var displayYear by remember { mutableIntStateOf(currentRealYear) }
    var displayMonth by remember { mutableIntStateOf(currentRealMonth) }
    var selectedDay by remember { mutableIntStateOf(currentRealDay) }

    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    // Helper to calculate days in month and start day offset (0 = Sunday)
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, displayYear)
        set(Calendar.MONTH, displayMonth)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // Sunday is 0

    // Filter transactions for currently displayed month
    val monthTransactions = remember(allTransactions, displayYear, displayMonth) {
        allTransactions.filter { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
            txCal.get(Calendar.YEAR) == displayYear && txCal.get(Calendar.MONTH) == displayMonth
        }
    }

    // Monthly totals
    val monthIncome = remember(monthTransactions) {
        monthTransactions.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
    }
    val monthExpense = remember(monthTransactions) {
        monthTransactions.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
    }
    val monthBalance = monthIncome - monthExpense

    // Map day to transactions in this month
    val dayTransactionsMap = remember(monthTransactions) {
        val map = mutableMapOf<Int, MutableList<TransactionEntity>>()
        for (tx in monthTransactions) {
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
            val day = txCal.get(Calendar.DAY_OF_MONTH)
            map.getOrPut(day) { mutableListOf() }.add(tx)
        }
        map
    }

    // Selected Day Transactions & Totals
    val selectedDayTransactions = dayTransactionsMap[selectedDay] ?: emptyList()
    val dayIncome = selectedDayTransactions.filter { it.type == TransactionType.INCOME.name }.sumOf { it.amount }
    val dayExpense = selectedDayTransactions.filter { it.type == TransactionType.EXPENSE.name }.sumOf { it.amount }
    val dayNet = dayIncome - dayExpense

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = strings.calendarTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.calendarSubtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("calendar_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.cancelBtn
                        )
                    }
                },
                actions = {
                    // Quick jump to Today button
                    Surface(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                displayYear = currentRealYear
                                displayMonth = currentRealMonth
                                selectedDay = currentRealDay
                            }
                            .testTag("calendar_jump_today_btn"),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.todayLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month Selector Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (displayMonth == 0) {
                                    displayMonth = 11
                                    displayYear -= 1
                                } else {
                                    displayMonth -= 1
                                }
                                selectedDay = 1
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("calendar_prev_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Month"
                            )
                        }

                        Text(
                            text = DateUtils.formatMonthYear(displayYear, displayMonth, language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.testTag("calendar_month_title")
                        )

                        IconButton(
                            onClick = {
                                if (displayMonth == 11) {
                                    displayMonth = 0
                                    displayYear += 1
                                } else {
                                    displayMonth += 1
                                }
                                selectedDay = 1
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("calendar_next_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Month"
                            )
                        }
                    }
                }
            }

            // Monthly Summary Overview Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Monthly Income
                        Column {
                            Text(
                                text = strings.monthlyIncomeLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = financialIncomeColor(),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyFormatter.format(monthIncome, language),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = financialIncomeColor()
                            )
                        }

                        // Monthly Expense
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = strings.monthlyExpenseLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = financialExpenseColor(),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyFormatter.format(monthExpense, language),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = financialExpenseColor()
                            )
                        }

                        // Monthly Net
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = strings.monthlyNetLabel,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyFormatter.format(monthBalance, language),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (monthBalance >= 0) financialIncomeColor() else financialExpenseColor()
                            )
                        }
                    }
                }
            }

            // Monthly Calendar Grid
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Weekday Headers (Sunday to Saturday)
                        val weekdayHeaders = if (language == AppLanguage.BN) {
                            listOf("রবি", "সোম", "মঙ্গল", "বুধ", "বৃহঃ", "শুক্র", "শনি")
                        } else {
                            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weekdayHeaders.forEachIndexed { index, name ->
                                val isWeekend = index == 5 || index == 6 // Fri/Sat weekend in BD or Sun
                                Text(
                                    text = name,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWeekend) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Grid of days
                        val totalCells = startDayOfWeek + daysInMonth
                        val numRows = (totalCells + 6) / 7

                        for (row in 0 until numRows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (col in 0 until 7) {
                                    val cellIndex = row * 7 + col
                                    val dayNum = cellIndex - startDayOfWeek + 1

                                    if (dayNum in 1..daysInMonth) {
                                        val isToday = displayYear == currentRealYear &&
                                                displayMonth == currentRealMonth &&
                                                dayNum == currentRealDay
                                        val isSelected = dayNum == selectedDay
                                        val dayTxs = dayTransactionsMap[dayNum] ?: emptyList()
                                        val hasIncome = dayTxs.any { it.type == TransactionType.INCOME.name }
                                        val hasExpense = dayTxs.any { it.type == TransactionType.EXPENSE.name }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    when {
                                                        isSelected -> MaterialTheme.colorScheme.primary
                                                        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .border(
                                                    width = if (isToday && !isSelected) 1.5.dp else 0.dp,
                                                    color = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .clickable { selectedDay = dayNum }
                                                .testTag("calendar_day_$dayNum"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                val dayText = if (language == AppLanguage.BN) {
                                                    CurrencyFormatter.toBanglaDigits(dayNum.toString())
                                                } else {
                                                    dayNum.toString()
                                                }
                                                Text(
                                                    text = dayText,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontSize = 15.sp,
                                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium
                                                    ),
                                                    color = when {
                                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                                        isToday -> MaterialTheme.colorScheme.primary
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )

                                                // Transaction indicator dots
                                                if (hasIncome || hasExpense) {
                                                    Row(
                                                        modifier = Modifier.padding(top = 2.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                    ) {
                                                        if (hasIncome) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(5.dp)
                                                                    .clip(CircleShape)
                                                                    .background(if (isSelected) Color.White else financialIncomeColor())
                                                            )
                                                        }
                                                        if (hasExpense) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(5.dp)
                                                                    .clip(CircleShape)
                                                                    .background(if (isSelected) Color.White.copy(alpha = 0.8f) else financialExpenseColor())
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // Empty cell for alignment
                                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Selected Day Section Header & Totals
            item {
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, displayYear)
                    set(Calendar.MONTH, displayMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }
                val selectedDateFormatted = DateUtils.formatDisplayDate(selectedCal.timeInMillis, language)

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedDateFormatted,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Quick Add Transaction for this date
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .pressClickable {
                                    val targetMillis = selectedCal.timeInMillis
                                    onOpenAddTransaction(targetMillis, TransactionType.EXPENSE)
                                }
                                .testTag("calendar_add_tx_btn"),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.addTransactionBtn,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Daily totals badge row
                    if (selectedDayTransactions.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = financialIncomeColor().copy(alpha = 0.12f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = strings.dailyIncomeLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = financialIncomeColor()
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CurrencyFormatter.format(dayIncome, language),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = financialIncomeColor()
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = financialExpenseColor().copy(alpha = 0.12f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = strings.dailyExpenseLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = financialExpenseColor()
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CurrencyFormatter.format(dayExpense, language),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = financialExpenseColor()
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = strings.monthlyNetLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CurrencyFormatter.format(dayNet, language),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (dayNet >= 0) financialIncomeColor() else financialExpenseColor()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Transactions list for selected day
            if (selectedDayTransactions.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🌿", fontSize = 34.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = strings.noTransactionsForDate,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(selectedDayTransactions, key = { it.id }) { tx ->
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

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Delete Confirmation Dialog
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
