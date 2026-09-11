package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.ElectricRickshaw
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.entity.CustomCategoryEntity

data class Category(
    val key: String,
    val nameBn: String,
    val nameEn: String,
    val icon: ImageVector,
    val type: TransactionType,
    val color: Color,
    val isCustom: Boolean = false,
    val groupKey: String = "",
    val isArchived: Boolean = false
)

data class CategoryGroup(
    val key: String,
    val nameBn: String,
    val nameEn: String,
    val emoji: String,
    val icon: ImageVector,
    val categories: List<Category>
)

object CategoryRegistry {

    // A. বাসা ও পরিবার (Home & Family)
    val RENT = Category("EXP_RENT", "বাসা ভাড়া", "House Rent", Icons.Default.Home, TransactionType.EXPENSE, Color(0xFF5C6BC0), groupKey = "GRP_HOME")
    val ELECTRICITY = Category("EXP_ELECTRICITY", "বিদ্যুৎ", "Electricity", Icons.Default.Bolt, TransactionType.EXPENSE, Color(0xFFFFB300), groupKey = "GRP_HOME")
    val GAS = Category("EXP_GAS", "গ্যাস", "Gas", Icons.Default.LocalGasStation, TransactionType.EXPENSE, Color(0xFFFF7043), groupKey = "GRP_HOME")
    val WATER = Category("EXP_WATER", "পানি", "Water", Icons.Default.WaterDrop, TransactionType.EXPENSE, Color(0xFF29B6F6), groupKey = "GRP_HOME")
    val INTERNET = Category("EXP_INTERNET", "ইন্টারনেট", "Internet", Icons.Default.Wifi, TransactionType.EXPENSE, Color(0xFF26A69A), groupKey = "GRP_HOME")
    val MOBILE = Category("EXP_MOBILE", "মোবাইল/ফোন", "Mobile / Phone", Icons.Default.PhoneAndroid, TransactionType.EXPENSE, Color(0xFF7E57C2), groupKey = "GRP_HOME")
    val BILLS = Category("EXP_BILLS", "বিল", "Utility Bills", Icons.Default.ReceiptLong, TransactionType.EXPENSE, Color(0xFFFFB74D), groupKey = "GRP_HOME")
    val HOME_OTHER = Category("EXP_HOME_OTHER", "বাসার অন্যান্য", "Other Home", Icons.Default.HomeWork, TransactionType.EXPENSE, Color(0xFF8D6E63), groupKey = "GRP_HOME")

    // B. খাবার ও বাজার (Food & Groceries)
    val FOOD = Category("EXP_FOOD", "খাবার", "Food & Dining", Icons.Default.Restaurant, TransactionType.EXPENSE, Color(0xFFE57373), groupKey = "GRP_FOOD")
    val GROCERY = Category("EXP_GROCERY", "বাজার", "Groceries", Icons.Default.ShoppingCart, TransactionType.EXPENSE, Color(0xFF81C784), groupKey = "GRP_FOOD")
    val SNACKS = Category("EXP_SNACKS", "নাস্তা/চা", "Snacks & Tea", Icons.Default.LocalCafe, TransactionType.EXPENSE, Color(0xFFA1887F), groupKey = "GRP_FOOD")
    val RESTAURANT = Category("EXP_RESTAURANT", "রেস্টুরেন্ট", "Restaurant", Icons.Default.RestaurantMenu, TransactionType.EXPENSE, Color(0xFFFF8A65), groupKey = "GRP_FOOD")
    val HOUSEHOLD = Category("EXP_HOUSEHOLD", "গৃহস্থালি", "Household", Icons.Default.Kitchen, TransactionType.EXPENSE, Color(0xFF4DB6AC), groupKey = "GRP_FOOD")

