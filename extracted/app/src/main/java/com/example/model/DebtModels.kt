package com.example.model

import com.example.data.entity.DebtEntity
import com.example.data.entity.DebtPaymentEntity

enum class DebtType {
    LENT,      // আমি ধার দিলাম -> আমি পাব
    BORROWED   // আমি ধার নিলাম -> আমি দেব
}

enum class DebtStatus {
    ACTIVE,
    PAID,
    OVERDUE
}

enum class DebtFilter {
    ALL,
    LENT,      // আমি পাব
    BORROWED,  // আমি দেব
    ACTIVE,    // সক্রিয়
    PAID,      // পরিশোধিত
    OVERDUE    // বকেয়া
}

typealias DebtFilterMode = DebtFilter

enum class DebtViewMode {
    LIST,
    BY_PERSON
}

data class DebtWithPayments(
    val debt: DebtEntity,
    val payments: List<DebtPaymentEntity> = emptyList()
) {
    val totalPaid: Double = payments.sumOf { it.amount }
    val remainingAmount: Double = (debt.originalAmount - totalPaid).coerceAtLeast(0.0)
    val progress: Float = if (debt.originalAmount > 0) {
        ((totalPaid / debt.originalAmount).toFloat()).coerceIn(0f, 1f)
    } else 0f

    val computedStatus: DebtStatus
        get() {
            if (remainingAmount <= 0.001 || totalPaid >= debt.originalAmount) {
                return DebtStatus.PAID
            }
            val now = System.currentTimeMillis()
            val due = debt.dueDate
            if (due != null && due < now) {
                return DebtStatus.OVERDUE
            }
            return DebtStatus.ACTIVE
        }

    val isFullyPaid: Boolean get() = computedStatus == DebtStatus.PAID
    val isOverdue: Boolean get() = computedStatus == DebtStatus.OVERDUE
}

data class PersonDebtSummary(
    val personName: String,
    val debts: List<DebtWithPayments>
) {
    val totalLentRemaining: Double = debts
        .filter { it.debt.type == DebtType.LENT.name && !it.isFullyPaid }
        .sumOf { it.remainingAmount }

    val totalBorrowedRemaining: Double = debts
        .filter { it.debt.type == DebtType.BORROWED.name && !it.isFullyPaid }
        .sumOf { it.remainingAmount }

    val netBalance: Double = totalLentRemaining - totalBorrowedRemaining
    val activeCount: Int = debts.count { !it.isFullyPaid }
    val totalCount: Int = debts.size
}
