package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.models.ServiceCategory
import com.example.data.models.ServiceData
import com.example.data.repository.AppRepository
import com.example.ui.navigation.Routes
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

class MainActivity : FragmentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "onecall_database"
        ).fallbackToDestructiveMigration().build()

        // Initialize Repository & ViewModel
        repository = AppRepository(database, applicationContext)
        
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(application, repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        })[MainViewModel::class.java]

        setContent {
            MyApplicationTheme {
                MainAppContainer(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val navController = rememberNavController()
    var isAuthenticated by remember { mutableStateOf(false) }
    val user by viewModel.userState.collectAsState()

    // Observe active routes to highlighted bottom bar items
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: Routes.HOME

    if (!isAuthenticated) {
        // Auth flow: Splash Screen -> Login Screen -> OTP Screen -> Authenticated Home
        NavHost(navController = navController, startDestination = Routes.SPLASH) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onTimeout = { 
                        navController.navigate(Routes.AUTH_LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.AUTH_LOGIN) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { 
                        navController.navigate(Routes.AUTH_OTP) 
                    },
                    onNavigateToSignup = { navController.navigate(Routes.AUTH_SIGNUP) }
                )
            }
            composable(Routes.AUTH_SIGNUP) {
                SignupScreen(
                    viewModel = viewModel,
                    onSignupSuccess = { 
                        navController.navigate(Routes.AUTH_OTP) 
                    },
                    onNavigateToLogin = { navController.navigate(Routes.AUTH_LOGIN) }
                )
            }
            composable(Routes.AUTH_OTP) {
                OTPScreen(
                    onOtpSuccess = { 
                        isAuthenticated = true 
                    },
                    onNavigateBack = { 
                        navController.navigateUp() 
                    }
                )
            }
        }
    } else {
        // Logged-in Responsive Layout supporting both Compact (mobile) and Wide (tablets/landscape) Screen Sizes
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp
            val isDark = androidx.compose.foundation.isSystemInDarkTheme()
            val navBg = if (isDark) DarkSurface else Color(0xFFF3EDF7)
            val navContentColor = if (isDark) Color.White else Color(0xFF1D1B20)
            
            val navSelectedIconColor = if (isDark) Color.White else Color(0xFF1D192B)
            val navSelectedTextColor = if (isDark) Color.White else Color(0xFF1D192B)
            val navIndicatorColor = if (isDark) NavyBluePrimary else Color(0xFFE8DEF8)
            val navUnselectedIconColor = if (isDark) Color.White.copy(0.6f) else Color(0xFF49454F).copy(0.7f)
            val navUnselectedTextColor = if (isDark) Color.White.copy(0.6f) else Color(0xFF49454F).copy(0.7f)

            Row(modifier = Modifier.fillMaxSize()) {
                // Side Navigation Rail (Adaptive Design Guideline)
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = navBg,
                        contentColor = navContentColor,
                        header = {
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .size(40.dp)
                                    .background(navIndicatorColor, shape = androidx.compose.foundation.shape.CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Filled.HomeRepairService, contentDescription = null, tint = navSelectedIconColor)
                            }
                        }
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        
                        NavigationRailItem(
                            selected = currentRoute == Routes.HOME,
                            onClick = { navigateToTab(navController, Routes.HOME) },
                            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Home") },
                            label = { Text("Home", fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = navSelectedIconColor,
                                selectedTextColor = navSelectedTextColor,
                                indicatorColor = navIndicatorColor,
                                unselectedIconColor = navUnselectedIconColor,
                                unselectedTextColor = navUnselectedTextColor
                            )
                        )
                        NavigationRailItem(
                            selected = currentRoute == Routes.SEARCH,
                            onClick = { navigateToTab(navController, Routes.SEARCH) },
                            icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search") },
                            label = { Text("Search", fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = navSelectedIconColor,
                                selectedTextColor = navSelectedTextColor,
                                indicatorColor = navIndicatorColor,
                                unselectedIconColor = navUnselectedIconColor,
                                unselectedTextColor = navUnselectedTextColor
                            )
                        )
                        NavigationRailItem(
                            selected = currentRoute == Routes.BOOKINGS_LIST,
                            onClick = { navigateToTab(navController, Routes.BOOKINGS_LIST) },
                            icon = { Icon(imageVector = Icons.Filled.Assignment, contentDescription = "Bookings") },
                            label = { Text("Bookings", fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = navSelectedIconColor,
                                selectedTextColor = navSelectedTextColor,
                                indicatorColor = navIndicatorColor,
                                unselectedIconColor = navUnselectedIconColor,
                                unselectedTextColor = navUnselectedTextColor
                            )
                        )
                        NavigationRailItem(
                            selected = currentRoute == Routes.CHAT_SUPPORT,
                            onClick = { navigateToTab(navController, Routes.CHAT_SUPPORT) },
                            icon = { Icon(imageVector = Icons.Filled.Chat, contentDescription = "Chat") },
                            label = { Text("Chat", fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = navSelectedIconColor,
                                selectedTextColor = navSelectedTextColor,
                                indicatorColor = navIndicatorColor,
                                unselectedIconColor = navUnselectedIconColor,
                                unselectedTextColor = navUnselectedTextColor
                            )
                        )
                        NavigationRailItem(
                            selected = currentRoute == Routes.PROFILE,
                            onClick = { navigateToTab(navController, Routes.PROFILE) },
                            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Profile") },
                            label = { Text("Profile", fontSize = 11.sp) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = navSelectedIconColor,
                                selectedTextColor = navSelectedTextColor,
                                indicatorColor = navIndicatorColor,
                                unselectedIconColor = navUnselectedIconColor,
                                unselectedTextColor = navUnselectedTextColor
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                // Main Content Viewport
                Scaffold(
                    modifier = Modifier.weight(1f),
                    bottomBar = {
                        // Standard Bottom Navigation for Compact Mobile Devices
                        if (!isWideScreen) {
                            NavigationBar(
                                containerColor = navBg,
                                contentColor = navContentColor
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == Routes.HOME,
                                    onClick = { navigateToTab(navController, Routes.HOME) },
                                    icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "Home") },
                                    label = { Text("Home", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = navSelectedIconColor,
                                        selectedTextColor = navSelectedTextColor,
                                        indicatorColor = navIndicatorColor,
                                        unselectedIconColor = navUnselectedIconColor,
                                        unselectedTextColor = navUnselectedTextColor
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Routes.SEARCH,
                                    onClick = { navigateToTab(navController, Routes.SEARCH) },
                                    icon = { Icon(imageVector = Icons.Filled.Search, contentDescription = "Search") },
                                    label = { Text("Search", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = navSelectedIconColor,
                                        selectedTextColor = navSelectedTextColor,
                                        indicatorColor = navIndicatorColor,
                                        unselectedIconColor = navUnselectedIconColor,
                                        unselectedTextColor = navUnselectedTextColor
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Routes.BOOKINGS_LIST,
                                    onClick = { navigateToTab(navController, Routes.BOOKINGS_LIST) },
                                    icon = { Icon(imageVector = Icons.Filled.Assignment, contentDescription = "Bookings") },
                                    label = { Text("Bookings", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = navSelectedIconColor,
                                        selectedTextColor = navSelectedTextColor,
                                        indicatorColor = navIndicatorColor,
                                        unselectedIconColor = navUnselectedIconColor,
                                        unselectedTextColor = navUnselectedTextColor
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Routes.CHAT_SUPPORT,
                                    onClick = { navigateToTab(navController, Routes.CHAT_SUPPORT) },
                                    icon = { Icon(imageVector = Icons.Filled.Chat, contentDescription = "Chat") },
                                    label = { Text("Chat", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = navSelectedIconColor,
                                        selectedTextColor = navSelectedTextColor,
                                        indicatorColor = navIndicatorColor,
                                        unselectedIconColor = navUnselectedIconColor,
                                        unselectedTextColor = navUnselectedTextColor
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Routes.PROFILE,
                                    onClick = { navigateToTab(navController, Routes.PROFILE) },
                                    icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = "Profile") },
                                    label = { Text("Profile", fontSize = 10.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = navSelectedIconColor,
                                        selectedTextColor = navSelectedTextColor,
                                        indicatorColor = navIndicatorColor,
                                        unselectedIconColor = navUnselectedIconColor,
                                        unselectedTextColor = navUnselectedTextColor
                                    )
                                )
                            }
                        }
                    },
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Routes.HOME) {
                            HomeScreen(
                                viewModel = viewModel,
                                user = user,
                                onNavigateToCategory = { category ->
                                    viewModel.selectCategory(category)
                                    navController.navigate(Routes.SERVICE_DETAIL)
                                },
                                onNavigateToDetail = { category, item ->
                                    viewModel.selectCategory(category)
                                    viewModel.selectServiceItem(item)
                                    navController.navigate(Routes.SERVICE_DETAIL)
                                },
                                onNavigateToShop = { navController.navigate(Routes.PRODUCTS_SHOP) },
                                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
                            )
                        }

                        composable(Routes.SEARCH) {
                            SearchScreen(
                                viewModel = viewModel,
                                onNavigateToCategory = { category ->
                                    viewModel.selectCategory(category)
                                    navController.navigate(Routes.SERVICE_DETAIL)
                                },
                                onNavigateToDetail = { category, item ->
                                    viewModel.selectCategory(category)
                                    viewModel.selectServiceItem(item)
                                    navController.navigate(Routes.SERVICE_DETAIL)
                                }
                            )
                        }

                        composable(Routes.NOTIFICATIONS) {
                            NotificationsScreen(
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }

                        composable(Routes.SERVICE_DETAIL) {
                            val selectedCat by viewModel.selectedCategory.collectAsState()
                            if (selectedCat != null) {
                                ServiceDetailScreen(
                                    viewModel = viewModel,
                                    category = selectedCat!!,
                                    onNavigateBack = { navController.navigateUp() },
                                    onBookService = { item, tier, price ->
                                        viewModel.selectServiceItem(item)
                                        navController.navigate(Routes.BOOKING_FORM)
                                    }
                                )
                            }
                        }

                        composable(Routes.BOOKING_FORM) {
                            val selectedItem by viewModel.selectedServiceItem.collectAsState()
                            if (selectedItem != null) {
                                BookingFormScreen(
                                    viewModel = viewModel,
                                    serviceName = selectedItem!!.name,
                                    tier = "Premium Quality", // default tier
                                    price = selectedItem!!.basePrice,
                                    onBookingSuccess = { navController.navigate(Routes.BOOKING_CONFIRMATION) },
                                    onNavigateBack = { navController.navigateUp() }
                                )
                            }
                        }

                        composable(Routes.BOOKING_CONFIRMATION) {
                            BookingConfirmationScreen(
                                viewModel = viewModel,
                                onNavigateToHome = { navigateToTab(navController, Routes.HOME) },
                                onNavigateToMyBookings = { navigateToTab(navController, Routes.BOOKINGS_LIST) }
                            )
                        }

                        composable(Routes.BOOKINGS_LIST) {
                            BookingsListScreen(
                                viewModel = viewModel,
                                onTrackBooking = { bookingId ->
                                    navController.navigate(Routes.bookingTrackingRoute(bookingId))
                                }
                            )
                        }

                        composable(Routes.BOOKING_TRACKING) { backStackEntry ->
                            val bookingId = backStackEntry.arguments?.getString("bookingId")?.toIntOrNull() ?: 0
                            BookingTrackingScreen(
                                viewModel = viewModel,
                                bookingId = bookingId,
                                onNavigateBack = { navController.navigateUp() },
                                onNavigateToChat = { bid ->
                                    navController.navigate(Routes.chatRoomRoute(bid))
                                }
                            )
                        }

                        composable(Routes.CHAT_ROOM) { backStackEntry ->
                            val bookingId = backStackEntry.arguments?.getString("bookingId")?.toIntOrNull() ?: 0
                            ChatScreen(
                                viewModel = viewModel,
                                bookingId = bookingId
                            )
                        }

                        composable(Routes.CHAT_SUPPORT) {
                            ChatScreen(
                                viewModel = viewModel,
                                bookingId = 0 // AI support thread
                            )
                        }

                        composable(Routes.EMERGENCY_SOS) {
                            EmergencySOSScreen(
                                viewModel = viewModel,
                                onTrackEmergency = { bookingId ->
                                    navController.navigate(Routes.bookingTrackingRoute(bookingId))
                                }
                            )
                        }

                        composable(Routes.PROFILE) {
                            ProfileScreen(
                                viewModel = viewModel,
                                user = user,
                                onNavigateToShop = { navController.navigate(Routes.PRODUCTS_SHOP) },
                                onNavigateToAdmin = { navController.navigate(Routes.ADMIN_DASHBOARD) },
                                onNavigateToTechnician = { navController.navigate(Routes.TECHNICIAN_DASHBOARD) },
                                onLogout = { isAuthenticated = false },
                                onRebook = { item ->
                                    viewModel.selectServiceItem(item)
                                    navController.navigate(Routes.BOOKING_FORM)
                                }
                            )
                        }

                        composable(Routes.PRODUCTS_SHOP) {
                            ProductsShopScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }

                        composable(Routes.ADMIN_DASHBOARD) {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }

                        composable(Routes.TECHNICIAN_DASHBOARD) {
                            TechnicianDashboardScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigateUp() }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun navigateToTab(navController: androidx.navigation.NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
