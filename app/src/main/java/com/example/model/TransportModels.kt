package com.example.model

import java.io.Serializable

enum class VehicleType(
    val title: String,
    val baseFare: Int,
    val perKmRate: Int,
    val description: String,
    val capacity: String,
    val iconEmoji: String
) {
    BIKE_DELIVERY("Bike Delivery", 25, 6, "Two-wheel rapid parcel delivery", "Max 15 kg", "🏍️"),
    MINI_TRUCK("Mini Truck", 150, 14, "Ideal for small room shifting", "Max 750 kg", "🚚"),
    TATA_ACE("Tata Ace", 190, 16, "Perfect for wholesale supplies", "Max 900 kg", "🚛"),
    PICKUP("Pickup 8ft", 250, 18, "For open loading & bulky items", "Max 1.5 Tons", "📦"),
    TEMPO("Tempo High", 350, 20, "For retail distribution & furniture", "Max 2.5 Tons", "🚙"),
    TRUCK("Box Truck", 600, 25, "Heavy goods industrial transport", "Max 5 Tons", "🚜")
}

enum class UserRole {
    CUSTOMER,
    DRIVER,
    ADMIN
}

enum class RideStatus {
    PENDING,        // Waiting matching / booking created
    ACCEPTED,       // Driver accepted, heading to pickup
    ARRIVED_PICKUP, // Driver has arrived at pickup location
    ACTIVE_TRIP,    // Customer picked up, driving to destination
    COMPLETED,      // Arrived at drop & payment done
    CANCELLED       // Cancelled by client/driver
}

enum class PaymentMethod {
    CASH,
    UPI,
    CARD,
    WALLET
}

data class UserProfile(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val role: UserRole,
    val walletBalance: Double = 150.0,
    val referralCode: String = "",
    val totalTrips: Int = 0,
    val isApproved: Boolean = true,
    // Driver specifications
    val vehicleType: VehicleType? = null,
    val vehicleNumber: String = "",
    val driverRating: Float = 4.8f,
    val isOnline: Boolean = false,
    val documentAadhaar: String = "",
    val documentDL: String = "",
    val documentRC: String = "",
    val vehiclePhoto: String = ""
) : Serializable

data class RideBooking(
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val driverId: String?,
    val driverName: String?,
    val driverPhone: String?,
    val driverRating: Float?,
    val vehicleType: VehicleType,
    val vehicleNumber: String?,
    val pickupName: String,
    val dropName: String,
    val distanceKm: Double,
    val estimatedFare: Int,
    val status: RideStatus,
    val paymentMethod: PaymentMethod,
    val dateString: String,
    val timeString: String,
    val otp: String = "4521"
) : Serializable

data class PromoCoupon(
    val code: String,
    val discountPercent: Int,
    val description: String,
    val isAvailable: Boolean = true
) : Serializable

data class BroadcastNotification(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "General", "Incentive", "Update"
    val timestamp: String
) : Serializable

data class ChartDataPoint(
    val label: String,
    val value: Float
) : Serializable
