package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ServiceCategory
import com.example.data.models.ServiceData
import com.example.data.models.ServiceItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

// ==========================================
// 1. SPLASH SCREEN
// ==========================================
@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1.1f else 0.8f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
    val opacity by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2200)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(NavyBlueDark, NavyBluePrimary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Logo Outer Ring
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f * opacity))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.HomeRepairService,
                        contentDescription = "One Call Service Logo",
                        tint = NavyBlueDark,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Header with letter spacing & scale animations
            Text(
                text = "One Call",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.testTag("splash_title")
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            // Sub-tagline
            Text(
                text = "YOUR SAFETY HOME • OUR PRIORITY",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    letterSpacing = 1.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Elegant Loading Indicator
            CircularProgressIndicator(
                color = GoldAccent,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// ==========================================
// 2. OTP SIGN-IN VERIFICATION SCREEN
// ==========================================
@Composable
fun OTPScreen(
    onOtpSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(45) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = countdownSeconds) {
        if (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(NavyBlueDark, NavyBluePrimary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Back Button & Title row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = NavyBluePrimary
                        )
                    }
                    Text(
                        text = "OTP Verification",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NavyBluePrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp)) // Equalizer space
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Key Lock Security Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(GoldLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LockPerson,
                        contentDescription = "OTP Lock Icon",
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Verify Your Number",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NavyBluePrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A 4-digit verification code has been sent to your mobile device (+91 ***** **210). Please enter it below to access your account.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Custom OTP Code Visualizer Box
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 0 until 4) {
                        val digit = if (i < otpValue.length) otpValue[i].toString() else ""
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .border(
                                    BorderStroke(
                                        width = 2.dp,
                                        color = if (showError) AlertRed else if (i == otpValue.length) GoldAccent else NavyBluePrimary.copy(alpha = 0.3f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                    if (i < otpValue.length) NavyBluePrimary.copy(alpha = 0.05f) else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBluePrimary,
                                    fontSize = 22.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hidden but functional input field to support platform/virtual keyboard
                OneCallTextField(
                    value = otpValue,
                    onValueChange = {
                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                            otpValue = it
                            showError = false
                        }
                    },
                    label = "Type 4-digit OTP",
                    leadingIcon = Icons.Filled.Pin,
                    placeholder = "Enter 4 digit code",
                    singleLine = true,
                    modifier = Modifier.testTag("otp_login_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Demo notification hint
                Card(
                    colors = CardDefaults.cardColors(containerColor = GoldLight.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Demo OTP PIN: 1234", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyBlueDark)
                    }
                }

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Invalid verification code. Please check and try again.",
                        color = AlertRed,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit OTP
                OneCallButton(
                    text = "VERIFY & SIGN IN",
                    onClick = {
                        focusManager.clearFocus()
                        if (otpValue == "1234") {
                            onOtpSuccess()
                        } else {
                            showError = true
                        }
                    },
                    containerColor = GoldAccent,
                    contentColor = NavyBlueDark,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "verify_otp_button"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Timer & Resend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Didn't receive code? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight
                    )
                    if (countdownSeconds > 0) {
                        Text(
                            text = "Resend in ${countdownSeconds}s",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = NavyBluePrimary
                        )
                    } else {
                        Text(
                            text = "Resend Now",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            ),
                            modifier = Modifier
                                .clickable {
                                    countdownSeconds = 45
                                    otpValue = ""
                                    showError = false
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. SERVICE SEARCH AND EXPLORE SCREEN
// ==========================================
@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onNavigateToCategory: (ServiceCategory) -> Unit,
    onNavigateToDetail: (ServiceCategory, ServiceItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    val focusManager = LocalFocusManager.current

    val searchResults = remember(searchQuery, selectedCategoryFilter) {
        val rawResults = ServiceData.searchServices(searchQuery)
        if (selectedCategoryFilter == "All") {
            rawResults
        } else {
            rawResults.filter { (category, _) -> 
                category.name.equals(selectedCategoryFilter, ignoreCase = true)
            }
        }
    }

    val staticFilters = listOf("All", "Plumbing", "Electrical", "Cleaning", "AC Service", "Painting", "Appliance")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // App Sticky Search Header
        Surface(
            color = NavyBluePrimary,
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Explore Home Services",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Find certified professionals for all home needs",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar Field
                OneCallTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "Search for plumbing, wiring, AC repairs...",
                    leadingIcon = Icons.Filled.Search,
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    } else null,
                    modifier = Modifier.testTag("explore_search_bar")
                )
            }
        }

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(staticFilters) { filter ->
                val isSelected = selectedCategoryFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryFilter = filter },
                    label = { 
                        Text(
                            text = filter, 
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAccent,
                        selectedLabelColor = NavyBlueDark,
                        containerColor = Color.White,
                        labelColor = NavyBluePrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = GoldAccent,
                        borderColor = NavyBluePrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.minimumInteractiveComponentSize()
                )
            }
        }

        // Search Results List
        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(NavyBluePrimary.copy(alpha = 0.05f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SearchOff,
                            contentDescription = "No results",
                            tint = NavyBluePrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Service matches Found",
                        fontWeight = FontWeight.Bold,
                        color = NavyBluePrimary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your filters or typing a different keyword (e.g., 'tap', 'leak', 'fan').",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "${searchResults.size} matches in database",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryLight,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                items(searchResults) { (category, item) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                focusManager.clearFocus()
                                onNavigateToDetail(category, item)
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Service Icon block
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(NavyBluePrimary.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                val vector = when (category.iconName) {
                                    "plumbing" -> Icons.Filled.Plumbing
                                    "electrical" -> Icons.Filled.Bolt
                                    "cleaning" -> Icons.Filled.CleaningServices
                                    "ac" -> Icons.Filled.AcUnit
                                    "painting" -> Icons.Filled.FormatPaint
                                    "appliance" -> Icons.Filled.Router
                                    "security" -> Icons.Filled.Security
                                    else -> Icons.Filled.HomeRepairService
                                }
                                Icon(
                                    imageVector = vector,
                                    contentDescription = category.name,
                                    tint = NavyBluePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OneCallBadge(
                                        text = category.name,
                                        backgroundColor = GoldLight,
                                        textColor = NavyBlueDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "⏱ ${item.duration}",
                                        fontSize = 10.sp,
                                        color = TextSecondaryLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    color = NavyBluePrimary,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondaryLight,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Pricing & CTA Action
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${item.basePrice.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccess,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "Book",
                                    tint = NavyBluePrimary.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. NOTIFICATIONS SYSTEM SCREEN
// ==========================================
data class OneCallNotification(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isRead: Boolean = false,
    val badgeColor: Color = GoldLight
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateBack: () -> Unit
) {
    // Standard system notifications list
    var notificationsList by remember {
        mutableStateOf(
            listOf(
                OneCallNotification(
                    id = 1,
                    title = "Plumbing Booking Confirmed",
                    message = "Your Leakage repair request has been successfully assigned to Technician Arun Kumar. Job scheduled today at 4:00 PM.",
                    timestamp = "10 Mins Ago",
                    icon = Icons.Filled.CheckCircle,
                    badgeColor = Color(0xFFE8F5E9)
                ),
                OneCallNotification(
                    id = 2,
                    title = "Security Biometrics Enabled",
                    message = "Biometric Profile Verification is active! Your wallet balance and system accounts are now secured with hardware locks.",
                    timestamp = "1 Hour Ago",
                    icon = Icons.Filled.Fingerprint,
                    badgeColor = GoldLight
                ),
                OneCallNotification(
                    id = 3,
                    title = "Loyalty Points Multiplier",
                    message = "Congrats! You earned 150 extra cashback points. Access your Profile tab to purchase One Call AMC packages for greater savings.",
                    timestamp = "Yesterday",
                    icon = Icons.Filled.MilitaryTech,
                    badgeColor = Color(0xFFE3F2FD)
                ),
                OneCallNotification(
                    id = 4,
                    title = "Annual Maintenance Offer",
                    message = "Special 20% discount on One Call Platinum Membership packages! Enjoy unlimited free SOS callouts and zero diagnostic rates.",
                    timestamp = "2 Days Ago",
                    icon = Icons.Filled.CardMembership,
                    badgeColor = GoldLight
                ),
                OneCallNotification(
                    id = 5,
                    title = "FCM Notification Channel Synced",
                    message = "Realtime dispatch tracking and technician chat messaging routes have been linked for instant background notifications.",
                    timestamp = "4 Days Ago",
                    icon = Icons.Filled.RssFeed,
                    badgeColor = Color(0xFFECEFF1)
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Notifications", fontWeight = FontWeight.Bold, color = Color.White) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (notificationsList.any { !it.isRead }) {
                        TextButton(
                            onClick = {
                                notificationsList = notificationsList.map { it.copy(isRead = true) }
                            }
                        ) {
                            Text("Mark All Read", color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBluePrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightBackground)
        ) {
            if (notificationsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(NavyBluePrimary.copy(alpha = 0.05f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.NotificationsNone,
                                contentDescription = "No Notifications",
                                tint = NavyBluePrimary.copy(alpha = 0.4f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Inbox Fully Cleared!",
                            fontWeight = FontWeight.Bold,
                            color = NavyBluePrimary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "We will notify you here when active service requests or security dispatches undergo updates.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryLight,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notificationsList) { notification ->
                        val cardBg = if (notification.isRead) Color.White else Color(0xFFFBF9FF)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(
                                width = 1.dp, 
                                color = if (notification.isRead) Color.Transparent else NavyBluePrimary.copy(alpha = 0.08f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Dynamic Symbol Icon
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(notification.badgeColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = notification.icon,
                                        contentDescription = null,
                                        tint = NavyBluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Content text
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = notification.title,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyBluePrimary,
                                            fontSize = 14.sp
                                        )
                                        
                                        if (!notification.isRead) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .background(GoldAccent, CircleShape)
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(2.dp))
                                    
                                    Text(
                                        text = notification.timestamp,
                                        fontSize = 10.sp,
                                        color = TextSecondaryLight,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    
                                    Spacer(modifier = Modifier.height(6.dp))
                                    
                                    Text(
                                        text = notification.message,
                                        fontSize = 12.sp,
                                        color = NavyBlueDark.copy(alpha = 0.8f),
                                        lineHeight = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Delete Button
                                IconButton(
                                    onClick = {
                                        notificationsList = notificationsList.filter { it.id != notification.id }
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.CenterVertically)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete, 
                                        contentDescription = "Dismiss",
                                        tint = AlertRed.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
