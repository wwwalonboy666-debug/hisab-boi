package com.example.util

import com.example.model.AppLanguage

class Strings(val lang: AppLanguage) {

    // App & Tagline
    val appName = "HisabBoi"
    val tagline = if (lang == AppLanguage.BN) "হিসাব রাখি, জীবনটা গুছিয়ে রাখি।" else "Keep track, keep life organized."

    // Navigation
    val navHome = if (lang == AppLanguage.BN) "Home" else "Home"
    val navTransactions = if (lang == AppLanguage.BN) "হিসাব" else "Transactions"
    val navReports = if (lang == AppLanguage.BN) "রিপোর্ট" else "Reports"
    val navSettings = if (lang == AppLanguage.BN) "Settings" else "Settings"

    // Greetings
    fun greeting(hourOfDay: Int, userName: String = ""): String {
        val cleanName = userName.trim()
        val nameSuffix = if (cleanName.isNotEmpty()) ", $cleanName" else ""
        return if (lang == AppLanguage.BN) {
            when (hourOfDay) {
                in 5..11 -> "শুভ সকাল 🌅$nameSuffix"
                in 12..16 -> "শুভ দুপুর ☀️$nameSuffix"
                in 17..20 -> "শুভ সন্ধ্যা 🌇$nameSuffix"
                else -> "শুভ রাত 🌙$nameSuffix"
            }
        } else {
            when (hourOfDay) {
                in 5..11 -> "Good morning 🌅$nameSuffix"
                in 12..16 -> "Good afternoon ☀️$nameSuffix"
                in 17..20 -> "Good evening 🌇$nameSuffix"
                else -> "Good night 🌙$nameSuffix"
            }
        }
    }

    val greetingSub = if (lang == AppLanguage.BN) "আজকের হিসাবটা গুছিয়ে নিই" else "Let's organize today's finances"

    // Home Balance
    val totalBalance = if (lang == AppLanguage.BN) "মোট ব্যালেন্স" else "Total Balance"
    val incomeLabel = if (lang == AppLanguage.BN) "↑ আয়" else "↑ Income"
    val expenseLabel = if (lang == AppLanguage.BN) "↓ খরচ" else "↓ Expense"

    // Quick Actions
    val addExpenseQuick = if (lang == AppLanguage.BN) "＋ খরচ" else "＋ Expense"
    val addIncomeQuick = if (lang == AppLanguage.BN) "＋ আয়" else "＋ Income"

    // Monthly Budget Card
    val monthBudgetTitle = if (lang == AppLanguage.BN) "এই মাসের বাজেট" else "This Month's Budget"
    val setBudgetPrompt = if (lang == AppLanguage.BN) "এই মাসের বাজেট সেট করুন" else "Set this month's budget"
    val setBudgetBtn = if (lang == AppLanguage.BN) "বাজেট সেট করুন" else "Set Budget"
    val editBudgetBtn = if (lang == AppLanguage.BN) "বাজেট পরিবর্তন" else "Edit Budget"
    val removeBudgetBtn = if (lang == AppLanguage.BN) "বাজেট মুছুন" else "Remove Budget"
    val budgetWarning80 = if (lang == AppLanguage.BN) "আপনার বাজেটের বেশিরভাগ ব্যবহার হয়ে গেছে।" else "Most of your budget has been utilized."
    val budgetWarning100 = if (lang == AppLanguage.BN) "এই মাসের বাজেট অতিক্রম হয়েছে।" else "This month's budget has been exceeded."
    val usedLabel = if (lang == AppLanguage.BN) "ব্যবহৃত" else "Used"
    val remainingLabel = if (lang == AppLanguage.BN) "অবশিষ্ট" else "Remaining"

    // Recent Transactions
    val recentTransactionsTitle = if (lang == AppLanguage.BN) "সাম্প্রতিক হিসাব" else "Recent Transactions"
    val viewAll = if (lang == AppLanguage.BN) "সব হিসাব দেখুন" else "View All"
    val emptyTransactionsTitle = if (lang == AppLanguage.BN) "এখনও কোনো হিসাব যোগ করা হয়নি।" else "No transactions recorded yet."
    val emptyTransactionsSub = if (lang == AppLanguage.BN) "আপনার প্রথম আয় বা খরচ যোগ করুন।" else "Add your first income or expense."