    // C. সন্তান ও শিক্ষা (Kids & Education)
    val EDUCATION = Category("EXP_EDUCATION", "শিক্ষা", "Education", Icons.Default.School, TransactionType.EXPENSE, Color(0xFF4DB6AC), groupKey = "GRP_EDUCATION")
    val SCHOOL_COLLEGE = Category("EXP_SCHOOL_COLLEGE", "স্কুল/কলেজ", "School/College", Icons.Default.School, TransactionType.EXPENSE, Color(0xFF42A5F5), groupKey = "GRP_EDUCATION")
    val SCHOOL_FEE = Category("EXP_SCHOOL_FEE", "স্কুল ফি", "School Fee", Icons.Default.Receipt, TransactionType.EXPENSE, Color(0xFF26C6DA), groupKey = "GRP_EDUCATION")
    val COACHING = Category("EXP_COACHING", "কোচিং", "Coaching", Icons.Default.MenuBook, TransactionType.EXPENSE, Color(0xFFAB47BC), groupKey = "GRP_EDUCATION")
    val BOOKS = Category("EXP_BOOKS", "বই/খাতা", "Books & Stationery", Icons.Default.Book, TransactionType.EXPENSE, Color(0xFF78909C), groupKey = "GRP_EDUCATION")
    val KIDS_CLOTHES = Category("EXP_KIDS_CLOTHES", " সন্তানের পোশাক", "Kids Clothing", Icons.Default.Checkroom, TransactionType.EXPENSE, Color(0xFFEC407A), groupKey = "GRP_EDUCATION")
    val KIDS_HEALTH = Category("EXP_KIDS_HEALTH", " সন্তানের চিকিৎসা", "Kids Healthcare", Icons.Default.Healing, TransactionType.EXPENSE, Color(0xFFEF5350), groupKey = "GRP_EDUCATION")
    val KIDS_OTHER = Category("EXP_KIDS_OTHER", "সন্তানের অন্যান্য", "Kids Other", Icons.Default.ChildCare, TransactionType.EXPENSE, Color(0xFFFFA726), groupKey = "GRP_EDUCATION")

    // D. যাতায়াত (Transport)
    val TRANSPORT = Category("EXP_TRANSPORT", "যাতায়াত", "Transport", Icons.Default.DirectionsBus, TransactionType.EXPENSE, Color(0xFF64B5F6), groupKey = "GRP_TRANSPORT")
    val BUS_RICKSHAW = Category("EXP_BUS_RICKSHAW", "বাস/রিকশা", "Bus / Rickshaw", Icons.Default.DirectionsBus, TransactionType.EXPENSE, Color(0xFF42A5F5), groupKey = "GRP_TRANSPORT")
    val CNG = Category("EXP_CNG", "CNG", "CNG Auto", Icons.Default.LocalTaxi, TransactionType.EXPENSE, Color(0xFF66BB6A), groupKey = "GRP_TRANSPORT")
    val RIDE_SHARE = Category("EXP_RIDE_SHARE", "রাইড শেয়ার", "Ride Share", Icons.Default.ElectricRickshaw, TransactionType.EXPENSE, Color(0xFFFFA726), groupKey = "GRP_TRANSPORT")
    val CAR = Category("EXP_CAR", "গাড়ি", "Car", Icons.Default.DirectionsCar, TransactionType.EXPENSE, Color(0xFF26A69A), groupKey = "GRP_TRANSPORT")
    val FUEL = Category("EXP_FUEL", "জ্বালানি", "Fuel / Petrol", Icons.Default.LocalGasStation, TransactionType.EXPENSE, Color(0xFFFF7043), groupKey = "GRP_TRANSPORT")
    val CAR_REPAIR = Category("EXP_CAR_REPAIR", "গাড়ি মেরামত", "Car Repair", Icons.Default.Build, TransactionType.EXPENSE, Color(0xFF78909C), groupKey = "GRP_TRANSPORT")

    // E. স্বাস্থ্য (Healthcare)
    val HEALTHCARE = Category("EXP_HEALTHCARE", "চিকিৎসা", "Healthcare", Icons.Default.LocalHospital, TransactionType.EXPENSE, Color(0xFFF06292), groupKey = "GRP_HEALTH")
    val DOCTOR = Category("EXP_DOCTOR", "ডাক্তার", "Doctor Consultation", Icons.Default.MedicalServices, TransactionType.EXPENSE, Color(0xFF26A69A), groupKey = "GRP_HEALTH")
    val MEDICINE = Category("EXP_MEDICINE", "ওষুধ", "Medicine", Icons.Default.Medication, TransactionType.EXPENSE, Color(0xFFEC407A), groupKey = "GRP_HEALTH")
    val TESTS = Category("EXP_TESTS", "পরীক্ষা", "Medical Tests", Icons.Default.Science, TransactionType.EXPENSE, Color(0xFF7E57C2), groupKey = "GRP_HEALTH")
    val HOSPITAL = Category("EXP_HOSPITAL", "হাসপাতাল", "Hospital", Icons.Default.LocalHospital, TransactionType.EXPENSE, Color(0xFFE57373), groupKey = "GRP_HEALTH")
    val HEALTH_OTHER = Category("EXP_HEALTH_OTHER", "স্বাস্থ্য অন্যান্য", "Other Healthcare", Icons.Default.Healing, TransactionType.EXPENSE, Color(0xFF8D6E63), groupKey = "GRP_HEALTH")

