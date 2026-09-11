package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.DebtType
import com.example.model.DebtWithPayments
import com.example.ui.theme.financialExpenseColor
import com.example.ui.theme.financialIncomeColor
import com.example.util.CurrencyFormatter
import com.example.util.DateUtils
import com.example.util.Strings
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDebtBottomSheet(
    initialType: DebtType = DebtType.LENT,
    existingDebt: DebtWithPayments? = null,
    language: AppLanguage,
    strings: Strings,
    onDismiss: () -> Unit,
    onSave: (personName: String, type: DebtType, amount: Double, createdDate: Long, dueDate: Long?, note: String) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedType by remember {
        mutableStateOf(
            existingDebt?.debt?.type?.let { DebtType.valueOf(it) } ?: initialType
        )
    }

    var personName by remember {
        mutableStateOf(existingDebt?.debt?.personName ?: "")
    }

    var amountText by remember {
        mutableStateOf(
            existingDebt?.debt?.let {
                if (it.originalAmount % 1.0 == 0.0) it.originalAmount.toLong().toString()
                else it.originalAmount.toString()
            } ?: ""
        )
    }

    var createdDateMillis by remember {
        mutableLongStateOf(existingDebt?.debt?.createdDate ?: System.currentTimeMillis())
    }

    var dueDateMillis by remember {
        mutableStateOf<Long?>(existingDebt?.debt?.dueDate)
    }

    var noteText by remember {
        mutableStateOf(existingDebt?.debt?.note ?: "")
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isLent = selectedType == DebtType.LENT
    val themeColor = if (isLent) financialIncomeColor() else financialExpenseColor()

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
                .testTag("add_edit_debt_bottom_sheet")
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        existingDebt != null -> strings.editDebtTitle
                        selectedType == DebtType.LENT -> strings.addLentTitle
                        else -> strings.addBorrowedTitle
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.cancelButton,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Type Toggle (If not editing existing debt)
            if (existingDebt == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = selectedType == DebtType.LENT,
                        onClick = { selectedType = DebtType.LENT },
                        label = {
                            Text(
                                text = strings.debtLentCardTitle,
                                fontWeight = if (selectedType == DebtType.LENT) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CallReceived,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = financialIncomeColor().copy(alpha = 0.15f),
                            selectedLabelColor = financialIncomeColor(),
                            selectedLeadingIconColor = financialIncomeColor()
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("debt_type_lent_chip")
                    )

                    FilterChip(
                        selected = selectedType == DebtType.BORROWED,
                        onClick = { selectedType = DebtType.BORROWED },
                        label = {
                            Text(
                                text = strings.debtBorrowedCardTitle,
                                fontWeight = if (selectedType == DebtType.BORROWED) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CallMade,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = financialExpenseColor().copy(alpha = 0.15f),
                            selectedLabelColor = financialExpenseColor(),
                            selectedLeadingIconColor = financialExpenseColor()
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("debt_type_borrowed_chip")
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Person Name Field
            Text(
                text = strings.personNameLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = personName,
                onValueChange = {
                    personName = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("debt_person_name_input"),
                placeholder = { Text(strings.personNameHint) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Field
            Text(
                text = strings.amountFieldLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("debt_amount_input"),
                placeholder = { Text("0") },
                prefix = {
                    Text(
                        text = "৳",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = themeColor,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                shape = RoundedCornerShape(14.dp)
            )

            // If editing, show note about already paid
            if (existingDebt != null && existingDebt.totalPaid > 0) {
                Text(
                    text = "${strings.paidAmountPrefix}: ${CurrencyFormatter.format(existingDebt.totalPaid, language)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date of Debt (Created Date)
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
                    .clickable {
                        val cal = Calendar.getInstance().apply { timeInMillis = createdDateMillis }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val updated = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                }
                                createdDateMillis = updated.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                    .testTag("debt_created_date_picker"),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = DateUtils.formatDisplayDate(createdDateMillis, language),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Due Date (Optional)
            Text(
                text = strings.debtDueDateLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        val cal = Calendar.getInstance().apply {
                            timeInMillis = dueDateMillis ?: System.currentTimeMillis()
                        }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val updated = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, year)
                                    set(Calendar.MONTH, month)
                                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    set(Calendar.HOUR_OF_DAY, 20)
                                    set(Calendar.MINUTE, 0)
                                }
                                dueDateMillis = updated.timeInMillis
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                    .testTag("debt_due_date_picker"),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (dueDateMillis != null) {
                        Text(
                            text = DateUtils.formatDisplayDate(dueDateMillis!!, language),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(
                            onClick = { dueDateMillis = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = if (language == AppLanguage.BN) "তারিখ নির্ধারিত নেই (যোগ করতে চাপুন)" else "No due date (Tap to set)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    .testTag("debt_note_input"),
                placeholder = { Text(strings.noteFieldPlaceholder) },
                shape = RoundedCornerShape(14.dp)
            )

            // Error message if any
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage!!,
                    color = financialExpenseColor(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val trimmedName = personName.trim()
                    if (trimmedName.isEmpty()) {
                        errorMessage = strings.personNameRequiredError
                        return@Button
                    }
                    val amount = CurrencyFormatter.parseBanglaDigits(amountText.trim()).toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    if (existingDebt != null && amount < existingDebt.totalPaid - 0.001) {
                        errorMessage = strings.originalAmountLessThanPaidError
                        return@Button
                    }

                    onSave(trimmedName, selectedType, amount, createdDateMillis, dueDateMillis, noteText)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("debt_save_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = strings.saveDebtBtn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
