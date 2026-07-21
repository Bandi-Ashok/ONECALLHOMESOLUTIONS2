package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.models.BookingEntity
import com.example.data.models.ChatEntity
import com.example.data.models.ProductEntity
import com.example.data.models.ServiceCategory
import com.example.data.models.ServiceData
import com.example.data.models.ServiceItem
import com.example.data.models.Technician
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
import com.example.data.repository.AppRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel(application: Application, private val repository: AppRepository) : AndroidViewModel(application) {

    // --- Core Flows from Repository ---
    val userState: StateFlow<UserEntity?> = repository.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val bookingsState: StateFlow<List<BookingEntity>> = repository.bookingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val productsState: StateFlow<List<ProductEntity>> = repository.productsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val addressesState: StateFlow<List<AddressEntity>> = repository.addressesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteTechniciansState: StateFlow<List<FavoriteTechnicianEntity>> = repository.favoriteTechniciansFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val savedPaymentsState: StateFlow<List<SavedPaymentMethodEntity>> = repository.savedPaymentsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val wishlistState: StateFlow<List<WishlistEntity>> = repository.wishlistFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val referralsState: StateFlow<List<ReferralEntity>> = repository.referralsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val auditLogsState: StateFlow<List<AuditLogEntity>> = repository.auditLogsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cancellationReportsState: StateFlow<List<CancellationReportEntity>> = repository.cancellationReportsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val refundReportsState: StateFlow<List<RefundReportEntity>> = repository.refundReportsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val amcReportsState: StateFlow<List<AmcReportEntity>> = repository.amcReportsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- UI Local Screen States ---
    private val _selectedCategory = MutableStateFlow<ServiceCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedServiceItem = MutableStateFlow<ServiceItem?>(null)
    val selectedServiceItem = _selectedServiceItem.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // --- Support & Technician Chat Message Flow ---
    private val _supportChats = MutableStateFlow<List<ChatEntity>>(emptyList())
    val supportChats = _supportChats.asStateFlow()

    private val _technicianChatsMap = MutableStateFlow<Map<Int, List<ChatEntity>>>(emptyMap())
    val technicianChatsMap = _technicianChatsMap.asStateFlow()

    // --- AI Cost Estimator State ---
    private val _estimatorInput = MutableStateFlow("")
    val estimatorInput = _estimatorInput.asStateFlow()

    private val _estimatorOutput = MutableStateFlow("")
    val estimatorOutput = _estimatorOutput.asStateFlow()

    private val _isEstimatorLoading = MutableStateFlow(false)
    val isEstimatorLoading = _isEstimatorLoading.asStateFlow()

    // --- Smart Support AI Chat State ---
    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading = _isChatLoading.asStateFlow()

    // --- Active Emergency SOS State ---
    private val _activeSosBooking = MutableStateFlow<BookingEntity?>(null)
    val activeSosBooking = _activeSosBooking.asStateFlow()

    private val _isSosAlarmActive = MutableStateFlow(false)
    val isSosAlarmActive = _isSosAlarmActive.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
            // Initialize default Support Chat welcome message if empty
            if (_supportChats.value.isEmpty()) {
                _supportChats.value = listOf(
                    ChatEntity(
                        bookingId = 0,
                        sender = "Support",
                        message = "Welcome to One Call Support! Ask me about our 28 service divisions, pricing models, or booking processes. How can I assist you today?",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }
        // Start real-time Firestore listener to automatically replicate status changes to local Room
        repository.startRealtimeFirestoreListener { bookingId, newStatus ->
            viewModelScope.launch {
                repository.db.bookingDao().updateBookingStatus(bookingId, newStatus)
            }
        }
    }

    fun selectCategory(category: ServiceCategory?) {
        _selectedCategory.value = category
    }

    fun selectServiceItem(item: ServiceItem?) {
        _selectedServiceItem.value = item
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setEstimatorInput(input: String) {
        _estimatorInput.value = input
    }

    // --- Interactive Operations & Business Rules ---

    fun runAICostEstimate() {
        val input = _estimatorInput.value.trim()
        if (input.isEmpty()) return
        viewModelScope.launch {
            _isEstimatorLoading.value = true
            _estimatorOutput.value = "Estimating project cost... Please wait while our AI engine analyzes materials, labour, and taxes."
            val output = repository.getAICostEstimate(input)
            _estimatorOutput.value = output
            _isEstimatorLoading.value = false
        }
    }

    fun sendSupportChatMessage(messageText: String) {
        val text = messageText.trim()
        if (text.isEmpty()) return

        val userChat = ChatEntity(bookingId = 0, sender = "User", message = text, timestamp = System.currentTimeMillis())
        _supportChats.value = _supportChats.value + userChat

        viewModelScope.launch {
            _isChatLoading.value = true
            val response = repository.getAISupportReply(text, _supportChats.value)
            val aiChat = ChatEntity(bookingId = 0, sender = "Support", message = response, timestamp = System.currentTimeMillis())
            _supportChats.value = _supportChats.value + aiChat
            _isChatLoading.value = false
        }
    }

    fun sendTechnicianChatMessage(bookingId: Int, messageText: String) {
        val text = messageText.trim()
        if (text.isEmpty()) return

        val userChat = ChatEntity(bookingId = bookingId, sender = "User", message = text, timestamp = System.currentTimeMillis())
        val currentChats = _technicianChatsMap.value[bookingId] ?: emptyList()
        _technicianChatsMap.value = _technicianChatsMap.value + (bookingId to (currentChats + userChat))

        viewModelScope.launch {
            delay(1500) // Realistic typing lag
            val assignedBooking = bookingsState.value.find { it.id == bookingId }
            val techName = assignedBooking?.technicianName ?: "Your Technician"
            
            val replyText = when {
                text.lowercase().contains("where") || text.lowercase().contains("time") || text.lowercase().contains("eta") -> {
                    "Hello! I am currently packing my tools and should reach your location within 15-20 minutes. Please keep the verification OTP ready!"
                }
                text.lowercase().contains("cost") || text.lowercase().contains("price") -> {
                    "The service charges are standard as shown in your digital invoice. Any additional parts required will be billed transparently with your approval."
                }
                else -> {
                    "Got it! I am on my way to assist you. If you have any gate-pass or parking instructions, please share."
                }
            }
            
            val techChat = ChatEntity(bookingId = bookingId, sender = "Technician", message = replyText, timestamp = System.currentTimeMillis())
            val updatedChats = _technicianChatsMap.value[bookingId] ?: emptyList()
            _technicianChatsMap.value = _technicianChatsMap.value + (bookingId to (updatedChats + techChat))
        }
    }

    fun createServiceBooking(
        categoryName: String,
        serviceItemName: String,
        selectedTier: String,
        price: Double,
        date: String,
        timeSlot: String,
        customAddress: String = ""
    ) {
        viewModelScope.launch {
            val defaultUser = userState.value
            val finalAddress = customAddress.ifEmpty { defaultUser?.address ?: "Default Gachibowli Address" }
            
            // Generate a random 4-digit verification OTP
            val verificationOtp = (1000..9999).random().toString()
            
            // Match a certified technician for this specialty
            val techList = ServiceData.technicians
            val matchedTech = techList.random()

            val newBooking = BookingEntity(
                category = categoryName,
                serviceName = serviceItemName,
                tier = selectedTier,
                price = price,
                date = date,
                timeSlot = timeSlot,
                address = finalAddress,
                status = "Pending",
                technicianName = matchedTech.name,
                technicianPhone = matchedTech.phone,
                technicianRating = matchedTech.rating,
                otp = verificationOtp,
                isEmergency = false,
                serviceWarrantyDays = when(selectedTier) {
                    "Deep" -> 30
                    "Premium" -> 15
                    else -> 7
                }
            )

            val bookingId = repository.addBooking(newBooking).toInt()
            
            // Sync booking to Firestore
            repository.syncBookingToFirestore(newBooking.copy(id = bookingId))
            
            // Seed a starter welcome message from the technician for this booking
            val welcomeChat = ChatEntity(
                bookingId = bookingId,
                sender = "Technician",
                message = "Hello! I am ${matchedTech.name}, your assigned specialist. I will arrive at your slot on $date ($timeSlot). Please feel free to message me here.",
                timestamp = System.currentTimeMillis()
            )
            _technicianChatsMap.value = _technicianChatsMap.value + (bookingId to listOf(welcomeChat))

            // Simulate Live Booking Lifecyle Updates:
            // 3 seconds: Assigned
            // 8 seconds: In Progress
            simulateLifecycleUpdates(bookingId)
        }
    }

    fun triggerEmergencySOS(customAddress: String = "") {
        viewModelScope.launch {
            _isSosAlarmActive.value = true
            val defaultUser = userState.value
            val finalAddress = customAddress.ifEmpty { defaultUser?.address ?: "Default Gachibowli Address" }
            
            val randomOtp = (1000..9999).random().toString()
            val tech = ServiceData.technicians.first() // Select top plumber/electrician

            val sosBooking = BookingEntity(
                category = "Emergency SOS",
                serviceName = "24/7 Priority Emergency Support",
                tier = "Platinum Emergency",
                price = 399.0, // standard call-out
                date = "Today",
                timeSlot = "Within 30 Mins",
                address = finalAddress,
                status = "Assigned", // Directly assigned for extreme speed!
                technicianName = tech.name,
                technicianPhone = tech.phone,
                technicianRating = tech.rating,
                otp = randomOtp,
                isEmergency = true,
                serviceWarrantyDays = 15
            )

            val bookingId = repository.addBooking(sosBooking).toInt()
            
            // Sync SOS booking to Firestore
            repository.syncBookingToFirestore(sosBooking.copy(id = bookingId))
            
            val fetchedSos = repository.bookingsFlow.stateIn(viewModelScope).value.find { it.id == bookingId }
            _activeSosBooking.value = fetchedSos ?: sosBooking

            // Simulate emergency progress quickly:
            delay(1000)
            repository.updateBookingStatusInFirestore(bookingId, "In Progress")
            _activeSosBooking.value = _activeSosBooking.value?.copy(status = "In Progress")
        }
    }

    fun stopSosAlarm() {
        _isSosAlarmActive.value = false
        _activeSosBooking.value = null
    }

    private fun simulateLifecycleUpdates(bookingId: Int) {
        viewModelScope.launch {
            // After 3 seconds, update Pending -> Assigned
            delay(3000)
            repository.updateBookingStatusInFirestore(bookingId, "Assigned")
            
            // After 8 seconds, update Assigned -> In Progress
            delay(5000)
            repository.updateBookingStatusInFirestore(bookingId, "In Progress")
        }
    }

    fun completeBookingWithOTP(bookingId: Int, enteredOtp: String): Boolean {
        val currentBooking = bookingsState.value.find { it.id == bookingId }
        return if (currentBooking != null && currentBooking.otp == enteredOtp) {
            viewModelScope.launch {
                repository.completeBooking(bookingId)
                
                // Award rewards points to user profile on successful completion
                val currentUser = userState.value
                if (currentUser != null) {
                    val earnedPoints = (currentBooking.price / 10).toInt() // 10% back as points
                    val updatedUser = currentUser.copy(
                        rewardsPoints = currentUser.rewardsPoints + earnedPoints,
                        walletBalance = currentUser.walletBalance + (earnedPoints * 0.2) // Cashback to wallet
                    )
                    repository.updateUserProfile(updatedUser)
                }
            }
            true
        } else {
            false
        }
    }

    fun cancelActiveBooking(bookingId: Int) {
        viewModelScope.launch {
            val bookingList = bookingsState.value
            val booking = bookingList.find { it.id == bookingId }
            repository.cancelBooking(bookingId)
            
            if (booking != null) {
                // Add cancellation report
                repository.addCancellation(
                    CancellationReportEntity(
                        bookingId = bookingId,
                        serviceName = booking.serviceName,
                        price = booking.price,
                        reason = "User self-cancellation via portal",
                        refundStatus = "Processed"
                    )
                )
                // Add refund report
                repository.addRefund(
                    RefundReportEntity(
                        bookingId = bookingId,
                        serviceName = booking.serviceName,
                        refundAmount = booking.price,
                        transactionId = "REF-${(100000..999999).random()}",
                        status = "Success"
                    )
                )
                // Add audit log
                repository.addAuditLog(
                    action = "Booking Cancelled",
                    details = "Job #${bookingId} (${booking.serviceName}) cancelled. Refund of ${booking.price} initiated successfully."
                )
                
                // Refund wallet balance
                val currentUser = userState.value
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        walletBalance = currentUser.walletBalance + booking.price
                    )
                    repository.updateUserProfile(updatedUser)
                    repository.addAuditLog(
                        action = "Wallet Balance Refunded",
                        details = "Refunded ${booking.price} to user ${currentUser.name}'s wallet."
                    )
                }
            }
        }
    }

    fun adminAssignTechnician(bookingId: Int, technicianName: String, technicianPhone: String) {
        viewModelScope.launch {
            val bookingList = bookingsState.value
            val booking = bookingList.find { it.id == bookingId }
            if (booking != null) {
                val updatedBooking = booking.copy(
                    status = "Assigned",
                    technicianName = technicianName,
                    technicianPhone = technicianPhone
                )
                repository.db.bookingDao().insertBooking(updatedBooking)
                repository.addAuditLog(
                    action = "Specialist Dispatched",
                    details = "Assigned $technicianName to Job #${bookingId} (${booking.serviceName}). Status changed to Assigned."
                )
            }
        }
    }

    fun adminCompleteJob(bookingId: Int) {
        viewModelScope.launch {
            val bookingList = bookingsState.value
            val booking = bookingList.find { it.id == bookingId }
            if (booking != null) {
                repository.completeBooking(bookingId)
                repository.addAuditLog(
                    action = "Job Marked Completed",
                    details = "Job #${bookingId} (${booking.serviceName}) completed. Specialist: ${booking.technicianName}."
                )
            }
        }
    }

    fun adminCancelJob(bookingId: Int, reason: String) {
        viewModelScope.launch {
            val bookingList = bookingsState.value
            val booking = bookingList.find { it.id == bookingId }
            if (booking != null) {
                repository.cancelBooking(bookingId)
                repository.addCancellation(
                    CancellationReportEntity(
                        bookingId = bookingId,
                        serviceName = booking.serviceName,
                        price = booking.price,
                        reason = reason,
                        refundStatus = "Processed"
                    )
                )
                repository.addRefund(
                    RefundReportEntity(
                        bookingId = bookingId,
                        serviceName = booking.serviceName,
                        refundAmount = booking.price,
                        transactionId = "REF-${(100000..999999).random()}",
                        status = "Success"
                    )
                )
                repository.addAuditLog(
                    action = "Job Cancelled by Admin",
                    details = "Job #${bookingId} (${booking.serviceName}) cancelled by admin. Reason: $reason. Refund of ${booking.price} initiated."
                )
                
                // Refund wallet balance if booking belongs to active user
                val currentUser = userState.value
                if (currentUser != null) {
                    val updatedUser = currentUser.copy(
                        walletBalance = currentUser.walletBalance + booking.price
                    )
                    repository.updateUserProfile(updatedUser)
                }
            }
        }
    }

    fun addAuditLog(action: String, details: String) {
        viewModelScope.launch {
            repository.addAuditLog(action, details)
        }
    }

    fun updateBookingStatusInFirestore(bookingId: Int, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatusInFirestore(bookingId, status)
        }
    }

    fun buyProductWithInstallation(product: ProductEntity) {
        viewModelScope.launch {
            val currentUser = userState.value ?: return@launch
            if (currentUser.walletBalance >= product.price) {
                // Deduct cost from wallet balance
                val updatedUser = currentUser.copy(
                    walletBalance = currentUser.walletBalance - product.price,
                    rewardsPoints = currentUser.rewardsPoints + (product.price / 20).toInt()
                )
                repository.updateUserProfile(updatedUser)

                // Create automatic priority installation booking for product
                val verificationOtp = (1000..9999).random().toString()
                val matchedTech = ServiceData.technicians.last() // Hardware install specialist

                val installationBooking = BookingEntity(
                    category = "Product Installation",
                    serviceName = "${product.name} - Doorstep Professional Setup",
                    tier = "Premium Setup",
                    price = 0.0, // Included free with purchase!
                    date = "Tomorrow",
                    timeSlot = "10:00 AM - 12:00 PM",
                    address = currentUser.address,
                    status = "Assigned",
                    technicianName = matchedTech.name,
                    technicianPhone = matchedTech.phone,
                    technicianRating = matchedTech.rating,
                    otp = verificationOtp,
                    isEmergency = false,
                    serviceWarrantyDays = 180 // Extended product warranty
                )

                val bookingId = repository.addBooking(installationBooking).toInt()
                
                // Sync installation booking to Firestore
                repository.syncBookingToFirestore(installationBooking.copy(id = bookingId))
            }
        }
    }

    fun addFundsToWallet(amount: Double) {
        viewModelScope.launch {
            val currentUser = userState.value ?: return@launch
            val updatedUser = currentUser.copy(
                walletBalance = currentUser.walletBalance + amount
            )
            repository.updateUserProfile(updatedUser)
        }
    }

    fun upgradeMembershipTier(newTier: String) {
        viewModelScope.launch {
            val currentUser = userState.value ?: return@launch
            var cost = 0.0
            var pointsBonus = 0
            when (newTier) {
                "Gold" -> {
                    cost = 499.0
                    pointsBonus = 100
                }
                "Platinum" -> {
                    cost = 999.0
                    pointsBonus = 250
                }
            }
            if (currentUser.walletBalance >= cost) {
                val updatedUser = currentUser.copy(
                    walletBalance = currentUser.walletBalance - cost,
                    membershipTier = newTier,
                    hasActiveAMC = true,
                    amcType = "Premium",
                    rewardsPoints = currentUser.rewardsPoints + pointsBonus
                )
                repository.updateUserProfile(updatedUser)
            }
        }
    }

    fun saveAddress(newAddress: String) {
        viewModelScope.launch {
            val currentUser = userState.value ?: return@launch
            val updatedUser = currentUser.copy(address = newAddress)
            repository.updateUserProfile(updatedUser)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val currentUser = userState.value ?: return@launch
            val updatedUser = currentUser.copy(isBiometricEnabled = enabled)
            repository.updateUserProfile(updatedUser)
        }
    }

    fun addAddress(address: AddressEntity) {
        viewModelScope.launch {
            repository.addAddress(address)
        }
    }

    fun removeAddress(id: Int) {
        viewModelScope.launch {
            repository.removeAddress(id)
        }
    }

    fun addFavoriteTechnician(tech: FavoriteTechnicianEntity) {
        viewModelScope.launch {
            repository.addFavoriteTechnician(tech)
        }
    }

    fun removeFavoriteTechnician(id: Int) {
        viewModelScope.launch {
            repository.removeFavoriteTechnician(id)
        }
    }

    fun addSavedPayment(payment: SavedPaymentMethodEntity) {
        viewModelScope.launch {
            repository.addSavedPayment(payment)
        }
    }

    fun removeSavedPayment(id: Int) {
        viewModelScope.launch {
            repository.removeSavedPayment(id)
        }
    }

    fun addToWishlist(item: WishlistEntity) {
        viewModelScope.launch {
            repository.addToWishlist(item)
        }
    }

    fun removeFromWishlist(id: Int) {
        viewModelScope.launch {
            repository.removeFromWishlist(id)
        }
    }

    fun removeFromWishlistByName(name: String) {
        viewModelScope.launch {
            repository.removeFromWishlistByName(name)
        }
    }

    fun addReferral(ref: ReferralEntity) {
        viewModelScope.launch {
            repository.addReferral(ref)
        }
    }

    // --- Booking Flow State Management (Android native equivalent of Redux Slice) ---
    private val _bookingFlowState = MutableStateFlow(BookingFlowState())
    val bookingFlowState = _bookingFlowState.asStateFlow()

    fun updateBookingStep(step: Int) {
        _bookingFlowState.value = _bookingFlowState.value.copy(currentStep = step)
    }

    fun updateBookingTier(tier: String) {
        _bookingFlowState.value = _bookingFlowState.value.copy(selectedTier = tier)
    }

    fun updateBookingAddress(
        type: String? = null,
        flat: String? = null,
        building: String? = null,
        landmark: String? = null,
        address: String? = null
    ) {
        val current = _bookingFlowState.value
        _bookingFlowState.value = current.copy(
            addressType = type ?: current.addressType,
            flatNo = flat ?: current.flatNo,
            buildingName = building ?: current.buildingName,
            landmark = landmark ?: current.landmark,
            addressInput = address ?: current.addressInput
        )
    }

    fun updateBookingSchedule(date: String? = null, timeSlot: String? = null) {
        val current = _bookingFlowState.value
        _bookingFlowState.value = current.copy(
            selectedDate = date ?: current.selectedDate,
            selectedTimeSlot = timeSlot ?: current.selectedTimeSlot
        )
    }

    fun addBookingImage(imageUri: String) {
        val current = _bookingFlowState.value
        if (!current.uploadedImages.contains(imageUri)) {
            _bookingFlowState.value = current.copy(uploadedImages = current.uploadedImages + imageUri)
        }
    }

    fun removeBookingImage(imageUri: String) {
        val current = _bookingFlowState.value
        _bookingFlowState.value = current.copy(uploadedImages = current.uploadedImages - imageUri)
    }

    fun updateTechNotes(notes: String) {
        _bookingFlowState.value = _bookingFlowState.value.copy(techNotes = notes)
    }

    fun updateCouponInput(input: String) {
        _bookingFlowState.value = _bookingFlowState.value.copy(couponInput = input)
    }

    fun setAppliedCouponCode(code: String?) {
        _bookingFlowState.value = _bookingFlowState.value.copy(appliedCouponCode = code)
    }

    fun updatePaymentDetails(
        method: String? = null,
        upiProvider: String? = null,
        number: String? = null,
        holder: String? = null,
        expiry: String? = null,
        cvv: String? = null
    ) {
        val current = _bookingFlowState.value
        _bookingFlowState.value = current.copy(
            paymentMethod = method ?: current.paymentMethod,
            selectedUpiProvider = upiProvider ?: current.selectedUpiProvider,
            cardNumber = number ?: current.cardNumber,
            cardHolder = holder ?: current.cardHolder,
            cardExpiry = expiry ?: current.cardExpiry,
            cardCvv = cvv ?: current.cardCvv
        )
    }

    fun redeemPointsToWallet() {
        viewModelScope.launch {
            val currentUser = userState.value ?: return@launch
            val points = currentUser.rewardsPoints
            if (points > 0) {
                val valueInCash = points.toDouble()
                val updatedUser = currentUser.copy(
                    walletBalance = currentUser.walletBalance + valueInCash,
                    rewardsPoints = 0
                )
                repository.updateUserProfile(updatedUser)
            }
        }
    }

    fun updateUserProfileDetails(name: String, email: String, phone: String) {
        viewModelScope.launch {
            val currentUser = userState.value ?: return@launch
            val updatedUser = currentUser.copy(
                name = name,
                email = email,
                phone = phone
            )
            repository.updateUserProfile(updatedUser)
        }
    }

    fun rescheduleBooking(bookingId: Int, newDate: String, newTimeSlot: String) {
        viewModelScope.launch {
            val bookingList = bookingsState.value
            val booking = bookingList.find { it.id == bookingId } ?: return@launch
            val updated = booking.copy(date = newDate, timeSlot = newTimeSlot)
            repository.addBooking(updated)
        }
    }

    fun submitBookingReview(bookingId: Int, rating: Float) {
        viewModelScope.launch {
            val bookingList = bookingsState.value
            val booking = bookingList.find { it.id == bookingId } ?: return@launch
            val updated = booking.copy(technicianRating = rating)
            repository.addBooking(updated)
        }
    }

    fun resetBookingFlow() {
        _bookingFlowState.value = BookingFlowState()
    }
}

data class BookingFlowState(
    val currentStep: Int = 1,
    val selectedTier: String = "Premium",
    val addressType: String = "Home",
    val flatNo: String = "",
    val buildingName: String = "",
    val landmark: String = "",
    val addressInput: String = "",
    val selectedDate: String = "July 20, 2026",
    val selectedTimeSlot: String = "11:00 AM - 01:00 PM",
    val uploadedImages: List<String> = emptyList(),
    val techNotes: String = "",
    val couponInput: String = "",
    val appliedCouponCode: String? = null,
    val paymentMethod: String = "UPI",
    val selectedUpiProvider: String = "gpay",
    val cardNumber: String = "",
    val cardHolder: String = "",
    val cardExpiry: String = "",
    val cardCvv: String = ""
)