    // Add / Edit Transaction
    val addExpenseTitle = if (lang == AppLanguage.BN) "খরচ যোগ করুন" else "Add Expense"
    val addIncomeTitle = if (lang == AppLanguage.BN) "আয় যোগ করুন" else "Add Income"
    val editTransactionTitle = if (lang == AppLanguage.BN) "হিসাব সম্পাদনা" else "Edit Transaction"
    val amountFieldLabel = if (lang == AppLanguage.BN) "পরিমাণ" else "Amount"
    val categoryFieldLabel = if (lang == AppLanguage.BN) "খাত নির্বাচন করুন" else "Select Category"
    val dateFieldLabel = if (lang == AppLanguage.BN) "তারিখ" else "Date"
    val noteFieldLabel = if (lang == AppLanguage.BN) "নোট (ঐচ্ছিক)" else "Note (Optional)"
    val saveExpenseBtn = if (lang == AppLanguage.BN) "খরচ সংরক্ষণ করুন" else "Save Expense"
    val saveIncomeBtn = if (lang == AppLanguage.BN) "আয় সংরক্ষণ করুন" else "Save Income"
    val updateTransactionBtn = if (lang == AppLanguage.BN) "হিসাব আপডেট করুন" else "Update Transaction"
    val invalidAmountError = if (lang == AppLanguage.BN) "দয়া করে সঠিক পরিমাণ লিখুন (০-এর বেশি)" else "Please enter a valid amount (> 0)"
    val savedSuccessToast = if (lang == AppLanguage.BN) "হিসাব সফলভাবে সংরক্ষিত হয়েছে ✓" else "Transaction saved successfully ✓"
    val updatedSuccessToast = if (lang == AppLanguage.BN) "হিসাব আপডেট করা হয়েছে ✓" else "Transaction updated successfully ✓"
    val deletedSuccessToast = if (lang == AppLanguage.BN) "হিসাব মুছে ফেলা হয়েছে" else "Transaction deleted"

    // Transactions Screen
    val allTransactionsTitle = if (lang == AppLanguage.BN) "সব হিসাব" else "All Transactions"
    val searchHint = if (lang == AppLanguage.BN) "হিসাব খুঁজুন (নোট বা খাত)..." else "Search transactions (note or category)..."
    val filterAll = if (lang == AppLanguage.BN) "সব" else "All"
    val filterIncome = if (lang == AppLanguage.BN) "আয়" else "Income"
    val filterExpense = if (lang == AppLanguage.BN) "খরচ" else "Expense"
    val netTotalLabel = if (lang == AppLanguage.BN) "নিট ব্যালেন্স" else "Net Total"
    val deleteConfirmTitle = if (lang == AppLanguage.BN) "হিসাব মুছে ফেলতে চান?" else "Delete this transaction?"
    val deleteConfirmMsg = if (lang == AppLanguage.BN) "এই হিসাবটি স্থায়ীভাবে মুছে যাবে।" else "This record will be permanently deleted."
    val cancelBtn = if (lang == AppLanguage.BN) "বাতিল" else "Cancel"
    val deleteBtn = if (lang == AppLanguage.BN) "মুছে ফেলুন" else "Delete"
    val editBtn = if (lang == AppLanguage.BN) "সম্পাদনা" else "Edit"

    // Reports Screen
    val reportsTitle = if (lang == AppLanguage.BN) "রিপোর্ট ও বিশ্লেষণ" else "Reports & Insights"
    val totalIncomeReport = if (lang == AppLanguage.BN) "মোট আয়" else "Total Income"
    val totalExpenseReport = if (lang == AppLanguage.BN) "মোট খরচ" else "Total Expense"
    val netBalanceReport = if (lang == AppLanguage.BN) "নিট সঞ্চয়" else "Net Savings"
    val categoryBreakdownTitle = if (lang == AppLanguage.BN) "খাতভিত্তিক ব্যয়ের বিবরণ" else "Category Spending Breakdown"
    val financialInsightsTitle = if (lang == AppLanguage.BN) "আর্থিক পর্যালোচনা" else "Financial Insights"
    val emptyReportTitle = if (lang == AppLanguage.BN) "রিপোর্ট দেখানোর মতো পর্যাপ্ত হিসাব নেই।" else "Not enough records to display report."
    val emptyReportSub = if (lang == AppLanguage.BN) "আরও কিছু হিসাব যোগ হলে এখানে আপনার আর্থিক চিত্র দেখা যাবে।" else "Add more transactions to view a detailed financial overview."

    // Savings Screen
    val savingsTitle = if (lang == AppLanguage.BN) "আপনার স্বপ্নের জন্য সঞ্চয় 🌱" else "Save for Your Dreams 🌱"
    val addSavingsGoal = if (lang == AppLanguage.BN) "নতুন সঞ্চয়ের লক্ষ্য" else "New Savings Goal"
    val goalNameLabel = if (lang == AppLanguage.BN) "লক্ষ্যের নাম (যেমন: নতুন ফোন)" else "Goal Name (e.g. New Phone)"
    val targetAmountLabel = if (lang == AppLanguage.BN) "লক্ষ্যের পরিমাণ" else "Target Amount"
    val initialAmountLabel = if (lang == AppLanguage.BN) "বর্তমান জমা (ঐচ্ছিক)" else "Initial Saved (Optional)"
    val addSavingsBtn = if (lang == AppLanguage.BN) "সঞ্চয় যোগ করুন" else "Add Savings"
    val createGoalBtn = if (lang == AppLanguage.BN) "লক্ষ্য তৈরি করুন" else "Create Goal"
    val emptySavingsTitle = if (lang == AppLanguage.BN) "আপনার প্রথম সঞ্চয়ের লক্ষ্য তৈরি করুন 🌱" else "Create your first savings goal 🌱"
    val emptySavingsSub = if (lang == AppLanguage.BN) "ছোট ছোট সঞ্চয় থেকেই বড় স্বপ্ন পূরণ হয়।" else "Small consistent savings build big dreams."
    val targetLabel = if (lang == AppLanguage.BN) "লক্ষ্য" else "Target"
    val savedLabel = if (lang == AppLanguage.BN) "জমেছে" else "Saved"

