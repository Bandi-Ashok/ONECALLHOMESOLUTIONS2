package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val membershipTier: String = "Silver", // Silver, Gold, Platinum
    val walletBalance: Double = 0.0,
    val rewardsPoints: Int = 0,
    val hasActiveAMC: Boolean = false,
    val amcType: String = "None", // None, Basic, Premium, Corporate
    val isBiometricEnabled: Boolean = false
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val serviceName: String,
    val tier: String = "Standard", // Standard, Premium, Deep
    val price: Double,
    val date: String,
    val timeSlot: String,
    val address: String,
    val status: String = "Pending", // Pending, Assigned, In Progress, Completed, Cancelled
    val technicianName: String = "",
    val technicianPhone: String = "",
    val technicianRating: Float = 0f,
    val otp: String = "",
    val isEmergency: Boolean = false,
    val serviceWarrantyDays: Int = 7,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val price: Double,
    val description: String,
    val warranty: String,
    val isAvailable: Boolean = true
)

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int = 0, // 0 for support chat
    val sender: String, // User, Support, Technician, AI
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val addressLine: String,
    val tag: String
)

@Entity(tableName = "favorite_technicians")
data class FavoriteTechnicianEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val phone: String,
    val rating: Float
)

@Entity(tableName = "saved_payments")
data class SavedPaymentMethodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardOrUpi: String,
    val type: String
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: String,
    val serviceName: String,
    val price: Double
)

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val friendName: String,
    val status: String,
    val rewardAmount: Double
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val adminUser: String = "Admin (Super)"
)

@Entity(tableName = "cancellation_reports")
data class CancellationReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val serviceName: String,
    val price: Double,
    val reason: String,
    val refundStatus: String = "Pending", // Pending, Processed
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "refund_reports")
data class RefundReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookingId: Int,
    val serviceName: String,
    val refundAmount: Double,
    val transactionId: String,
    val status: String = "Success", // Pending, Success, Failed
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "amc_reports")
data class AmcReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val planType: String, // Basic, Premium, Corporate
    val price: Double,
    val startDate: String,
    val expiryDate: String,
    val status: String = "Active" // Active, Expired
)

// --- Domain Models & Mock Data definitions ---

data class ServiceCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String, // Matching icon identifier
    val items: List<ServiceItem> = emptyList()
)

data class ServiceItem(
    val name: String,
    val basePrice: Double,
    val description: String,
    val duration: String,
    val features: List<String> = emptyList()
)

data class Technician(
    val name: String,
    val rating: Float,
    val completedJobs: Int,
    val specialty: String,
    val phone: String,
    val verified: Boolean = true
)

