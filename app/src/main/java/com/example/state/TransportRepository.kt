package com.example.state

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

object TransportRepository {

    // Active logged in user profile (dynamically switched for role demo)
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Real-time globally active booking
    private val _activeBooking = MutableStateFlow<RideBooking?>(null)
    val activeBooking: StateFlow<RideBooking?> = _activeBooking.asStateFlow()

    // Platform Commission (Default 20%)
    private val _commissionRate = MutableStateFlow(20)
    val commissionRate: StateFlow<Int> = _commissionRate.asStateFlow()

    // Rajkot Locations List for easy simulated selection
    val rajkotLocations = listOf(
        "Metoda GIDC, Sector-2",
        "Aji Vasahat Industrial Zone, Rajkot",
        "Yagnik Road, Near Shastri Maidan",
        "Kalavad Road, Kotecha Chowk",
        "Madhapar Chowk, Bypass Highway",
        "Gondal Road Transport Nagar",
        "150 Feet Ring Road, Raiya Chowk",
        "Kuvadva Road Warehouse",
        "Bhakti Nagar GIDC",
        "Rajkot Railway Station, Junction Bar"
    )

    // Users Database list
    private val _allUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    // Ride Booking History base
    private val _allBookings = MutableStateFlow<List<RideBooking>>(emptyList())
    val allBookings: StateFlow<List<RideBooking>> = _allBookings.asStateFlow()

    // Promo code coupons
    private val _allCoupons = MutableStateFlow<List<PromoCoupon>>(emptyList())
    val allCoupons: StateFlow<List<PromoCoupon>> = _allCoupons.asStateFlow()

    // System announcements / broadcasts
    private val _notifications = MutableStateFlow<List<BroadcastNotification>>(emptyList())
    val notifications: StateFlow<List<BroadcastNotification>> = _notifications.asStateFlow()

    init {
        resetData()
    }