    // Plant stages
    fun plantStageName(progress: Float): String {
        return if (lang == AppLanguage.BN) {
            when {
                progress >= 1.0f -> "পরিপূর্ণ বৃক্ষ 🌳 (অভিনন্দন!)"
                progress >= 0.75f -> "ফলবান তরু 🪴"
                progress >= 0.50f -> "তরুণ চারা 🌿"
                progress >= 0.25f -> "নবীন কুঁড়ি 🌱"
                else -> "বীজ বোনা হয়েছে 🌰"
            }
        } else {
            when {
                progress >= 1.0f -> "Full Grown Tree 🌳 (Completed!)"
                progress >= 0.75f -> "Thriving Plant 🪴"
                progress >= 0.50f -> "Young Sapling 🌿"
                progress >= 0.25f -> "Sprouting Seedling 🌱"
                else -> "Planted Seed 🌰"
            }
        }
    }

    // Settings Screen
    val settingsTitle = if (lang == AppLanguage.BN) "সেটিংস" else "Settings"
    val appearanceSection = if (lang == AppLanguage.BN) "চেহারা (Appearance)" else "Appearance"
    val themeSystem = if (lang == AppLanguage.BN) "সিস্টেম ডিফল্ট" else "System Default"
    val themeLight = if (lang == AppLanguage.BN) "লাইট মোড" else "Light Mode"
    val themeDark = if (lang == AppLanguage.BN) "ডার্ক মোড" else "Dark Mode"

    val languageSection = if (lang == AppLanguage.BN) "ভাষা (Language)" else "Language"
    val langBn = "বাংলা (Bangla)"
    val langEn = "English"

    val currencySection = if (lang == AppLanguage.BN) "মুদ্রা" else "Currency"
    val currencyDisplay = "বাংলাদেশী টাকা (BDT ৳)"

    val notificationSection = if (lang == AppLanguage.BN) "অনুস্মারক ও নোটিফিকেশন" else "Reminders & Notifications"
    val dailyReminderTitle = if (lang == AppLanguage.BN) "দৈনিক হিসাবের স্মরণিকা" else "Daily Accounting Reminder"
    val dailyReminderSub = if (lang == AppLanguage.BN) "প্রতিদিন রাতে হিসাব লিখে রাখার তাগিদ" else "A peaceful reminder to log expenses each evening"

    val aboutSection = if (lang == AppLanguage.BN) "হিসাববই সম্পর্কে" else "About HisabBoi"
    val privacyTitle = if (lang == AppLanguage.BN) "গোপনীয়তা ও অফলাইন নিরাপত্তা" else "Privacy & Local Offline Data"
    val privacyDetails = if (lang == AppLanguage.BN)
        "আপনার আর্থিক সকল হিসাব সম্পূর্ণভাবে আপনার ডিভাইসেই সংরক্ষিত থাকে। কোনো সার্ভারে আপনার তথ্য পাঠানো হয় না।"
    else
        "All your financial records are stored strictly on your local device. Zero data is sent to external servers."
    val termsTitle = if (lang == AppLanguage.BN) "শর্তাবলী" else "Terms of Use"
    val appVersionLabel = if (lang == AppLanguage.BN) "সংস্করণ" else "Version"

    // Onboarding
    val onb1Title = if (lang == AppLanguage.BN) "सहজে হিসাব রাখুন 🌿" else "Effortless Accounting 🌿"
    val onb1Sub = if (lang == AppLanguage.BN) "আপনার প্রতিদিনের আয় ও খরচ সহজভাবে গুছিয়ে রাখুন।" else "Organize daily income and expenses with peaceful ease."

    val onb2Title = if (lang == AppLanguage.BN) "আপনার বাজেট বুঝুন 📊" else "Understand Your Budget 📊"
    val onb2Sub = if (lang == AppLanguage.BN) "মাসে কোথায় কত খরচ হচ্ছে তা এক নজরে দেখুন।" else "See where your money goes each month at a glance."

    val onb3Title = if (lang == AppLanguage.BN) "আপনার স্বপ্নের জন্য সঞ্চয় করুন 🌱" else "Save for Your Dreams 🌱"
    val onb3Sub = if (lang == AppLanguage.BN) "সঞ্চয়ের লক্ষ্য তৈরি করুন এবং ধীরে ধীরে এগিয়ে যান।" else "Set savings goals and watch them grow like a nurturing plant."

    val onbStartBtn = if (lang == AppLanguage.BN) "শুরু করি →" else "Get Started →"
    val nextBtn = if (lang == AppLanguage.BN) "পরবর্তী" else "Next"
    val skipBtn = if (lang == AppLanguage.BN) "এড়িয়ে যান" else "Skip"