    // F. নিজের জন্য (Personal)
    val SHOPPING = Category("EXP_SHOPPING", "পোশাক / শপিং", "Shopping & Clothes", Icons.Default.Storefront, TransactionType.EXPENSE, Color(0xFFBA68C8), groupKey = "GRP_PERSONAL")
    val PERSONAL_CARE = Category("EXP_PERSONAL_CARE", "ব্যক্তিগত যত্ন", "Personal Care", Icons.Default.Face, TransactionType.EXPENSE, Color(0xFFF06292), groupKey = "GRP_PERSONAL")
    val GADGETS = Category("EXP_GADGETS", "মোবাইল/গ্যাজেট", "Gadgets & Electronics", Icons.Default.Devices, TransactionType.EXPENSE, Color(0xFF5C6BC0), groupKey = "GRP_PERSONAL")
    val SELF_LEARNING = Category("EXP_SELF_LEARNING", "নিজের শিক্ষা", "Self Learning", Icons.Default.AutoStories, TransactionType.EXPENSE, Color(0xFF26A69A), groupKey = "GRP_PERSONAL")
    val PERSONAL_OTHER = Category("EXP_PERSONAL_OTHER", "নিজের অন্যান্য", "Other Personal", Icons.Default.Person, TransactionType.EXPENSE, Color(0xFF78909C), groupKey = "GRP_PERSONAL")

    // G. সামাজিক ও বিনোদন (Social & Entertainment)
    val ENTERTAINMENT = Category("EXP_ENTERTAINMENT", "বিনোদন", "Entertainment", Icons.Default.Movie, TransactionType.EXPENSE, Color(0xFFAED581), groupKey = "GRP_SOCIAL")
    val TRAVEL = Category("EXP_TRAVEL", "ঘোরাঘুরি", "Travel & Tour", Icons.Default.Flight, TransactionType.EXPENSE, Color(0xFF29B6F6), groupKey = "GRP_SOCIAL")
    val GIFT_EXPENSE = Category("EXP_GIFT", "উপহার", "Gift Given", Icons.Default.CardGiftcard, TransactionType.EXPENSE, Color(0xFFFFB300), groupKey = "GRP_SOCIAL")
    val EVENTS = Category("EXP_EVENTS", "অনুষ্ঠান/দাওয়াত", "Events & Parties", Icons.Default.Celebration, TransactionType.EXPENSE, Color(0xFFAB47BC), groupKey = "GRP_SOCIAL")
    val SOCIAL_COST = Category("EXP_SOCIAL", "সামাজিক খরচ", "Social Expenses", Icons.Default.Diversity3, TransactionType.EXPENSE, Color(0xFF26C6DA), groupKey = "GRP_SOCIAL")

    // H. ব্যবসা ও কাজ (Business & Work)
    val BUSINESS_EXPENSE = Category("EXP_BUSINESS_EXPENSE", "ব্যবসার খরচ", "Business Expense", Icons.Default.BusinessCenter, TransactionType.EXPENSE, Color(0xFF5C6BC0), groupKey = "GRP_BUSINESS")
    val OFFICE = Category("EXP_OFFICE", "অফিস", "Office Costs", Icons.Default.Apartment, TransactionType.EXPENSE, Color(0xFF42A5F5), groupKey = "GRP_BUSINESS")
    val WORK_COMMUTE = Category("EXP_WORK_COMMUTE", "কাজের যাতায়াত", "Work Commute", Icons.Default.DirectionsBus, TransactionType.EXPENSE, Color(0xFF66BB6A), groupKey = "GRP_BUSINESS")
    val TOOLS = Category("EXP_TOOLS", "সরঞ্জাম", "Tools & Supplies", Icons.Default.Build, TransactionType.EXPENSE, Color(0xFF8D6E63), groupKey = "GRP_BUSINESS")
    val WORK_OTHER = Category("EXP_WORK_OTHER", "অন্যান্য কাজের খরচ", "Other Work Costs", Icons.Default.WorkOutline, TransactionType.EXPENSE, Color(0xFF78909C), groupKey = "GRP_BUSINESS")

