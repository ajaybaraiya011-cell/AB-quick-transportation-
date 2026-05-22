package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.state.TransportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class TransportViewModel : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = TransportRepository.currentUser
    val activeBooking: StateFlow<RideBooking?> = TransportRepository.activeBooking
    val allUsers: StateFlow<List<UserProfile>> = TransportRepository.allUsers
    val allBookings: StateFlow<List<RideBooking>> = TransportRepository.allBookings
    val allCoupons: StateFlow<List<PromoCoupon>> = TransportRepository.allCoupons
    val notifications: StateFlow<List<BroadcastNotification>> = TransportRepository.notifications
    val commissionRate: StateFlow<Int> = TransportRepository.commissionRate

    // Select active role demo (Customer / Driver / Admin)
    fun switchRole(role: UserRole) {
        TransportRepository.switchUserRole(role)
    }

    // Toggle driver online status
    fun toggleDriverOnline() {
        val user = currentUser.value ?: return
        if (user.role == UserRole.DRIVER) {
            val nextState = !user.isOnline
            TransportRepository.updateProfile(user.id) { profile ->
                profile.copy(isOnline = nextState)
            }
        }
    }

    // Book commercial vehicle
    fun createRideRequest(
        vehicleType: VehicleType,
        pickup: String,
        drop: String,
        paymentMethod: PaymentMethod,
        appliedPromoCode: String = ""
    ): RideBooking? {
        val customer = currentUser.value ?: return null
        val distance = generateSimulatedDistance()
        var baseFare = vehicleType.baseFare + (distance * vehicleType.perKmRate).toInt()
        
        // Apply promotional code if matching
        if (appliedPromoCode.isNotEmpty()) {
            val promo = allCoupons.value.firstOrNull { it.code.equals(appliedPromoCode, ignoreCase = true) }
            if (promo != null) {
                val discountAmount = (baseFare * (promo.discountPercent / 100.0)).toInt()
                baseFare = (baseFare - discountAmount).coerceAtLeast(10)
            }
        }

        return TransportRepository.requestRide(
            customerId = customer.id,
            customerName = customer.name,
            customerPhone = customer.phone,
            vehicleType = vehicleType,
            pickup = pickup,
            drop = drop,
            distance = distance,
            fare = baseFare,
            payMethod = paymentMethod
        )
    }

    // Driver accepts ride request
    fun acceptRide(bookingId: String, driverId: String): Boolean {
        return TransportRepository.acceptRide(bookingId, driverId)
    }

    // Advance booking step
    fun proceedActiveRide(): RideStatus? {
        return TransportRepository.advanceRideStatus()
    }

    // Cancel current booking
    fun cancelCurrentRide() {
        TransportRepository.cancelActiveRide()
    }

    // Simulated driver coordinates generator for UI Maps
    private fun generateSimulatedDistance(): Double {
        // Distance in Rajkot city usually ranges from 1.5 to 15 kilometers
        val num = Random.nextDouble(1.8, 12.5)
        return Math.round(num * 10.0) / 10.0
    }

    // Simulated driver documents registration
    fun submitDriverRegistration(
        name: String,
        phone: String,
        aadhaar: String,
        dl: String,
        rc: String,
        vehicleType: VehicleType,
        vehicleNum: String
    ) {
        val driverId = "D00${allUsers.value.filter { it.role == UserRole.DRIVER }.size + 1}"
        val newDriver = UserProfile(
            id = driverId,
            name = name,
            phone = phone,
            email = "${name.lowercase().replace(" ", "")}@abquick.com",
            role = UserRole.DRIVER,
            walletBalance = 0.0,
            referralCode = "JOIN50",
            totalTrips = 0,
            isApproved = false, // Set to false to trigger admin approval card!
            vehicleType = vehicleType,
            vehicleNumber = vehicleNum,
            isOnline = false,
            documentAadhaar = aadhaar,
            documentDL = dl,
            documentRC = rc,
            vehiclePhoto = "photo_uploaded.png"
        )
        TransportRepository.addUser(newDriver)
    }

    // Verification controls (Admin)
    fun approveDriver(driverId: String) {
        TransportRepository.verifyDriver(driverId, approve = true)
        // Broadcast congrats notification to drivers
        TransportRepository.sendBroadcast(
            "Driver Approved: ${allUsers.value.firstOrNull { it.id == driverId }?.name ?: "Partner"}",
            "Welcome GJ-03 logistics partner to the active fleet of AB Quick Transportation Service!",
            "Update"
        )
    }

    // Reject, Block, or suspend user (Admin)
    fun suspendUser(userId: String) {
        TransportRepository.removeUser(userId)
    }

    // Modify commission configuration (Admin)
    fun updateCommissionSetting(rate: Int) {
        TransportRepository.updateCommission(rate)
    }

    // Dispatches coupon promo (Admin)
    fun createCoupon(code: String, percent: Int, desc: String) {
        TransportRepository.addPromoCoupon(PromoCoupon(code, percent, desc))
    }

    // Launch instant notification push (Admin)
    fun broadcastNotification(title: String, message: String, tag: String) {
        TransportRepository.sendBroadcast(title, message, tag)
    }

    // Adds a newly requested customer profile manually (Admin)
    fun addCustomerProfile(name: String, phone: String, email: String) {
        val cId = "C00${allUsers.value.filter { it.role == UserRole.CUSTOMER }.size + 1}"
        val profile = UserProfile(
            id = cId,
            name = name,
            phone = phone,
            email = email,
            role = UserRole.CUSTOMER,
            walletBalance = 150.0
        )
        TransportRepository.addUser(profile)
    }
}