    // Category Management & Custom Categories
    val addCustomCategoryBtn = if (lang == AppLanguage.BN) "＋ নতুন ক্যাটাগরি" else "＋ New Category"
    val createCategoryBtn = if (lang == AppLanguage.BN) "ক্যাটাগরি তৈরি করুন" else "Create Category"
    val newCustomCategoryTitle = if (lang == AppLanguage.BN) "নতুন ক্যাটাগরি" else "New Category"
    val editCustomCategoryTitle = if (lang == AppLanguage.BN) "ক্যাটাগরি সম্পাদনা" else "Edit Category"
    val categoryNameLabel = if (lang == AppLanguage.BN) "ক্যাটাগরির নাম" else "Category Name"
    val categoryNameHint = if (lang == AppLanguage.BN) "যেমন: খামার, মায়ের ওষুধ, ভ্রমণ" else "e.g. Farm, Mother's Medicine, Travel"
    val categoryTypeLabel = if (lang == AppLanguage.BN) "ধরণ" else "Type"
    val selectIconLabel = if (lang == AppLanguage.BN) "আইকন নির্বাচন করুন" else "Select Icon"
    val manageCategoriesTitle = if (lang == AppLanguage.BN) "ক্যাটাগরি পরিচালনা" else "Manage Categories"
    val manageCategoriesSub = if (lang == AppLanguage.BN) "কাস্টম ক্যাটাগরি যোগ ও নিয়ন্ত্রণ করুন" else "Add, edit and manage custom categories"
    val customCategoriesSection = if (lang == AppLanguage.BN) "কাস্টম ক্যাটাগরি তালিকা" else "Custom Categories"
    val noCustomCategories = if (lang == AppLanguage.BN) "এখনও কোনো কাস্টম ক্যাটাগরি নেই।" else "No custom categories created yet."
    val deleteCategoryConfirmTitle = if (lang == AppLanguage.BN) "ক্যাটাগরি মুছে ফেলতে চান?" else "Delete Category?"
    val deleteCategoryConfirmMsg = if (lang == AppLanguage.BN)
        "এই ক্যাটাগরিটি মুছে ফেললে এর সমস্ত পূর্বের হিসাব নিরাপদে 'অন্যান্য' ক্যাটাগরিতে স্থানান্তরিত হবে। কোনো আর্থিক হিসাব হারাবে না।"
    else
        "Deleting this category will safely reassign its transactions to 'Other'. No records will be lost."
    val defaultCategoryNotice = if (lang == AppLanguage.BN) "সিস্টেমের ডিফল্ট ক্যাটাগরি পরিবর্তন করা সম্ভব নয়।" else "Default system categories cannot be deleted."
    val categorySaveSuccess = if (lang == AppLanguage.BN) "ক্যাটাগরি সফলভাবে সংরক্ষিত হয়েছে ✓" else "Category saved successfully ✓"
    val categoryDeleteSuccess = if (lang == AppLanguage.BN) "ক্যাটাগরি মুছে ফেলা হয়েছে" else "Category deleted"
    val categoryArchivedSuccess = if (lang == AppLanguage.BN) "ক্যাটাগরি আর্কাইভ করা হয়েছে ✓" else "Category archived successfully ✓"
    val categoryUnarchivedSuccess = if (lang == AppLanguage.BN) "ক্যাটাগরি পুনরুদ্ধার করা হয়েছে ✓" else "Category unarchived successfully ✓"
    val archiveCategoryTitle = if (lang == AppLanguage.BN) "ক্যাটাগরি আর্কাইভ করুন" else "Archive Category"
    val categoryCannotDeleteTitle = if (lang == AppLanguage.BN) "ক্যাটাগরিটি মুছে ফেলা যাবে না" else "Category Cannot Be Deleted"
    fun categoryCannotDeleteMsg(count: Int) = if (lang == AppLanguage.BN)
        "এই ক্যাটাগরিতে $count টি লেনদেন যুক্ত আছে। আর্থিক তথ্য সুরক্ষিত রাখতে এটি সরাসরি মুছে ফেলা যাবে না। আপনি চাইলে এটিকে 'আর্কাইভ' করতে পারেন যাতে নতুন লেনদেনে এটি না আসে।"
    else
        "This category is used in $count transactions. To preserve your financial records, it cannot be permanently deleted. You can archive it instead to hide it from future transactions."
    val archiveBtn = if (lang == AppLanguage.BN) "আর্কাইভ করুন" else "Archive"
    val unarchiveBtn = if (lang == AppLanguage.BN) "পুনরুদ্ধার করুন" else "Unarchive"
    val archivedTag = if (lang == AppLanguage.BN) "আর্কাইভকৃত" else "Archived"
    val activeCategoriesTab = if (lang == AppLanguage.BN) "সক্রিয়" else "Active"
    val archivedCategoriesTab = if (lang == AppLanguage.BN) "আর্কাইভকৃত" else "Archived"
    val noArchivedCategories = if (lang == AppLanguage.BN) "কোনো আর্কাইভকৃত ক্যাটাগরি নেই।" else "No archived categories."

