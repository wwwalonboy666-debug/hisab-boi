package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.ThemeMode
import com.example.ui.components.ProfileAvatar
import com.example.viewmodel.HisabBoiViewModel

@Composable
fun SettingsScreen(
    viewModel: HisabBoiViewModel,
    onOpenBackupRestore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val language by viewModel.currentLanguage.collectAsState()
    val themeMode by viewModel.currentThemeMode.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val strings by viewModel.strings.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var showProfileEditDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showCategoryManagementSheet by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Text(
                text = strings.settingsTitle,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Section: Profile & Greeting
        item {
            SettingsCard(title = strings.profileSettingsTitle) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showProfileEditDialog = true }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .testTag("settings_profile_row"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        ProfileAvatar(
                            photoPath = userProfile.photoPath,
                            photoUpdatedAt = userProfile.photoUpdatedAt,
                            size = 48.dp
                        )
                        Column {
                            Text(
                                text = userProfile.name.ifEmpty { strings.guestUser },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.profileSettingsSub,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = strings.profileEditTitle,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Section: Appearance
        item {
            SettingsCard(title = strings.appearanceSection) {
                ThemeOptionRow(
                    title = strings.themeSystem,
                    icon = Icons.Default.BrightnessMedium,
                    isSelected = themeMode == ThemeMode.SYSTEM,
                    onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                    testTag = "theme_system_radio"
                )
                ThemeOptionRow(
                    title = strings.themeLight,
                    icon = Icons.Default.LightMode,
                    isSelected = themeMode == ThemeMode.LIGHT,
                    onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                    testTag = "theme_light_radio"
                )
                ThemeOptionRow(
                    title = strings.themeDark,
                    icon = Icons.Default.DarkMode,
                    isSelected = themeMode == ThemeMode.DARK,
                    onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
                    testTag = "theme_dark_radio"
                )
            }
        }

        // Section: Language
        item {
            SettingsCard(title = strings.languageSection) {
                ThemeOptionRow(
                    title = strings.langBn,
                    icon = Icons.Default.Language,
                    isSelected = language == AppLanguage.BN,
                    onClick = { viewModel.setLanguage(AppLanguage.BN) },
                    testTag = "lang_bn_radio"
                )
                ThemeOptionRow(
                    title = strings.langEn,
                    icon = Icons.Default.Language,
                    isSelected = language == AppLanguage.EN,
                    onClick = { viewModel.setLanguage(AppLanguage.EN) },
                    testTag = "lang_en_radio"
                )
            }
        }

        // Section: Currency
        item {
            SettingsCard(title = strings.currencySection) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = strings.currencyDisplay,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Section: Category Management
        item {
            SettingsCard(title = strings.manageCategoriesTitle) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showCategoryManagementSheet = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("manage_categories_setting_btn"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.manageCategoriesTitle,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.manageCategoriesSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section: Reminders & Notifications
        item {
            SettingsCard(title = strings.notificationSection) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.dailyReminderTitle,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.dailyReminderSub,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("reminders_switch")
                    )
                }
            }
        }

        // Section: Google Drive Backup & Restore
        item {
            SettingsCard(title = strings.backupRestoreTitle) {
                ClickableSettingRow(
                    title = strings.backupRestoreSettingTitle,
                    icon = Icons.Default.CloudSync,
                    onClick = onOpenBackupRestore,
                    testTag = "settings_backup_restore_row"
                )
            }
        }

        // Section: About & Privacy
        item {
            SettingsCard(title = strings.aboutSection) {
                ClickableSettingRow(
                    title = strings.privacyTitle,
                    icon = Icons.Default.Security,
                    onClick = { showPrivacyDialog = true },
                    testTag = "privacy_policy_row"
                )
                ClickableSettingRow(
                    title = strings.termsTitle,
                    icon = Icons.Default.Info,
                    onClick = { showTermsDialog = true },
                    testTag = "terms_row"
                )
                ClickableSettingRow(
                    title = "${strings.aboutSection} - v1.0.0",
                    icon = Icons.Default.PrivacyTip,
                    onClick = { showAboutDialog = true },
                    testTag = "about_row"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Profile Edit Dialog
    if (showProfileEditDialog) {
        ProfileEditDialog(
            viewModel = viewModel,
            userProfile = userProfile,
            onDismiss = { showProfileEditDialog = false }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = strings.privacyTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == AppLanguage.BN) {
                        "হিসাববই একটি ১০০% অফলাইন ও ব্যক্তিগত অ্যাপ্লিকেশন।\n\n" +
                                "• কোনো সার্ভার নেই: আপনার আয়, ব্যয় বা ব্যক্তিগত কোনো হিসাব কোনো সার্ভার বা ক্লাউডে পাঠানো হয় না।\n" +
                                "• ট্র্যাকিং বা বিজ্ঞাপন নেই: অ্যাপে কোনো ট্র্যাকার বা থার্ড-পার্টি বিজ্ঞাপন নেই।\n" +
                                "• আপনার ডিভাইসেই সব নিরাপদ: আপনার সব হিসাব রুম ডেটাবেজে সম্পূর্ণ লোকালভাবে সংরক্ষিত থাকে।"
                    } else {
                        "HisabBoi is a 100% offline and private application.\n\n" +
                                "• Zero Servers: Your financial records never leave your local device.\n" +
                                "• Zero Tracking & Ads: No analytics, tracking or commercial advertising.\n" +
                                "• Completely Local: All data is securely stored inside your local device database."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("ঠিক আছে")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    text = strings.termsTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (language == AppLanguage.BN) {
                        "হিসাববই ব্যক্তিগত ব্যবহারের জন্য তৈরি একটি সহজ আর্থিক হিসাবের খাতা। ব্যবহারকারী নিজ দায়িত্বে হিসাব সংরক্ষণ করেন। অ্যাপটি কোনো আর্থিক পরামর্শ প্রদান করে না।"
                    } else {
                        "HisabBoi is designed as a peaceful personal accounting ledger. All calculations are performed strictly on your device for personal tracking purposes."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("ঠিক আছে")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hisabboi_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "HisabBoi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "“${strings.tagline}”",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (language == AppLanguage.BN) {
                            "প্রকৃতি, অর্থ ও প্রশান্তি—এই ভাবনায় বাংলাদেশের মানুষের জন্য তৈরি একটি সহজ, সুন্দর ও অফলাইন ফাইন্যান্স অ্যাপ।"
                        } else {
                            "Nature × Finance × Peace: A beautiful, peaceful, offline-first personal finance app crafted with love."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${strings.appVersionLabel} 1.0.0 (Native Android / Compose)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("ঠিক আছে")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Category Management Sheet
    if (showCategoryManagementSheet) {
        CategoryManagementBottomSheet(
            viewModel = viewModel,
            strings = strings,
            language = language,
            onDismiss = { showCategoryManagementSheet = false }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
    }
}

@Composable
private fun ClickableSettingRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