    // I. আর্থিক (Financial)
    val LOAN = Category("EXP_LOAN", "ঋণের কিস্তি", "Loan Installment", Icons.Default.AccountBalance, TransactionType.EXPENSE, Color(0xFFE57373), groupKey = "GRP_FINANCIAL")
    val BANK_CHARGES = Category("EXP_BANK_CHARGES", "ব্যাংক/সার্ভিস চার্জ", "Bank Charges", Icons.Default.CreditCard, TransactionType.EXPENSE, Color(0xFFFFB74D), groupKey = "GRP_FINANCIAL")
    val FINANCIAL_OTHER = Category("EXP_FINANCIAL_OTHER", "অন্যান্য আর্থিক খরচ", "Other Financial", Icons.Default.CurrencyExchange, TransactionType.EXPENSE, Color(0xFF90A4AE), groupKey = "GRP_FINANCIAL")

    // J. অন্যান্য (Other)
    val OTHER_EXPENSE = Category("EXP_OTHER", "অন্যান্য", "Other Expense", Icons.Default.MoreHoriz, TransactionType.EXPENSE, Color(0xFF90A4AE), groupKey = "GRP_OTHER")

    // Income Categories
    val SALARY = Category("INC_SALARY", "বেতন", "Salary", Icons.Default.Payments, TransactionType.INCOME, Color(0xFF4CAF50))
    val BUSINESS = Category("INC_BUSINESS", "ব্যবসা", "Business", Icons.Default.Storefront, TransactionType.INCOME, Color(0xFF2E7D32))
    val FREELANCING = Category("INC_FREELANCING", "ফ্রিল্যান্সিং", "Freelancing", Icons.Default.Work, TransactionType.INCOME, Color(0xFF00897B))
    val GIFT = Category("INC_GIFT", "উপহার", "Gift", Icons.Default.CardGiftcard, TransactionType.INCOME, Color(0xFFFF8F00))
    val OTHER_INCOME = Category("INC_OTHER", "অন্যান্য", "Other Income", Icons.Default.MonetizationOn, TransactionType.INCOME, Color(0xFF558B2F))

    // Group Definitions for 2-Tier Category Picker
    val expenseGroups = listOf(
        CategoryGroup("GRP_HOME", "বাসা ও পরিবার", "Home & Family", "🏠", Icons.Default.Home, listOf(RENT, ELECTRICITY, GAS, WATER, INTERNET, MOBILE, BILLS, HOME_OTHER)),
        CategoryGroup("GRP_FOOD", "খাবার ও বাজার", "Food & Groceries", "🍚", Icons.Default.Restaurant, listOf(FOOD, GROCERY, SNACKS, RESTAURANT, HOUSEHOLD)),
        CategoryGroup("GRP_EDUCATION", "সন্তান ও শিক্ষা", "Kids & Education", "👨‍👩‍👧", Icons.Default.School, listOf(EDUCATION, SCHOOL_COLLEGE, SCHOOL_FEE, COACHING, BOOKS, KIDS_CLOTHES, KIDS_HEALTH, KIDS_OTHER)),
        CategoryGroup("GRP_TRANSPORT", "যাতায়াত", "Transport", "🚗", Icons.Default.DirectionsBus, listOf(TRANSPORT, BUS_RICKSHAW, CNG, RIDE_SHARE, CAR, FUEL, CAR_REPAIR)),
        CategoryGroup("GRP_HEALTH", "স্বাস্থ্য", "Healthcare", "🏥", Icons.Default.LocalHospital, listOf(HEALTHCARE, DOCTOR, MEDICINE, TESTS, HOSPITAL, HEALTH_OTHER)),
        CategoryGroup("GRP_PERSONAL", "নিজের জন্য", "Personal", "👤", Icons.Default.Person, listOf(SHOPPING, PERSONAL_CARE, GADGETS, SELF_LEARNING, PERSONAL_OTHER)),
        CategoryGroup("GRP_SOCIAL", "সামাজিক ও বিনোদন", "Social & Fun", "🎉", Icons.Default.Celebration, listOf(ENTERTAINMENT, TRAVEL, GIFT_EXPENSE, EVENTS, SOCIAL_COST)),
        CategoryGroup("GRP_BUSINESS", "ব্যবসা ও কাজ", "Business & Work", "💼", Icons.Default.BusinessCenter, listOf(BUSINESS_EXPENSE, OFFICE, WORK_COMMUTE, TOOLS, WORK_OTHER)),
        CategoryGroup("GRP_FINANCIAL", "আর্থিক", "Financial", "💳", Icons.Default.CreditCard, listOf(LOAN, BANK_CHARGES, FINANCIAL_OTHER)),
        CategoryGroup("GRP_OTHER", "অন্যান্য", "Other", "📦", Icons.Default.MoreHoriz, listOf(OTHER_EXPENSE))
    )

