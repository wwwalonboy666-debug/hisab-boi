package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CustomCategoryEntity
import com.example.data.entity.TransactionEntity
import com.example.model.AppLanguage
import com.example.model.CategoryRegistry
import com.example.model.TransactionType
import com.example.ui.components.AddEditCustomCategoryDialog
import com.example.ui.components.AmountCalculatorDialog
import com.example.ui.components.CategoryPicker
import com.example.ui.theme.financialExpenseColor
import com.example.ui.theme.financialIncomeColor
import com.example.ui.theme.pressClickable
import com.example.ui.theme.pressScale
import com.example.util.CurrencyFormatter
import com.example.util.DateUtils
import com.example.util.Strings
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionSheet(
    initialType: TransactionType = TransactionType.EXPENSE,
    existingTransaction: TransactionEntity? = null,
    customCategories: List<CustomCategoryEntity> = emptyList(),
    language: AppLanguage,
    strings: Strings,
    initialDateMillis: Long? = null,
    onDismiss: () -> Unit,
    onCreateCustomCategory: ((nameBn: String, nameEn: String, type: TransactionType, iconName: String, colorHex: String, onCreated: (String) -> Unit) -> Unit)? = null,
    onSave: (type: TransactionType, amount: Double, categoryKey: String, dateMillis: Long, note: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var selectedType by remember {
        mutableStateOf(
            if (existingTransaction != null) {
                if (existingTransaction.type == TransactionType.INCOME.name) TransactionType.INCOME else TransactionType.EXPENSE
            } else initialType
        )
    }

    var amountText by remember {
        mutableStateOf(
            if (existingTransaction != null) {
                if (existingTransaction.amount % 1.0 == 0.0) existingTransaction.amount.toLong().toString()
                else existingTransaction.amount.toString()
            } else ""
        )
    }

    var selectedCategoryKey by remember {
        mutableStateOf(
            existingTransaction?.categoryKey ?: if (selectedType == TransactionType.EXPENSE) {
                CategoryRegistry.expenseCategories.first().key
            } else {
                CategoryRegistry.incomeCategories.first().key
            }
        )
    }

    var selectedDateMillis by remember {
        mutableLongStateOf(existingTransaction?.dateMillis ?: initialDateMillis ?: System.currentTimeMillis())
    }

    var noteText by remember {
        mutableStateOf(existingTransaction?.note ?: "")
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showAddCustomCategoryDialog by remember { mutableStateOf(false) }
    var showCalculator by remember { mutableStateOf(false) }

    val builtInCategories = if (selectedType == TransactionType.EXPENSE) {
        CategoryRegistry.expenseCategories
    } else {
        CategoryRegistry.incomeCategories
    }
    val customForType = customCategories.filter {
        if (selectedType == TransactionType.EXPENSE) it.type == "EXPENSE" else it.type == "INCOME"
    }

    // Ensure selected category key belongs to current type
    if (builtInCategories.none { it.key == selectedCategoryKey } && customForType.none { it.key == selectedCategoryKey }) {
        selectedCategoryKey = builtInCategories.first().key
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("add_edit_transaction_sheet")
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        existingTransaction != null -> strings.editTransactionTitle
                        selectedType == TransactionType.EXPENSE -> strings.addExpenseTitle
                        else -> strings.addIncomeTitle
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_sheet_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Type Toggle Tabs
            TabRow(
                selectedTabIndex = if (selectedType == TransactionType.EXPENSE) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("type_tab_row")
            ) {
                Tab(
                    selected = selectedType == TransactionType.EXPENSE,
                    onClick = {
                        selectedType = TransactionType.EXPENSE
                        selectedCategoryKey = CategoryRegistry.expenseCategories.first().key
                    },
                    text = {
                        Text(
                            text = strings.filterExpense,
                            fontWeight = if (selectedType == TransactionType.EXPENSE) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedType == TransactionType.EXPENSE) financialExpenseColor() else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                Tab(
                    selected = selectedType == TransactionType.INCOME,
                    onClick = {
                        selectedType = TransactionType.INCOME
                        selectedCategoryKey = CategoryRegistry.incomeCategories.first().key
                    },
                    text = {
                        Text(
                            text = strings.filterIncome,
                            fontWeight = if (selectedType == TransactionType.INCOME) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedType == TransactionType.INCOME) financialIncomeColor() else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount Input Field with integrated Calculator trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.amountFieldLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .pressClickable { showCalculator = true }
                        .testTag("open_calculator_chip"),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.calculatorTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_input_field"),
                placeholder = {
                    Text(
                        text = "0",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                prefix = {
                    Text(
                        text = "৳",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedType == TransactionType.EXPENSE) financialExpenseColor() else financialIncomeColor(),
                        modifier = Modifier.padding(end = 6.dp)
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { showCalculator = true },
                        modifier = Modifier.testTag("amount_calculator_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = strings.calculatorTitle,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                shape = RoundedCornerShape(14.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = financialExpenseColor(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category Picker
            Text(
                text = strings.categoryFieldLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            CategoryPicker(
                transactionType = selectedType,
                selectedCategoryKey = selectedCategoryKey,
                customCategories = customCategories,
                language = language,
                strings = strings,
                onCategorySelected = { category ->
                    selectedCategoryKey = category.key
                },
                onAddNewCategoryClick = {
                    showAddCustomCategoryDialog = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Date Picker Row
            Text(
                text = strings.dateFieldLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .pressClickable {
                        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val updated = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                }
                                selectedDateMillis = updated.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                    .testTag("date_picker_btn"),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = DateUtils.formatDisplayDate(selectedDateMillis, language),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Note (Optional)
            Text(
                text = strings.noteFieldLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_input_field"),
                placeholder = {
                    Text(
                        text = if (language == AppLanguage.BN) "নোট লিখুন..." else "Add a note...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save / Update Button
            val buttonColor = if (selectedType == TransactionType.EXPENSE) financialExpenseColor() else MaterialTheme.colorScheme.primary

            Button(
                onClick = {
                    val sanitized = CurrencyFormatter.parseBanglaDigits(amountText.trim())
                    val amount = sanitized.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    onSave(selectedType, amount, selectedCategoryKey, selectedDateMillis, noteText.trim())
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .pressScale()
                    .testTag("save_transaction_submit_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = when {
                        existingTransaction != null -> strings.updateTransactionBtn
                        selectedType == TransactionType.EXPENSE -> strings.saveExpenseBtn
                        else -> strings.saveIncomeBtn
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Custom Category Dialog
    if (showAddCustomCategoryDialog) {
        AddEditCustomCategoryDialog(
            initialType = selectedType,
            existingCategory = null,
            strings = strings,
            language = language,
            onDismiss = { showAddCustomCategoryDialog = false },
            onSave = { nameBn, nameEn, type, iconName, colorHex ->
                onCreateCustomCategory?.invoke(nameBn, nameEn, type, iconName, colorHex) { newKey ->
                    selectedCategoryKey = newKey
                }
                showAddCustomCategoryDialog = false
            }
        )
    }

    // Amount Calculator Dialog
    if (showCalculator) {
        AmountCalculatorDialog(
            initialAmount = amountText,
            strings = strings,
            language = language,
            onDismiss = { showCalculator = false },
            onApplyAmount = { calculated ->
                amountText = calculated
                errorMessage = null
            }
        )
    }
}
