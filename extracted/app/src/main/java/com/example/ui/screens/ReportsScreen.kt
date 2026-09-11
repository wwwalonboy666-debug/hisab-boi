package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.EmptyStateNatureView
import com.example.ui.components.MonthSelector
import com.example.ui.theme.financialExpenseColor
import com.example.ui.theme.financialIncomeColor
import com.example.util.CurrencyFormatter
import com.example.viewmodel.CategorySpend
import com.example.viewmodel.HisabBoiViewModel

@Composable
fun ReportsScreen(
    viewModel: HisabBoiViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val strings by viewModel.strings.collectAsState()

    val year by viewModel.selectedYear.collectAsState()
    val monthIndex by viewModel.selectedMonthIndex.collectAsState()

    val reportState by viewModel.reportState.collectAsState()

    val incomeColor = financialIncomeColor()
    val expenseColor = financialExpenseColor()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("reports_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Text(
                text = strings.reportsTitle,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
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

        // 3 Key Financial Indicators (Income, Expense, Net)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Income
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = strings.totalIncomeReport,
                            style = MaterialTheme.typography.labelSmall,
                            color = incomeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.format(reportState.totalIncome, language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Total Expense
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = strings.totalExpenseReport,
                            style = MaterialTheme.typography.labelSmall,
                            color = expenseColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.format(reportState.totalExpense, language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Net Savings
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = strings.netBalanceReport,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.format(reportState.netBalance, language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (reportState.netBalance >= 0) incomeColor else expenseColor
                        )
                    }
                }
            }
        }

        // Empty state check
        if (reportState.categorySpends.isEmpty() && reportState.totalIncome <= 0.0) {
            item {
                EmptyStateNatureView(
                    title = strings.emptyReportTitle,
                    subtitle = strings.emptyReportSub
                )
            }
        } else {
            // Donut Chart + Category Breakdown
            if (reportState.categorySpends.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = strings.categoryBreakdownTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Donut Chart Canvas
                            Box(
                                modifier = Modifier.size(170.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.size(150.dp)) {
                                    var currentAngle = -90f
                                    val strokeWidth = 24.dp.toPx()

                                    reportState.categorySpends.forEach { spend ->
                                        val sweep = (spend.percentage / 100f) * 360f
                                        drawArc(
                                            color = spend.category.color,
                                            startAngle = currentAngle,
                                            sweepAngle = sweep,
                                            useCenter = false,
                                            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                            size = Size(size.width - strokeWidth, size.height - strokeWidth),
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                        )
                                        currentAngle += sweep
                                    }
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = strings.filterExpense,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = CurrencyFormatter.format(reportState.totalExpense, language),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // Category List with progress bars
                items(reportState.categorySpends) { spend ->
                    CategorySpendRow(spend = spend, language = language)
                }
            }

            // Financial Insights Section
            item {
                Text(
                    text = strings.financialInsightsTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Insight Card 1: Top Category
            if (reportState.topCategory != null) {
                val topCatName = if (language == AppLanguage.BN) reportState.topCategory!!.nameBn else reportState.topCategory!!.nameEn
                val insightText = if (language == AppLanguage.BN) {
                    "এই মাসে আপনার সবচেয়ে বেশি খরচ হয়েছে $topCatName খাতে (${CurrencyFormatter.format(reportState.topCategoryAmount, language)})।"
                } else {
                    "Your highest expense this month was on $topCatName (${CurrencyFormatter.format(reportState.topCategoryAmount, language)})."
                }
                item {
                    InsightCard(
                        iconEmoji = "💡",
                        text = insightText
                    )
                }
            }

            // Insight Card 2: Income Spent Percentage
            if (reportState.incomeSpentPercentage != null && reportState.totalIncome > 0) {
                val pctRounded = Math.round(reportState.incomeSpentPercentage!!)
                val pctStr = CurrencyFormatter.formatRawNumber(pctRounded.toDouble(), language)
                val insightText = if (language == AppLanguage.BN) {
                    "এই মাসে আপনার আয়ের $pctStr% খরচ হয়েছে।"
                } else {
                    "You have spent $pctStr% of your income this month."
                }
                item {
                    InsightCard(
                        iconEmoji = "📊",
                        text = insightText
                    )
                }
            }

            // Insight Card 3: Compared to previous month
            if (reportState.expenseDifferencePercentage != null) {
                val diff = reportState.expenseDifferencePercentage!!
                val diffRounded = Math.abs(Math.round(diff))
                val diffStr = CurrencyFormatter.formatRawNumber(diffRounded.toDouble(), language)
                val isIncrease = diff > 0
                val insightText = if (language == AppLanguage.BN) {
                    if (isIncrease) "গত মাসের তুলনায় আপনার খরচ $diffStr% বেড়েছে।"
                    else "গত মাসের তুলনায় আপনার খরচ $diffStr% কমেছে 🌿"
                } else {
                    if (isIncrease) "Your spending increased by $diffStr% compared to last month."
                    else "Your spending decreased by $diffStr% compared to last month 🌿"
                }
                item {
                    InsightCard(
                        iconEmoji = if (isIncrease) "📈" else "🌱",
                        text = insightText
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CategorySpendRow(
    spend: CategorySpend,
    language: AppLanguage
) {
    val categoryName = if (language == AppLanguage.BN) spend.category.nameBn else spend.category.nameEn
    val percentageFormatted = CurrencyFormatter.formatRawNumber(spend.percentage.toDouble(), language) + "%"

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(spend.category.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = spend.category.icon,
                            contentDescription = null,
                            tint = spend.category.color,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = CurrencyFormatter.format(spend.amount, language),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = percentageFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (spend.percentage / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = spend.category.color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun InsightCard(
    iconEmoji: String,
    text: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = iconEmoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
