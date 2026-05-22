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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.state.TransportRepository
import com.example.viewmodel.TransportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: TransportViewModel,
    onBackToHome: () -> Unit
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val allBookings by viewModel.allBookings.collectAsState()
    val activeBooking by viewModel.activeBooking.collectAsState()
    val allCoupons by viewModel.allCoupons.collectAsState()
    val announcements by viewModel.notifications.collectAsState()
    val commRate by viewModel.commissionRate.collectAsState()

    var activeTab by remember { mutableStateOf("analytics") } // "analytics", "drivers", "coupons", "settings"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AB Quick - Admin Dispatch Command", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Real-time control tower • Rajkot Fleet Hub", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(Color.Black, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "Fleet Size: ${allUsers.filter { it.role == UserRole.DRIVER }.size}",
                            color = Color(0xFFFACC15),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = activeTab == "analytics",
                    onClick = { activeTab = "analytics" },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analytics") },
                    label = { Text("Logistics", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == "drivers",
                    onClick = { activeTab = "drivers" },
                    icon = { Icon(Icons.Default.VerifiedUser, contentDescription = "KYC Hub") },
                    label = { Text("Approvals", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == "coupons",
                    onClick = { activeTab = "coupons" },
                    icon = { Icon(Icons.Default.Discount, contentDescription = "Coupons") },
                    label = { Text("Promos", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = activeTab == "settings",
                    onClick = { activeTab = "settings" },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
                    label = { Text("Settings", fontSize = 11.sp) }
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
                "analytics" -> {
                    AdminAnalyticsTab(
                        allUsers = allUsers,
                        allBookings = allBookings,
                        allAnnouncements = announcements,
                        activeBooking = activeBooking,
                        commission = commRate,
                        onDeleteUser = { id -> viewModel.suspendUser(id) },
                        onBroadcast = { t, m, tag -> viewModel.broadcastNotification(t, m, tag) }
                    )
                }
                "drivers" -> {
                    AdminDriversKycTab(
                        drivers = allUsers.filter { it.role == UserRole.DRIVER },
                        onApprove = { id -> viewModel.approveDriver(id) },
                        onReject = { id -> viewModel.suspendUser(id) }
                    )
                }
                "coupons" -> {
                    AdminCouponsTab(
                        coupons = allCoupons,
                        onCreateCoupon = { code, d, desc -> viewModel.createCoupon(code, d, desc) }
                    )
                }
                "settings" -> {
                    AdminSettingsTab(
                        commission = commRate,
                        onUpdateCommission = { viewModel.updateCommissionSetting(it) },
                        onAddCustomer = { name, ph, em -> viewModel.addCustomerProfile(name, ph, em) }
                    )
                }
            }
        }
    }
}

// GRAPHICAL ANALYTICS AND HISTORIC TIMELINE
@Composable
fun AdminAnalyticsTab(
    allUsers: List<UserProfile>,
    allBookings: List<RideBooking>,
    allAnnouncements: List<BroadcastNotification>,
    activeBooking: RideBooking?,
    commission: Int,
    onDeleteUser: (String) -> Unit,
    onBroadcast: (String, String, String) -> Unit
) {
    val scrollState = rememberScrollState()

    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastBody by remember { mutableStateOf("") }
    var broadcastMsgOk by remember { mutableStateOf("") }

    val totalBookingsAmount = allBookings.filter { it.status == RideStatus.COMPLETED }.sumOf { it.estimatedFare }
    val simulatedActiveLoadsCount = allBookings.size + (if (activeBooking != null) 1 else 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // SUMMARY ANALYTICS CARDS (Grid Equivalent)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Est. Gross Sales",
                value = "₹$totalBookingsAmount",
                color = Color(0xFFFACC15),
                onValue = "Rajkot HQ"
            )

            AdminSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Total Dispatches",
                value = "$simulatedActiveLoadsCount",
                color = Color(0xFF3B82F6),
                onValue = "Fleet Queue"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminSummaryCard(
                modifier = Modifier.weight(1f),
                title = "Verified Drivers",
                value = "${allUsers.filter { it.role == UserRole.DRIVER && it.isApproved }.size}",
                color = Color(0xFF22C55E),
                onValue = "GJ03 active"
            )

            AdminSummaryCard(
                modifier = Modifier.weight(1f),
                title = "System Customers",
                value = "${allUsers.filter { it.role == UserRole.CUSTOMER }.size}",
                color = Color.Black,
                onValue = "Registered"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GRAPHIC DISPATCH CANVAS
        Text("Weekly Dispatch Revenue Analytics", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Simulated platform Revenue index chart:", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    // Draw a continuous line representation
                    drawLine(Color(0xFFFACC15), start = androidx.compose.ui.geometry.Offset(50f, 180f), end = androidx.compose.ui.geometry.Offset(250f, 100f), strokeWidth = 8f)
                    drawLine(Color(0xFFFACC15), start = androidx.compose.ui.geometry.Offset(250f, 100f), end = androidx.compose.ui.geometry.Offset(450f, 140f), strokeWidth = 8f)
                    drawLine(Color(0xFFFACC15), start = androidx.compose.ui.geometry.Offset(450f, 140f), end = androidx.compose.ui.geometry.Offset(650f, 40f), strokeWidth = 8f)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ACTIVE MAP RADAR LIVE TRACKING DISPLAY
        if (activeBooking != null) {
            Text("📍 Operational Live Booking Tracker", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                border = BorderStroke(1.dp, Color(0xFFEAB308)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ACTIVE LIVE LOAD: ${activeBooking.id}", fontWeight = FontWeight.Bold, color = Color(0xFFB45309), fontSize = 13.sp)
                    Text("Customer: ${activeBooking.customerName} | Driver: ${activeBooking.driverName ?: "Awaiting match"}", fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Route: ${activeBooking.pickupName} -> ${activeBooking.dropName}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // BROADCAST SYSTEM ALERTS
        Text("Broadcaster Notification Center", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Send platform alert to Rajkot fleet & customer apps:", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = broadcastTitle,
                    onValueChange = { broadcastTitle = it },
                    placeholder = { Text("Emergency Weather / Surges Title") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                TextField(
                    value = broadcastBody,
                    onValueChange = { broadcastBody = it },
                    placeholder = { Text("Notice alert descriptions...") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (broadcastTitle.isNotBlank()) {
                            onBroadcast(broadcastTitle, broadcastBody, "Update")
                            broadcastMsgOk = "Broadcast Alert issued successfully!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Push Global Alert Notification", fontWeight = FontWeight.Bold)
                }

                if (broadcastMsgOk.isNotEmpty()) {
                    Text(broadcastMsgOk, color = Color(0xFF22C55E), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ACCOUNTS FLEET LIST ADMIN PANEL
        Text("Customer & Fleet Partner Accounts Database", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        allUsers.forEach { user ->
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
                            .size(36.dp)
                            .background(
                                if (user.role == UserRole.DRIVER) Color(0xFFFEF08A) else Color(0xFFDBEAFE),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (user.role == UserRole.DRIVER) "🏍️" else "👤")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("${user.name} [${user.role.name}]", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Ph: ${user.phone} | Balance: ₹${user.walletBalance.toInt()}", fontSize = 11.sp, color = Color.Gray)
                    }

                    // Suspend / Delete Account Action
                    IconButton(onClick = { onDeleteUser(user.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Block", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// INDIVIDUAL SUMMARY WIDGETS
@Composable
fun AdminSummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    color: Color,
    onValue: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color, modifier = Modifier.padding(vertical = 4.dp))
            Text(onValue, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

// VERIFICATION PIPELINE KYC WIDGETS
@Composable
fun AdminDriversKycTab(
    drivers: List<UserProfile>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    val pendingDrivers = drivers.filter { !it.isApproved }

    if (pendingDrivers.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Verified, "Verified", tint = Color(0xFF22C55E), modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("Perfect Operational Cleanliness!", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Zero pending driver partner approvals pending in Rajkot district.", fontSize = 11.sp, color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F9))
                .padding(16.dp)
        ) {
            item {
                Text("Pending Aadhaar/RC Verifications (${pendingDrivers.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(pendingDrivers) { dr ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(dr.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Ph: ${dr.phone} • Specified Vehicle: ${dr.vehicleType?.title ?: "Unassigned"}", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Documents copy specs
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF3F4F6))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("Aadhaar Plate UID: ${dr.documentAadhaar.ifBlank { "MOCKED-AADHAAR-COPY" }}", fontSize = 11.sp)
                                Text("Driving License (DL): ${dr.documentDL.ifBlank { "MOCKED-DL-GJ03-COPY" }}", fontSize = 11.sp)
                                Text("RC Copy registration: ${dr.documentRC.ifBlank { "MOCKED-RC-GJ03-REG" }}", fontSize = 11.sp)
                                Text("Assigned Number: ${dr.vehicleNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onReject(dr.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6), contentColor = Color.Black),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reject Copy")
                            }

                            Button(
                                onClick = { onApprove(dr.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Approve Partner")
                            }
                        }
                    }
                }
            }
        }
    }
}

// PROMO COUPONS CREATION BOARD
@Composable
fun AdminCouponsTab(
    coupons: List<PromoCoupon>,
    onCreateCoupon: (String, Int, String) -> Unit
) {
    var promoCode by remember { mutableStateOf("") }
    var discPercent by remember { mutableStateOf("15") }
    var promoDesc by remember { mutableStateOf("") }
    var promoMsgOk by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(16.dp)
    ) {
        item {
            Text("Create Promo coupon discount criteria", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = promoCode,
                        onValueChange = { promoCode = it.uppercase() },
                        placeholder = { Text("COUPON SPEC CODE (e.g. MONSOON20)") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    TextField(
                        value = discPercent,
                        onValueChange = { discPercent = it },
                        placeholder = { Text("Discount Deduct Percentage (e.g. 20)") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    TextField(
                        value = promoDesc,
                        onValueChange = { promoDesc = it },
                        placeholder = { Text("Coupon details description...") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (promoCode.isNotBlank() && discPercent.isNotBlank()) {
                                onCreateCoupon(promoCode, discPercent.toIntOrNull() ?: 15, promoDesc)
                                promoCode = ""
                                promoDesc = ""
                                promoMsgOk = "New Promo coupon registered successfully!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15), contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save New Coupon Criteria", fontWeight = FontWeight.Bold)
                    }

                    if (promoMsgOk.isNotEmpty()) {
                        Text(promoMsgOk, color = Color(0xFF22C55E), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Active System Promo Coupons Database", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(coupons) { coup ->
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
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(coup.code, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Discount Flat ${coup.discountPercent}% OFF", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(coup.description, fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// SYSTEM SETTINGS DIALS
@Composable
fun AdminSettingsTab(
    commission: Int,
    onUpdateCommission: (Int) -> Unit,
    onAddCustomer: (String, String, String) -> Unit
) {
    var updatedComm by remember { mutableStateOf(commission.toString()) }
    var custName by remember { mutableStateOf("") }
    var custPhone by remember { mutableStateOf("") }
    var custEmail by remember { mutableStateOf("") }
    var settingMsgOk by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Active Billing Commission configuration", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Default Plate Platform Commission (%) on completed trips",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = updatedComm,
                        onValueChange = { updatedComm = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val rate = updatedComm.toIntOrNull()
                            if (rate != null && rate in 0..100) {
                                onUpdateCommission(rate)
                                settingMsgOk = "Pricing commission configuration updated!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                    ) {
                        Text("Save Config")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Manual Customer Profile Builder", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = custName,
                    onValueChange = { custName = it },
                    placeholder = { Text("Customer full Name") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                TextField(
                    value = custPhone,
                    onValueChange = { custPhone = it },
                    placeholder = { Text("Contact Number (+91)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                TextField(
                    value = custEmail,
                    onValueChange = { custEmail = it },
                    placeholder = { Text("Corporate email copy...") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (custName.isNotBlank() && custPhone.isNotBlank()) {
                            onAddCustomer(custName, custPhone, custEmail)
                            custName = ""
                            custPhone = ""
                            custEmail = ""
                            settingMsgOk = "New customer details saved successfully!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15), contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register Manual Customer", fontWeight = FontWeight.Bold)
                }

                if (settingMsgOk.isNotEmpty()) {
                    Text(settingMsgOk, color = Color(0xFF22C55E), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