object ServiceData {
    val categories = listOf(
        ServiceCategory(
            id = "cleaning",
            name = "Cleaning",
            description = "Deep cleaning, bathroom & kitchen sanitization, and sofa details.",
            iconName = "cleaning",
            items = listOf(
                ServiceItem("Full House Deep Cleaning", 2499.0, "Complete sanitization of all rooms, bathrooms, and kitchen.", "4-5 hours", listOf("Floor scrubbing", "Cabinet cleaning", "Glass wiping", "Bathroom disinfection")),
                ServiceItem("Bathroom Deep Cleaning", 499.0, "Thorough tile descaling, toilet scrubbing, and stain removal.", "1-2 hours", listOf("Wall descaling", "Fitting polishing", "Siphon disinfection")),
                ServiceItem("Kitchen Deep Cleaning", 1299.0, "Oil & grease removal, chimney, exhaust fan, and countertop scrubbing.", "2-3 hours", listOf("Chimney filter cleaning", "Stove scrubbing", "Cabinet exterior"))
            )
        ),
        ServiceCategory(
            id = "painting",
            name = "Painting",
            description = "Wall putty work, luxury interior and exterior coats, wall designs.",
            iconName = "painting",
            items = listOf(
                ServiceItem("Interior 1BHK Wall Painting", 8500.0, "Complete interior painting with putty and premium paint.", "2 days", listOf("Wall preparation", "Putty coat", "2 coats of premium paint", "Clean up")),
                ServiceItem("Single Designer Accent Wall", 2499.0, "Modern texture, ombre, or pattern stencils for your master wall.", "4 hours", listOf("Texture design", "Stencil application", "Custom colour consultation"))
            )
        ),
        ServiceCategory(
            id = "electrical",
            name = "Electrical",
            description = "Fan repairs, socket fittings, safe home wiring, and installations.",
            iconName = "electrical",
            items = listOf(
                ServiceItem("Ceiling Fan Repair & Install", 199.0, "Mounting new ceiling fan or replacing regulator/capacitor.", "30 mins", listOf("Wiring check", "Stator test", "Silent operation check")),
                ServiceItem("Switchboard Install & Upgrade", 299.0, "Replacing worn sockets or installing a modern modular board.", "45 mins", listOf("Conduit verification", "Modular switches", "Safety fuse test"))
            )
        ),
        ServiceCategory(
            id = "plumbing",
            name = "Plumbing",
            description = "Fix dripping taps, installations, pipeline unclogging, geysers.",
            iconName = "plumbing",
            items = listOf(
                ServiceItem("Tap & Faucet Repair/Fitting", 149.0, "Fixing leaks, replacing washers or fitting a new faucet.", "30 mins", listOf("Leak diagnosis", "Gasket replacement", "Operational check")),
                ServiceItem("Water Heater Geyser Service", 399.0, "Element scaling clean up and checking for electrical leakage.", "1 hour", listOf("Safety valve check", "Anode check", "Descaling"))
            )
        ),
        ServiceCategory(
            id = "ac",
            name = "AC",
            description = "AC installation, cooling gas refills, filter foam wash, and jet repair.",
            iconName = "ac",
            items = listOf(
                ServiceItem("AC Foam & Jet Service", 599.0, "Deep filter cleaning with dynamic foam and high-pressure jet.", "45 mins", listOf("Foam soak", "Coil jet-wash", "Drain tray flush")),
                ServiceItem("Air Conditioner Gas Charging", 1999.0, "Refilling refrigerant gas and checking for leakage.", "1 hour", listOf("Leakage patch-up", "Vacuum run", "Gas top-up"))
            )
        ),
        ServiceCategory(
            id = "construction",
            name = "Construction",
            description = "Home extensions, masonry works, wall construction, tile laying, and cement work.",
            iconName = "construction",
            items = listOf(
                ServiceItem("Masonry & Wall Construction", 4500.0, "Custom cement partition wall construction with clean red brick masonry.", "1 day", listOf("Brick selection", "Mortar preparation", "Alignment verification")),
                ServiceItem("Tile Installation & Repair", 1200.0, "Replacing cracked tiles or installing new ceramic/vitrified flooring.", "3 hours", listOf("Surface leveling", "Adhesive application", "Grouting"))
            )
        ),
        ServiceCategory(
            id = "interior",
            name = "Interior",
            description = "Modular kitchens, custom wardrobe designs, ceiling styling, and 3D interior plans.",
            iconName = "interior",
            items = listOf(
                ServiceItem("Modular Kitchen Consultation", 1500.0, "Site measurement, layout planning, and custom 3D kitchen styling.", "2 hours", listOf("Space assessment", "Material selection", "Cost estimation")),
                ServiceItem("Custom Wardrobe Designing", 2500.0, "Creating detailed blueprints for modern slide/hinge bedroom wardrobes.", "3 hours", listOf("Storage analysis", "Design catalog briefing", "Hardware consultation"))
            )
        ),
        ServiceCategory(
            id = "pestcontrol",
            name = "Pest Control",
            description = "Gel cockroach control, anti-termite wood injections, bedbug heat.",
            iconName = "pestcontrol",
            items = listOf(
                ServiceItem("Cockroach & Ant Gel Treatment", 699.0, "Odourless, food-safe herbal gel application in kitchen & cabinets.", "1 hour", listOf("Gel injection", "Moisture barrier check", "3-month protection guarantee")),
                ServiceItem("Anti-Termite Treatment", 2499.0, "Drill-fill-seal barrier injection for walls and wooden cabinets.", "3 hours", listOf("Chemical barrier", "Cabinet spraying", "1-year warranty"))
            )
        ),
        ServiceCategory(
            id = "roofing",
            name = "Roofing",
            description = "Shed setups, sheet replacement, leak proofing, and metal roof structure welding.",
            iconName = "roofing",
            items = listOf(
                ServiceItem("Metal Sheet Roof Repair", 1800.0, "Fixing loose screws, replacing rusted sheets, and applying roof sealants.", "2 hours", listOf("Safety harness check", "Rust treatment", "Waterproof coating")),
                ServiceItem("New Roof Shed Installation", 15000.0, "Welded frame structure with fiber/metal sheets setup.", "2 days", listOf("Welded supports", "Shed sheet fixing", "Drainage alignment"))
            )
        ),
        ServiceCategory(
            id = "laundry",
            name = "Laundry",
            description = "Premium dry cleaning, wash & iron, stain removal, and steam press.",
            iconName = "laundry",
            items = listOf(
                ServiceItem("5kg Wash & Steam Iron", 349.0, "Everyday clothes wash, tumble dry, and premium steam pressing.", "24 hours", listOf("Fabric separation", "Eco-friendly soap", "Steam iron & fold")),
                ServiceItem("Premium Suit Dry Cleaning", 499.0, "Gentle chemical cleaning, stain treatment, and protective hanger wrapping.", "2 days", listOf("Stain diagnosis", "Dry chemical bath", "Protective coat bag"))
            )
        ),
        ServiceCategory(
            id = "gardening",
            name = "Gardening",
            description = "Lawn mowing, organic soil top-up, trimming, and nursery setup.",
            iconName = "gardening",
            items = listOf(
                ServiceItem("Garden Lawn Mowing & Trimming", 599.0, "Mowing overgrown lawns and clean trimming of bushes/edges.", "1.5 hours", listOf("Mower setup", "Borders trimming", "Green waste collection")),
                ServiceItem("Organic Soil & Manure Enrichment", 799.0, "Adding vermicompost, organic soil mixture, and plant nutrition booster.", "1 hour", listOf("Soil aeration", "Manure mixing", "Moisture check"))
            )
        ),
        ServiceCategory(
            id = "beauty",
            name = "Beauty",
            description = "Salon at home, dynamic haircuts, facial treatments, and pedicure.",
            iconName = "beauty",
            items = listOf(
                ServiceItem("Premium Facial & Clean Up", 1199.0, "Exfoliating wash, steam, blackhead removal, and massage.", "1 hour", listOf("Hydration therapy", "Organic cream scrub", "De-tan pack")),
                ServiceItem("Men's Haircut & Grooming", 399.0, "Professional dynamic haircut, beard styling, and hair wash.", "45 mins", listOf("Style consultation", "Precision trim", "Shampoo wash"))
            )
        ),
        ServiceCategory(
            id = "vehiclecare",
            name = "Vehicle Care",
            description = "Doorstep car detailing, waterless foam wash, and exterior polishing.",
            iconName = "vehiclecare",
            items = listOf(
                ServiceItem("SUV Eco Foam Wash", 799.0, "Exterior pressure wash, vacuum interior, tire shine, and dashboard polish.", "1.5 hours", listOf("Foam soak", "Underbody pressure jet", "Perfume spray")),
                ServiceItem("Car Exterior Wax Polish", 1499.0, "High gloss wax finish with orbital buffer machine to remove swirls.", "2 hours", listOf("Claybar treatment", "Wax application", "Microfiber buffing"))
            )
        ),
        ServiceCategory(
            id = "healthcare",
            name = "Healthcare",
            description = "Physiotherapy, doorstep blood test sample pickup, and elderly care.",
            iconName = "healthcare",
            items = listOf(
                ServiceItem("Physiotherapy Session (At Home)", 899.0, "Certified therapist visit for joint pains, post-surgery, or posture alignment.", "1 hour", listOf("Pain assessment", "Therapeutic exercises", "Muscle stimulation")),
                ServiceItem("Complete Health Screening Test", 1299.0, "Doorstep blood and urine sample pickup with online reports in 12 hours.", "20 mins", listOf("Phlebotomist visit", "NABL certified lab", "Free doctor consultation"))
            )
        ),
        ServiceCategory(
            id = "travel",
            name = "Travel",
            description = "Airport cabs, customized holiday itinerary plans, and visa assistance.",
            iconName = "travel",
            items = listOf(
                ServiceItem("Outstation Taxi Booking Guide", 299.0, "Securing trusted drivers and verified dynamic outstation cabs.", "30 mins", listOf("Verification", "Driver mapping", "Route advice")),
                ServiceItem("Customized Holiday Planner", 999.0, "Detailed hotel, route, and sight-seeing plans made by experts.", "1 day", listOf("Itinerary breakdown", "Budget optimization", "Activity bookings"))
            )
        ),
        ServiceCategory(
            id = "emergency",
            name = "Emergency",
            description = "24/7 immediate assistance for fire hazard, major leaks, and secure locks.",
            iconName = "emergency",
            items = listOf(
                ServiceItem("SOS Major Pipeline Burst", 1499.0, "Immediate dispatch within 30 mins to clamp high-pressure water pipeline bursts.", "1 hour", listOf("Quick shutoff", "Pipe welding/clamping", "Surge prevention")),
                ServiceItem("Emergency Lock Out Resolution", 999.0, "Emergency master-key locksmith dispatch to open jammed safety doors.", "45 mins", listOf("Lock bypass", "No door damage tech", "New backup key"))
            )
        ),
        ServiceCategory(
            id = "amc",
            name = "AMC",
            description = "Annual Maintenance Contracts for electrical, plumbing, and appliances.",
            iconName = "amc",
            items = listOf(
                ServiceItem("Platinum Home AMC Cover", 4999.0, "1-Year unlimited plumbing, electrical, and AC inspections with zero diagnostics fee.", "1 year", listOf("4 quarterly checkups", "Priority scheduling", "Free minor parts")),
                ServiceItem("Basic Appliance AMC Plan", 1999.0, "Covers servicing of 1 AC, 1 Water Purifier, and 1 Washing Machine.", "1 year", listOf("Bi-annual tune-up", "Free filter cleaning", "24-hour backup support"))
            )
        ),
        ServiceCategory(
            id = "inspection",
            name = "Inspection",
            description = "Thermal imaging scan, structural checks, and electrical audits.",
            iconName = "inspection",
            items = listOf(
                ServiceItem("Comprehensive Pre-Buy Home Audit", 3499.0, "Detailed checking of wall dampness, concrete strength, wiring faults.", "3 hours", listOf("Thermal camera leak search", "Circuit insulation test", "Structural integrity")),
                ServiceItem("Electrical Fire Safety Audit", 1200.0, "Detailed check of earthing, load calculation, MCB functionality.", "1.5 hours", listOf("Earthing voltage check", "Overload thermal scan", "Certificate issue"))
            )
        ),
        ServiceCategory(
            id = "security",
            name = "Security",
            description = "CCTV camera setup, smart door locks, and alarm systems.",
            iconName = "security",
            items = listOf(
                ServiceItem("Smart Door Lock Installation", 999.0, "Installation of digital passcode, biometric, and RF locks.", "1.5 hours", listOf("Precision cutting", "Bezel mounting", "App integration")),
                ServiceItem("4-Channel CCTV Camera Setup", 2999.0, "Mounting, network cabling, and DVR/Remote viewing config.", "3 hours", listOf("4 IP Cameras", "Cat6 cabling", "Mobile live stream config"))
            )
        ),
        ServiceCategory(
            id = "smarthome",
            name = "Smart Home",
            description = "Smart lighting, voice assistant hubs, and automatic curtains setup.",
            iconName = "smarthome",
            items = listOf(
                ServiceItem("Voice Assistant Hub Setup", 799.0, "Integrating smart bulbs, plugs, and speakers with Alexa/Google Home.", "1 hour", listOf("Hub installation", "Device pairing", "Custom voice routines")),
                ServiceItem("Smart RGB Ambient lighting", 1299.0, "Concealed LED strip setup with app control and music sync.", "1.5 hours", listOf("Profile alignment", "Adapter wiring", "App sync configuration"))
            )
        ),
        ServiceCategory(
            id = "furniture",
            name = "Furniture",
            description = "Bed assembly, wardrobe repairs, wall shelves installation, and sofa repair.",
            iconName = "furniture",
            items = listOf(
                ServiceItem("King Size Bed Assembly", 999.0, "Flatpack unboxing, hydraulic lift fitting, and secure frame setup.", "2 hours", listOf("Unboxing & sorting", "Hydraulic gas lift setup", "Level testing")),
                ServiceItem("Modular Wall Shelf Mounting", 399.0, "Drilling and precise spirit-level mounting of floating shelves.", "45 mins", listOf("Wall structural scan", "Precision drilling", "Load capacity test"))
            )
        ),
        ServiceCategory(
            id = "packers",
            name = "Packers",
            description = "Safe local house relocation and fragile items wrapping.",
            iconName = "packers",
            items = listOf(
                ServiceItem("Local Shifting (1BHK)", 4999.0, "Loading, 10km transport, and unloading with 2 helpers.", "4 hours", listOf("Bubble wrap protection", "Compact cargo loader", "Safe transit insurance"))
            )
        ),
        ServiceCategory(
            id = "property",
            name = "Property Management",
            description = "Tenant verification, rent collection audits, and regular safety inspections.",
            iconName = "property",
            items = listOf(
                ServiceItem("Tenant Aadhaar Verification", 499.0, "Aadhaar and police verification run through national registry.", "1 day", listOf("Document pickup", "Background run", "Verification report")),
                ServiceItem("Bi-Annual Property Health Check", 1999.0, "Visiting rented property to capture video of structural status.", "1.5 hours", listOf("HD video capture", "Leakage scanning", "Maintenance check list"))
            )
        ),
        ServiceCategory(
            id = "solar",
            name = "Solar",
            description = "Solar panel cleaning, inverter synchronization, and solar grid installations.",
            iconName = "solar",
            items = listOf(
                ServiceItem("Solar Panel Cleaning & Checkup", 1499.0, "Cleaning dust build-ups and optimizing power conversion efficiency.", "1.5 hours", listOf("Demineralized water wash", "Inverter checking", "Voltage efficiency report")),
                ServiceItem("Residential Solar Feasibility Audit", 999.0, "Measuring roof shadows, solar exposure, and calculating load.", "1 hour", listOf("Drone mapping survey", "Shadow modeling", "Sizing calculation"))
            )
        ),
        ServiceCategory(
            id = "waterproofing",
            name = "Waterproofing",
            description = "Roof and terrace leak proofing, damp walls injection treatment.",
            iconName = "waterproofing",
            items = listOf(
                ServiceItem("Monsoon Wall Damp Proofing", 1999.0, "Injection of waterproof chemical barrier to block capillary water rise.", "3 hours", listOf("Chemical drill-fill", "Damp curing", "Anti-fungal base coat")),
                ServiceItem("Terrace Leakage Rubber Coat", 7999.0, "3-layer elastomeric polymer coating to permanently seal cracks.", "1 day", listOf("Pressure crack routing", "Primer layer", "3 coats elastomeric polymer"))
            )
        )
    )