    // V3.1 Calendar Strings
    val calendarTitle = if (lang == AppLanguage.BN) "ক্যালেন্ডার" else "Calendar"
    val calendarSubtitle = if (lang == AppLanguage.BN) "তারিখ অনুযায়ী আর্থিক বিবরণ" else "Financial activity by date"
    val dailyTotalExpense = if (lang == AppLanguage.BN) "দিনের খরচ" else "Daily Expense"
    val dailyTotalIncome = if (lang == AppLanguage.BN) "দিনের আয়" else "Daily Income"
    val dailyNet = if (lang == AppLanguage.BN) "দিনের নিট হিসাব" else "Daily Net"
    val noTransactionsOnDate = if (lang == AppLanguage.BN) "এই তারিখে কোনো লেনদেন হয়নি 🌿" else "No transactions on this date 🌿"
    val monthlyTotalIncome = if (lang == AppLanguage.BN) "মাসের মোট আয়" else "Monthly Income"
    val monthlyTotalExpense = if (lang == AppLanguage.BN) "মাসের মোট খরচ" else "Monthly Expense"
    val monthlyBalance = if (lang == AppLanguage.BN) "মাসের ব্যালেন্স" else "Monthly Balance"
    val todayLabel = if (lang == AppLanguage.BN) "আজ" else "Today"
    val selectedDateTransactions = if (lang == AppLanguage.BN) "নির্বাচিত দিনের লেনদেনসমূহ" else "Transactions on Selected Day"

    val monthlyIncomeLabel get() = monthlyTotalIncome
    val monthlyExpenseLabel get() = monthlyTotalExpense
    val monthlyNetLabel get() = monthlyBalance
    val dailyIncomeLabel get() = dailyTotalIncome
    val dailyExpenseLabel get() = dailyTotalExpense
    val dailyNetLabel get() = dailyNet
    val noTransactionsForDate get() = noTransactionsOnDate
    val addTransactionBtn = if (lang == AppLanguage.BN) "＋ লেনদেন" else "＋ Transaction"

    // V3.1 Calculator Strings
    val calculatorTitle = if (lang == AppLanguage.BN) "ক্যালকুলেটর" else "Calculator"
    val calculatorUseResult = if (lang == AppLanguage.BN) "পরিমাণ হিসেবে ব্যবহার করুন" else "Use as Amount"
    val divideByZeroError = if (lang == AppLanguage.BN) "০ দিয়ে ভাগ করা যায় না" else "Cannot divide by zero"
    val invalidExpressionError = if (lang == AppLanguage.BN) "অকার্যকর গণনা" else "Invalid expression"

    // V2 Debt Manager Strings
    val debtManagerTitle = if (lang == AppLanguage.BN) "ধার-দেনা" else "Debts"
    val debtManagerSubtitle = if (lang == AppLanguage.BN) "আপনার পাওনা ও দেনা এক জায়গায়" else "Your debts and receivables in one peaceful place"
    val debtLentCardTitle = if (lang == AppLanguage.BN) "আমি পাব" else "I Will Receive"
    val debtLentCardSub = if (lang == AppLanguage.BN) "অন্যের কাছে পাওনা" else "Money owed to you"
    val debtBorrowedCardTitle = if (lang == AppLanguage.BN) "আমি দেব" else "I Need to Pay"
    val debtBorrowedCardSub = if (lang == AppLanguage.BN) "অন্যকে দিতে হবে" else "Money you owe others"

    val debtAddLentAction = if (lang == AppLanguage.BN) "＋ টাকা ধার দিলাম" else "＋ I Lent Money"
    val debtAddBorrowedAction = if (lang == AppLanguage.BN) "＋ টাকা ধার নিলাম" else "＋ I Borrowed Money"
    val myDebtsSectionTitle = if (lang == AppLanguage.BN) "আমার ধার-দেনা" else "My Debts"

    val addLentTitle = if (lang == AppLanguage.BN) "টাকা ধার দিলাম" else "I Lent Money"
    val addBorrowedTitle = if (lang == AppLanguage.BN) "টাকা ধার নিলাম" else "I Borrowed Money"
    val editDebtTitle = if (lang == AppLanguage.BN) "ধারের হিসাব সম্পাদনা" else "Edit Debt"

    val personNameLabel = if (lang == AppLanguage.BN) "ব্যক্তির নাম" else "Person Name"
    val personNameHint = if (lang == AppLanguage.BN) "যেমন: করিম, রহিম, মামুন ভাই" else "e.g. Karim, Rahim, Mamun"
    val debtDueDateLabel = if (lang == AppLanguage.BN) "পরিশোধের তারিখ (ঐচ্ছিক)" else "Due Date (Optional)"
    val debtDueDateDisplay = if (lang == AppLanguage.BN) "পরিশোধের তারিখ" else "Due Date"
    val saveDebtBtn = if (lang == AppLanguage.BN) "সংরক্ষণ করুন" else "Save"
    val personNameRequiredError = if (lang == AppLanguage.BN) "দয়া করে ব্যক্তির নাম লিখুন" else "Please enter person name"

    val debtDetailsTitle = if (lang == AppLanguage.BN) "ধারের বিস্তারিত" else "Debt Details"
    val repaymentHistoryTitle = if (lang == AppLanguage.BN) "পরিশোধের ইতিহাস" else "Repayment History"
    val actionPaymentReceived = if (lang == AppLanguage.BN) "＋ টাকা পেলাম" else "＋ Payment Received"
    val actionPaymentMade = if (lang == AppLanguage.BN) "＋ টাকা পরিশোধ করলাম" else "＋ Payment Made"
    val addRepaymentTitle = if (lang == AppLanguage.BN) "টাকা পরিশোধ যোগ করুন" else "Add Repayment"
    val overpaymentError = if (lang == AppLanguage.BN) "বাকি টাকার চেয়ে বেশি পরিশোধ করা যাবে না।" else "Payment cannot exceed the remaining amount."
    val fullPaymentAlhamdulillah = if (lang == AppLanguage.BN) "আলহামদুলিল্লাহ, পুরো টাকা পরিশোধ হয়েছে। 🌿" else "Alhamdulillah, fully repaid. 🌿"

