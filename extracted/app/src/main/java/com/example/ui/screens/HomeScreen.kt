package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.pressScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SavingsGoalEntity
import com.example.data.entity.TransactionEntity
import com.example.model.AppLanguage
import com.example.model.TransactionType
import com.example.ui.components.EmptyStateNatureView
import com.example.ui.components.GrowingPlantVisualizer
import com.example.ui.components.HomeDebtSummaryCard
import com.example.ui.components.MainBalanceCard
import com.example.ui.components.MonthlyBudgetCard
import com.example.ui.components.PeacefulNatureHeaderCard
import com.example.ui.components.ProfileAvatar
import com.example.ui.components.QuickActionButtons
import com.example.ui.components.TransactionItemRow
import com.example.util.CurrencyFormatter
import com.example.util.Strings
import com.example.viewmodel.HisabBoiViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: HisabBoiViewModel,
    onOpenAddTransaction: (TransactionType) -> Unit,
    onOpenEditTransaction: (TransactionEntity) -> Unit,
    onOpenSetBudget: () -> Unit,
    onOpenSavings: () -> Unit,
    onOpenAllTransactions: () -> Unit,
    onOpenDebtManager: () -> Unit = {},
    onOpenCalendar: () -> Unit = {},
    onOpenBackupRestore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val strings by viewModel.strings.collectAsState()

    val overallBalance by viewModel.overallBalance.collectAsState()
    val overallIncome by viewModel.overallIncome.collectAsState()
    val overallExpense by viewModel.overallExpense.collectAsState()

    val budgetState by viewModel.budgetState.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val savingsGoals by viewModel.allSavingsGoals.collectAsState()
    val customCategories by viewModel.customCategories.collectAsState()
    val lentRemaining by viewModel.totalLentRemaining.collectAsState()
    val borrowedRemaining by viewModel.totalBorrowedRemaining.collectAsState()

    val userName by viewModel.userNameDisplay.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val backupState by viewModel.backupUiState.collectAsState()
    var bannerDismissed by remember { mutableStateOf(false) }

    val hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingText = strings.greeting(hourOfDay, userName)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Greeting Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = greetingText,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Clip
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = strings.greetingSub,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Calendar Button
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .pressClickable { onOpenCalendar() }
                                .testTag("home_calendar_btn"),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = strings.calendarTitle,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Profile Avatar (or peaceful leaf emblem if no photo)
                        ProfileAvatar(
                            photoPath = userProfile.photoPath,
                            photoUpdatedAt = userProfile.photoUpdatedAt,
                            size = 44.dp,
                            modifier = Modifier.testTag("home_profile_avatar")
                        )
                    }
                }
            }
        }

        // Nature Hero Visual Card
        item {
            PeacefulNatureHeaderCard()
        }

        // Opt-in Google Drive Backup Banner
        if (backupState.account == null && !bannerDismissed) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("opt_in_backup_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.CloudSync,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (language == AppLanguage.BN) "গুগল ড্রাইভে ব্যাকআপ" else "Google Drive Backup",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            IconButton(
                                onClick = { bannerDismissed = true },
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("dismiss_backup_banner_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = strings.cancelBtn,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = strings.backupBannerText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            lineHeight = 18.sp
                        )

                        Button(
                            onClick = onOpenBackupRestore,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .pressScale()
                                .testTag("backup_banner_action_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.backupBannerBtn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Total Balance Card
        item {
            MainBalanceCard(
                balance = overallBalance,
                income = overallIncome,
                expense = overallExpense,
                language = language,
                strings = strings
            )
        }

        // Quick Action Buttons
        item {
            QuickActionButtons(
                strings = strings,
                onAddExpense = { onOpenAddTransaction(TransactionType.EXPENSE) },
                onAddIncome = { onOpenAddTransaction(TransactionType.INCOME) }
            )
        }

        // Monthly Budget Card
        item {
            MonthlyBudgetCard(
                budgetState = budgetState,
                language = language,
                strings = strings,
                onSetBudgetClick = onOpenSetBudget
            )
        }

        // Debt Manager Summary Card
        item {
            HomeDebtSummaryCard(
                lentRemaining = lentRemaining,
                borrowedRemaining = borrowedRemaining,
                language = language,
                strings = strings,
                onViewDetailsClick = onOpenDebtManager
            )
        }

        // Savings Snapshot Card (if any goals exist)
        if (savingsGoals.isNotEmpty()) {
            val featuredGoal = savingsGoals.first()
            val progress = if (featuredGoal.targetAmount > 0)
                (featuredGoal.savedAmount / featuredGoal.targetAmount).toFloat()
            else 0f

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .pressClickable { onOpenSavings() }
                        .testTag("home_savings_card"),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.5.dp,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GrowingPlantVisualizer(
                            progress = progress,
                            stageName = strings.plantStageName(progress),
                            modifier = Modifier.size(90.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.savingsTitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = featuredGoal.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${CurrencyFormatter.format(featuredGoal.savedAmount, language)} / ${CurrencyFormatter.format(featuredGoal.targetAmount, language)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View Savings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.recentTransactionsTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (recentTransactions.isNotEmpty()) {
                    Text(
                        text = strings.viewAll,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .pressClickable { onOpenAllTransactions() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("view_all_transactions_btn")
                    )
                }
            }
        }

        // Recent Transactions List or Empty State
        if (recentTransactions.isEmpty()) {
            item {
                EmptyStateNatureView(
                    title = strings.emptyTransactionsTitle,
                    subtitle = strings.emptyTransactionsSub
                )
            }
        } else {
            items(recentTransactions, key = { it.id }) { tx ->
                TransactionItemRow(
                    transaction = tx,
                    language = language,
                    customCategories = customCategories,
                    onEdit = onOpenEditTransaction,
                    onDelete = { viewModel.deleteTransaction(it) },
                    showActions = false
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
