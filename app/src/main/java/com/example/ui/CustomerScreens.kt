package com.example.ui

import com.example.ui.theme.*
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.state.TransportRepository
import com.example.viewmodel.TransportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboard(
    viewModel: TransportViewModel,
    onBackToHome: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val activeBooking by viewModel.activeBooking.collectAsState()
    val historyBookings by viewModel.allBookings.collectAsState()
    val availableCoupons by viewModel.allCoupons.collectAsState()

    var activeTab by remember { mutableStateOf("home") } // "home", "history", "profile"
    var isAuthFlowCompleted by remember { mutableStateOf(true) } // Pre-auth toggled for immediate demo

    val user = currentUser
    // Mock Customer Login screen if logged out
    if (user == null || !isAuthFlowCompleted) {
        CustomerAuthScreen(
            onAuthSuccess = { name, phone ->
                TransportRepository.addUser(
                    UserProfile("C_USER", name, phone, "${name.lowercase().replace(" ", "")}@gmail.com", UserRole.CUSTOMER, 350.0, "ABQ50")
                )
                TransportRepository.switchUserRole(UserRole.CUSTOMER)
                isAuthFlowCompleted = true
            },
            onBackToHome = onBackToHome
        )
    } else {
        Scaffold(
            topBar = {
                Surface(
                    shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
                    color = YellowPrimary,
                    shadowElevation = 8.dp
                ) {
                    TopAppBar(
                        title = {
                            Column {
                                Text(
                                    "AB QUICK",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = BrandBlack,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    "DELIVERING CARGO IN RAJKOT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandBlack.copy(alpha = 0.6f)
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackToHome) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = BrandBlack
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = BrandBlack
                        ),
                        actions = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .background(BrandBlack, RoundedCornerShape(50.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    "Wallet: ₹${user.walletBalance.toInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = YellowPrimary
                                )
                            }
                        }
                    )
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    NavigationBarItem(
                        selected = activeTab == "home",
                        onClick = { activeTab = "home" },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlack,
                            selectedTextColor = BrandBlack,
                            indicatorColor = YellowPrimary,
                            unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
                            unselectedTextColor = Color.Gray.copy(alpha = 0.6f)
                        ),
                        icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Book") },
                        label = { Text("Book Loader", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = activeTab == "history",
                        onClick = { activeTab = "history" },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlack,
                            selectedTextColor = BrandBlack,
                            indicatorColor = YellowPrimary,
                            unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
                            unselectedTextColor = Color.Gray.copy(alpha = 0.6f)
                        ),
                        icon = { Icon(Icons.Default.History, contentDescription = "History") },
                        label = { Text("My Trips", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = activeTab == "profile",
                        onClick = { activeTab = "profile" },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandBlack,
                            selectedTextColor = BrandBlack,
                            indicatorColor = YellowPrimary,
                            unselectedIconColor = Color.Gray.copy(alpha = 0.6f),
                            unselectedTextColor = Color.Gray.copy(alpha = 0.6f)
                        ),
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile", fontSize = 11.sp) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    "home" -> {
                        if (activeBooking != null) {
                            CustomerBookingTracker(
                                activeBooking = activeBooking!!,
                                onCancelRide = { viewModel.cancelCurrentRide() },
                                onProceedActiveRide = { viewModel.proceedActiveRide() }
                            )
                        } else {
                            CustomerBookingForm(viewModel = viewModel)
                        }
                    }
                    "history" -> {
                        CustomerTripsList(historyBookings = historyBookings.filter { it.customerId == user.id })
                    }
                    "profile" -> {
                        CustomerProfileTab(
                            profile = user,
                            coupons = availableCoupons,
                            onLogout = {
                                isAuthFlowCompleted = false
                                TransportRepository.switchUserRole(UserRole.ADMIN) // fallback to admin
                            }
                        )
                    }
                }
            }
        }
    }
}

// SMS OTP LOGIN SCREEN
@Composable
fun CustomerAuthScreen(
    onAuthSuccess: (String, String) -> Unit,
    onBackToHome: () -> Unit
) {
    var mobileNum by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var otpField by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.LocalShipping,
            contentDescription = "Logo",
            tint = YellowPrimary,
            modifier = Modifier.size(72.dp)
        )
        Text(
            text = "AB QUICK TRANSPORT",
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            fontSize = 22.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "Instant Commercial vehicle dispatch",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = if (isOtpSent) "Enter 4-Digit OTP" else "Customer Verification",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (!isOtpSent) {
                    TextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        placeholder = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.LightGray)
                    )

                    TextField(
                        value = mobileNum,
                        onValueChange = { if (it.length <= 10) mobileNum = it },
                        placeholder = { Text("Mobile Phone Number (+91)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.LightGray)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (mobileNum.length == 10 && customerName.isNotBlank()) {
                                isOtpSent = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = BrandBlack)
                    ) {
                        Text("Send OTP Verification", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "An OTP code has been sent to +91 $mobileNum (Simulated: '4521')",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    TextField(
                        value = otpField,
                        onValueChange = { if (it.length <= 4) otpField = it },
                        placeholder = { Text("Enter OTP Code") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (otpField == "4521" || otpField.length == 4) {
                                onAuthSuccess(customerName, mobileNum)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = BrandBlack)
                    ) {
                        Text("Verify & Start Booking", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBackToHome) {
            Text("Cancel, Back to Website", color = Color.White)
        }
    }
}

// MAIN BOOKING FORM
@Composable
fun CustomerBookingForm(viewModel: TransportViewModel) {
    val locations = TransportRepository.rajkotLocations
    var pickupLoc by remember { mutableStateOf(locations[0]) }
    var dropLoc by remember { mutableStateOf(locations[1]) }
    var selectedVehicle by remember { mutableStateOf(VehicleType.BIKE_DELIVERY) }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.CASH) }
    var promoInput by remember { mutableStateOf("") }
    var promoAppliedMsg by remember { mutableStateOf("") }

    var isDropdownPickupExpanded by remember { mutableStateOf(false) }
    var isDropdownDropExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp)
    ) {
        item {
            // STEP 1: ROUTING DETAILS
            Text("1. Load Routes (Rajkot Areas)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Pickup box
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = "Pickup", tint = Color(0xFF3B82F6))
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            Text(
                                text = "Pickup: $pickupLoc",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isDropdownPickupExpanded = true }
                                    .padding(vertical = 8.dp)
                            )
                            DropdownMenu(
                                expanded = isDropdownPickupExpanded,
                                onDismissRequest = { isDropdownPickupExpanded = false }
                            ) {
                                locations.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(loc) },
                                        onClick = {
                                            pickupLoc = loc
                                            isDropdownPickupExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFE5E7EB))

                    // Drop box
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Drop", tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            Text(
                                text = "Dropoff: $dropLoc",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isDropdownDropExpanded = true }
                                    .padding(vertical = 8.dp)
                            )
                            DropdownMenu(
                                expanded = isDropdownDropExpanded,
                                onDismissRequest = { isDropdownDropExpanded = false }
                            ) {
                                locations.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(loc) },
                                        onClick = {
                                            dropLoc = loc
                                            isDropdownDropExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // MAP PREVIEW PRECISE MOCKUP FROM THE HTML SPECIFICATION
            Text("📍 Operational Live Fleet Map", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE2E8F0)) // Tailwind bg-gray-200
                    .border(BorderStroke(1.dp, Color(0xFFCBD5E1)), RoundedCornerShape(24.dp))
            ) {
                // Simulated roads Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw horizontal main road
                    drawLine(
                        color = Color.White,
                        start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
                        strokeWidth = 16f
                    )
                    // Draw vertical crossing paths
                    drawLine(
                        color = Color.White,
                        start = androidx.compose.ui.geometry.Offset(size.width * 0.35f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width * 0.35f, size.height),
                        strokeWidth = 12f
                    )
                    drawLine(
                        color = Color.White,
                        start = androidx.compose.ui.geometry.Offset(size.width * 0.7f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width * 0.7f, size.height),
                        strokeWidth = 12f
                    )
                }
                
                // Active Driver Markers in Yellow Gold with heavy black border
                // Driver 1 (Bike 🛵)
                Box(
                    modifier = Modifier
                        .offset(x = 60.dp, y = 40.dp)
                        .size(32.dp)
                        .background(YellowPrimary, RoundedCornerShape(50.dp))
                        .border(BorderStroke(2.dp, BrandBlack), RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛵", fontSize = 14.sp)
                }
                
                // Driver 2 (Tata Ace 🚙)
                Box(
                    modifier = Modifier
                        .offset(x = 190.dp, y = 100.dp)
                        .size(32.dp)
                        .background(YellowPrimary, RoundedCornerShape(50.dp))
                        .border(BorderStroke(2.dp, BrandBlack), RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚙", fontSize = 14.sp)
                }
                
                // Driver 3 (Mini Truck 🚛)
                Box(
                    modifier = Modifier
                        .offset(x = 280.dp, y = 60.dp)
                        .size(32.dp)
                        .background(YellowPrimary, RoundedCornerShape(50.dp))
                        .border(BorderStroke(2.dp, BrandBlack), RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚛", fontSize = 14.sp)
                }

                // Map controls +/- overlay in bottom corner
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .shadow(2.dp, RoundedCornerShape(6.dp))
                            .clickable { /* Zoom in */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandBlack)
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .shadow(2.dp, RoundedCornerShape(6.dp))
                            .clickable { /* Zoom out */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandBlack)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("2. Choose Fleet Category Size", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // FLEET LIST
        items(VehicleType.values()) { vehicle ->
            val isSelected = selectedVehicle == vehicle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { selectedVehicle = vehicle },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) YellowPrimary.copy(alpha = 0.15f) else Color.White
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) YellowPrimary else Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(vehicle.iconEmoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = vehicle.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "${vehicle.capacity} • ${vehicle.description}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Est. ₹${vehicle.baseFare}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isSelected) Color(0xFFEAB308) else Color.Black
                        )
                        Text(
                            text = "Base",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("3. Coupon & Promo Codes", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = promoInput,
                        onValueChange = { promoInput = it },
                        placeholder = { Text("ABQUICK50 / FIRSTFREE") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (promoInput.isNotBlank()) {
                                promoAppliedMsg = "Code Applied! Discount verified."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                    ) {
                        Text("Apply", fontSize = 12.sp)
                    }
                }
                if (promoAppliedMsg.isNotEmpty()) {
                    Text(
                        text = promoAppliedMsg,
                        color = Color(0xFF22C55E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("4. Payment Options", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            // Payment method selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedPayment == method
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPayment = method },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandBlack else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = method.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) YellowPrimary else Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.createRideRequest(
                        vehicleType = selectedVehicle,
                        pickup = pickupLoc,
                        drop = dropLoc,
                        paymentMethod = selectedPayment,
                        appliedPromoCode = promoInput
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlack, contentColor = YellowPrimary),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Instant Booking", fontWeight = FontWeight.Black, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text("→", fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Driver matches instantly. Fares are dynamic based on Rajkot market demand.",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// LIVE BOOKING RADAR AND DISPATCH TRACKER CODES
@Composable
fun CustomerBookingTracker(
    activeBooking: RideBooking,
    onCancelRide: () -> Unit,
    onProceedActiveRide: () -> Unit
) {
    val curr = activeBooking

    var openChatDialog by remember { mutableStateOf(false) }
    var chatText by remember { mutableStateOf("") }
    val chatHistory = remember { mutableStateListOf("Hello, Rajesh here. I am arriving at Metoda Warehouse GIDC.", "Please keep the load items ready.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
    ) {
        // Upper section: Maps simulation canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFE2E8F0))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background grid representation for map roads
                drawLine(Color.White, start = androidx.compose.ui.geometry.Offset(0f, size.height/2), end = androidx.compose.ui.geometry.Offset(size.width, size.height/2), strokeWidth = 12f)
                drawLine(Color.White, start = androidx.compose.ui.geometry.Offset(size.width/2, 0f), end = androidx.compose.ui.geometry.Offset(size.width/2, size.height), strokeWidth = 12f)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = BrandBlack.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Simulated Route distance: ${curr.distanceKm} km \nStatus: GJ RTO dispatch queue online",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Pickup dropping pins indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF3B82F6), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("Pickup 📍\n${curr.pickupName.take(15)}..", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    // Loading dispatch emoji animating back and forth
                    Text("🚚💨", fontSize = 32.sp)

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEF4444), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("Dropoff 🏁\n${curr.dropName.take(15)}..", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Lower Section: Driver Details card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(YellowPrimary, RoundedCornerShape(50.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👨‍✈️", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = curr.driverName ?: "Ab Quick Allocator...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = curr.vehicleNumber ?: "Matching nearest driver Partner...",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Call support action
                    IconButton(
                        onClick = { /* Simulated dialler launched */ },
                        modifier = Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(50.dp))
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF22C55E))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { openChatDialog = true },
                        modifier = Modifier.background(Color(0xFFF3F4F6), RoundedCornerShape(50.dp))
                    ) {
                        Icon(Icons.Default.Message, contentDescription = "Chat", tint = Color(0xFF3B82F6))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Estimated Fare", fontSize = 10.sp, color = Color.Gray)
                        Text("₹${curr.estimatedFare}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    Column {
                        Text("Payment Type", fontSize = 10.sp, color = Color.Gray)
                        Text(curr.paymentMethod.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    Column {
                        Text("OTP Code", fontSize = 10.sp, color = Color.Gray)
                        Box(modifier = Modifier.background(YellowPrimary.copy(alpha = 0.15f)).padding(horizontal = 6.dp)) {
                            Text(curr.otp, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = BrandBlack)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ride state indicator badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(YellowPrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Current Status: ${curr.status.name} • Sim is running.",
                        fontWeight = FontWeight.Bold,
                        color = BrandBlack,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cancel Booking
                    Button(
                        onClick = onCancelRide,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel Ride")
                    }

                    // Advance Simulator button (For easy demo purposes!)
                    Button(
                        onClick = onProceedActiveRide,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlack, contentColor = YellowPrimary),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Advance Step ⏭️")
                    }
                }
            }
        }
    }

    // Chat dialog box mockup
    if (openChatDialog) {
        AlertDialog(
            onDismissRequest = { openChatDialog = false },
            title = { Text("Driver Partner Chat Box") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFF3F4F6))
                            .padding(8.dp)
                    ) {
                        Column {
                            chatHistory.forEach { msg ->
                                Text("• $msg", fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = chatText,
                        onValueChange = { chatText = it },
                        placeholder = { Text("Message...") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (chatText.isNotBlank()) {
                        chatHistory.add("Customer: $chatText")
                        chatText = ""
                    }
                }) {
                    Text("Send Message")
                }
            },
            dismissButton = {
                TextButton(onClick = { openChatDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

// INVOICES AND PREVIOUS TRIPS
@Composable
fun CustomerTripsList(historyBookings: List<RideBooking>) {
    if (historyBookings.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📭 No operational trip records found", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("Book a commercial load to get started.", fontSize = 11.sp, color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F9))
                .padding(16.dp)
        ) {
            items(historyBookings) { r ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Job ID: ${r.id}",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 13.sp
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (r.status == RideStatus.COMPLETED) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = r.status.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (r.status == RideStatus.COMPLETED) Color(0xFF15803D) else Color(0xFFB91C1C)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("📅 Date: ${r.dateString} at ${r.timeString}", fontSize = 11.sp, color = Color.Gray)
                        Text("🚛 Loader Size: ${r.vehicleType.title}", fontSize = 13.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = "", tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(r.pickupName, fontSize = 11.sp, color = Color.DarkGray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Default.LocationOn, contentDescription = "", tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(r.dropName, fontSize = 11.sp, color = Color.DarkGray)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Method: ${r.paymentMethod.name}", fontSize = 11.sp, color = Color.Gray)
                            Text("Invoice: ₹${r.estimatedFare}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

// CUSTOMER SETTINGS & PROMO COUNCILS
@Composable
fun CustomerProfileTab(
    profile: UserProfile,
    coupons: List<PromoCoupon>,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BrandBlack),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Signed In as Customer Profile", color = YellowPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(profile.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text("Phone: ${profile.phone} • Email: ${profile.email}", color = Color.LightGray, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Corporate Balance", color = Color.Gray, fontSize = 10.sp)
                        Text("₹${profile.walletBalance.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Button(
                        onClick = { /* Add money action simulated */ },
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = BrandBlack),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Add Money", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Active Rajkot Coupons & Referral Matches", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        coupons.forEach { coup ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(YellowPrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = coup.code,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlack,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Flat ${coup.discountPercent}% OFF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(coup.description, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Switch Profile / Log out", fontWeight = FontWeight.Bold)
        }
    }
}