    val statusPaid = if (lang == AppLanguage.BN) "পরিশোধিত" else "Paid"
    val statusOverdue = if (lang == AppLanguage.BN) "বকেয়া" else "Overdue"
    val statusActive = if (lang == AppLanguage.BN) "সক্রিয়" else "Active"

    val totalAmountPrefix = if (lang == AppLanguage.BN) "মোট" else "Total"
    val paidAmountPrefix = if (lang == AppLanguage.BN) "পরিশোধ" else "Paid"
    val remainingAmountPrefix = if (lang == AppLanguage.BN) "বাকি" else "Remaining"
    val duePrefix = if (lang == AppLanguage.BN) "Due:" else "Due:"

    val deleteDebtConfirmTitle = if (lang == AppLanguage.BN) "এই ধারটির সব তথ্য মুছে ফেলবেন?" else "Delete this debt record?"
    val deleteDebtConfirmMsg = if (lang == AppLanguage.BN) "এই ধারটির সমস্ত তথ্য ও পরিশোধের ইতিহাস স্থায়ীভাবে মুছে যাবে।" else "All information and repayment history for this debt will be permanently deleted."

    val debtReminderSettingTitle = if (lang == AppLanguage.BN) "ধার-দেনা রিমাইন্ডার" else "Debt Reminders"
    val debtReminderSettingSub = if (lang == AppLanguage.BN) "পরিশোধের নির্ধারিত তারিখের আগে শান্ত অনুস্মারক" else "Peaceful notification reminder before due date"
    val reminderTimingLabel = if (lang == AppLanguage.BN) "রিমাইন্ডারের সময়" else "Reminder Timing"
    val reminderOnDueDate = if (lang == AppLanguage.BN) "পরিশোধের দিন" else "On due date"
    val reminderOneDayBefore = if (lang == AppLanguage.BN) "১ দিন আগে" else "1 day before"
    val reminderThreeDaysBefore = if (lang == AppLanguage.BN) "৩ দিন আগে" else "3 days before"
    val reminderSevenDaysBefore = if (lang == AppLanguage.BN) "৭ দিন আগে" else "7 days before"

    val emptyDebtTitle = if (lang == AppLanguage.BN) "এখনও কোনো ধার-দেনা নেই 🌿" else "No debts recorded yet 🌿"
    val emptyDebtSub = if (lang == AppLanguage.BN) "আপনার পাওনা ও দেনা এখানে গুছিয়ে রাখতে পারবেন।" else "Keep your lent and borrowed records organized here."
    val newDebtRecordBtn = if (lang == AppLanguage.BN) "＋ নতুন হিসাব" else "＋ New Record"
    val viewDetailsBtn = if (lang == AppLanguage.BN) "বিস্তারিত দেখুন" else "View Details"

    val filterByPerson = if (lang == AppLanguage.BN) "ব্যক্তি অনুযায়ী" else "By Person"
    val filterList = if (lang == AppLanguage.BN) "তালিকা" else "List"
    val filterActiveDebts = if (lang == AppLanguage.BN) "সক্রিয়" else "Active"
    val filterPaidDebts = if (lang == AppLanguage.BN) "পরিশোধিত" else "Paid"
    val filterOverdueDebts = if (lang == AppLanguage.BN) "বকেয়া" else "Overdue"

    val searchDebtHint = if (lang == AppLanguage.BN) "ব্যক্তির নাম দিয়ে খুঁজুন..." else "Search by person name..."
    val noDebtFoundForSearch = if (lang == AppLanguage.BN) "কোনো ধার খুঁজে পাওয়া যায়নি।" else "No debt records found matching search."
    val originalAmountLessThanPaidError = if (lang == AppLanguage.BN) "মূল পরিমাণ ইতোমধ্যে পরিশোধিত টাকার চেয়ে কম হতে পারে না।" else "Original amount cannot be less than already repaid amount."
    val repaymentSavedSuccess = if (lang == AppLanguage.BN) "পরিশোধ সংরক্ষিত হয়েছে ✓" else "Repayment recorded successfully ✓"
    val debtSavedSuccess = if (lang == AppLanguage.BN) "ধার সংরক্ষিত হয়েছে ✓" else "Debt recorded successfully ✓"
    val debtUpdatedSuccess = if (lang == AppLanguage.BN) "ধার আপডেট হয়েছে ✓" else "Debt updated successfully ✓"
    val debtDeletedSuccess = if (lang == AppLanguage.BN) "ধার মুছে ফেলা হয়েছে" else "Debt record deleted"

