package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import com.example.data.models.BookingEntity
import com.example.data.models.ProductEntity
import com.example.data.models.ServiceCategory
import com.example.data.models.ServiceData
import com.example.data.models.ServiceItem
import com.example.data.models.Technician
import com.example.data.models.UserEntity
import com.example.ui.components.*
import com.example.ui.navigation.Routes
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import androidx.fragment.app.FragmentActivity
import com.example.ui.BiometricHelper
import android.widget.Toast
import java.text.NumberFormat
import java.util.Locale

private val rupeeFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

fun Double.toRupeeString(): String {
    return rupeeFormat.format(this).replace("Rs.", "₹").replace("INR", "₹")
}

// --- 1. AUTH SCREENS ---

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

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
                // Branded Logo Header
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.HomeRepairService,
                        contentDescription = "One Call Logo",
                        tint = NavyBlueDark,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "One Call",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NavyBluePrimary
                    )
                )
                Text(
                    text = "Your Safety Home Our Priority",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )
                Spacer(modifier = Modifier.height(24.dp))

                OneCallTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    leadingIcon = Icons.Filled.Email,
                    testTag = "email_input"
                )
                Spacer(modifier = Modifier.height(12.dp))
                OneCallTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    leadingIcon = Icons.Filled.Lock,
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Filled.Visibility, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "password_input"
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (showError) {
                    Text(
                        text = "Please enter valid credentials.",
                        color = AlertRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                OneCallButton(
                    text = "LOGIN",
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            onLoginSuccess()
                        } else {
                            showError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "login_submit_button"
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onNavigateToSignup) {
                    Text(
                        text = "Don't have an account? Sign Up",
                        color = NavyBlueMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}



@Composable
fun SignupScreen(
    viewModel: MainViewModel,
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyBlueDark, NavyBluePrimary)))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 32.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = NavyBluePrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OneCallTextField(value = name, onValueChange = { name = it }, label = "Full Name", leadingIcon = Icons.Filled.Person)
                    Spacer(modifier = Modifier.height(12.dp))
                    OneCallTextField(value = email, onValueChange = { email = it }, label = "Email Address", leadingIcon = Icons.Filled.Email)
                    Spacer(modifier = Modifier.height(12.dp))
                    OneCallTextField(value = phone, onValueChange = { phone = it }, label = "Phone Number", leadingIcon = Icons.Filled.Phone)
                    Spacer(modifier = Modifier.height(12.dp))
                    OneCallTextField(value = address, onValueChange = { address = it }, label = "Home Address", leadingIcon = Icons.Filled.LocationOn)
                    Spacer(modifier = Modifier.height(12.dp))
                    OneCallTextField(value = password, onValueChange = { password = it }, label = "Password", leadingIcon = Icons.Filled.Lock)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    OneCallButton(
                        text = "SIGN UP",
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank()) {
                                viewModel.saveAddress(address)
                                onSignupSuccess()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onNavigateToLogin) {
                        Text("Already have an account? Login", color = NavyBlueMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- 2. HOME SCREEN ---

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    user: UserEntity?,
    onNavigateToCategory: (ServiceCategory) -> Unit,
    onNavigateToDetail: (ServiceCategory, ServiceItem) -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val context = LocalContext.current
    var searchVal by remember { mutableStateOf("") }
    val categories = ServiceData.categories
    
    // UI States for Sliders and Toggles
    var bannerIndex by remember { mutableStateOf(0) }
    var testimonialIndex by remember { mutableStateOf(0) }
    var showAllCategories by remember { mutableStateOf(false) }
    
    val banners = listOf(
        Triple("🌧️ Special Monsoon Offer", "20% OFF on Waterproofing & Leak Repairs", "Protect your walls and terrace before the heavy rains start."),
        Triple("❄️ AC Service Festival", "Flat ₹150 OFF on High-Pressure Jet Cleaning", "Prepare your home for optimal cooling efficiency."),
        Triple("🛡️ Smart Home Safety", "Biometric Smart Door Locks - Free Setup", "Upgrade your entry gate security with premium verified locks.")
    )

    val testimonials = listOf(
        Pair("Rajesh Gowda, HSR Layout", "“The waterproofing team was highly professional. They identified the terrace crack and sealed it in 3 hours. 5 stars!”"),
        Pair("Meera Nair, Indiranagar", "“Super quick geyser repair! The technician arrived within 25 minutes of booking and replaced the heating element safely.”"),
        Pair("Ankit Sharma, Whitefield", "“Excellent deep cleaning service. Every corner of my kitchen, including the chimney, is now spotless. Worth every rupee.”")
    )

    val popularServices = listOf(
        Triple("Full House Deep Cleaning", 2499.0, "cleaning"),
        Triple("AC Foam & Jet Service", 599.0, "ac"),
        Triple("Tap & Faucet Repair/Fitting", 149.0, "plumbing"),
        Triple("Smart Door Lock Installation", 999.0, "security"),
        Triple("Cockroach & Ant Gel Treatment", 699.0, "pestcontrol")
    )

    val blogs = listOf(
        Triple("5 Dampness Fixes For Heavy Monsoon Rains", "Read about simple steps to check wall moisture and prevent mold.", "10 mins read"),
        Triple("How to Lower Your AC Electricity Bill by 30%", "Simple tips like jet cleaning filters and thermostat tuning.", "6 mins read"),
        Triple("Why Modular Smart Door Locks are Worth the Hype", "Compare fingerprint scanners, RFID cards, and remote passcodes.", "8 mins read")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            
            // 1. HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(NavyBluePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Home, contentDescription = "One Call Logo", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "One Call",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = NavyBluePrimary
                        )
                        Text(
                            text = "Premium Home Solutions",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = AlertRed) {
                                    Text("2", color = Color.White, fontSize = 9.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "View Notifications",
                                tint = NavyBluePrimary
                            )
                        }
                    }

                    OneCallBadge(
                        text = user?.membershipTier ?: "Silver",
                        backgroundColor = GoldLight,
                        textColor = GoldAccent
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // 2. CURRENT LOCATION
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location Pin",
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CURRENT LOCATION",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondaryLight
                        )
                        Text(
                            text = user?.address ?: "HSR Layout, Sector 7, Bengaluru",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NavyBluePrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "Verified Pin",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenSuccess,
                        modifier = Modifier
                            .background(GreenSuccess.copy(0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // 3. SEARCH BAR
            OneCallTextField(
                value = searchVal,
                onValueChange = { searchVal = it },
                label = "Search 300+ plumbing, cleaning, painting...",
                leadingIcon = Icons.Filled.Search,
                testTag = "home_search_bar"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (searchVal.isNotBlank()) {
            // SEARCH RESULTS VIEW
            val searchResults = ServiceData.searchServices(searchVal)
            if (searchResults.isEmpty()) {
                item {
                    EmptyState(title = "No Services Found", subtitle = "Try searching for 'plumbing', 'cleaning', or 'ac'.", icon = Icons.Filled.SearchOff)
                }
            } else {
                item {
                    SectionHeader(title = "Search Results for '$searchVal'")
                }
                items(searchResults) { (category, item) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onNavigateToDetail(category, item) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(GoldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Filled.Build, contentDescription = null, tint = GoldAccent)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                Text(category.name, fontSize = 12.sp, color = TextSecondaryLight)
                                Text(item.basePrice.toRupeeString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            }
                        }
                    }
                }
            }
        } else {
            // MAIN HOME SECTIONS

            // 4. EMERGENCY BUTTON (SOS)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = AlertRed.copy(0.08f)),
                    border = BorderStroke(1.5.dp, AlertRed.copy(0.4f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(AlertRed, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Filled.Warning, contentDescription = "SOS Alert", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "🚨 EMERGENCY SOS DISPATCH",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = AlertRed
                                )
                                Text(
                                    text = "Tap for urgent gas/water leak, electrical short, or lockout.",
                                    fontSize = 11.sp,
                                    color = NavyBlueDark
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                val emergencyCat = ServiceData.categories.firstOrNull { it.id == "emergency" }
                                if (emergencyCat != null) {
                                    onNavigateToCategory(emergencyCat)
                                } else {
                                    Toast.makeText(context, "Initiating Urgent Emergency Dispatch...", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .background(AlertRed, RoundedCornerShape(8.dp))
                                .size(40.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "SOS Trigger", tint = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 5. BANNER SLIDER
            item {
                val banner = banners[bannerIndex]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyBluePrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OneCallBadge(text = banner.first, backgroundColor = GoldLight, textColor = GoldAccent)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(banners.size) { idx ->
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = if (idx == bannerIndex) GoldAccent else Color.White.copy(alpha = 0.4f),
                                                shape = CircleShape
                                            )
                                            .clickable { bannerIndex = idx }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(banner.second, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(banner.third, color = Color.White.copy(0.8f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OneCallButton(
                                text = "Claim Benefit Now",
                                onClick = {
                                    val catId = if (bannerIndex == 0) "waterproofing" else if (bannerIndex == 1) "ac" else "security"
                                    val matchedCat = ServiceData.categories.firstOrNull { it.id == catId }
                                    if (matchedCat != null) {
                                        onNavigateToCategory(matchedCat)
                                    } else {
                                        Toast.makeText(context, "Offer Applied! Select service.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                containerColor = GoldAccent,
                                contentColor = NavyBlueDark
                            )
                            
                            Row {
                                IconButton(
                                    onClick = { bannerIndex = (bannerIndex - 1 + banners.size) % banners.size },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(0.1f))
                                ) {
                                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Previous Banner", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { bannerIndex = (bannerIndex + 1) % banners.size },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(0.1f))
                                ) {
                                    Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "Next Banner", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 6. POPULAR SERVICES
            item {
                SectionHeader(title = "Most Booked Services")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(popularServices) { service ->
                        val matchedCategory = ServiceData.categories.firstOrNull { it.id == service.third }
                        val matchedItem = matchedCategory?.items?.firstOrNull() ?: ServiceItem(service.first, service.second, "Description", "1 Hour")
                        Card(
                            modifier = Modifier
                                .width(170.dp)
                                .clickable {
                                    if (matchedCategory != null) {
                                        viewModel.selectCategory(matchedCategory)
                                        viewModel.selectServiceItem(matchedItem)
                                        onNavigateToDetail(matchedCategory, matchedItem)
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .background(
                                            Brush.verticalGradient(listOf(NavyBluePrimary.copy(alpha = 0.1f), NavyBluePrimary.copy(alpha = 0.25f))),
                                            RoundedCornerShape(8.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (service.third) {
                                            "cleaning" -> Icons.Filled.CleaningServices
                                            "ac" -> Icons.Filled.Tv
                                            "plumbing" -> Icons.Filled.Plumbing
                                            "security" -> Icons.Filled.Security
                                            "pestcontrol" -> Icons.Filled.BugReport
                                            else -> Icons.Filled.HomeRepairService
                                        },
                                        contentDescription = service.first,
                                        tint = NavyBluePrimary,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(service.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyBluePrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(service.second.toRupeeString(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(11.dp))
                                        Text("4.9", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 7. CATEGORIES (Polished, clean, fully functional expandable 4-column grid of 25 items)
            item {
                SectionHeader(
                    title = "Explore All Categories",
                    actionText = if (showAllCategories) "Show Less" else "View All (${categories.size})",
                    onActionClick = { showAllCategories = !showAllCategories }
                )
                
                val visibleCategories = if (showAllCategories) categories else categories.take(8)
                
                // Representing the Grid cleanly via modular chunking
                val chunkedList = visibleCategories.chunked(4)
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    chunkedList.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowItems.forEach { category ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToCategory(category) },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(GoldLight, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val icon = when (category.id) {
                                                "cleaning" -> Icons.Filled.CleaningServices
                                                "painting" -> Icons.Filled.Brush
                                                "plumbing" -> Icons.Filled.Plumbing
                                                "electrical" -> Icons.Filled.ElectricalServices
                                                "ac" -> Icons.Filled.Tv
                                                "security" -> Icons.Filled.Security
                                                "pestcontrol" -> Icons.Filled.BugReport
                                                "packers" -> Icons.Filled.LocalShipping
                                                "emergency" -> Icons.Filled.Warning
                                                "amc" -> Icons.Filled.Assignment
                                                "inspection" -> Icons.Filled.Search
                                                "solar" -> Icons.Filled.WbSunny
                                                else -> Icons.Filled.HomeRepairService
                                            }
                                            Icon(imageVector = icon, contentDescription = category.name, tint = NavyBluePrimary, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = category.name,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NavyBluePrimary,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            // Pad remaining cells if the row is incomplete
                            if (rowItems.size < 4) {
                                repeat(4 - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 8. RECENTLY BOOKED
            item {
                SectionHeader(title = "Recently Booked & History")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(GreenSuccess.copy(0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Full House Deep Cleaning", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary)
                                    Text("Completed on July 10, 2026", fontSize = 11.sp, color = TextSecondaryLight)
                                }
                            }
                            
                            OneCallBadge(text = "Delivered", backgroundColor = GreenSuccess.copy(alpha = 0.12f), textColor = GreenSuccess)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Technician: Rajesh Prasad", fontSize = 11.sp, color = TextSecondaryLight)
                            Row(
                                modifier = Modifier.clickable {
                                    val cleaningCat = ServiceData.categories.firstOrNull { it.id == "cleaning" }
                                    if (cleaningCat != null) {
                                        onNavigateToCategory(cleaningCat)
                                    }
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Rebook Service", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 9. OFFERS
            item {
                SectionHeader(title = "Exclusive Promo Coupons")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        Pair("MONSOON20", "20% Off Waterproofing"),
                        Pair("FIRSTCALL", "Free Initial Inspection")
                    ).forEach { coupon ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    Toast.makeText(context, "Code '${coupon.first}' Copied!", Toast.LENGTH_SHORT).show()
                                },
                            colors = CardDefaults.cardColors(containerColor = GoldLight.copy(0.3f)),
                            border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = coupon.first,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = coupon.second,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    color = NavyBluePrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "TAP TO COPY",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 10. MEMBERSHIP DETAILS
            item {
                SectionHeader(title = "Premium Memberships")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyBlueDark),
                    border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("One Call Club Premium", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            OneCallBadge(text = "Platinum Club", backgroundColor = GoldLight, textColor = GoldAccent)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Enjoy unlimited free SOS Emergency Dispatches, complete 12-point seasonal electric checks, and 0% markup on parts.",
                            color = Color.White.copy(0.85f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Upgrade starting at ₹1,999/year", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OneCallButton(
                                text = "Learn Benefits",
                                onClick = { Toast.makeText(context, "Membership Details Opened!", Toast.LENGTH_SHORT).show() },
                                containerColor = Color.White.copy(0.15f),
                                contentColor = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 11. NEARBY TECHNICIANS
            item {
                SectionHeader(title = "Top Verified Technicians Nearby")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ServiceData.technicians) { tech ->
                        Card(
                            modifier = Modifier.width(160.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(GreenSuccess, CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ONLINE NOW", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GreenSuccess)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(tech.name, fontWeight = FontWeight.Bold, color = NavyBluePrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(tech.specialty, fontSize = 11.sp, color = TextSecondaryLight, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${tech.rating} (${tech.completedJobs} jobs)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 12. RATINGS & TRUST
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.25f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(0.4f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("4.93", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), color = NavyBluePrimary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) {
                                    Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("10,000+ Ratings", fontSize = 10.sp, color = TextSecondaryLight)
                        }
                        
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(60.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        
                        Column(modifier = Modifier.weight(0.6f).padding(start = 14.dp)) {
                            Text("Safe & Insured Work", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary)
                            Text("Every repair booking is backstopped by ₹10,000 damage coverage insurance protection automatically.", fontSize = 11.sp, color = TextSecondaryLight)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 13. TESTIMONIALS
            item {
                SectionHeader(title = "What Happy Customers Say")
                val item = testimonials[testimonialIndex]
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(item.second, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal), color = NavyBlueDark, minLines = 3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.first, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GoldAccent)
                            Row {
                                IconButton(
                                    onClick = { testimonialIndex = (testimonialIndex - 1 + testimonials.size) % testimonials.size },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Prev Testimonial", tint = NavyBluePrimary, modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { testimonialIndex = (testimonialIndex + 1) % testimonials.size },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "Next Testimonial", tint = NavyBluePrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 14. BLOGS & TIPS
            item {
                SectionHeader(title = "Home Care Blogs & Tips")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(blogs) { blog ->
                        Card(
                            modifier = Modifier
                                .width(230.dp)
                                .clickable { Toast.makeText(context, "Opening '${blog.first}'...", Toast.LENGTH_SHORT).show() },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(blog.first, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(blog.second, fontSize = 11.sp, color = TextSecondaryLight, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(blog.third, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    Text("Read Tip ↗", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 15. FOOTER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.VerifiedUser, contentDescription = null, tint = NavyBluePrimary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("One Call Quality Guarantee", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyBluePrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Aadhaar verified pros. 7-Day repair warranty. 100% secure payments. No hidden charges. Call support at 1800-103-CALL.",
                            fontSize = 10.sp,
                            color = TextSecondaryLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "© 2026 One Call Technologies Private Limited. All rights reserved.",
                            fontSize = 8.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- 3. SERVICE DETAIL & AI COST ESTIMATOR SCREEN ---

@Composable
fun ServiceDetailScreen(
    viewModel: MainViewModel,
    category: ServiceCategory,
    onNavigateBack: () -> Unit,
    onBookService: (ServiceItem, String, Double) -> Unit
) {
    val context = LocalContext.current
    val items = category.items.ifEmpty { 
        listOf(ServiceItem("Standard ${category.name}", 499.0, "Standard consultation, inspection, and core repairs.", "1-2 Hours"))
    }
    var selectedItem by remember { mutableStateOf(items.first()) }
    var selectedTier by remember { mutableStateOf("Standard") } // Standard, Premium, Deep
    
    val basePrice = selectedItem.basePrice
    val finalPrice = when (selectedTier) {
        "Premium" -> basePrice * 1.3
        "Deep" -> basePrice * 1.6
        else -> basePrice
    }

    // Dynamic available slots and dates states
    var selectedDate by remember { mutableStateOf("July 20, 2026") }
    var selectedTimeSlot by remember { mutableStateOf("11:00 AM - 01:00 PM") }

    // AI Cost Estimator states
    val estimatorInput by viewModel.estimatorInput.collectAsState()
    val estimatorOutput by viewModel.estimatorOutput.collectAsState()
    val isEstimatorLoading by viewModel.isEstimatorLoading.collectAsState()

    // Excluded list based on industry standard
    val excludedItems = listOf(
        "Cost of replacement parts or physical spare materials",
        "Major structural scaffolding or masonry work",
        "Post-job heavy debris disposal (except standard cleanup)"
    )

    // Frequently Asked Questions
    val faqs = listOf(
        Pair("Are materials included in the price?", "No, the price is for expert labor only. Any spare parts, materials, or accessories required will be procured by the technician at actual market rates, with a 100% transparent GST invoice."),
        Pair("What if the service takes longer than estimated?", "Our pricing is fixed per job. Even if the technician takes extra time, you will not be charged a rupee more than the estimated total."),
        Pair("Are your professionals verified?", "Yes, absolutely! Every One Call technician undergoes a multi-stage background check, police verification, and a rigorous 24-point hands-on skill certification program."),
        Pair("How does the 30-day warranty work?", "If any issue arises from the completed service within 30 days, we will dispatch a specialist to diagnose and repair it completely free of charge. Your satisfaction is fully guaranteed.")
    )

    // Customer reviews list
    val reviews = listOf(
        Triple("Shalini Hegde", "⭐⭐⭐⭐⭐", "“The professional arrived exactly on time and wore clean booties. They explained the issue before starting work and cleaned up beautifully afterwards. Very satisfied with the service!”"),
        Triple("Vinay Kumar", "⭐⭐⭐⭐⭐", "“Extremely efficient service. He diagnosed the short circuit in less than 5 minutes and fixed it safely. One Call is indeed a premium service. Worth every rupee.”"),
        Triple("Deepak Sen", "⭐⭐⭐⭐★", "“Excellent quality deep cleaning. They paid close attention to detail, especially in the kitchen. Minor delay of 15 minutes in arrival but the work was stellar.”")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NavyBluePrimary
                )
            }
            Text(category.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondaryLight, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
        }

        // 1. SERVICE IMAGE (Gradient canvas illustration)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.5f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val gradientColors = when (category.id) {
                        "cleaning" -> listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))
                        "painting" -> listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                        "electrical" -> listOf(Color(0xFFFFFDE7), Color(0xFFFFF9C4))
                        "plumbing" -> listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                        "ac" -> listOf(Color(0xFFE8EAF6), Color(0xFFC5CAE9))
                        "security" -> listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC))
                        else -> listOf(GoldLight, GoldAccent.copy(0.2f))
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(gradientColors))
                    )
                    
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val icon = when (category.id) {
                            "cleaning" -> Icons.Filled.CleaningServices
                            "painting" -> Icons.Filled.Brush
                            "plumbing" -> Icons.Filled.Plumbing
                            "electrical" -> Icons.Filled.ElectricalServices
                            "ac" -> Icons.Filled.Tv
                            "security" -> Icons.Filled.Security
                            "pestcontrol" -> Icons.Filled.BugReport
                            "packers" -> Icons.Filled.LocalShipping
                            "emergency" -> Icons.Filled.Warning
                            "amc" -> Icons.Filled.Assignment
                            "inspection" -> Icons.Filled.Search
                            "solar" -> Icons.Filled.WbSunny
                            else -> Icons.Filled.HomeRepairService
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = NavyBluePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "One Call Premium Protection",
                            fontWeight = FontWeight.Bold,
                            color = NavyBluePrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Background verified & certified specialists only",
                            color = TextSecondaryLight,
                            fontSize = 11.sp
                        )
                    }

                    // Rating Overlay Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(0.65f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("4.93 (142 reviews)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Top Left Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(GoldAccent, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("BESTSELLER", color = NavyBlueDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sub-services list (Select Item)
        item {
            Text("Select Specific Service Option", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { item ->
                    val isSelected = selectedItem == item
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedItem = item },
                        label = { Text(item.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyBluePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Pricing Quality Tier Selection
        item {
            Text("Choose Service Quality Tier", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val tiers = listOf("Standard", "Premium", "Deep")
                tiers.forEach { tier ->
                    val isSelected = selectedTier == tier
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTier = tier },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) NavyBluePrimary else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (isSelected) GoldAccent else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(tier, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else NavyBluePrimary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            val multiplierText = when (tier) {
                                "Premium" -> "+30% Quality"
                                "Deep" -> "+60% Extra Details"
                                else -> "Standard"
                            }
                            Text(multiplierText, fontSize = 10.sp, color = if (isSelected) Color.White.copy(0.8f) else TextSecondaryLight)
                        }
                    }
                }
            }
        }

        // 2. PRICE & DURATION DETAIL CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(selectedItem.name, fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(selectedItem.description, style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.AccessTime, contentDescription = "Duration", tint = NavyBluePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Estimated Duration", fontSize = 10.sp, color = TextSecondaryLight)
                                Text(selectedItem.duration, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Billing (GST Inc.)", fontSize = 10.sp, color = TextSecondaryLight)
                            Text(finalPrice.toRupeeString(), fontWeight = FontWeight.Bold, fontSize = 20.sp, color = GoldAccent)
                        }
                    }
                }
            }
        }

        // 3. INCLUDED & EXCLUDED CHECKS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Included Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("INCLUDED", fontWeight = FontWeight.Bold, color = GreenSuccess, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    val includes = selectedItem.features.ifEmpty {
                        listOf("100% background-verified specialist", "All custom tools & equipment check", "Free cleanup & garbage bagging")
                    }
                    includes.forEach { inc ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(inc, fontSize = 11.sp, color = NavyBlueDark)
                        }
                    }
                }

                // Excluded Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Cancel, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXCLUDED", fontWeight = FontWeight.Bold, color = AlertRed, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    excludedItems.forEach { exc ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = AlertRed, modifier = Modifier.size(14.dp).padding(top = 2.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(exc, fontSize = 11.sp, color = NavyBlueDark)
                        }
                    }
                }
            }
        }

        // 4. CERTIFIED TECHNICIAN INFORMATION
        item {
            Text("Certified Professional Nearby", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            val assignedTech = ServiceData.technicians.firstOrNull() ?: Technician("Rajesh Prasad", 4.93f, 184, "Master Technician", "+919876543210")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(54.dp)) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(GoldLight, CircleShape)
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = assignedTech.name.split(" ").map { it.take(1) }.joinToString(""),
                                fontWeight = FontWeight.Bold,
                                color = NavyBluePrimary,
                                fontSize = 16.sp
                            )
                        }
                        // Online green dot indicator
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color.White, CircleShape)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(GreenSuccess, CircleShape)
                                      .align(Alignment.Center)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(assignedTech.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NavyBluePrimary)
                        Text("Verified ${category.name} Specialist", fontSize = 11.sp, color = TextSecondaryLight)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${assignedTech.rating} • ${assignedTech.completedJobs} completed jobs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                        }
                    }
                    OneCallBadge(text = "Online Now", backgroundColor = GreenSuccess.copy(0.12f), textColor = GreenSuccess)
                }
            }
        }

        // 5. WARRANTY CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = GoldLight.copy(0.25f)),
                border = BorderStroke(1.dp, GoldAccent.copy(0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.Shield, contentDescription = "Shield", tint = GoldAccent, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("30-Day Reservice Warranty", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary)
                        Text("Every booking includes free reservice diagnosis, 100% parts transparency & up to ₹10,000 comprehensive insurance protection.", fontSize = 11.sp, color = TextSecondaryLight)
                    }
                }
            }
        }

        // 6. AVAILABLE BOOKING SLOTS
        item {
            Text("Available Booking Slots", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            
            Text("Select Date", fontSize = 11.sp, color = TextSecondaryLight, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            val dates = listOf("July 20, 2026", "July 21, 2026", "July 22, 2026", "July 23, 2026")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dates) { d ->
                    val isSelected = selectedDate == d
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDate = d },
                        label = { Text(d) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyBluePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Select Time Range", fontSize = 11.sp, color = TextSecondaryLight, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            val slots = listOf("08:00 AM - 10:00 AM", "11:00 AM - 01:00 PM", "02:00 PM - 04:00 PM", "05:00 PM - 07:00 PM")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                slots.forEach { slot ->
                    val isSelected = selectedTimeSlot == slot
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTimeSlot = slot },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) NavyBluePrimary else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (isSelected) GoldAccent else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                    ) {
                        Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = slot.replace(" - ", "\nto\n"),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else NavyBluePrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // 7. CUSTOMER REVIEWS SLIDER
        item {
            Text("Customer Reviews", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(reviews) { r ->
                    Card(
                        modifier = Modifier.width(260.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(NavyBluePrimary.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(r.first.take(1), fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(r.first, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NavyBluePrimary)
                                }
                                Text(r.second, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(r.third, fontSize = 11.sp, color = NavyBlueDark, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Verified Purchase", fontSize = 8.sp, color = GreenSuccess, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 8. FAQ ACCORDION
        item {
            Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                faqs.forEach { faq ->
                    var isExpanded by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = faq.first,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = NavyBluePrimary,
                                    modifier = Modifier.weight(0.9f)
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = NavyBluePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = faq.second,
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }
                }
            }
        }

        // 9. DYNAMIC AI-POWERED PROJECT COST ESTIMATOR
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlueDark),
                border = BorderStroke(1.dp, GoldAccent.copy(0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(GoldAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, tint = NavyBlueDark, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("AI Custom Cost Estimator", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Have a complex custom repair or renovation request? Describe it to our AI engine to get instant feasibility, material analysis, and precise pricing ranges.",
                        color = Color.White.copy(0.7f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OneCallTextField(
                        value = estimatorInput,
                        onValueChange = { viewModel.setEstimatorInput(it) },
                        label = "Describe your custom repair request...",
                        placeholder = "e.g., Replacing bathroom pipes with CPVC, installing new mixer taps, descaling shower joints",
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OneCallButton(
                        text = "Get AI Cost Estimate",
                        onClick = { viewModel.runAICostEstimate() },
                        enabled = !isEstimatorLoading,
                        containerColor = GoldAccent,
                        contentColor = NavyBlueDark,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (estimatorOutput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(0.08f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            if (isEstimatorLoading) {
                                CircularProgressIndicator(color = GoldAccent, modifier = Modifier.align(Alignment.Center).size(24.dp))
                            } else {
                                Text(
                                    text = estimatorOutput,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // 10. PRIMARY BOOK NOW CALL TO ACTION
        item {
            Spacer(modifier = Modifier.height(8.dp))
            OneCallButton(
                text = "Book Professional Service",
                onClick = { 
                    onBookService(selectedItem, selectedTier, finalPrice) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("book_professional_service_button"),
                icon = Icons.Filled.CalendarMonth
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- 4. BOOKING FORM SCREEN ---

data class ServiceAddOn(
    val name: String,
    val price: Double,
    val description: String
)

data class BookingCoupon(
    val code: String,
    val value: Double,
    val isPercentage: Boolean,
    val maxDiscount: Double? = null,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    viewModel: MainViewModel,
    serviceName: String,
    tier: String,
    price: Double,
    onBookingSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(1) } // Steps 1 to 7

    // Dynamic states
    var selectedTier by remember { mutableStateOf("Premium") }
    val addOns = remember {
        mutableStateListOf(
            ServiceAddOn("Eco-Friendly Cleansers", 149.0, "Biodegradable, pet-safe, and baby-safe materials"),
            ServiceAddOn("Post-Service Sanitization", 99.0, "Full UV sanitization and virus cold fogging"),
            ServiceAddOn("90-Day Extended Warranty", 199.0, "Triples your service protection from 30 to 90 days")
        )
    }
    val selectedAddOns = remember { mutableStateListOf<ServiceAddOn>() }

    // Step 2 Address states
    var addressType by remember { mutableStateOf("Home") } // Home, Office, Other
    var flatNo by remember { mutableStateOf("") }
    var buildingName by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }
    val user by viewModel.userState.collectAsState()

    LaunchedEffect(user) {
        if (user != null && addressInput.isEmpty()) {
            addressInput = user!!.address
            flatNo = "A-402"
            buildingName = "Prestige Heights"
            landmark = "Near City Park"
        }
    }

    // Step 3 Date states
    var selectedDate by remember { mutableStateOf("July 20, 2026") }

    // Step 4 Time states
    var selectedTimeSlot by remember { mutableStateOf("11:00 AM - 01:00 PM") }

    // Step 5 Upload images states
    val uploadedImages = remember { mutableStateListOf<String>() }
    var techNotes by remember { mutableStateOf("") }

    // Step 6 Coupon states
    var couponInput by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<BookingCoupon?>(null) }
    var couponError by remember { mutableStateOf("") }
    var couponSuccessMessage by remember { mutableStateOf("") }

    // Step 7 Payment states
    var paymentMethod by remember { mutableStateOf("UPI") } // UPI, Card, PayLater
    var selectedUpiProvider by remember { mutableStateOf("gpay") } // gpay, phonepe, paytm
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var isPaymentLoading by remember { mutableStateOf(false) }

    // Calculations
    val baseWithTier = price * when (selectedTier) {
        "Premium" -> 1.2
        "Deep" -> 1.4
        else -> 1.0
    }
    val addOnsTotal = selectedAddOns.sumOf { it.price }
    val subtotal = baseWithTier + addOnsTotal

    val discount = when (val cp = appliedCoupon) {
        null -> 0.0
        else -> {
            if (cp.isPercentage) {
                (subtotal * cp.value / 100.0).coerceAtMost(cp.maxDiscount ?: Double.MAX_VALUE)
            } else {
                cp.value.coerceAtMost(subtotal)
            }
        }
    }

    // Apply slot modification price (Morning -20, evening +49, others 0)
    val slotAdjustment = when (selectedTimeSlot) {
        "08:00 AM - 10:00 AM" -> -20.0
        "05:00 PM - 07:00 PM" -> 49.0
        else -> 0.0
    }

    val taxRate = 0.18
    val gstAmount = ((subtotal - discount) + slotAdjustment).coerceAtLeast(0.0) * taxRate
    val grandTotal = ((subtotal - discount) + slotAdjustment).coerceAtLeast(0.0) + gstAmount

    // Coupons available
    val availableCoupons = listOf(
        BookingCoupon("ONEFIRST", 150.0, false, null, "Flat ₹150 off on your first booking"),
        BookingCoupon("MONSOON30", 30.0, true, 250.0, "30% off up to ₹250 on all monsoon services"),
        BookingCoupon("SAVEMORE", 15.0, true, 500.0, "Save 15% flat on professional packages")
    )

    // Form Navigation Click
    val onNextClick: () -> Unit = {
        var isValid = true
        when (currentStep) {
            2 -> {
                if (flatNo.isBlank() || buildingName.isBlank() || addressInput.isBlank()) {
                    isValid = false
                    Toast.makeText(context, "Please fill in flat/house number and building details", Toast.LENGTH_SHORT).show()
                }
            }
            7 -> {
                if (paymentMethod == "Card") {
                    if (cardNumber.length < 16 || cardExpiry.isBlank() || cardCvv.length < 3 || cardHolder.isBlank()) {
                        isValid = false
                        Toast.makeText(context, "Please enter valid card details to proceed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (isValid) {
            if (currentStep < 7) {
                currentStep++
            } else {
                // Execute Secure Booking creation with Simulated Bank Payment Verification
                isPaymentLoading = true
                coroutineScope.launch {
                    kotlinx.coroutines.delay(1800) // Beautiful realistic latency
                    isPaymentLoading = false
                    
                    val finalAddressText = "$flatNo, $buildingName, $landmark. Route: $addressInput"
                    viewModel.createServiceBooking(
                        categoryName = "Home Solution",
                        serviceItemName = serviceName,
                        selectedTier = selectedTier,
                        price = grandTotal,
                        date = selectedDate,
                        timeSlot = selectedTimeSlot,
                        customAddress = finalAddressText
                    )
                    Toast.makeText(context, "Payment verified! Booking confirmed.", Toast.LENGTH_LONG).show()
                    onBookingSuccess()
                }
            }
        }
    }

    val onBackClick: () -> Unit = {
        if (currentStep > 1) {
            currentStep--
        } else {
            onNavigateBack()
        }
    }

    // Step titles helper
    fun getStepTitle(step: Int): String {
        return when (step) {
            1 -> "Select Service Details"
            2 -> "Confirm Service Address"
            3 -> "Select Appointment Date"
            4 -> "Choose Preferred Time"
            5 -> "Upload Problem Images"
            6 -> "Apply Coupons & Offers"
            7 -> "Secure Payment & Booking"
            else -> ""
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // STEP PROGRESS HEADER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NavyBluePrimary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Secure Checkout",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = NavyBluePrimary
                        )
                        Text(
                            text = serviceName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom Linear Progress
                LinearProgressIndicator(
                    progress = { currentStep / 7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = GoldAccent,
                    trackColor = NavyBluePrimary.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step $currentStep of 7: ${getStepTitle(currentStep)}",
                        fontWeight = FontWeight.Bold,
                        color = NavyBluePrimary,
                        fontSize = 13.sp
                    )
                    OneCallBadge(
                        text = "${(currentStep * 100 / 7)}% Completed",
                        backgroundColor = GoldAccent.copy(0.12f),
                        textColor = NavyBlueDark
                    )
                }
            }

            // PRIMARY SCROLLABLE FORM VIEW
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // STEP RENDERER
                item {
                    when (currentStep) {
                        1 -> {
                            // --- STEP 1: SERVICE DETAILS & QUALITY TIER & ADD-ONS ---
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f)),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Configuring Solution for", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextSecondaryLight)
                                        Text(serviceName, fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Selected Tier: $selectedTier (Base: ${price.toRupeeString()})", fontSize = 12.sp, color = NavyBluePrimary)
                                    }
                                }

                                Text("Confirm Quality Tier", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val tiers = listOf("Standard", "Premium", "Deep")
                                    tiers.forEach { t ->
                                        val isSelected = selectedTier == t
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { selectedTier = t },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) NavyBluePrimary else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(1.dp, if (isSelected) GoldAccent else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(t, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else NavyBluePrimary, fontSize = 13.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                val desc = when (t) {
                                                    "Premium" -> "+20% Price"
                                                    "Deep" -> "+40% Price"
                                                    else -> "Base Price"
                                                }
                                                Text(desc, fontSize = 9.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondaryLight)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Optional Premium Add-ons", fontWeight = FontWeight.Bold, color = NavyBluePrimary)

                                addOns.forEach { addOn ->
                                    val isChecked = selectedAddOns.contains(addOn)
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if (isChecked) selectedAddOns.remove(addOn) else selectedAddOns.add(addOn)
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isChecked) NavyBluePrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(1.dp, if (isChecked) NavyBluePrimary else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checked ->
                                                    if (checked == true) selectedAddOns.add(addOn) else selectedAddOns.remove(addOn)
                                                },
                                                colors = CheckboxDefaults.colors(checkedColor = NavyBluePrimary)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(addOn.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary)
                                                Text(addOn.description, fontSize = 10.sp, color = TextSecondaryLight)
                                            }
                                            Text(
                                                text = "+${addOn.price.toRupeeString()}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = GoldAccent
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // --- STEP 2: ADDRESS SELECTION ---
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Where should we send the specialist?", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val types = listOf(
                                        Triple("Home", Icons.Filled.Home, "Home"),
                                        Triple("Office", Icons.Filled.Work, "Office"),
                                        Triple("Other", Icons.Filled.Apartment, "Other")
                                    )
                                    types.forEach { (label, icon, value) ->
                                        val isSelected = addressType == value
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { addressType = value },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) NavyBluePrimary else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(1.dp, if (isSelected) GoldAccent else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else NavyBluePrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = label,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = if (isSelected) Color.White else NavyBluePrimary
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                OneCallTextField(
                                    value = flatNo,
                                    onValueChange = { flatNo = it },
                                    label = "Flat, House No, Building Floor *",
                                    leadingIcon = Icons.Filled.Home
                                )

                                OneCallTextField(
                                    value = buildingName,
                                    onValueChange = { buildingName = it },
                                    label = "Apartment or Society Name, Street *",
                                    leadingIcon = Icons.Filled.Apartment
                                )

                                OneCallTextField(
                                    value = landmark,
                                    onValueChange = { landmark = it },
                                    label = "Landmark, Locality (Optional)",
                                    leadingIcon = Icons.Filled.LocationOn
                                )

                                OneCallTextField(
                                    value = addressInput,
                                    onValueChange = { addressInput = it },
                                    label = "Verified Area City Location *",
                                    leadingIcon = Icons.Filled.PinDrop
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Beautiful Live map simulation box
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Filled.LocationOn,
                                                contentDescription = null,
                                                tint = AlertRed,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Map Pin Configured",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = NavyBlueDark
                                            )
                                            Text(
                                                text = "GPS: 12.9716° N, 77.5946° E (Matched)",
                                                fontSize = 9.sp,
                                                color = TextSecondaryLight
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // --- STEP 3: DATE SELECTION ---
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Choose Appointment Date", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                Text("Our technicians are highly punctual. Select a convenient date.", fontSize = 11.sp, color = TextSecondaryLight)

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = NavyBlueDark)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            "JULY 2026",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        
                                        // Calendar grid simulator
                                        val calendarDays = listOf(
                                            Pair("Mon", "20"),
                                            Pair("Tue", "21"),
                                            Pair("Wed", "22"),
                                            Pair("Thu", "23"),
                                            Pair("Fri", "24"),
                                            Pair("Sat", "25"),
                                            Pair("Sun", "26")
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            calendarDays.forEach { (day, num) ->
                                                val formattedDate = "July $num, 2026"
                                                val isSelected = selectedDate == formattedDate
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(if (isSelected) GoldAccent else Color.Transparent)
                                                        .clickable { selectedDate = formattedDate }
                                                        .padding(vertical = 8.dp, horizontal = 10.dp)
                                                ) {
                                                    Text(
                                                        text = day,
                                                        color = if (isSelected) NavyBlueDark else Color.White.copy(alpha = 0.7f),
                                                        fontSize = 10.sp
                                                    )
                                                    Text(
                                                        text = num,
                                                        color = if (isSelected) NavyBlueDark else Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = GoldLight.copy(0.15f)),
                                    border = BorderStroke(1.dp, GoldAccent.copy(0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Selected Date: $selectedDate",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = NavyBluePrimary
                                        )
                                    }
                                }
                            }
                        }

                        4 -> {
                            // --- STEP 4: TIME SLOT SELECTION ---
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Choose Preferred Time Slot", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                Text("Each slot has optimized technician routing to assure timely service.", fontSize = 11.sp, color = TextSecondaryLight)

                                val times = listOf(
                                    Triple("08:00 AM - 10:00 AM", "Morning Saver (Save ₹20)", -20.0),
                                    Triple("11:00 AM - 01:00 PM", "Popular Slot (Standard)", 0.0),
                                    Triple("02:00 PM - 04:00 PM", "Afternoon (Standard)", 0.0),
                                    Triple("05:00 PM - 07:00 PM", "Evening Rush (Add ₹49)", 49.0)
                                )

                                times.forEach { (slot, description, surcharge) ->
                                    val isSelected = selectedTimeSlot == slot
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedTimeSlot = slot },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) NavyBluePrimary else MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(1.dp, if (isSelected) GoldAccent else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.AccessTime,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else NavyBluePrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = slot,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = if (isSelected) Color.White else NavyBluePrimary
                                                    )
                                                    Text(
                                                        text = description,
                                                        fontSize = 9.sp,
                                                        color = if (isSelected) Color.White.copy(0.8f) else TextSecondaryLight
                                                    )
                                                }
                                            }

                                            if (surcharge != 0.0) {
                                                OneCallBadge(
                                                    text = if (surcharge > 0) "+₹${surcharge.toInt()}" else "-₹${(-surcharge).toInt()}",
                                                    backgroundColor = if (isSelected) GoldAccent else NavyBluePrimary.copy(alpha = 0.08f),
                                                    textColor = if (isSelected) NavyBlueDark else NavyBluePrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        5 -> {
                            // --- STEP 5: UPLOAD IMAGES ---
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Upload Solution Space Images (Optional)", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                Text("Show us what needs to be fixed. Our experts will come with pre-matched replacement tools and components.", fontSize = 11.sp, color = TextSecondaryLight)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val simulatedNames = listOf("faulty_wiring_1.jpg", "geyser_fitting.png", "ceiling_crack_close.jpg")
                                            val newPic = simulatedNames.random()
                                            if (!uploadedImages.contains(newPic)) {
                                                uploadedImages.add(newPic)
                                                Toast.makeText(context, "Uploaded $newPic successfully", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Image already uploaded", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, NavyBluePrimary)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CloudUpload,
                                            contentDescription = null,
                                            tint = NavyBluePrimary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Tap to Capture or Upload Photo",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = NavyBluePrimary
                                        )
                                        Text(
                                            "Supports JPG, PNG up to 10MB",
                                            fontSize = 10.sp,
                                            color = TextSecondaryLight
                                        )
                                    }
                                }

                                if (uploadedImages.isNotEmpty()) {
                                    Text("Uploaded Attachments (${uploadedImages.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NavyBluePrimary)
                                    
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(uploadedImages) { img ->
                                            Card(
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f)),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(imageVector = Icons.Filled.Image, contentDescription = null, tint = NavyBluePrimary, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(img, fontSize = 11.sp, maxLines = 1, color = NavyBluePrimary)
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Icon(
                                                        imageVector = Icons.Filled.Delete,
                                                        contentDescription = "Delete",
                                                        tint = AlertRed,
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .clickable { uploadedImages.remove(img) }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                
                                OneCallTextField(
                                    value = techNotes,
                                    onValueChange = { techNotes = it },
                                    label = "Instructions for Specialist (e.g. Call before arrival)",
                                    leadingIcon = Icons.Filled.RateReview
                                )
                            }
                        }

                        6 -> {
                            // --- STEP 6: COUPON ---
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Apply Promo Coupon Code", fontWeight = FontWeight.Bold, color = NavyBluePrimary)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { couponInput = it; couponError = ""; couponSuccessMessage = "" },
                                        label = { Text("Enter Promo Code") },
                                        modifier = Modifier.weight(1f),
                                        leadingIcon = { Icon(imageVector = Icons.Filled.ConfirmationNumber, contentDescription = null, tint = NavyBluePrimary) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NavyBluePrimary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val matched = availableCoupons.find { it.code.equals(couponInput.trim(), ignoreCase = true) }
                                            if (matched != null) {
                                                appliedCoupon = matched
                                                couponSuccessMessage = "Coupon ${matched.code} applied! Saved discount of ${if (matched.isPercentage) "${matched.value}%" else matched.value.toRupeeString()}"
                                                couponError = ""
                                            } else {
                                                couponError = "Invalid Promo Code. Try ONEFIRST or MONSOON30."
                                                couponSuccessMessage = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NavyBluePrimary),
                                        modifier = Modifier.height(56.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Apply", color = Color.White)
                                    }
                                }

                                if (couponError.isNotEmpty()) {
                                    Text(couponError, color = AlertRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                if (couponSuccessMessage.isNotEmpty()) {
                                    Text(couponSuccessMessage, color = GreenSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Available Coupons for You", fontWeight = FontWeight.Bold, color = NavyBluePrimary)

                                availableCoupons.forEach { cp ->
                                    val isCurrent = appliedCoupon?.code == cp.code
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                appliedCoupon = cp
                                                couponInput = cp.code
                                                couponSuccessMessage = "Applied ${cp.code} successfully!"
                                                couponError = ""
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCurrent) GreenSuccess.copy(0.05f) else MaterialTheme.colorScheme.surface
                                        ),
                                        border = BorderStroke(1.dp, if (isCurrent) GreenSuccess else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Filled.Percent, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(cp.code, fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 14.sp)
                                                    if (isCurrent) {
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        OneCallBadge(text = "APPLIED", backgroundColor = GreenSuccess.copy(0.12f), textColor = GreenSuccess)
                                                    }
                                                }
                                                Text(cp.description, fontSize = 10.sp, color = TextSecondaryLight)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        7 -> {
                            // --- STEP 7: PAYMENT ---
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Select Secure Payment Gateway", fontWeight = FontWeight.Bold, color = NavyBluePrimary)

                                // Payment Choice Tabs
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val paymentTabs = listOf(
                                        Triple("UPI", Icons.Filled.QrCode, "UPI"),
                                        Triple("Card", Icons.Filled.CreditCard, "Card"),
                                        Triple("Cash", Icons.Filled.AccountBalance, "PayLater")
                                    )

                                    paymentTabs.forEach { (label, icon, method) ->
                                        val isSelected = paymentMethod == method
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { paymentMethod = method },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) NavyBluePrimary else MaterialTheme.colorScheme.surface
                                            ),
                                            border = BorderStroke(1.dp, if (isSelected) GoldAccent else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else NavyBluePrimary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = label,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = if (isSelected) Color.White else NavyBluePrimary
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                when (paymentMethod) {
                                    "UPI" -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("Select UPI App Provider", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                                            
                                            val providers = listOf(
                                                Pair("Google Pay", "gpay"),
                                                Pair("PhonePe", "phonepe"),
                                                Pair("Paytm UPI", "paytm")
                                            )
                                            providers.forEach { (name, id) ->
                                                val isSelected = selectedUpiProvider == id
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable { selectedUpiProvider = id },
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = if (isSelected) NavyBluePrimary.copy(0.05f) else MaterialTheme.colorScheme.surface
                                                    ),
                                                    border = BorderStroke(1.dp, if (isSelected) NavyBluePrimary else MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(12.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        RadioButton(
                                                            selected = isSelected,
                                                            onClick = { selectedUpiProvider = id },
                                                            colors = RadioButtonDefaults.colors(selectedColor = NavyBluePrimary)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(name, fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 13.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    "Card" -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(130.dp),
                                                colors = CardDefaults.cardColors(containerColor = NavyBlueDark),
                                                border = BorderStroke(1.dp, GoldAccent.copy(0.5f))
                                            ) {
                                                Column(modifier = Modifier.padding(14.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(imageVector = Icons.Filled.CreditCard, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                                        Text("SECURE CARD", fontWeight = FontWeight.Bold, color = Color.White.copy(0.7f), fontSize = 10.sp)
                                                    }
                                                    Spacer(modifier = Modifier.weight(1f))
                                                    Text(
                                                        text = cardNumber.ifEmpty { "••••••••••••••••" }.chunked(4).joinToString("   "),
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 16.sp,
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                    Spacer(modifier = Modifier.weight(1f))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Column {
                                                            Text("CARD HOLDER", fontSize = 7.sp, color = Color.White.copy(0.6f))
                                                            Text(cardHolder.ifEmpty { "YOUR NAME" }.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                        }
                                                        Column(horizontalAlignment = Alignment.End) {
                                                            Text("EXPIRES", fontSize = 7.sp, color = Color.White.copy(0.6f))
                                                            Text(cardExpiry.ifEmpty { "MM/YY" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                        }
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            OutlinedTextField(
                                                value = cardNumber,
                                                onValueChange = { if (it.length <= 16 && it.all { char -> char.isDigit() }) cardNumber = it },
                                                label = { Text("Card Number (16 Digits) *") },
                                                leadingIcon = { Icon(imageVector = Icons.Filled.CreditCard, contentDescription = null, tint = NavyBluePrimary) },
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBluePrimary),
                                                shape = RoundedCornerShape(12.dp),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            OutlinedTextField(
                                                value = cardHolder,
                                                onValueChange = { cardHolder = it },
                                                label = { Text("Card Holder Name *") },
                                                leadingIcon = { Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null, tint = NavyBluePrimary) },
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBluePrimary),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedTextField(
                                                    value = cardExpiry,
                                                    onValueChange = { cardExpiry = it },
                                                    label = { Text("Expiry (MM/YY)") },
                                                    modifier = Modifier.weight(1f),
                                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBluePrimary),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                OutlinedTextField(
                                                    value = cardCvv,
                                                    onValueChange = { if (it.length <= 3 && it.all { char -> char.isDigit() }) cardCvv = it },
                                                    label = { Text("CVV") },
                                                    modifier = Modifier.weight(1f),
                                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyBluePrimary),
                                                    shape = RoundedCornerShape(12.dp),
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    visualTransformation = PasswordVisualTransformation()
                                                )
                                            }
                                        }
                                    }

                                    "PayLater" -> {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = GoldLight.copy(0.15f)),
                                            border = BorderStroke(1.dp, GoldAccent.copy(0.3f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text("Pay Cash/UPI Post-Service", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary)
                                                    Text("No advance payment required. Safely settle total directly to the technician with cash or any UPI QR scan.", fontSize = 11.sp, color = TextSecondaryLight)
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Bill Breakup Card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(0.4f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Billing Breakup Detail", fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Base Service (${selectedTier})", fontSize = 11.sp, color = TextSecondaryLight)
                                            Text(baseWithTier.toRupeeString(), fontSize = 11.sp, color = NavyBluePrimary)
                                        }
                                        if (addOnsTotal > 0) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Optional Add-ons", fontSize = 11.sp, color = TextSecondaryLight)
                                                Text("+${addOnsTotal.toRupeeString()}", fontSize = 11.sp, color = NavyBluePrimary)
                                            }
                                        }
                                        if (slotAdjustment != 0.0) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Slot Surcharge/Saver", fontSize = 11.sp, color = TextSecondaryLight)
                                                Text(
                                                    text = if (slotAdjustment > 0) "+${slotAdjustment.toRupeeString()}" else "-${(-slotAdjustment).toRupeeString()}",
                                                    fontSize = 11.sp,
                                                    color = if (slotAdjustment > 0) NavyBluePrimary else GreenSuccess
                                                )
                                            }
                                        }
                                        if (discount > 0) {
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Applied Coupon Discount", fontSize = 11.sp, color = GreenSuccess)
                                                Text("-${discount.toRupeeString()}", fontSize = 11.sp, color = GreenSuccess, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(0.4f))

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("GST/Service Cess (18%)", fontSize = 11.sp, color = TextSecondaryLight)
                                            Text(gstAmount.toRupeeString(), fontSize = 11.sp, color = NavyBluePrimary)
                                        }

                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = NavyBluePrimary)

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Grand Total Payable", fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 14.sp)
                                            Text(grandTotal.toRupeeString(), fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 18.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // BOTTOM NAVIGATION ACTION ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .weight(0.4f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBluePrimary),
                    border = BorderStroke(1.dp, NavyBluePrimary)
                ) {
                    Text("Back", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onNextClick,
                    modifier = Modifier
                        .weight(0.6f)
                        .height(50.dp)
                        .testTag("booking_flow_next_button"),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBluePrimary)
                ) {
                    Text(
                        text = if (currentStep == 7) "Pay & Confirm" else "Next Step",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Processing / loading layer
        if (isPaymentLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = GoldAccent, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Securing Gateway...",
                            fontWeight = FontWeight.Bold,
                            color = NavyBluePrimary,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Verifying token authorization with UPI bank servers. Please do not close.",
                            color = TextSecondaryLight,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// --- 5. BOOKING CONFIRMATION SCREEN ---

@Composable
fun BookingConfirmationScreen(
    viewModel: MainViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMyBookings: () -> Unit
) {
    // Custom native compose scale & fade-in animation to match "Framer Motion" checkmark behavior
    val scale = remember { androidx.compose.animation.core.Animatable(0f) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
            )
        )
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyBlueDark, NavyBluePrimary))),
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
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size((76 * scale.value).dp)
                        .background(GreenSuccess.copy(0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Success", tint = GreenSuccess, modifier = Modifier.size(52.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                androidx.compose.animation.AnimatedVisibility(
                    visible = showContent,
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically(initialOffsetY = { 30 })
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Booking Confirmed!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = NavyBluePrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Your home solution specialist is successfully scheduled. We have sent a professional and Aadhaar-cleared technician to handle your requests.",
                            fontSize = 12.sp,
                            color = TextSecondaryLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        OneCallButton(
                            text = "Track Technician Arrival",
                            onClick = onNavigateToMyBookings,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = onNavigateToHome) {
                            Text("Return to Dashboard", color = GoldAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- 6. BOOKINGS LIST SCREEN ---

@Composable
fun BookingsListScreen(
    viewModel: MainViewModel,
    onTrackBooking: (Int) -> Unit
) {
    val bookings by viewModel.bookingsState.collectAsState()
    var selectedTab by remember { mutableStateOf("Upcoming") } // Upcoming, Completed, Cancelled

    val context = LocalContext.current

    // Dialog state management
    var rescheduleBookingId by remember { mutableStateOf<Int?>(null) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var selectedRescheduleDate by remember { mutableStateOf("July 21, 2026") }
    var selectedRescheduleSlot by remember { mutableStateOf("11:00 AM - 01:00 PM") }

    var invoiceBooking by remember { mutableStateOf<BookingEntity?>(null) }
    var showInvoiceDialog by remember { mutableStateOf(false) }

    var reviewBookingId by remember { mutableStateOf<Int?>(null) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var userRating by remember { mutableStateOf(5f) }
    var reviewComment by remember { mutableStateOf("") }

    val rescheduleDates = listOf("July 21, 2026", "July 22, 2026", "July 23, 2026", "July 24, 2026")
    val rescheduleSlots = listOf("09:00 AM - 11:00 AM", "11:00 AM - 01:00 PM", "02:00 PM - 04:00 PM", "04:00 PM - 06:00 PM")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("My Service Orders", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = NavyBluePrimary)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            val tabs = listOf("Upcoming", "Completed", "Cancelled")
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = tab }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        tab,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) NavyBluePrimary else TextSecondaryLight,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth(0.8f)
                            .background(if (isSelected) NavyBluePrimary else Color.Transparent)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        val filteredBookings = bookings.filter { booking ->
            when (selectedTab) {
                "Upcoming" -> booking.status != "Completed" && booking.status != "Cancelled"
                "Completed" -> booking.status == "Completed"
                "Cancelled" -> booking.status == "Cancelled"
                else -> true
            }
        }

        if (filteredBookings.isEmpty()) {
            EmptyState(
                title = "No Bookings Found",
                subtitle = "Schedule any plumbing, cleaning, electrical, or painting job to view progress here.",
                icon = Icons.Filled.CalendarToday
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredBookings) { booking ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTrackBooking(booking.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(booking.serviceName, fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 15.sp)
                                val badgeColor = when (booking.status) {
                                    "Pending" -> YellowPending
                                    "Assigned" -> NavyBlueMedium
                                    "In Progress" -> GreenSuccess
                                    "Completed" -> GreenSuccess
                                    else -> AlertRed
                                }
                                OneCallBadge(text = booking.status, backgroundColor = badgeColor.copy(0.15f), textColor = badgeColor)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Scheduled: ${booking.date} | ${booking.timeSlot}", fontSize = 11.sp, color = TextSecondaryLight)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(GoldLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(booking.technicianName.ifEmpty { "Matching specialist..." }, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(booking.price.toRupeeString(), fontWeight = FontWeight.Bold, color = GoldAccent)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Interactive context actions based on tab
                            if (selectedTab == "Upcoming") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OneCallButton(
                                        text = "Track",
                                        onClick = { onTrackBooking(booking.id) },
                                        icon = Icons.Filled.MyLocation,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    OneCallButton(
                                        text = "Reschedule",
                                        onClick = {
                                            rescheduleBookingId = booking.id
                                            selectedRescheduleDate = booking.date
                                            selectedRescheduleSlot = booking.timeSlot
                                            showRescheduleDialog = true
                                        },
                                        containerColor = GoldLight,
                                        contentColor = NavyBluePrimary,
                                        modifier = Modifier.weight(1.5f)
                                    )
                                    IconButton(
                                        onClick = {
                                            val phoneToDial = booking.technicianPhone.ifEmpty { "+919441234567" }
                                            try {
                                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                                    data = android.net.Uri.parse("tel:$phoneToDial")
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Dialer action: Calling $phoneToDial", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(NavyBlueMedium.copy(0.1f), CircleShape)
                                    ) {
                                        Icon(imageVector = Icons.Filled.Phone, contentDescription = "Call", tint = NavyBlueMedium, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            // Cancel booking confirmation
                                            viewModel.cancelActiveBooking(booking.id)
                                            Toast.makeText(context, "Order Cancelled Successfully", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(AlertRed.copy(0.1f), CircleShape)
                                    ) {
                                        Icon(imageVector = Icons.Filled.Cancel, contentDescription = "Cancel order", tint = AlertRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                            } else if (selectedTab == "Completed") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OneCallButton(
                                        text = "View Invoice",
                                        onClick = {
                                            invoiceBooking = booking
                                            showInvoiceDialog = true
                                        },
                                        containerColor = NavyBlueMedium,
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Filled.ReceiptLong
                                    )
                                    OneCallButton(
                                        text = if (booking.technicianRating > 0f) "Reviewed (${booking.technicianRating.toInt()}★)" else "Review Expert",
                                        onClick = {
                                            reviewBookingId = booking.id
                                            userRating = if (booking.technicianRating > 0f) booking.technicianRating else 5f
                                            reviewComment = ""
                                            showReviewDialog = true
                                        },
                                        containerColor = if (booking.technicianRating > 0f) Color.LightGray else GoldAccent,
                                        contentColor = NavyBluePrimary,
                                        modifier = Modifier.weight(1.2f),
                                        icon = Icons.Filled.StarRate
                                    )
                                }
                            } else {
                                // Cancelled state
                                OneCallButton(
                                    text = "Book Service Again",
                                    onClick = {
                                        Toast.makeText(context, "Navigating to booking catalog", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // RESCHEDULE DIALOG
    if (showRescheduleDialog && rescheduleBookingId != null) {
        AlertDialog(
            onDismissRequest = { showRescheduleDialog = false },
            title = { Text("Reschedule Service Appointment", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select convenient Date:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(rescheduleDates) { d ->
                            val isSel = d == selectedRescheduleDate
                            Card(
                                modifier = Modifier
                                    .clickable { selectedRescheduleDate = d }
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) NavyBluePrimary else Color.White
                                ),
                                border = BorderStroke(1.dp, if (isSel) NavyBluePrimary else Color.LightGray)
                            ) {
                                Text(
                                    d,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = if (isSel) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Select Time Slot:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        rescheduleSlots.forEach { s ->
                            val isSel = s == selectedRescheduleSlot
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRescheduleSlot = s },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) GoldLight else Color.White
                                ),
                                border = BorderStroke(1.dp, if (isSel) GoldAccent else Color.LightGray)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSel,
                                        onClick = { selectedRescheduleSlot = s },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(s, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OneCallButton(
                    text = "Confirm Reschedule",
                    onClick = {
                        viewModel.rescheduleBooking(rescheduleBookingId!!, selectedRescheduleDate, selectedRescheduleSlot)
                        showRescheduleDialog = false
                        Toast.makeText(context, "Service Rescheduled Successfully", Toast.LENGTH_LONG).show()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showRescheduleDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // INVOICE DIALOG
    if (showInvoiceDialog && invoiceBooking != null) {
        val b = invoiceBooking!!
        val base = b.price
        val gst = base * 0.18
        val discount = if (b.tier == "Premium") base * 0.1 else 0.0
        val grandTotal = base + gst - discount

        AlertDialog(
            onDismissRequest = { showInvoiceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.ReceiptLong, contentDescription = null, tint = NavyBluePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("One Call Tax Invoice", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Invoice No: OC-${b.id}-${b.timestamp.toString().takeLast(6)}", fontSize = 11.sp, color = TextSecondaryLight)
                    Text("Issued To: Ashok Kumar", fontSize = 11.sp, color = TextSecondaryLight)
                    Text("Service Date: ${b.date}", fontSize = 11.sp, color = TextSecondaryLight)
                    Spacer(modifier = Modifier.height(16.dp))

                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(b.serviceName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(base.toRupeeString(), fontWeight = FontWeight.Bold)
                    }
                    Text("Quality Tier: ${b.tier}", fontSize = 11.sp, color = TextSecondaryLight)
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 12.sp, color = TextSecondaryLight)
                        Text(base.toRupeeString(), fontSize = 12.sp)
                    }
                    if (discount > 0.0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Membership Discount (10%)", fontSize = 12.sp, color = GreenSuccess)
                            Text("- ${discount.toRupeeString()}", fontSize = 12.sp, color = GreenSuccess)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("CGST & SGST (18%)", fontSize = 12.sp, color = TextSecondaryLight)
                        Text(gst.toRupeeString(), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Grand Total Paid", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NavyBluePrimary)
                        Text(grandTotal.toRupeeString(), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GoldAccent)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Payment Status: Successful via UPI Wallet Auto-Pay", fontSize = 10.sp, color = GreenSuccess, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OneCallButton(
                        text = "Download PDF",
                        onClick = {
                            Toast.makeText(context, "PDF saved to downloads directory!", Toast.LENGTH_SHORT).show()
                            showInvoiceDialog = false
                        },
                        containerColor = GoldAccent,
                        contentColor = NavyBlueDark
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showInvoiceDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            }
        )
    }

    // REVIEW DIALOG
    if (showReviewDialog && reviewBookingId != null) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Rate Service Specialist", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("How was your experience with the specialist?", fontSize = 12.sp, color = TextSecondaryLight)
                    Spacer(modifier = Modifier.height(16.dp))

                    RatingBar(
                        rating = userRating,
                        interactive = true,
                        starSize = 36.dp,
                        onRatingChanged = { userRating = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OneCallTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = "Review comments (optional)",
                        placeholder = "Tell us what you liked (punctual, extremely professional, expert fixing, etc.)",
                        singleLine = false
                    )
                }
            },
            confirmButton = {
                OneCallButton(
                    text = "Submit Review",
                    onClick = {
                        viewModel.submitBookingReview(reviewBookingId!!, userRating)
                        showReviewDialog = false
                        Toast.makeText(context, "Thank you! Your feedback has been registered.", Toast.LENGTH_LONG).show()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Maybe Later", color = Color.Gray)
                }
            }
        )
    }
}

// --- 7. BOOKING TRACKING SCREEN ---

@Composable
fun BookingTrackingScreen(
    viewModel: MainViewModel,
    bookingId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToChat: (Int) -> Unit
) {
    val bookings by viewModel.bookingsState.collectAsState()
    val booking = bookings.find { it.id == bookingId }
    var otpInput by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf(false) }

    if (booking == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onNavigateBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Technician Live Tracking", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = NavyBluePrimary)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Live ETA Indicator
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBluePrimary)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ESTIMATED ARRIVAL TIME", color = Color.White.copy(0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (booking.status == "Completed") "Service Completed" else "12 Mins",
                        color = GoldAccent,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Priority SLA Dispatch Route Active", color = Color.White.copy(0.9f), fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Real-Time Firestore Sync Status & Simulation Panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.4f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CloudSync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Firestore Real-Time Stream",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFF4CAF50).copy(0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Connected",
                                color = Color(0xFF2E7D32),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Track real-time status changes below. Change status here to simulate a Firestore database update:",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val statuses = listOf("Pending", "Assigned", "In Progress", "Completed")
                        statuses.forEach { status ->
                            val isSelected = booking.status == status
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    // Update the status on Firestore!
                                    viewModel.updateBookingStatusInFirestore(bookingId, status)
                                },
                                label = { Text(status, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Visual Status Timeline
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Service Progress (Firestore Sync)", fontWeight = FontWeight.Bold, color = NavyBluePrimary, modifier = Modifier.padding(bottom = 16.dp))
                    
                    val steps = listOf(
                        Triple("Pending", "Service Request Received", Icons.Filled.Description),
                        Triple("Assigned", "Technician Dispatched", Icons.Filled.DirectionsRun),
                        Triple("In Progress", "Work Underway", Icons.Filled.Build),
                        Triple("Completed", "Service Completed", Icons.Filled.CheckCircle)
                    )
                    
                    val currentStatusIndex = when(booking.status) {
                        "Pending" -> 0
                        "Assigned" -> 1
                        "In Progress" -> 2
                        "Completed" -> 3
                        else -> 0
                    }
                    
                    steps.forEachIndexed { index, (statusTitle, statusDesc, icon) ->
                        val isCompleted = index <= currentStatusIndex
                        val isCurrent = index == currentStatusIndex
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Column for the indicator and connecting line
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = if (isCompleted) {
                                                if (isCurrent) GoldAccent else NavyBluePrimary
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            },
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isCompleted) {
                                            if (isCurrent) NavyBlueDark else Color.White
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                                        },
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                
                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(36.dp)
                                            .background(
                                                color = if (index < currentStatusIndex) {
                                                    NavyBluePrimary
                                                } else {
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                }
                                            )
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            // Status text details
                            Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)) {
                                Text(
                                    text = statusTitle,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) NavyBluePrimary else if (isCompleted) NavyBlueMedium else TextSecondaryLight,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = statusDesc,
                                    fontSize = 11.sp,
                                    color = if (isCurrent) TextSecondaryLight else TextSecondaryLight.copy(0.7f)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Technician Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(GoldLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(booking.technicianName, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                Text("One Call Certified Specialist", fontSize = 11.sp, color = TextSecondaryLight)
                            }
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${booking.technicianRating}", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OneCallButton(
                            text = "Call Specialist",
                            onClick = { /* trigger intent dialer */ },
                            icon = Icons.Filled.Phone,
                            containerColor = NavyBlueMedium,
                            modifier = Modifier.weight(1f)
                        )
                        OneCallButton(
                            text = "Chat In-App",
                            onClick = { onNavigateToChat(booking.id) },
                            icon = Icons.Filled.Chat,
                            containerColor = GoldAccent,
                            contentColor = NavyBlueDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // OTP Service Verification (Security Section 3)
        if (booking.status != "Completed" && booking.status != "Cancelled") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GoldLight),
                    border = BorderStroke(1.dp, GoldAccent.copy(0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Security verification OTP", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Share this secret code with your specialist ONLY upon secure completion of the job to release standard warranties.",
                            fontSize = 11.sp,
                            color = TextSecondaryLight,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = booking.otp,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyBluePrimary,
                            letterSpacing = 6.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Technician Complete Job Form (Mocking)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Verify Job Completion", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        OneCallTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it },
                            label = "Enter Verification OTP",
                            placeholder = "Enter the 4-digit code shown above to verify",
                            testTag = "otp_completion_input"
                        )
                        if (otpError) {
                            Text("Invalid OTP code. Please enter the correct verification code.", color = AlertRed, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OneCallButton(
                            text = "Verify & Close Order",
                            onClick = {
                                val success = viewModel.completeBookingWithOTP(bookingId, otpInput)
                                if (success) {
                                    otpError = false
                                    onNavigateBack()
                                } else {
                                    otpError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "verify_close_btn"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- 8. CHAT SCREEN (AI Support + Specialist Threads) ---

@Composable
fun ChatScreen(
    viewModel: MainViewModel,
    bookingId: Int = 0 // 0 means AI support chat, otherwise specialist chat
) {
    val supportChats by viewModel.supportChats.collectAsState()
    val techChatsMap by viewModel.technicianChatsMap.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val bookings by viewModel.bookingsState.collectAsState()

    val currentBooking = bookings.find { it.id == bookingId }
    val isSupport = bookingId == 0

    val chatMessages = if (isSupport) {
        supportChats
    } else {
        techChatsMap[bookingId] ?: emptyList()
    }

    var messageInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyBluePrimary)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSupport) Icons.Filled.AutoAwesome else Icons.Filled.Person,
                        contentDescription = null,
                        tint = NavyBlueDark
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isSupport) "One Call AI Support" else currentBooking?.technicianName ?: "Technician Thread",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = if (isSupport) "Active and responsive 24/7" else "Assigned Service Specialist",
                        fontSize = 11.sp,
                        color = Color.White.copy(0.8f)
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatMessages) { chat ->
                val isUser = chat.sender == "User"
                val alignment = if (isUser) Alignment.End else Alignment.Start
                val bubbleColor = if (isUser) NavyBluePrimary else MaterialTheme.colorScheme.surfaceVariant
                val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = alignment) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 0.dp,
                            bottomEnd = if (isUser) 0.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(containerColor = bubbleColor),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(chat.message, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(chat.sender, fontSize = 9.sp, color = TextSecondaryLight)
                }
            }

            if (isChatLoading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Support is writing a response...", fontSize = 11.sp, color = TextSecondaryLight)
                    }
                }
            }
        }

        // Input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Ask something...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_message_input"),
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        if (isSupport) {
                            viewModel.sendSupportChatMessage(messageInput)
                        } else {
                            viewModel.sendTechnicianChatMessage(bookingId, messageInput)
                        }
                        messageInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(NavyBluePrimary, CircleShape)
                    .testTag("chat_send_button")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// --- 9. EMERGENCY SOS SCREEN (24/7 Priority Emergency) ---

@Composable
fun EmergencySOSScreen(
    viewModel: MainViewModel,
    onTrackEmergency: (Int) -> Unit
) {
    val isAlarmActive by viewModel.isSosAlarmActive.collectAsState()
    val activeSosBooking by viewModel.activeSosBooking.collectAsState()
    var address by remember { mutableStateOf("") }
    val user by viewModel.userState.collectAsState()

    LaunchedEffect(user) {
        if (user != null) {
            address = user!!.address
        }
    }

    LaunchedEffect(activeSosBooking) {
        if (activeSosBooking != null) {
            // Automatically navigate to tracker if alarm active
            onTrackEmergency(activeSosBooking!!.id)
        }
    }

    // Dynamic Pulsing Animation for SOS Button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isAlarmActive) 1.25f else 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "24/7 EMERGENCY SOS DISPATCH",
            fontWeight = FontWeight.Bold,
            color = AlertRed,
            fontSize = 18.sp,
            letterSpacing = 1.sp
        )
        Text(
            "Burst Pipe, Gas Leaks, Blackouts, Locked Doors",
            fontSize = 11.sp,
            color = TextSecondaryLight,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Large Pulsing SOS Button
        Box(
            modifier = Modifier
                .size(240.dp)
                .clickable {
                    if (!isAlarmActive) {
                        viewModel.triggerEmergencySOS(address)
                    } else {
                        viewModel.stopSosAlarm()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Ripple background
            Surface(
                modifier = Modifier
                    .size((180 * pulseScale).dp),
                shape = CircleShape,
                color = if (isAlarmActive) AlertRed.copy(0.15f) else GoldAccent.copy(0.1f)
            ) {}
            Surface(
                modifier = Modifier
                    .size((140 * pulseScale).dp),
                shape = CircleShape,
                color = if (isAlarmActive) AlertRed.copy(0.3f) else GoldAccent.copy(0.2f)
            ) {}
            
            // Core button
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = if (isAlarmActive) AlertRed else GoldAccent,
                shadowElevation = ClickableDefaults.elevation()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isAlarmActive) Icons.Filled.Warning else Icons.Filled.PowerSettingsNew,
                        contentDescription = "Trigger SOS",
                        tint = if (isAlarmActive) Color.White else NavyBlueDark,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAlarmActive) "DISPATCHING..." else "PRESS FOR SOS",
                        fontWeight = FontWeight.Bold,
                        color = if (isAlarmActive) Color.White else NavyBlueDark,
                        fontSize = 11.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        OneCallTextField(value = address, onValueChange = { address = it }, label = "SOS Dispatch Address", leadingIcon = Icons.Filled.LocationOn)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚠️ Critical Priority Standard SLA", fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 13.sp)
                BulletRow("30-Minute Guaranteed local zone arrival.")
                BulletRow("Assigned straight to top-rated master technicians.")
                BulletRow("Standard diagnostic callout fee: ₹399 only.")
            }
        }
    }
}

object ClickableDefaults {
    fun elevation(): Dp {
        return 8.dp
    }
}

@Composable
fun BulletRow(text: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
        Text("• ", fontWeight = FontWeight.Bold, color = GoldAccent)
        Text(text, fontSize = 11.sp, color = TextSecondaryLight)
    }
}

// --- 10. PROFILE SCREEN (Membership, AMC, Wallet) ---

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    user: UserEntity?,
    onNavigateToShop: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToTechnician: () -> Unit,
    onLogout: () -> Unit,
    onRebook: (com.example.data.models.ServiceItem) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var isProfileUnlocked by remember { mutableStateOf(user?.isBiometricEnabled != true) }
    var biometricErrorMessage by remember { mutableStateOf<String?>(null) }
    
    var animateUserInfo by remember { mutableStateOf(false) }
    LaunchedEffect(isProfileUnlocked) {
        if (isProfileUnlocked) {
            animateUserInfo = true
        }
    }

    var addFundsVal by remember { mutableStateOf("") }
    var isFundingOpen by remember { mutableStateOf(false) }

    // STATE FOR PROFILE AVATAR DIALOG
    var showAvatarDialog by remember { mutableStateOf(false) }
    var selectedAvatarIndex by remember { mutableStateOf(1) }
    val avatarIcons = listOf(
        Icons.Filled.Person to "Standard",
        Icons.Filled.WorkspacePremium to "Gold Premium",
        Icons.Filled.SupportAgent to "Support Buddy",
        Icons.Filled.Face to "Cool Resident",
        Icons.Filled.AccountCircle to "Executive Client",
        Icons.Filled.MilitaryTech to "Elite AMC"
    )

    // STATE FOR PROFILE ACCORDION/EXPANDABLE CARDS
    var isPersonalDetailsExpanded by remember { mutableStateOf(false) }
    var isAddressesExpanded by remember { mutableStateOf(false) }
    var isPaymentMethodsExpanded by remember { mutableStateOf(false) }
    var isInvoicesExpanded by remember { mutableStateOf(false) }
    var isMembershipExpanded by remember { mutableStateOf(false) }
    var isWalletExpanded by remember { mutableStateOf(false) }
    var isRewardsExpanded by remember { mutableStateOf(false) }
    var isReferExpanded by remember { mutableStateOf(false) }
    var isSettingsExpanded by remember { mutableStateOf(false) }
    var isFavoritesExpanded by remember { mutableStateOf(false) }
    var isWishlistExpanded by remember { mutableStateOf(false) }

    // EDIT PERSONAL DETAILS FORM
    var editName by remember { mutableStateOf(user?.name ?: "Ashok Kumar") }
    var editEmail by remember { mutableStateOf(user?.email ?: "ashok.kumar@example.com") }
    var editPhone by remember { mutableStateOf(user?.phone ?: "+91 94412 34567") }

    // ADDRESSES DIALOG STATE
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var newAddressInput by remember { mutableStateOf("") }
    var newAddressTag by remember { mutableStateOf("Home") }

    // PAYMENT CARD DIALOG STATE
    var showAddCardDialog by remember { mutableStateOf(false) }
    var newCardNo by remember { mutableStateOf("") }
    var newCardHolderName by remember { mutableStateOf("") }
    var newCardTypeInput by remember { mutableStateOf("Visa") }

    // INVOICE DIALOG STATE
    var selectedInvoiceBooking by remember { mutableStateOf<BookingEntity?>(null) }
    var showInvoiceDialog by remember { mutableStateOf(false) }
    var isGstInvoiceEnabled by remember { mutableStateOf(false) }
    var userGstin by remember { mutableStateOf("") }
    var userBusinessName by remember { mutableStateOf("") }

    // SETTINGS/PREFERENCES STATE
    var pushAlertsEnabled by remember { mutableStateOf(true) }
    var localDarkModeSimulated by remember { mutableStateOf(false) }

    val bookings by viewModel.bookingsState.collectAsState()
    val completedBookings = bookings.filter { it.status == "Completed" }

    val savedAddresses by viewModel.addressesState.collectAsState()
    val savedCards by viewModel.savedPaymentsState.collectAsState()

    // If biometric lock is enabled and profile is not yet unlocked, show the locked view overlay
    if (user?.isBiometricEnabled == true && !isProfileUnlocked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(NavyBluePrimary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Fingerprint,
                            contentDescription = "Profile Locked Icon",
                            tint = NavyBluePrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Profile Locked",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = NavyBluePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Biometric authentication is required to access your user profile, wallet balance, and system settings.",
                        fontSize = 13.sp,
                        color = TextSecondaryLight,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (biometricErrorMessage != null) {
                        Text(
                            biometricErrorMessage!!,
                            color = AlertRed,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    OneCallButton(
                        text = "Unlock Profile",
                        onClick = {
                            if (activity != null) {
                                BiometricHelper.authenticate(
                                    activity = activity,
                                    title = "Unlock Profile",
                                    subtitle = "Verify your identity",
                                    description = "Authenticate using biometrics to view your profile.",
                                    onSuccess = {
                                        isProfileUnlocked = true
                                        biometricErrorMessage = null
                                    },
                                    onError = { error ->
                                        biometricErrorMessage = error
                                    }
                                )
                            } else {
                                isProfileUnlocked = true
                            }
                        },
                        containerColor = GoldAccent,
                        contentColor = NavyBlueDark,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    TextButton(onClick = onLogout) {
                        Text("Log Out", color = AlertRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // User Meta Card (With clickable Avatar Dialog trigger)
            item {
                androidx.compose.animation.AnimatedVisibility(
                    visible = animateUserInfo,
                    enter = androidx.compose.animation.fadeIn(
                        animationSpec = androidx.compose.animation.core.tween(durationMillis = 600)
                    ) + androidx.compose.animation.slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = androidx.compose.animation.core.tween(durationMillis = 600)
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = if (localDarkModeSimulated) Color.Black else NavyBluePrimary)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(GoldAccent)
                                    .clickable { showAvatarDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = avatarIcons[selectedAvatarIndex].first,
                                    contentDescription = "User avatar",
                                    tint = NavyBlueDark,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user?.name ?: "User Name", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                                Text(user?.email ?: "user@example.com", fontSize = 12.sp, color = Color.White.copy(0.8f))
                                Text(user?.phone ?: "", fontSize = 12.sp, color = Color.White.copy(0.8f))
                            }
                            IconButton(onClick = { showAvatarDialog = true }) {
                                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Avatar", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Wallet Balance & Rewards Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ONE CALL WALLET BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                            Text((user?.walletBalance ?: 0.0).toRupeeString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            Text("${user?.rewardsPoints ?: 0} loyalty cashback points earned", fontSize = 11.sp, color = GreenSuccess)
                        }
                        OneCallButton(
                            text = "Add Funds",
                            onClick = { isFundingOpen = true },
                            containerColor = GoldAccent,
                            contentColor = NavyBlueDark
                        )
                    }
                }
            }

            // Add funds section (Inline load)
            if (isFundingOpen) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldLight),
                        border = BorderStroke(1.dp, GoldAccent.copy(0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Add Funds to Wallet", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            OneCallTextField(
                                value = addFundsVal,
                                onValueChange = { addFundsVal = it },
                                label = "Amount in INR (₹)",
                                testTag = "add_funds_input"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OneCallButton(
                                    text = "Cancel",
                                    onClick = { isFundingOpen = false },
                                    containerColor = Color.LightGray,
                                    contentColor = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                OneCallButton(
                                    text = "Load Wallet",
                                    onClick = {
                                        val amt = addFundsVal.toDoubleOrNull()
                                        if (amt != null) {
                                            viewModel.addFundsToWallet(amt)
                                            isFundingOpen = false
                                            addFundsVal = ""
                                            Toast.makeText(context, "Loaded ₹$amt successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    testTag = "add_funds_submit"
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 1: PERSONAL DETAILS (EDITABLE PROFILE)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isPersonalDetailsExpanded = !isPersonalDetailsExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.AccountBox, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Personal Details", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isPersonalDetailsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isPersonalDetailsExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OneCallTextField(
                                value = editName,
                                onValueChange = { editName = it },
                                label = "Full Name",
                                leadingIcon = Icons.Filled.Person
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OneCallTextField(
                                value = editEmail,
                                onValueChange = { editEmail = it },
                                label = "Email Address",
                                leadingIcon = Icons.Filled.Email
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OneCallTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = "Phone Number",
                                leadingIcon = Icons.Filled.Phone
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OneCallButton(
                                text = "Save Profile Changes",
                                onClick = {
                                    viewModel.updateUserProfileDetails(editName, editEmail, editPhone)
                                    Toast.makeText(context, "Profile details saved successfully!", Toast.LENGTH_SHORT).show()
                                    isPersonalDetailsExpanded = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // SECTION 2: SAVED ADDRESSES MANAGER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAddressesExpanded = !isAddressesExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Home, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Manage Addresses (${savedAddresses.size})", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isAddressesExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isAddressesExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            savedAddresses.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.tag, fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 12.sp)
                                        Text(item.addressLine, fontSize = 11.sp, color = TextSecondaryLight)
                                    }
                                    IconButton(onClick = {
                                        viewModel.removeAddress(item.id)
                                        Toast.makeText(context, "Address deleted!", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = AlertRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Divider()
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OneCallButton(
                                text = "Add New Address",
                                onClick = { showAddAddressDialog = true },
                                icon = Icons.Filled.Add,
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = GoldLight,
                                contentColor = NavyBluePrimary
                            )
                        }
                    }
                }
            }

            // SECTION 3: SAVED PAYMENT METHODS MANAGER
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isPaymentMethodsExpanded = !isPaymentMethodsExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.CreditCard, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Payment Methods", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isPaymentMethodsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isPaymentMethodsExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            savedCards.forEach { c ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (c.type == "UPI") Icons.Filled.AccountBalanceWallet else Icons.Filled.CreditCard,
                                            contentDescription = null,
                                            tint = NavyBlueMedium,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(c.cardOrUpi, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Type: ${c.type}", fontSize = 10.sp, color = TextSecondaryLight)
                                        }
                                    }
                                    IconButton(onClick = {
                                        viewModel.removeSavedPayment(c.id)
                                        Toast.makeText(context, "Payment Method Removed!", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = AlertRed, modifier = Modifier.size(18.dp))
                                    }
                                }
                                Divider()
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OneCallButton(
                                text = "Link Card / UPI",
                                onClick = { showAddCardDialog = true },
                                icon = Icons.Filled.Add,
                                modifier = Modifier.fillMaxWidth(),
                                containerColor = GoldLight,
                                contentColor = NavyBluePrimary
                            )
                        }
                    }
                }
            }

            // SECTION: FAVORITE TECHNICIANS
            item {
                val favTechs by viewModel.favoriteTechniciansState.collectAsState()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isFavoritesExpanded = !isFavoritesExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Favorite Technicians (${favTechs.size})", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isFavoritesExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isFavoritesExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            if (favTechs.isEmpty()) {
                                Text("No favorite technicians marked yet. You can bookmark technicians during checkout or tracking.", fontSize = 11.sp, color = TextSecondaryLight)
                            } else {
                                favTechs.forEach { tech ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(NavyBluePrimary.copy(alpha = 0.1f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = NavyBluePrimary, modifier = Modifier.size(20.dp))
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(tech.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Text("${tech.category} • ★ ${tech.rating}", fontSize = 10.sp, color = TextSecondaryLight)
                                            }
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = {
                                                try {
                                                    val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                                        data = android.net.Uri.parse("tel:${tech.phone}")
                                                    }
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, "Calling ${tech.name} (${tech.phone})", Toast.LENGTH_SHORT).show()
                                                }
                                            }) {
                                                Icon(imageVector = Icons.Filled.Phone, contentDescription = "Call", tint = GreenSuccess, modifier = Modifier.size(18.dp))
                                            }
                                            IconButton(onClick = {
                                                viewModel.removeFavoriteTechnician(tech.id)
                                                Toast.makeText(context, "${tech.name} removed from favorites", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = AlertRed, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }

            // SECTION: SERVICE WISHLIST
            item {
                val wishlistItems by viewModel.wishlistState.collectAsState()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isWishlistExpanded = !isWishlistExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Favorite, contentDescription = null, tint = AlertRed)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Service Wishlist (${wishlistItems.size})", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isWishlistExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isWishlistExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            if (wishlistItems.isEmpty()) {
                                Text("Your service wishlist is currently empty.", fontSize = 11.sp, color = TextSecondaryLight)
                            } else {
                                wishlistItems.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(item.serviceName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Category: ${item.categoryId.replaceFirstChar { it.uppercase() }} • Starting at ₹${item.price}", fontSize = 10.sp, color = TextSecondaryLight)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            OneCallButton(
                                                text = "Book Now",
                                                onClick = {
                                                    val matchedItem = ServiceData.categories.flatMap { it.items }.find { it.name == item.serviceName }
                                                        ?: com.example.data.models.ServiceItem(item.serviceName, item.price, "Wishlisted professional home service", "1 hour", emptyList())
                                                    onRebook(matchedItem)
                                                },
                                                containerColor = GoldAccent,
                                                contentColor = NavyBlueDark,
                                                modifier = Modifier.height(30.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            IconButton(onClick = {
                                                viewModel.removeFromWishlist(item.id)
                                                Toast.makeText(context, "${item.serviceName} removed from wishlist", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove", tint = AlertRed, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 4: INVOICES & PAST BOOKINGS SHORTCUT
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isInvoicesExpanded = !isInvoicesExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Receipt, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Invoices & Receipts", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isInvoicesExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isInvoicesExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            if (completedBookings.isEmpty()) {
                                Text("No invoice records available. Invoice logs are generated upon completing services.", fontSize = 11.sp, color = TextSecondaryLight)
                            } else {
                                completedBookings.forEach { b ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(b.serviceName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Booking ID: #${b.id} | ${b.date}", fontSize = 10.sp, color = TextSecondaryLight)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(b.price.toRupeeString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            IconButton(onClick = {
                                                val matchedItem = ServiceData.categories.flatMap { it.items }.find { it.name == b.serviceName }
                                                    ?: com.example.data.models.ServiceItem(b.serviceName, b.price, "Professional home service", "1 hour", emptyList())
                                                onRebook(matchedItem)
                                                Toast.makeText(context, "Opening booking form for ${b.serviceName}", Toast.LENGTH_SHORT).show()
                                            }) {
                                                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Rebook", tint = GreenSuccess, modifier = Modifier.size(18.dp))
                                            }
                                            IconButton(onClick = {
                                                selectedInvoiceBooking = b
                                                showInvoiceDialog = true
                                            }) {
                                                Icon(imageVector = Icons.Filled.Receipt, contentDescription = "View Invoice", tint = NavyBluePrimary, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 5: MEMBERSHIP CARD & PLAN INFO
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isMembershipExpanded = !isMembershipExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.CardMembership, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("My AMC Membership Plans", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isMembershipExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isMembershipExpanded) {
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = GoldLight)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Current Subscription: ${user?.membershipTier ?: "None"}", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                    Text("Valid until: July 19, 2027", fontSize = 11.sp, color = TextSecondaryLight)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Your active benefits:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("• 10% or 20% discount on all domestic services automatically", fontSize = 10.sp)
                                    Text("• Zero-diagnostic inspection visit charge", fontSize = 10.sp)
                                    Text("• Priority specialist match in 15 minutes", fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (user?.membershipTier == "Gold") GoldLight else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(1.dp, if (user?.membershipTier == "Gold") GoldAccent else Color.Transparent)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Gold Plan", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                        Text("₹499/Year", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("10% order discount. Priority scheduling.", fontSize = 10.sp, textAlign = TextAlign.Center)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        if (user?.membershipTier != "Gold" && user?.membershipTier != "Platinum") {
                                            OneCallButton(text = "Buy Gold", onClick = { viewModel.upgradeMembershipTier("Gold") })
                                        } else if (user?.membershipTier == "Gold") {
                                            Text("ACTIVE", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (user?.membershipTier == "Platinum") GoldLight else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(1.dp, if (user?.membershipTier == "Platinum") GoldAccent else Color.Transparent)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Platinum", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                        Text("₹999/Year", fontSize = 11.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("20% Discount. Free emergency dispatch coverage.", fontSize = 10.sp, textAlign = TextAlign.Center)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        if (user?.membershipTier != "Platinum") {
                                            OneCallButton(text = "Buy Platinum", onClick = { viewModel.upgradeMembershipTier("Platinum") })
                                        } else {
                                            Text("ACTIVE", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 6: WALLET TRANSACTION LOGS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isWalletExpanded = !isWalletExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Wallet Transaction Ledger", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isWalletExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isWalletExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val transactions = listOf(
                                Triple("Fund Load Successful", "+₹1,500.00", "July 19, 2026"),
                                Triple("Switchboard Fix Payment", "-₹299.00", "July 15, 2026"),
                                Triple("Cashback Rewards Redirection", "+₹150.00", "July 10, 2026")
                            )
                            transactions.forEach { txn ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(txn.first, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(txn.third, fontSize = 9.sp, color = TextSecondaryLight)
                                    }
                                    Text(
                                        txn.second,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (txn.second.startsWith("+")) GreenSuccess else AlertRed
                                    )
                                }
                                Divider()
                            }
                        }
                    }
                }
            }

            // SECTION 7: LOYALTY REWARDS REDEMPTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isRewardsExpanded = !isRewardsExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Redeem, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Cashback & Loyalty Rewards", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isRewardsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isRewardsExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Convert your loyalty cashback points directly into actual Indian Rupee (INR) wallet balance. (1 Point = ₹1 Cash)",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = GoldLight)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("REDEEMABLE BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                                    Text("${user?.rewardsPoints ?: 0} Points", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                                    Text("Equivalent Cash Value: ₹${user?.rewardsPoints ?: 0}.00", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OneCallButton(
                                        text = "Redeem Points to Wallet Balance",
                                        onClick = {
                                            if ((user?.rewardsPoints ?: 0) > 0) {
                                                val convertedPoints = user?.rewardsPoints ?: 0
                                                viewModel.redeemPointsToWallet()
                                                Toast.makeText(context, "Successfully redeemed $convertedPoints points! ₹$convertedPoints loaded to wallet.", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "You do not have any reward points to redeem!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = (user?.rewardsPoints ?: 0) > 0
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 8: REFER & EARN
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isReferExpanded = !isReferExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Share, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Refer & Earn Elite Perks", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isReferExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isReferExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Share your custom referral code with friends. Once they book their first home service, both of you get ₹250 loaded to your One Call wallets!",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = NavyBluePrimary.copy(alpha = 0.05f)),
                                border = BorderStroke(1.dp, GoldAccent.copy(0.4f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("YOUR EXCLUSIVE REFERRAL CODE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                                    Text("ONECALL-ASHOK-9892", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = NavyBluePrimary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OneCallButton(
                                        text = "Copy Referral Code",
                                        onClick = {
                                            try {
                                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("OneCall Referral Code", "ONECALL-ASHOK-9892")
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Referral code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Copied code: ONECALL-ASHOK-9892", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        containerColor = GoldAccent,
                                        contentColor = NavyBlueDark
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Referrals", fontSize = 10.sp, color = TextSecondaryLight)
                                            Text("3 Friends", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Total Earned", fontSize = 10.sp, color = TextSecondaryLight)
                                            Text("₹750.00", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenSuccess)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 9: GENERAL SETTINGS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSettingsExpanded = !isSettingsExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.Settings, contentDescription = null, tint = NavyBluePrimary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("System Preferences & Alerts", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Icon(
                                imageVector = if (isSettingsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle Section"
                            )
                        }

                        if (isSettingsExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Push Notifications", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Receive real-time technician and booking alerts", fontSize = 10.sp, color = TextSecondaryLight)
                                }
                                Switch(
                                    checked = pushAlertsEnabled,
                                    onCheckedChange = { pushAlertsEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent)
                                )
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Simulated App Dark Theme", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Toggle between eye-protection mode and standard colors", fontSize = 10.sp, color = TextSecondaryLight)
                                }
                                Switch(
                                    checked = localDarkModeSimulated,
                                    onCheckedChange = { localDarkModeSimulated = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 10: BIOMETRIC LOCK CONFIG
            item {
                SectionHeader(title = "Security & Privacy")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(NavyBluePrimary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Fingerprint,
                                        contentDescription = "Biometric Lock Icon",
                                        tint = NavyBluePrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Biometric Profile Lock", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                                    Text(
                                        "Require fingerprint to view profile",
                                        fontSize = 11.sp,
                                        color = TextSecondaryLight
                                    )
                                }
                            }
                            
                            val isBiometricEnabled = user?.isBiometricEnabled == true
                            Switch(
                                checked = isBiometricEnabled,
                                onCheckedChange = { checked ->
                                    if (activity != null) {
                                        val actText = if (checked) "Enable" else "Disable"
                                        BiometricHelper.authenticate(
                                            activity = activity,
                                            title = "$actText Biometric Lock",
                                            subtitle = "Verify identity to confirm",
                                            description = "Confirm biometric preference for profile security.",
                                            onSuccess = {
                                                viewModel.setBiometricEnabled(checked)
                                                // Reset unlocked state when turning on to demonstrate immediately
                                                if (checked) {
                                                    isProfileUnlocked = false
                                                }
                                                Toast.makeText(context, "Biometric Lock ${actText}d", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { error ->
                                                Toast.makeText(context, "Verification failed: $error", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    } else {
                                        viewModel.setBiometricEnabled(checked)
                                    }
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val isHardwareSupported = BiometricHelper.isBiometricAvailable(context)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isHardwareSupported) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                contentDescription = null,
                                tint = if (isHardwareSupported) GreenSuccess else AlertRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHardwareSupported) {
                                    "Biometric hardware active and ready on this device"
                                } else {
                                    "Biometrics not setup or unsupported on this device"
                                },
                                fontSize = 11.sp,
                                color = if (isHardwareSupported) GreenSuccess else AlertRed
                            )
                        }
                    }
                }
            }

            // Quick Access Controls (Portal Roles)
            item {
                SectionHeader(title = "Portal Workspace Settings")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("Product Store Shop", fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Buy certified home hardware and secure installations") },
                            leadingContent = { Icon(imageVector = Icons.Filled.ShoppingBag, contentDescription = null, tint = NavyBluePrimary) },
                            modifier = Modifier.clickable(onClick = onNavigateToShop)
                        )
                        Divider()
                        ListItem(
                            headlineContent = { Text("Admin Dashboard Portal", fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("Manage approvals, view operations, analytics and reports") },
                            leadingContent = { Icon(imageVector = Icons.Filled.SupervisorAccount, contentDescription = null, tint = NavyBluePrimary) },
                            modifier = Modifier.clickable(onClick = onNavigateToAdmin)
                        )
                        Divider()
                        ListItem(
                            headlineContent = { Text("Technician Console", fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("View active jobs, earnings, navigate maps and updates") },
                            leadingContent = { Icon(imageVector = Icons.Filled.Engineering, contentDescription = null, tint = NavyBluePrimary) },
                            modifier = Modifier.clickable(onClick = onNavigateToTechnician)
                        )
                    }
                }
            }

            item {
                OneCallButton(
                    text = "LOG OUT OF PLATFORM",
                    onClick = onLogout,
                    containerColor = AlertRed,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "logout_button"
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // PROFILE AVATAR CHOICE DIALOG
    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = { Text("Select Custom Avatar Profile", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select a preset avatar representation to set as your primary profile appearance:", fontSize = 11.sp, color = TextSecondaryLight)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        avatarIcons.forEachIndexed { index, pair ->
                            val isSel = index == selectedAvatarIndex
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedAvatarIndex = index },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) GoldLight else Color.White
                                ),
                                border = BorderStroke(1.dp, if (isSel) GoldAccent else Color.LightGray)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(if (isSel) GoldAccent else Color.LightGray, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(imageVector = pair.first, contentDescription = null, tint = NavyBlueDark)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(pair.second, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (isSel) {
                                        Icon(imageVector = Icons.Filled.Check, contentDescription = "Selected", tint = NavyBluePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OneCallButton(
                    text = "Save Avatar",
                    onClick = {
                        showAvatarDialog = false
                        Toast.makeText(context, "Avatar updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            }
        )
    }

    // ADD ADDRESS DIALOG
    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Service Delivery Address", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OneCallTextField(
                        value = newAddressInput,
                        onValueChange = { newAddressInput = it },
                        label = "Complete Delivery Address",
                        placeholder = "Flat No, Building, Area, landmark, city, pin code"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Address Type Label:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Home", "Office", "Other").forEach { tag ->
                            val isSel = tag == newAddressTag
                            Card(
                                modifier = Modifier
                                    .clickable { newAddressTag = tag }
                                    .weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) NavyBluePrimary else Color.White
                                ),
                                border = BorderStroke(1.dp, if (isSel) NavyBluePrimary else Color.LightGray)
                            ) {
                                Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(tag, color = if (isSel) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.align(Alignment.Center))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OneCallButton(
                    text = "Save Address",
                    onClick = {
                        if (newAddressInput.isNotBlank()) {
                            viewModel.addAddress(com.example.data.models.AddressEntity(addressLine = newAddressInput, tag = newAddressTag))
                            newAddressInput = ""
                            showAddAddressDialog = false
                            Toast.makeText(context, "New delivery address registered!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Address description cannot be blank!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // ADD CARD/UPI DIALOG
    if (showAddCardDialog) {
        AlertDialog(
            onDismissRequest = { showAddCardDialog = false },
            title = { Text("Link Payment Option", fontWeight = FontWeight.Bold, color = NavyBluePrimary) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Visa", "Mastercard", "UPI").forEach { type ->
                            val isSel = type == newCardTypeInput
                            Card(
                                modifier = Modifier
                                    .clickable { newCardTypeInput = type }
                                    .weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSel) NavyBluePrimary else Color.White
                                ),
                                border = BorderStroke(1.dp, if (isSel) NavyBluePrimary else Color.LightGray)
                            ) {
                                Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                    Text(type, color = if (isSel) Color.White else Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.align(Alignment.Center))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (newCardTypeInput == "UPI") {
                        OneCallTextField(
                            value = newCardNo,
                            onValueChange = { newCardNo = it },
                            label = "UPI Address (ID)",
                            placeholder = "username@okbank"
                        )
                    } else {
                        OneCallTextField(
                            value = newCardNo,
                            onValueChange = { newCardNo = it },
                            label = "16-Digit Card Number",
                            placeholder = "4111 2222 3333 4444"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OneCallTextField(
                            value = newCardHolderName,
                            onValueChange = { newCardHolderName = it },
                            label = "Card Holder Name",
                            placeholder = "ASHOK KUMAR"
                        )
                    }
                }
            },
            confirmButton = {
                OneCallButton(
                    text = "Link Account",
                    onClick = {
                        if (newCardNo.isNotBlank()) {
                            val displayStr = if (newCardTypeInput == "UPI") "UPI ID: $newCardNo" else "${newCardTypeInput} Card •••• ${newCardNo.takeLast(4)}"
                            viewModel.addSavedPayment(com.example.data.models.SavedPaymentMethodEntity(cardOrUpi = displayStr, type = newCardTypeInput))
                            newCardNo = ""
                            newCardHolderName = ""
                            showAddCardDialog = false
                            Toast.makeText(context, "Payment method securely authorized!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Input field cannot be empty!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showAddCardDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // DIGITAL INVOICE AND GST DIALOG
    if (showInvoiceDialog && selectedInvoiceBooking != null) {
        val b = selectedInvoiceBooking!!
        val cgst = if (isGstInvoiceEnabled) b.price * 0.09 else 0.0
        val sgst = if (isGstInvoiceEnabled) b.price * 0.09 else 0.0
        val totalTax = cgst + sgst
        val totalWithTax = b.price + totalTax

        AlertDialog(
            onDismissRequest = { showInvoiceDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Digital Tax Invoice", fontWeight = FontWeight.Bold, color = NavyBluePrimary, fontSize = 16.sp)
                    IconButton(onClick = {
                        Toast.makeText(context, "PDF generated! Invoice saved to local device successfully.", Toast.LENGTH_LONG).show()
                    }) {
                        Icon(imageVector = Icons.Filled.PictureAsPdf, contentDescription = "Download PDF", tint = AlertRed)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = NavyBluePrimary.copy(alpha = 0.05f)),
                        border = BorderStroke(1.dp, NavyBluePrimary.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("ONE CALL HOME SOLUTIONS", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = NavyBluePrimary)
                                Text("ORIGINAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Regd. Office: DLF Cyber City, Hyderabad, 500081", fontSize = 9.sp, color = TextSecondaryLight)
                            if (isGstInvoiceEnabled) {
                                Text("Company GSTIN: 36AAFCO8311D1ZP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("INVOICE NO: INV-2026-004${b.id}", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            Text("Date: ${b.date}", fontSize = 9.sp, color = TextSecondaryLight)
                            Text("Customer: ${user?.name ?: "Ashok Kumar"}", fontSize = 9.sp, color = TextSecondaryLight)
                            Text("Contact: ${user?.phone ?: "+91 94412 34567"}", fontSize = 9.sp, color = TextSecondaryLight)
                            Text("Work Address: ${b.address.take(45)}...", fontSize = 9.sp, color = TextSecondaryLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Business GST Invoice (18% ITC)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Switch(
                            checked = isGstInvoiceEnabled,
                            onCheckedChange = { isGstInvoiceEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldAccent)
                        )
                    }

                    if (isGstInvoiceEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OneCallTextField(
                            value = userBusinessName,
                            onValueChange = { userBusinessName = it },
                            label = "Business / Company Name",
                            placeholder = "Acme Technologies Pvt Ltd"
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OneCallTextField(
                            value = userGstin,
                            onValueChange = { userGstin = it },
                            label = "Customer GSTIN (15-digit)",
                            placeholder = "36AAAAA0000A1Z5"
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("BILLING DESCRIPTION", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = TextSecondaryLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(b.serviceName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(b.price.toRupeeString(), fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 11.sp, color = TextSecondaryLight)
                        Text(b.price.toRupeeString(), fontSize = 11.sp)
                    }
                    if (isGstInvoiceEnabled) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("CGST @ 9%", fontSize = 11.sp, color = TextSecondaryLight)
                            Text(cgst.toRupeeString(), fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SGST @ 9%", fontSize = 11.sp, color = TextSecondaryLight)
                            Text(sgst.toRupeeString(), fontSize = 11.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount (Net)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary)
                        Text(totalWithTax.toRupeeString(), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NavyBluePrimary)
                    }
                }
            },
            confirmButton = {
                OneCallButton(
                    text = "Close Invoice",
                    onClick = { showInvoiceDialog = false }
                )
            }
        )
    }
}

// --- 11. PRODUCTS SHOP SCREEN ---

@Composable
fun ProductsShopScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val products by viewModel.productsState.collectAsState()
    val user by viewModel.userState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("One Call Product Catalog", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = NavyBluePrimary)
        }
        Text("All hardware supplies include FREE Doorstep Professional Installation and verification.", fontSize = 11.sp, color = TextSecondaryLight, modifier = Modifier.padding(start = 12.dp))
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(products) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(product.name, fontWeight = FontWeight.Bold, color = NavyBluePrimary, modifier = Modifier.width(200.dp))
                            Text(product.price.toRupeeString(), fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(product.description, fontSize = 11.sp, color = TextSecondaryLight)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Warranty: ${product.warranty}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            
                            val canBuy = (user?.walletBalance ?: 0.0) >= product.price
                            OneCallButton(
                                text = if (canBuy) "Buy & Install" else "Low Balance",
                                onClick = { viewModel.buyProductWithInstallation(product) },
                                enabled = canBuy,
                                containerColor = if (canBuy) NavyBluePrimary else Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 12. ADMIN DASHBOARD SCREEN (Section 5 Multi-Property Management & Reports) ---

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val bookings by viewModel.bookingsState.collectAsState()
    val totalRevenue = bookings.filter { it.status == "Completed" }.sumOf { it.price }
    val activeJobsCount = bookings.filter { it.status == "In Progress" || it.status == "Assigned" || it.status == "Pending" }.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Admin Operations Portal", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = NavyBluePrimary)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Analytics Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = GoldLight)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("TOTAL REVENUE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    Text(totalRevenue.toRupeeString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                }
            }
            Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ACTIVE DISPATCHES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondaryLight)
                    Text("$activeJobsCount Active", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyBluePrimary)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Active Dispatch Orders List", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(booking.serviceName, fontWeight = FontWeight.Bold)
                            OneCallBadge(text = booking.status, backgroundColor = NavyBlueMedium.copy(0.15f), textColor = NavyBlueMedium)
                        }
                        Text("Address: ${booking.address}", fontSize = 10.sp, color = TextSecondaryLight, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Assigned Specialist: ${booking.technicianName}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- 13. TECHNICIAN CONSOLE SCREEN (Section 5 Jobs & Earnings) ---

@Composable
fun TechnicianDashboardScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val bookings by viewModel.bookingsState.collectAsState()
    val techJobs = bookings.filter { it.status == "In Progress" || it.status == "Assigned" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Technician Console", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = NavyBluePrimary)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Performance Metrics Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NavyBluePrimary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Engineering, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ACTIVE AGENT STATUS: ON-DUTY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("TODAY'S EARNINGS", fontSize = 10.sp, color = Color.White.copy(0.7f))
                        Text("₹2,450.00", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("RATING SCORE", fontSize = 10.sp, color = Color.White.copy(0.7f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.9 Stars", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text("Active Assigned Jobs (${techJobs.size})", fontWeight = FontWeight.Bold, color = NavyBluePrimary)
        Spacer(modifier = Modifier.height(8.dp))

        if (techJobs.isEmpty()) {
            EmptyState(title = "No Jobs Assigned", subtitle = "You are currently off-duty or there are no priority bookings in your local zone.", icon = Icons.Filled.TaskAlt)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(techJobs) { job ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(job.serviceName, fontWeight = FontWeight.Bold)
                                OneCallBadge(text = job.status, backgroundColor = YellowPending.copy(0.15f), textColor = YellowPending)
                            }
                            Text("Dispatch Location: ${job.address}", fontSize = 11.sp, color = TextSecondaryLight)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Simulate Technician Location Check-in
                            OneCallButton(
                                text = "Arrived at Customer Location",
                                onClick = { /* Updates GPS status */ },
                                containerColor = GreenSuccess,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