    fun resetData() {
        val initialUsers = listOf(
            UserProfile("C001", "Meera Patel", "9265454753", "meera.patel@gmail.com", UserRole.CUSTOMER, 420.0, "ABQ50", 12),
            UserProfile("C002", "Hardik Solanki", "9909988776", "hardik@email.com", UserRole.CUSTOMER, 120.0, "SAVE10", 3),
            UserProfile(
                "D001", "Rajesh Gondaliya", "8765432109", "rajesh.g@email.com", UserRole.DRIVER, 680.0, "DRV100", 87,
                isApproved = true, vehicleType = VehicleType.MINI_TRUCK, vehicleNumber = "GJ 03 AZ 1524", driverRating = 4.9f, isOnline = true
            ),
            UserProfile(
                "D002", "Sanjay Makwana", "8899882211", "sanjay.m@email.com", UserRole.DRIVER, 150.0, "DRV50", 22,
                isApproved = true, vehicleType = VehicleType.BIKE_DELIVERY, vehicleNumber = "GJ 03 XY 8899", driverRating = 4.6f, isOnline = false
            ),
            UserProfile(
                "D003", "Vijay Jadeja", "8523697410", "vijay.j@email.com", UserRole.DRIVER, 0.0, "DRVNEW", 0,
                isApproved = false, vehicleType = VehicleType.TATA_ACE, vehicleNumber = "GJ 03 AB 7721", driverRating = 4.5f, isOnline = false
            ),
            UserProfile("A001", "Ajay Baraiya (Admin)", "9265454753", "ajaybaraiya011@gmail.com", UserRole.ADMIN, 150000.0, "ADMIN")
        )
        _allUsers.value = initialUsers

        // Auto logged in to Customer Meera Patel initially
        _currentUser.value = initialUsers[0]

        _allBookings.value = listOf(
            RideBooking("ABQ-1004", "C001", "Meera Patel", "9265454753", "D001", "Rajesh Gondaliya", "8765432109", 4.9f, VehicleType.MINI_TRUCK, "GJ 03 AZ 1524", "Metoda GIDC, Sector-2", "Gondal Road Transport Nagar", 8.4, 268, RideStatus.COMPLETED, PaymentMethod.UPI, "2026-05-21", "14:23"),
            RideBooking("ABQ-1003", "C001", "Meera Patel", "9265454753", "D002", "Sanjay Makwana", "8899882211", 4.6f, VehicleType.BIKE_DELIVERY, "GJ 03 XY 8899", "Yagnik Road, Near Shastri Maidan", "Kalavad Road, Kotecha Chowk", 3.2, 44, RideStatus.COMPLETED, PaymentMethod.WALLET, "2026-05-20", "11:05"),
            RideBooking("ABQ-1002", "C002", "Hardik Solanki", "9909988776", "D001", "Rajesh Gondaliya", "8765432109", 4.9f, VehicleType.MINI_TRUCK, "GJ 03 AZ 1524", "Aji Vasahat Industrial Zone, Rajkot", "Madhapar Chowk, Bypass Highway", 11.2, 330, RideStatus.COMPLETED, PaymentMethod.CASH, "2026-05-19", "09:40"),
            RideBooking("ABQ-1001", "C001", "Meera Patel", "9265454753", null, null, null, null, VehicleType.TATA_ACE, null, "Kuvadva Road Warehouse", "Bhakti Nagar GIDC", 14.0, 414, RideStatus.CANCELLED, PaymentMethod.CASH, "2026-05-18", "16:15")
        )

        _allCoupons.value = listOf(
            PromoCoupon("ABQUICK50", 15, "15% OFF on commercial load shifting up to ₹150"),
            PromoCoupon("RAJKOTGOODS", 10, "10% Flat promo for GIDC logistics workers"),
            PromoCoupon("FIRSTFREE", 100, "100% OFF on Bike Delivery up to ₹50")
        )

        _notifications.value = listOf(
            BroadcastNotification("N001", "Welcome to AB Quick Service", "Premium goods transport platform launched in Rajkot, Gujarat!", "General", "2026-05-22 10:00"),
            BroadcastNotification("N002", "Rainy Surge Alert in Rajkot", "Stay safe and earn 1.5x on Bike Deliveries between 4 PM to 8 PM.", "Incentive", "2026-05-22 15:30")
        )
    }

    // Role switcher
    fun switchUserRole(role: UserRole) {
        val matching = _allUsers.value.firstOrNull { it.role == role }
        if (matching != null) {
            _currentUser.value = matching
        } else {
            // Create brand new standard matching user if not exists
            val newProfile = when(role) {
                UserRole.CUSTOMER -> UserProfile("C-MOCKED", "Guest Customer", "9000000000", "guest.cust@abquick.com", role)
                UserRole.DRIVER -> UserProfile("D-MOCKED", "Guest Driver", "8000000000", "guest.drv@abquick.com", role, vehicleType = VehicleType.BIKE_DELIVERY, vehicleNumber = "GJ 03 QQ 1122", isApproved = true)
                UserRole.ADMIN -> UserProfile("A-MOCKED", "Guest Admin", "9265454753", "admin.guest@abquick.com", role)
            }
            _allUsers.update { it + newProfile }
            _currentUser.value = newProfile
        }
    }

    // Update logged in user state (e.g. online switch)
    fun updateProfile(id: String, block: (UserProfile) -> UserProfile) {
        _allUsers.update { list ->
            list.map { if (it.id == id) block(it) else it }
        }
        val current = _currentUser.value
        if (current != null && current.id == id) {
            _currentUser.value = block(current)
        }
    }

    // Booking Creation (Requested by Customer)
    fun requestRide(
        customerId: String,
        customerName: String,
        customerPhone: String,
        vehicleType: VehicleType,
        pickup: String,
        drop: String,
        distance: Double,
        fare: Int,
        payMethod: PaymentMethod
    ): RideBooking {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val now = Date()

        val id = "ABQ-${1000 + _allBookings.value.size + 1}"
        val newBooking = RideBooking(
            id = id,
            customerId = customerId,
            customerName = customerName,
            customerPhone = customerPhone,
            driverId = null,
            driverName = null,
            driverPhone = null,
            driverRating = null,
            vehicleType = vehicleType,
            vehicleNumber = null,
            pickupName = pickup,
            dropName = drop,
            distanceKm = distance,
            estimatedFare = fare,
            status = RideStatus.PENDING,
            paymentMethod = payMethod,
            dateString = dateFormat.format(now),
            timeString = timeFormat.format(now)
        )

        _activeBooking.value = newBooking
        return newBooking
    }