    // Flat list of built-in expense categories
    val baseExpenseCategories: List<Category> = expenseGroups.flatMap { it.categories }

    // Flat list of built-in income categories
    val baseIncomeCategories: List<Category> = listOf(SALARY, BUSINESS, FREELANCING, GIFT, OTHER_INCOME)

    val expenseCategories: List<Category> = baseExpenseCategories
    val incomeCategories: List<Category> = baseIncomeCategories
    val allCategories: List<Category> = expenseCategories + incomeCategories

    // Available Icons for Custom Category Creation
    val availableCustomIcons = listOf(
        "Storefront" to Icons.Default.Storefront,
        "Pets" to Icons.Default.Pets,
        "Agriculture" to Icons.Default.Agriculture,
        "Spa" to Icons.Default.Spa,
        "Flight" to Icons.Default.Flight,
        "DirectionsCar" to Icons.Default.DirectionsCar,
        "Medication" to Icons.Default.Medication,
        "ChildCare" to Icons.Default.ChildCare,
        "Work" to Icons.Default.Work,
        "School" to Icons.Default.School,
        "Home" to Icons.Default.Home,
        "ShoppingCart" to Icons.Default.ShoppingCart,
        "Celebration" to Icons.Default.Celebration,
        "AccountBalance" to Icons.Default.AccountBalance
    )

    fun resolveIcon(iconName: String): ImageVector {
        return availableCustomIcons.find { it.first.equals(iconName, ignoreCase = true) }?.second
            ?: Icons.Default.Spa
    }

    fun parseColor(hex: String, defaultColor: Color = Color(0xFF4CAF50)): Color {
        return try {
            val cleanHex = hex.removePrefix("#")
            val colorLong = cleanHex.toLong(16)
            if (cleanHex.length <= 6) {
                Color(colorLong or 0x00000000FF000000)
            } else {
                Color(colorLong)
            }
        } catch (e: Exception) {
            defaultColor
        }
    }

    fun customEntityToCategory(entity: CustomCategoryEntity): Category {
        val type = if (entity.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
        return Category(
            key = entity.key,
            nameBn = entity.nameBn,
            nameEn = entity.nameEn,
            icon = resolveIcon(entity.iconName),
            type = type,
            color = parseColor(entity.colorHex),
            isCustom = true,
            groupKey = "GRP_CUSTOM",
            isArchived = entity.isArchived
        )
    }

    fun getCategory(key: String, customCategories: List<CustomCategoryEntity> = emptyList()): Category {
        // 1. Check built-in categories first
        val builtIn = allCategories.find { it.key == key }
        if (builtIn != null) return builtIn

        // 2. Check custom categories
        val custom = customCategories.find { it.key == key }
        if (custom != null) {
            return customEntityToCategory(custom)
        }

        // 3. Backward-compatibility or safe fallback
        return if (key.startsWith("INC_")) OTHER_INCOME else OTHER_EXPENSE
    }
}