    // Helper to search for services
    fun searchServices(query: String): List<Pair<ServiceCategory, ServiceItem>> {
        val results = mutableListOf<Pair<ServiceCategory, ServiceItem>>()
        categories.forEach { category ->
            category.items.forEach { item ->
                if (item.name.contains(query, ignoreCase = true) || 
                    item.description.contains(query, ignoreCase = true) || 
                    category.name.contains(query, ignoreCase = true)) {
                    results.add(Pair(category, item))
                }
            }
        }
        return results
    }

    val technicians = listOf(
        Technician("Arun Kumar", 4.9f, 342, "Plumbing & Sanitization", "+91 98765 43210"),
        Technician("Sanjay Sharma", 4.8f, 218, "HVAC & AC Systems", "+91 87654 32109"),
        Technician("Vikram Singh", 4.7f, 412, "Electrical Wiring", "+91 76543 21098"),
        Technician("Rajesh Prasad", 4.9f, 156, "Deep Cleaning Experts", "+91 65432 10987"),
        Technician("Deepak Das", 4.6f, 89, "Smart Locks & Automation", "+91 54321 09876")
    )

    val products = listOf(
        ProductEntity(1, "Premium High-Speed Ceiling Fan (1200mm)", "Electrical", 2199.0, "3-Blade energy saver double ball-bearing fan with custom installation option.", "2 Years", true),
        ProductEntity(2, "SecureTouch Smart Biometric Door Lock", "Security", 6499.0, "Fingerprint, passcode, and physical key entry with emergency USB charging.", "1 Year", true),
        ProductEntity(3, "Modular 6-Socket Switch Board with USB", "Electrical", 599.0, "Flame-retardant polycarbonate board with built-in surge protector and dual USB ports.", "3 Years", true),
        ProductEntity(4, "Premium Water Softener Shower Head Filter", "Plumbing", 1299.0, "15-stage filtration removes hard water, chlorine, and heavy metals.", "6 Months", true),
        ProductEntity(5, "Wireless Outdoor Night-Vision Security Camera", "Security", 3499.0, "Full HD 1080p camera with active deterrence spotlight, dual-way audio and local storage.", "1.5 Years", true)
    )
}

data class TransactionModel(
    val title: String,
    val amount: String,
    val date: String,
    val timestamp: Long,
    val status: String,
    val type: String // Debit or Credit
)

