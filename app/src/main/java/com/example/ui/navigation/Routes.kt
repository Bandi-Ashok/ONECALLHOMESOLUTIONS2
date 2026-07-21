package com.example.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val AUTH_LOGIN = "auth_login"
    const val AUTH_SIGNUP = "auth_signup"
    const val AUTH_OTP = "auth_otp"
    
    // Bottom bar items
    const val HOME = "home"
    const val SEARCH = "search"
    const val BOOKINGS_LIST = "bookings_list"
    const val CHAT_SUPPORT = "chat_support"
    const val PROFILE = "profile"

    // SOS Dispatch is still available as a nested route
    const val EMERGENCY_SOS = "emergency_sos"

    // Nested screens
    const val NOTIFICATIONS = "notifications"
    const val SERVICE_DETAIL = "service_detail"
    const val BOOKING_FORM = "booking_form"
    const val BOOKING_CONFIRMATION = "booking_confirmation"
    const val BOOKING_TRACKING = "booking_tracking/{bookingId}"
    const val CHAT_ROOM = "chat_room/{bookingId}"
    const val PRODUCTS_SHOP = "products_shop"
    
    // Admin & Technician dashboards
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val TECHNICIAN_DASHBOARD = "technician_dashboard"

    fun bookingTrackingRoute(bookingId: Int) = "booking_tracking/$bookingId"
    fun chatRoomRoute(bookingId: Int) = "chat_room/$bookingId"
}