    // Accept Booking (Requested by Driver)
    fun acceptRide(bookingId: String, driverId: String): Boolean {
        val currentActive = _activeBooking.value ?: return false
        if (currentActive.id != bookingId || currentActive.status != RideStatus.PENDING) return false

        val driver = _allUsers.value.firstOrNull { it.id == driverId } ?: return false

        val updated = currentActive.copy(
            driverId = driver.id,
            driverName = driver.name,
            driverPhone = driver.phone,
            driverRating = driver.driverRating,
            vehicleNumber = driver.vehicleNumber,
            status = RideStatus.ACCEPTED
        )

        _activeBooking.value = updated
        return true
    }

    // Transit Steps
    fun advanceRideStatus(): RideStatus? {
        val curr = _activeBooking.value ?: return null
        val next = when(curr.status) {
            RideStatus.PENDING -> return null
            RideStatus.ACCEPTED -> RideStatus.ARRIVED_PICKUP
            RideStatus.ARRIVED_PICKUP -> RideStatus.ACTIVE_TRIP
            RideStatus.ACTIVE_TRIP -> RideStatus.COMPLETED
            RideStatus.COMPLETED, RideStatus.CANCELLED -> return null
        }

        val updated = curr.copy(status = next)
        _activeBooking.value = updated

        if (next == RideStatus.COMPLETED) {
            _allBookings.update { listOf(updated) + it }
            // Add balance details to driver, subtract if wallet from customer
            if (updated.driverId != null) {
                val commissionShare = updated.estimatedFare * (_commissionRate.value / 100.0)
                val netEarning = updated.estimatedFare - commissionShare
                updateProfile(updated.driverId) { d ->
                    d.copy(
                        walletBalance = d.walletBalance + netEarning,
                        totalTrips = d.totalTrips + 1
                    )
                }
            }
            updateProfile(updated.customerId) { c ->
                val newBal = if (updated.paymentMethod == PaymentMethod.WALLET) {
                    c.walletBalance - updated.estimatedFare
                } else {
                    c.walletBalance
                }
                c.copy(
                    walletBalance = newBal,
                    totalTrips = c.totalTrips + 1
                )
            }
            _activeBooking.value = null // clear active slots
        }

        return next
    }

    // Cancel active order
    fun cancelActiveRide() {
        val curr = _activeBooking.value ?: return
        val updated = curr.copy(status = RideStatus.CANCELLED)
        _allBookings.update { listOf(updated) + it }
        _activeBooking.value = null
    }

    // Settings Modification (Admin)
    fun updateCommission(rate: Int) {
        if (rate in 0..100) {
            _commissionRate.value = rate
        }
    }

    // Add Driver Approval / Action (Admin)
    fun verifyDriver(driverId: String, approve: Boolean) {
        updateProfile(driverId) { d ->
            d.copy(isApproved = approve)
        }
    }

    // Create custom promotion code (Admin)
    fun addPromoCoupon(coupon: PromoCoupon) {
        _allCoupons.update { it + coupon }
    }

    // Create announcement
    fun sendBroadcast(title: String, desc: String, tag: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val id = "B-${_notifications.value.size + 1}"
        val post = BroadcastNotification(id, title, desc, tag, dateFormat.format(Date()))
        _notifications.update { listOf(post) + it }
    }

    // Add new user directly (Admin)
    fun addUser(user: UserProfile) {
        _allUsers.update { it + user }
    }

    // Delete a user
    fun removeUser(userId: String) {
        _allUsers.update { list -> list.filter { it.id != userId } }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = null
        }
    }
}
