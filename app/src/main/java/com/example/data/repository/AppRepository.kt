package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.models.BookingEntity
import com.example.data.models.ChatEntity
import com.example.data.models.ProductEntity
import com.example.data.models.ServiceData
import com.example.data.models.UserEntity
import com.example.data.models.AddressEntity
import com.example.data.models.FavoriteTechnicianEntity
import com.example.data.models.SavedPaymentMethodEntity
import com.example.data.models.WishlistEntity
import com.example.data.models.ReferralEntity
import com.example.data.models.AuditLogEntity
import com.example.data.models.CancellationReportEntity
import com.example.data.models.RefundReportEntity
import com.example.data.models.AmcReportEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class AppRepository(
    val db: AppDatabase,
    val context: Context
) {
    private val TAG = "AppRepository"

    val userFlow: Flow<UserEntity?> = db.userDao().getUser()
    val bookingsFlow: Flow<List<BookingEntity>> = db.bookingDao().getAllBookings()
    val productsFlow: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    
    val addressesFlow: Flow<List<AddressEntity>> = db.addressDao().getAllAddresses()
    val favoriteTechniciansFlow: Flow<List<FavoriteTechnicianEntity>> = db.favoriteTechnicianDao().getAllFavoriteTechnicians()
    val savedPaymentsFlow: Flow<List<SavedPaymentMethodEntity>> = db.savedPaymentMethodDao().getAllPayments()
    val wishlistFlow: Flow<List<WishlistEntity>> = db.wishlistDao().getWishlist()
    val referralsFlow: Flow<List<ReferralEntity>> = db.referralDao().getAllReferrals()
    
    val auditLogsFlow: Flow<List<AuditLogEntity>> = db.auditLogDao().getAllLogs()
    val cancellationReportsFlow: Flow<List<CancellationReportEntity>> = db.cancellationReportDao().getAllCancellations()
    val refundReportsFlow: Flow<List<RefundReportEntity>> = db.refundReportDao().getAllRefunds()
    val amcReportsFlow: Flow<List<AmcReportEntity>> = db.amcReportDao().getAllAmcReports()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Seeds initial data if empty
    suspend fun checkAndSeedDatabase() = withContext(Dispatchers.IO) {
        try {
            val currentUser = db.userDao().getUserSync()
            if (currentUser == null) {
                // Seed default user
                val defaultUser = UserEntity(
                    id = 1,
                    name = "Ashok Kumar",
                    email = "ashok.kumar@example.com",
                    phone = "+91 94412 34567",
                    address = "Flat 402, Sunshine Heights, Gachibowli, Hyderabad, TS, 500032",
                    membershipTier = "Platinum", // Treat user as valued Platinum member
                    walletBalance = 1500.0,
                    rewardsPoints = 450,
                    hasActiveAMC = true,
                    amcType = "Premium"
                )
                db.userDao().insertUser(defaultUser)
                Log.d(TAG, "Seeded default user.")
            }

            // Seed default products
            val products = db.productDao().getAllProducts().firstOrNull()
            if (products.isNullOrEmpty()) {
                db.productDao().insertProducts(ServiceData.products)
                Log.d(TAG, "Seeded default product catalog.")
            }

            // Seed default addresses
            val addresses = db.addressDao().getAllAddresses().firstOrNull()
            if (addresses.isNullOrEmpty()) {
                db.addressDao().insertAddress(AddressEntity(addressLine = "Flat 402, Sunshine Heights, Gachibowli, Hyderabad, TS, 500032", tag = "Home"))
                db.addressDao().insertAddress(AddressEntity(addressLine = "Tower B, 12th Floor, DLF Cyber City, Hyderabad, TS, 500081", tag = "Office"))
                Log.d(TAG, "Seeded default addresses.")
            }

            // Seed default favorite technicians
            val favs = db.favoriteTechnicianDao().getAllFavoriteTechnicians().firstOrNull()
            if (favs.isNullOrEmpty()) {
                db.favoriteTechnicianDao().insertFavorite(FavoriteTechnicianEntity(name = "Arun Kumar", category = "Plumbing & Sanitization", phone = "+91 98765 43210", rating = 4.9f))
                db.favoriteTechnicianDao().insertFavorite(FavoriteTechnicianEntity(name = "Sanjay Sharma", category = "HVAC & AC Systems", phone = "+91 87654 32109", rating = 4.8f))
                Log.d(TAG, "Seeded default favorite technicians.")
            }

            // Seed default saved payment methods
            val payments = db.savedPaymentMethodDao().getAllPayments().firstOrNull()
            if (payments.isNullOrEmpty()) {
                db.savedPaymentMethodDao().insertPayment(SavedPaymentMethodEntity(cardOrUpi = "HDFC Bank Credit Card •••• 4567", type = "Visa"))
                db.savedPaymentMethodDao().insertPayment(SavedPaymentMethodEntity(cardOrUpi = "SBI Platinum Debit Card •••• 9210", type = "Mastercard"))
                db.savedPaymentMethodDao().insertPayment(SavedPaymentMethodEntity(cardOrUpi = "ashok@okhdfcbank", type = "UPI"))
                Log.d(TAG, "Seeded default payment methods.")
            }

            // Seed default referrals
            val referrals = db.referralDao().getAllReferrals().firstOrNull()
            if (referrals.isNullOrEmpty()) {
                db.referralDao().insertReferral(ReferralEntity(friendName = "Ramesh Kumar", status = "Completed", rewardAmount = 250.0))
                db.referralDao().insertReferral(ReferralEntity(friendName = "Suresh Patel", status = "Registered", rewardAmount = 0.0))
                Log.d(TAG, "Seeded default referrals.")
            }

            // Seed default wishlist items
            val wishlist = db.wishlistDao().getWishlist().firstOrNull()
            if (wishlist.isNullOrEmpty()) {
                db.wishlistDao().addToWishlist(WishlistEntity(categoryId = "electrical", serviceName = "Smart Switchboard Installation", price = 899.0))
                Log.d(TAG, "Seeded default wishlist.")
            }

            // Seed default audit logs
            val auditLogs = db.auditLogDao().getAllLogs().firstOrNull()
            if (auditLogs.isNullOrEmpty()) {
                db.auditLogDao().insertLog(AuditLogEntity(action = "System Startup", details = "Database seeded successfully. All home services catalogs updated."))
                db.auditLogDao().insertLog(AuditLogEntity(action = "Biometric Configuration", details = "FaceID & Fingerprint security module initialized."))
                db.auditLogDao().insertLog(AuditLogEntity(action = "Dispatch Board Sync", details = "Specialist real-time tracking linked with regional nodes."))
                Log.d(TAG, "Seeded default audit logs.")
            }

            // Seed default cancellation reports
            val cancellations = db.cancellationReportDao().getAllCancellations().firstOrNull()
            if (cancellations.isNullOrEmpty()) {
                db.cancellationReportDao().insertCancellation(CancellationReportEntity(bookingId = 991, serviceName = "Full House Deep Cleaning", price = 2499.0, reason = "Customer requested date shift to next weekend", refundStatus = "Processed"))
                db.cancellationReportDao().insertCancellation(CancellationReportEntity(bookingId = 992, serviceName = "Sofa Spa & Shampooing", price = 899.0, reason = "Booking placed by mistake, cancelled within 5 mins", refundStatus = "Processed"))
                db.cancellationReportDao().insertCancellation(CancellationReportEntity(bookingId = 993, serviceName = "Ceiling Fan Installation", price = 189.0, reason = "Technician delayed due to heavy rain downpour", refundStatus = "Pending"))
                Log.d(TAG, "Seeded default cancellations.")
            }

            // Seed default refund reports
            val refunds = db.refundReportDao().getAllRefunds().firstOrNull()
            if (refunds.isNullOrEmpty()) {
                db.refundReportDao().insertRefund(RefundReportEntity(bookingId = 991, serviceName = "Full House Deep Cleaning", refundAmount = 2499.0, transactionId = "TXN-REFUND-82741"))
                db.refundReportDao().insertRefund(RefundReportEntity(bookingId = 992, serviceName = "Sofa Spa & Shampooing", refundAmount = 899.0, transactionId = "TXN-REFUND-11094"))
                Log.d(TAG, "Seeded default refunds.")
            }

            // Seed default AMC reports
            val amcs = db.amcReportDao().getAllAmcReports().firstOrNull()
            if (amcs.isNullOrEmpty()) {
                db.amcReportDao().insertAmc(AmcReportEntity(customerName = "Ashok Kumar", planType = "Premium", price = 4999.0, startDate = "2026-01-10", expiryDate = "2027-01-10", status = "Active"))
                db.amcReportDao().insertAmc(AmcReportEntity(customerName = "Vikram Aditya", planType = "Corporate", price = 14999.0, startDate = "2026-03-15", expiryDate = "2027-03-15", status = "Active"))
                db.amcReportDao().insertAmc(AmcReportEntity(customerName = "Sneha Reddy", planType = "Basic", price = 1999.0, startDate = "2025-05-20", expiryDate = "2026-05-20", status = "Expired"))
                Log.d(TAG, "Seeded default AMC reports.")
            }

        } catch (e: Exception) {
            Log.e(TAG, "Database seeding error: ${e.message}")
        }
    }

    suspend fun addAuditLog(action: String, details: String) = withContext(Dispatchers.IO) {
        db.auditLogDao().insertLog(AuditLogEntity(action = action, details = details))
    }

    suspend fun addCancellation(cancel: CancellationReportEntity) = withContext(Dispatchers.IO) {
        db.cancellationReportDao().insertCancellation(cancel)
    }

    suspend fun addRefund(refund: RefundReportEntity) = withContext(Dispatchers.IO) {
        db.refundReportDao().insertRefund(refund)
    }

    suspend fun addAmcReport(amc: AmcReportEntity) = withContext(Dispatchers.IO) {
        db.amcReportDao().insertAmc(amc)
    }

    suspend fun addAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        db.addressDao().insertAddress(address)
    }

    suspend fun removeAddress(id: Int) = withContext(Dispatchers.IO) {
        db.addressDao().deleteAddress(id)
    }

    suspend fun addFavoriteTechnician(tech: FavoriteTechnicianEntity) = withContext(Dispatchers.IO) {
        db.favoriteTechnicianDao().insertFavorite(tech)
    }

    suspend fun removeFavoriteTechnician(id: Int) = withContext(Dispatchers.IO) {
        db.favoriteTechnicianDao().deleteFavorite(id)
    }

    suspend fun addSavedPayment(payment: SavedPaymentMethodEntity) = withContext(Dispatchers.IO) {
        db.savedPaymentMethodDao().insertPayment(payment)
    }

    suspend fun removeSavedPayment(id: Int) = withContext(Dispatchers.IO) {
        db.savedPaymentMethodDao().deletePayment(id)
    }

    suspend fun addToWishlist(item: WishlistEntity) = withContext(Dispatchers.IO) {
        db.wishlistDao().addToWishlist(item)
    }

    suspend fun removeFromWishlist(id: Int) = withContext(Dispatchers.IO) {
        db.wishlistDao().removeFromWishlist(id)
    }

    suspend fun removeFromWishlistByName(name: String) = withContext(Dispatchers.IO) {
        db.wishlistDao().removeFromWishlistByName(name)
    }

    suspend fun addReferral(ref: ReferralEntity) = withContext(Dispatchers.IO) {
        db.referralDao().insertReferral(ref)
    }

    suspend fun updateUserProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        db.userDao().insertUser(user)
    }

    suspend fun addBooking(booking: BookingEntity): Long = withContext(Dispatchers.IO) {
        db.bookingDao().insertBooking(booking)
    }

    private fun initFirebase() {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:1234567890:android:abc123xyz")
                    .setApiKey("dummy-api-key-for-firestore-realtime")
                    .setProjectId("onecall-aistudio-fallback")
                    .build()
                FirebaseApp.initializeApp(context, options)
                Log.d(TAG, "Firebase initialized programmatically with dummy options")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}")
        }
    }

    fun syncBookingToFirestore(booking: BookingEntity) {
        try {
            initFirebase()
            val dbInstance = FirebaseFirestore.getInstance()
            val data = hashMapOf(
                "id" to booking.id,
                "category" to booking.category,
                "serviceName" to booking.serviceName,
                "tier" to booking.tier,
                "price" to booking.price,
                "date" to booking.date,
                "timeSlot" to booking.timeSlot,
                "address" to booking.address,
                "status" to booking.status,
                "technicianName" to booking.technicianName,
                "technicianPhone" to booking.technicianPhone,
                "technicianRating" to booking.technicianRating,
                "otp" to booking.otp,
                "isEmergency" to booking.isEmergency,
                "timestamp" to booking.timestamp
            )
            dbInstance.collection("bookings")
                .document(booking.id.toString())
                .set(data)
                .addOnSuccessListener {
                    Log.d("FirestoreSync", "Booking ${booking.id} synced successfully to Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreSync", "Failed to sync booking ${booking.id}: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error in syncBookingToFirestore: ${e.message}")
        }
    }

    fun updateBookingStatusInFirestore(bookingId: Int, status: String) {
        try {
            initFirebase()
            val dbInstance = FirebaseFirestore.getInstance()
            dbInstance.collection("bookings")
                .document(bookingId.toString())
                .update("status", status)
                .addOnSuccessListener {
                    Log.d("FirestoreSync", "Booking status updated to $status in Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreSync", "Failed to update booking status in Firestore: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error in updateBookingStatusInFirestore: ${e.message}")
        }
    }

    fun startRealtimeFirestoreListener(onStatusUpdated: (Int, String) -> Unit) {
        try {
            initFirebase()
            val dbInstance = FirebaseFirestore.getInstance()
            dbInstance.collection("bookings")
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("FirestoreSync", "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        for (doc in snapshots.documentChanges) {
                            val bookingId = doc.document.id.toIntOrNull()
                            val status = doc.document.getString("status")
                            if (bookingId != null && status != null) {
                                Log.d("FirestoreSync", "Firestore change detected: Booking $bookingId -> $status")
                                onStatusUpdated(bookingId, status)
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error in startRealtimeFirestoreListener: ${e.message}")
        }
    }

    suspend fun cancelBooking(bookingId: Int) = withContext(Dispatchers.IO) {
        db.bookingDao().updateBookingStatus(bookingId, "Cancelled")
        updateBookingStatusInFirestore(bookingId, "Cancelled")
    }

    suspend fun completeBooking(bookingId: Int) = withContext(Dispatchers.IO) {
        db.bookingDao().updateBookingStatus(bookingId, "Completed")
        updateBookingStatusInFirestore(bookingId, "Completed")
    }

    suspend fun addChatMessage(chat: ChatEntity): Long = withContext(Dispatchers.IO) {
        db.chatDao().insertChat(chat)
    }

    fun getChatsForBooking(bookingId: Int): Flow<List<ChatEntity>> {
        return db.chatDao().getChatsForBooking(bookingId)
    }

    /**
     * AI-Powered Cost Estimator using direct REST Gemini API.
     * Gracefully falls back to local heuristic estimates on missing key or network issue.
     */
    suspend fun getAICostEstimate(serviceDescription: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackCostEstimate(serviceDescription, "API Key is not configured in Secrets panel.")
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        
        val systemPrompt = """
            You are the expert pricing and feasibility estimator for 'One Call Home Solutions' (India). 
            Given a customer's description of a home repair, installation, renovation, or maintenance request, 
            provide a professional, friendly, and structured cost breakdown in Indian Rupees (INR).
            
            Format your response clearly using Material Design standards:
            1. Short Feasibility assessment (e.g., "Highly Feasible, Standard Equipment Required")
            2. Estimated Cost Range (e.g., ₹1,200 - ₹2,500)
            3. Breakdown:
               - Materials Required & Cost (Est.)
               - Professional Labour Charge (Est.)
               - G.S.T & Service Taxes (18% standard in India)
            4. Recommended Service category (e.g., "We recommend scheduling an 'Electrical Switchboard Upgrade' from our menu").
            5. Time estimate (e.g., "1.5 to 2 hours").
            
            Keep the response highly professional, clean, concise, and easy to scan on a mobile screen. 
            Do not write long text blocks. Use simple bullet points. Avoid any sales-pitch hype.
        """.trimIndent()

        val jsonRequest = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": "Customer Request: $serviceDescription"
                    }
                  ]
                }
              ],
              "systemInstruction": {
                "parts": [
                  {
                    "text": "$systemPrompt"
                  }
                ]
              }
            }
        """.trimIndent()

        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonRequest.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext getFallbackCostEstimate(serviceDescription, "API Response: ${response.code}")
                }
                val bodyString = response.body?.string() ?: ""
                val rawText = parseGeminiResponseText(bodyString)
                if (rawText.isNullOrBlank()) {
                    return@withContext getFallbackCostEstimate(serviceDescription, "Unable to extract response content.")
                }
                return@withContext rawText
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini API call failed", e)
            return@withContext getFallbackCostEstimate(serviceDescription, "Network or system error: ${e.localizedMessage}")
        }
    }

    /**
     * AI-Powered Support Assistant Chatbot.
     */
    suspend fun getAISupportReply(userMessage: String, chatHistory: List<ChatEntity>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackSupportReply(userMessage)
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        
        val historyPrompt = chatHistory.takeLast(6).joinToString("\n") { 
            "${it.sender}: ${it.message}"
        }

        val systemPrompt = """
            You are 'One Call Assistant', the friendly, warm, and highly professional 24/7 AI helper for 'One Call Home Solutions'.
            We provide comprehensive home management services including:
            1. Cleaning (deep, bathroom, kitchen, sofa)
            2. Painting (interior, exterior, stencils, metal)
            3. Plumbing (taps, water heaters, drainage clear)
            4. Electrical (fans, switches, wiring, inverter)
            5. Home Appliances (AC, Fridge, Washing machine, RO water purifier)
            6. Smart Home & Security (CCTV, locks)
            7. Pest Control (cockroaches, termites, rodents)
            8. Packers & Movers, civil works, senior citizen care, etc.
            9. 24/7 Priority Emergency dispatch (30-minute arrival standard).
            
            Your goal is to answer customer questions with warmth, clarity, and precision, and guide them to select and book the right service in our app.
            Keep your replies friendly, helpful, short (max 3-4 sentences), and professional.
            Avoid generic jargon or stock clichés. Suggest how they can book immediately.
        """.trimIndent()

        val fullPrompt = "Previous Dialogue:\n$historyPrompt\n\nCustomer: $userMessage"

        val jsonRequest = """
            {
              "contents": [
                {
                  "parts": [
                    {
                      "text": "$fullPrompt"
                    }
                  ]
                }
              ],
              "systemInstruction": {
                "parts": [
                  {
                    "text": "$systemPrompt"
                  }
                ]
              }
            }
        """.trimIndent()

        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonRequest.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    val parsed = parseGeminiResponseText(bodyString)
                    if (!parsed.isNullOrBlank()) {
                        return@withContext parsed
                    }
                }
                return@withContext getFallbackSupportReply(userMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini support chat failed", e)
            return@withContext getFallbackSupportReply(userMessage)
        }
    }

    private fun parseGeminiResponseText(responseJson: String): String? {
        return try {
            // Quick JSON navigation using Map parsing to avoid complex schemas
            val adapter = moshi.adapter(Map::class.java)
            val map = adapter.fromJson(responseJson) ?: return null
            val candidates = map["candidates"] as? List<*> ?: return null
            val firstCandidate = candidates.firstOrNull() as? Map<*, *> ?: return null
            val content = firstCandidate["content"] as? Map<*, *> ?: return null
            val parts = content["parts"] as? List<*> ?: return null
            val firstPart = parts.firstOrNull() as? Map<*, *> ?: return null
            firstPart["text"] as? String
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini response JSON", e)
            null
        }
    }

    private fun getFallbackCostEstimate(serviceDescription: String, reason: String): String {
        // Generates an elegant fallback response using standard calculations
        val priceHeuristic = when {
            serviceDescription.contains("paint", true) || serviceDescription.contains("wall", true) -> "₹4,500 - ₹12,000"
            serviceDescription.contains("ac", true) || serviceDescription.contains("cooling", true) || serviceDescription.contains("fridge", true) -> "₹800 - ₹2,500"
            serviceDescription.contains("clean", true) || serviceDescription.contains("sofa", true) || serviceDescription.contains("wash", true) -> "₹600 - ₹2,200"
            serviceDescription.contains("leak", true) || serviceDescription.contains("tap", true) || serviceDescription.contains("plumb", true) -> "₹150 - ₹900"
            serviceDescription.contains("wire", true) || serviceDescription.contains("short", true) || serviceDescription.contains("electrical", true) -> "₹250 - ₹1,500"
            serviceDescription.contains("cctv", true) || serviceDescription.contains("camera", true) || serviceDescription.contains("lock", true) -> "₹1,200 - ₹5,000"
            else -> "₹499 - ₹3,500"
        }

        return """
            🔍 **One Call Estimate Heuristic** (Local Engine)
            
            *Estimate for: "$serviceDescription"*
            
            **Estimated Range:** $priceHeuristic
            
            **Feasibility Status:** Highly Feasible. Can be scheduled immediately.
            
            **Breakdown (Estimated):**
            * **Materials & Spares:** ₹200 - ₹1,200
            * **Professional Labour:** ₹250 - ₹1,800
            * **Applicable G.S.T:** Included (18%)
            
            *Recommended Category:* Please check our corresponding service menu for upfront flat rates.
            *Safety Warranty:* Includes a **7-Day Service Re-service Guarantee**.
            
            ⚠️ *Note: $reason. Real-time AI estimators will automatically enable once the API key is set in the Secrets Panel.*
        """.trimIndent()
    }

    private fun getFallbackSupportReply(userMessage: String): String {
        val query = userMessage.lowercase()
        return when {
            query.contains("clean", true) || query.contains("house", true) -> {
                "We offer highly professional Full House Deep Cleaning, Bathroom cleaning, and Kitchen descaling! You can explore and book this directly under the 'Cleaning' tab on our Home dashboard with instant slot confirmation."
            }
            query.contains("plumb", true) || query.contains("leak", true) || query.contains("tap", true) -> {
                "Leakages and tap repairs are our specialty. You can request a certified plumber right away. If it is a severe leakage or pipe burst, please head to the **SOS Tab** to book our 24/7 Emergency Plumber for dispatch within 30 minutes!"
            }
            query.contains("electrical", true) || query.contains("fan", true) || query.contains("switch", true) -> {
                "Our certified electricians can fix switchboards, install ceiling fans, and handle complete house rewiring safely. Tap on 'Electrical Services' from the category slider on the Home page to schedule a professional."
            }
            query.contains("emergency", true) || query.contains("sos", true) || query.contains("urgent", true) -> {
                "For urgent issues, please use our **24/7 Emergency SOS feature**! This guarantees prioritized technician dispatch with a 30-minute standard local response. Simply tap the 'SOS' icon in the bottom bar!"
            }
            query.contains("amc", true) || query.contains("membership", true) || query.contains("plan", true) -> {
                "Our Annual Maintenance Contracts (AMC) keep your home running smoothly with priority dispatch, free regular inspections, and special discounts. You can view your Membership tier or upgrade on your Profile screen."
            }
            else -> {
                "Hello! Welcome to One Call Home Solutions support. I am here to help you coordinate home repairs, cleaning, appliance services, or smart automation. What specific issue can we resolve for you today?"
            }
        }
    }
}