    // Aliases for Debt screens
    val cancelButton get() = cancelBtn
    val deleteButton get() = deleteBtn
    val backButton = if (lang == AppLanguage.BN) "ফিরে যান" else "Back"
    val noteFieldPlaceholder = if (lang == AppLanguage.BN) "নোট লিখুন..." else "Add a note..."
    val searchPersonPlaceholder = searchDebtHint
    val viewModeList = filterList
    val viewModeByPerson = filterByPerson
    val filterLent = if (lang == AppLanguage.BN) "পাওনা" else "Lent"
    val filterBorrowed = if (lang == AppLanguage.BN) "দেনা" else "Borrowed"
    val filterActive = statusActive
    val filterOverdue = statusOverdue
    val filterPaid = statusPaid

    // Backup & Restore
    val backupRestoreTitle = if (lang == AppLanguage.BN) "☁️ ব্যাকআপ ও রিস্টোর" else "☁️ Backup & Restore"
    val backupHeaderTitle = if (lang == AppLanguage.BN) "আপনার হিসাব নিরাপদে সংরক্ষণ করুন 🌿" else "Keep your accounting safe 🌿"
    val backupExplanation = if (lang == AppLanguage.BN) {
        "আপনার হিসাববইয়ের সমস্ত তথ্য নিরাপদে গুগল ড্রাইভে ব্যাকআপ রাখা যায়। ফোন পরিবর্তন বা হারিয়ে গেলে সহজেই পুরোনো হিসাব ফিরিয়ে আনতে পারবেন।\n\nএই ব্যাকআপ সম্পূর্ণ ঐচ্ছিক—গুগল অ্যাকাউন্ট ছাড়াও হিসাববই সম্পূর্ণ স্বাভাবিক ও অফলাইনে ব্যবহার করা যায়।"
    } else {
        "Your HisabBoi records can be safely backed up to your Google Drive. Restore your data anytime if you change or lose your phone.\n\nThis is completely optional—HisabBoi remains 100% usable offline without a Google account."
    }
    val googleAccountSection = if (lang == AppLanguage.BN) "গুগল অ্যাকাউন্ট" else "Google Account"
    val connectGoogleBtn = if (lang == AppLanguage.BN) "Google Account সংযুক্ত করুন" else "Connect Google Account"
    val disconnectGoogleBtn = if (lang == AppLanguage.BN) "Google Account সংযোগ বিচ্ছিন্ন করুন" else "Disconnect Google Account"
    val connectedStatus = if (lang == AppLanguage.BN) "সংযুক্ত" else "Connected"
    val notConnectedStatus = if (lang == AppLanguage.BN) "সংযুক্ত নয়" else "Not connected"
    val backupNowBtn = if (lang == AppLanguage.BN) "☁️ এখনই Backup করুন" else "☁️ Backup Now"
    val restoreBackupBtn = if (lang == AppLanguage.BN) "↩️ Backup Restore করুন" else "↩️ Restore Backup"
    val backupSuccessMsg = if (lang == AppLanguage.BN) "✅ Backup সফল হয়েছে" else "✅ Backup completed successfully"
    val backupFailedMsg = if (lang == AppLanguage.BN) "❌ Backup সম্পন্ন করা যায়নি। তবে আপনার বর্তমান হিসাব সম্পূর্ণ নিরাপদ আছে।" else "❌ Backup could not be completed. Your local data remains completely safe."
    val restoreSuccessMsg = if (lang == AppLanguage.BN) "✅ আপনার হিসাব সফলভাবে Restore হয়েছে" else "✅ Your data has been restored successfully"
    val restoreFailedMsg = if (lang == AppLanguage.BN) "❌ Backup Restore করা যায়নি।\nআপনার বর্তমান হিসাব নিরাপদ আছে।" else "❌ Failed to restore backup.\nYour current records remain safe."
    val safetyBackupDialogTitle = if (lang == AppLanguage.BN) "পুরোনো হিসাব ফিরিয়ে আনবেন?" else "Restore previous data?"
    val safetyBackupDialogMsg = if (lang == AppLanguage.BN) {
        "Google Drive-এর Backup দিয়ে বর্তমান হিসাব প্রতিস্থাপন করা হবে। Restore করার আগে বর্তমান হিসাবের একটি safety backup রাখা হবে।"
    } else {
        "Current records will be replaced with the Google Drive backup. A local safety backup will be created before restoring."
    }
    val disconnectDialogMsg = if (lang == AppLanguage.BN) {
        "Google Account সংযোগ বিচ্ছিন্ন করলে Automatic Backup বন্ধ হবে। আপনার ফোনের বর্তমান হিসাব মুছে যাবে না।"
    } else {
        "Disconnecting will disable Automatic Backup. Your local phone records will NOT be deleted."
    }
    val deleteCloudBackupBtn = if (lang == AppLanguage.BN) "🗑️ Google Drive Backup মুছে দিন" else "🗑️ Delete Cloud Backup"
    val deleteCloudBackupDialogTitle = if (lang == AppLanguage.BN) "Google Drive Backup মুছে ফেলবেন?" else "Delete Google Drive Backup?"
    val deleteCloudBackupDialogMsg = if (lang == AppLanguage.BN) {
        "আপনার Google Drive-এ সংরক্ষিত ব্যাকআপ ফাইলটি চিরতরে মুছে যাবে। তবে আপনার ফোনের বর্তমান হিসাব সুরক্ষিত থাকবে।"
    } else {
        "The backup file in your Google Drive will be permanently deleted. Your phone's current data will remain safe."
    }
    val deleteCloudSuccessMsg = if (lang == AppLanguage.BN) "Google Drive ব্যাকআপ মুছে ফেলা হয়েছে" else "Google Drive backup has been deleted"
    val autoBackupTitle = if (lang == AppLanguage.BN) "⚙️ Automatic Backup" else "⚙️ Automatic Backup"
    val autoBackupOff = if (lang == AppLanguage.BN) "বন্ধ" else "Off"
    val autoBackupDaily = if (lang == AppLanguage.BN) "প্রতিদিন" else "Daily"
    val lastBackupLabel = if (lang == AppLanguage.BN) "সর্বশেষ ব্যাকআপ:" else "Last backup:"
    val noBackupYet = if (lang == AppLanguage.BN) "এখনো ব্যাকআপ নেওয়া হয়নি" else "No backup taken yet"
    val backingUpProgress = if (lang == AppLanguage.BN) "হিসাব এনক্রিপ্ট ও ব্যাকআপ হচ্ছে..." else "Encrypting and backing up data..."
    val restoringProgress = if (lang == AppLanguage.BN) "ব্যাকআপ ফাইল যাচাই ও রিস্টোর হচ্ছে..." else "Verifying and restoring data..."
    val restoreConfirmBtn = if (lang == AppLanguage.BN) "Restore করুন" else "Restore"
    val disconnectConfirmBtn = if (lang == AppLanguage.BN) "সংযোগ বিচ্ছিন্ন করুন" else "Disconnect"
    val deleteCloudConfirmBtn = if (lang == AppLanguage.BN) "মুছে ফেলুন" else "Delete"
    val saveBtn = if (lang == AppLanguage.BN) "সংরক্ষণ করুন" else "Save"
    val backupBannerText = if (lang == AppLanguage.BN) {
        "আপনার হিসাব নিরাপদ রাখতে জিমেইলে ব্যাকআপ সেভ করুন। ফোন পরিবর্তন করলেও সব হিসাব থাকবে সুরক্ষিত।"
    } else {
        "Save backup to your Google account to keep your finances secure. Restore anytime on any device."
    }
    val backupBannerBtn = if (lang == AppLanguage.BN) "ব্যাকআপ চালু করুন" else "Enable Backup"
    val backupRestoreSettingTitle = if (lang == AppLanguage.BN) "Google Drive ব্যাকআপ ও রিস্টোর" else "Google Drive Backup & Restore"
    val backupRestoreSettingSub = if (lang == AppLanguage.BN) "অ্যাকাউন্ট ব্যাকআপ, অটো-ব্যাকআপ ও রিস্টোর" else "Account backup, auto-backup & restore"

