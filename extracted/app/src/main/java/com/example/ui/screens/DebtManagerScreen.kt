package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DebtFilterMode
import com.example.model.DebtType
import com.example.model.DebtViewMode
import com.example.model.DebtWithPayments
import com.example.ui.components.DebtEmptyState
import com.example.ui.components.DebtItemCard
import com.example.ui.components.DebtManagerHeaderCards
import com.example.ui.components.PersonDebtGroupCard
import com.example.viewmodel.HisabBoiViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtManagerScreen(
    viewModel: HisabBoiViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onNavigateBack() }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    val language by viewModel.currentLanguage.collectAsState()
    val strings by viewModel.strings.collectAsState()
    val lentTotal by viewModel.totalLentRemaining.collectAsState()
    val borrowedTotal by viewModel.totalBorrowedRemaining.collectAsState()
    val filterMode by viewModel.debtFilterMode.collectAsState()
    val viewMode by viewModel.debtViewMode.collectAsState()
    val searchQuery by viewModel.debtSearchQuery.collectAsState()
    val filteredDebts by viewModel.filteredDebts.collectAsState()
    val personSummaries by viewModel.personDebtSummaries.collectAsState()

    // Sheet states
    var showAddEditSheet by remember { mutableStateOf(false) }
    var addDebtType by remember { mutableStateOf(DebtType.LENT) }
    var editingDebt by remember { mutableStateOf<DebtWithPayments?>(null) }

    var selectedDetailDebtId by remember { mutableStateOf<Long?>(null) }
    val activeDetailsDebt = filteredDebts.find { it.debt.id == selectedDetailDebtId }

    var selectedRepaymentDebtId by remember { mutableStateOf<Long?>(null) }
    val activeRepaymentDebt = filteredDebts.find { it.debt.id == selectedRepaymentDebtId }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("debt_manager_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = strings.debtManagerTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = strings.debtManagerSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("debt_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.backButton
                        )
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🌿", fontSize = 18.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingDebt = null
                    addDebtType = DebtType.LENT
                    showAddEditSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.testTag("debt_fab_add")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = strings.newDebtRecordBtn
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Balance Summary & Action Buttons
            item {
                DebtManagerHeaderCards(
                    lentTotal = lentTotal,
                    borrowedTotal = borrowedTotal,
                    language = language,
                    strings = strings,
                    onAddLentClick = {
                        editingDebt = null
                        addDebtType = DebtType.LENT
                        showAddEditSheet = true
                    },
                    onAddBorrowedClick = {
                        editingDebt = null
                        addDebtType = DebtType.BORROWED
                        showAddEditSheet = true
                    }
                )
            }

            // 2. Search Box
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setDebtSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("debt_search_input"),
                    placeholder = { Text(strings.searchPersonPlaceholder) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setDebtSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // 3. View Mode Toggle (List vs By Person) & Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // View Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (viewMode == DebtViewMode.LIST) strings.viewModeList else strings.viewModeByPerson,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .padding(2.dp)
                        ) {
                            FilterChip(
                                selected = viewMode == DebtViewMode.LIST,
                                onClick = { viewModel.setDebtViewMode(DebtViewMode.LIST) },
                                label = { Text(strings.viewModeList, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("view_mode_list_chip")
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            FilterChip(
                                selected = viewMode == DebtViewMode.BY_PERSON,
                                onClick = { viewModel.setDebtViewMode(DebtViewMode.BY_PERSON) },
                                label = { Text(strings.viewModeByPerson, fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("view_mode_person_chip")
                            )
                        }
                    }

                    // Horizontal Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // All
                        FilterChip(
                            selected = filterMode == DebtFilterMode.ALL,
                            onClick = { viewModel.setDebtFilterMode(DebtFilterMode.ALL) },
                            label = { Text(strings.filterAll) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("debt_filter_all")
                        )

                        // Lent
                        FilterChip(
                            selected = filterMode == DebtFilterMode.LENT,
                            onClick = { viewModel.setDebtFilterMode(DebtFilterMode.LENT) },
                            label = { Text(strings.filterLent) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("debt_filter_lent")
                        )

                        // Borrowed
                        FilterChip(
                            selected = filterMode == DebtFilterMode.BORROWED,
                            onClick = { viewModel.setDebtFilterMode(DebtFilterMode.BORROWED) },
                            label = { Text(strings.filterBorrowed) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("debt_filter_borrowed")
                        )

                        // Active
                        FilterChip(
                            selected = filterMode == DebtFilterMode.ACTIVE,
                            onClick = { viewModel.setDebtFilterMode(DebtFilterMode.ACTIVE) },
                            label = { Text(strings.filterActive) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("debt_filter_active")
                        )

                        // Overdue
                        FilterChip(
                            selected = filterMode == DebtFilterMode.OVERDUE,
                            onClick = { viewModel.setDebtFilterMode(DebtFilterMode.OVERDUE) },
                            label = { Text(strings.filterOverdue) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("debt_filter_overdue")
                        )

                        // Paid
                        FilterChip(
                            selected = filterMode == DebtFilterMode.PAID,
                            onClick = { viewModel.setDebtFilterMode(DebtFilterMode.PAID) },
                            label = { Text(strings.filterPaid) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                selectedLabelColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("debt_filter_paid")
                        )
                    }
                }
            }

            // 4. Content Items: List or By Person
            if (filteredDebts.isEmpty()) {
                item {
                    DebtEmptyState(
                        strings = strings,
                        onAddDebtClick = {
                            editingDebt = null
                            addDebtType = DebtType.LENT
                            showAddEditSheet = true
                        }
                    )
                }
            } else if (viewMode == DebtViewMode.LIST) {
                items(filteredDebts, key = { it.debt.id }) { item ->
                    DebtItemCard(
                        item = item,
                        language = language,
                        strings = strings,
                        onClick = { selectedDetailDebtId = item.debt.id }
                    )
                }
            } else {
                items(personSummaries, key = { it.personName }) { summary ->
                    PersonDebtGroupCard(
                        personSummary = summary,
                        language = language,
                        strings = strings,
                        onDebtClick = { selectedDetailDebtId = it.debt.id }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Add or Edit Debt BottomSheet
    if (showAddEditSheet) {
        AddEditDebtBottomSheet(
            initialType = addDebtType,
            existingDebt = editingDebt,
            language = language,
            strings = strings,
            onDismiss = {
                showAddEditSheet = false
                editingDebt = null
            },
            onSave = { personName, type, amount, createdDate, dueDate, note ->
                if (editingDebt != null) {
                    viewModel.updateDebt(
                        debtId = editingDebt!!.debt.id,
                        personName = personName,
                        type = type,
                        amount = amount,
                        createdDate = createdDate,
                        dueDate = dueDate,
                        note = note,
                        context = context
                    )
                } else {
                    viewModel.addDebt(
                        personName = personName,
                        type = type,
                        amount = amount,
                        createdDate = createdDate,
                        dueDate = dueDate,
                        note = note,
                        context = context
                    )
                }
            }
        )
    }

    // Debt Details BottomSheet
    if (activeDetailsDebt != null) {
        DebtDetailsBottomSheet(
            debtItem = activeDetailsDebt,
            language = language,
            strings = strings,
            onDismiss = { selectedDetailDebtId = null },
            onEditClick = {
                editingDebt = activeDetailsDebt
                selectedDetailDebtId = null
                showAddEditSheet = true
            },
            onDeleteClick = {
                viewModel.deleteDebt(activeDetailsDebt.debt, context)
                selectedDetailDebtId = null
            },
            onAddRepaymentClick = {
                selectedRepaymentDebtId = activeDetailsDebt.debt.id
            },
            onDeletePaymentClick = { payment ->
                viewModel.deleteRepayment(payment)
            }
        )
    }

    // Add Repayment BottomSheet
    if (activeRepaymentDebt != null) {
        AddRepaymentBottomSheet(
            debtItem = activeRepaymentDebt,
            language = language,
            strings = strings,
            onDismiss = { selectedRepaymentDebtId = null },
            onSaveRepayment = { amount, date, note ->
                viewModel.addRepayment(
                    debtId = activeRepaymentDebt.debt.id,
                    amount = amount,
                    date = date,
                    note = note,
                    context = context
                )
            }
        )
    }
}