    // Profile & Greeting
    val profileWelcomeTitle = if (lang == AppLanguage.BN) "HisabBoi-তে আপনাকে স্বাগতম 🌿" else "Welcome to HisabBoi 🌿"
    val profileWelcomeSub = if (lang == AppLanguage.BN) "সহজ ও শান্ত পরিবেশে আপনার ব্যক্তিগত হিসাব গুছিয়ে রাখুন" else "Organize your personal finances in a calm and simple way"
    val profileNameQuestion = if (lang == AppLanguage.BN) "আপনার নাম কী?" else "What is your name?"
    val profileNamePlaceholder = if (lang == AppLanguage.BN) "আপনার নাম লিখুন (যেমন: আরমান)" else "Enter your name (e.g. Arman)"
    val profileNameRequired = if (lang == AppLanguage.BN) "নাম লেখা আবশ্যক" else "Name is required"
    val profileAddPhoto = if (lang == AppLanguage.BN) "প্রোফাইল ছবি যোগ করুন" else "Add profile photo"
    val profileChangePhoto = if (lang == AppLanguage.BN) "ছবি পরিবর্তন করুন" else "Change photo"
    val profileRemovePhoto = if (lang == AppLanguage.BN) "ছবি মুছে ফেলুন" else "Remove photo"
    val profilePhotoOptional = if (lang == AppLanguage.BN) "ছবি যোগ করা ঐচ্ছিক" else "Adding photo is optional"
    val profileStartBtn = if (lang == AppLanguage.BN) "শুরু করি 🌿" else "Get Started 🌿"
    val profileSettingsTitle = if (lang == AppLanguage.BN) "প্রোফাইল ও শুভেচ্ছা" else "Profile & Greeting"
    val profileSettingsSub = if (lang == AppLanguage.BN) "নাম ও প্রোফাইল ছবি সম্পাদনা করুন" else "Edit name and profile photo"
    val profileEditTitle = if (lang == AppLanguage.BN) "প্রোফাইল সম্পাদনা" else "Edit Profile"
    val profileSaveSuccess = if (lang == AppLanguage.BN) "প্রোফাইল সফলভাবে আপডেট হয়েছে" else "Profile updated successfully"
    val guestUser = if (lang == AppLanguage.BN) "গেস্ট ইউজার" else "Guest User"
}
