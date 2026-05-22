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
import kotlin.random.Random

// MULTILINGUAL DICTIONARY FOR DRIVER LOCALIZATION
enum class DriverLanguage {
    ENGLISH, HINDI, GUJARATI
}

class DriverTranslations(val lang: DriverLanguage) {
    val onlineStatusOnline = when(lang) {
        DriverLanguage.ENGLISH -> "You are Online"
        DriverLanguage.HINDI -> "आप ऑनलाइन हैं"
        DriverLanguage.GUJARATI -> "તમે ઓનલાઇન છો"
    }
    val onlineStatusOffline = when(lang) {
        DriverLanguage.ENGLISH -> "You are Offline"
        DriverLanguage.HINDI -> "आप ऑफलाइन हैं"
        DriverLanguage.GUJARATI -> "તમે ઓફલાઇન છો"
    }
    val startDuty = when(lang) {
        DriverLanguage.ENGLISH -> "Go Online for Bookings"
        DriverLanguage.HINDI -> "बुकिंग के लिए ऑनलाइन जाएं"
        DriverLanguage.GUJARATI -> "બુકિંગ શરૂ કરવા ઓનલાઇન થાઓ"
    }
    val todayEarnings = when(lang) {
        DriverLanguage.ENGLISH -> "Today's Earnings"
        DriverLanguage.HINDI -> "आज की कुल कमाई"
        DriverLanguage.GUJARATI -> "આજની કમાણી"
    }
    val completedTrips = when(lang) {
        DriverLanguage.ENGLISH -> "Trips Done"
        DriverLanguage.HINDI -> "पूरे किए गए सफर"
        DriverLanguage.GUJARATI -> "કુલ આંટા ફેરા"
    }
    val walletBal = when(lang) {
        DriverLanguage.ENGLISH -> "Wallet Balance"
        DriverLanguage.HINDI -> "बटुए की राशि (वॉलेट)"
        DriverLanguage.GUJARATI -> "વોલેટ બેલેન્સ"
    }
    val withdraw = when(lang) {
        DriverLanguage.ENGLISH -> "Withdraw Earnings"
        DriverLanguage.HINDI -> "बैंक में पैसे ट्रांसफर करें"
        DriverLanguage.GUJARATI -> "કમાણી ઉપાડો"
    }
    val languageSelect = when(lang) {
        DriverLanguage.ENGLISH -> "Select Interface Language"
        DriverLanguage.HINDI -> "ऐप की भाषा चुनें"
        DriverLanguage.GUJARATI -> "એપ્લિકેશન ભાષા બદલો"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboard(
    viewModel: TransportViewModel,
    onBackToHome: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val activeBooking by viewModel.activeBooking.collectAsState()
    val allHistory by viewModel.allBookings.collectAsState()

    var activeTab by remember { mutableStateOf("home") } // "home", "earnings", "profile"
    var isRegistered by remember { mutableStateOf(true) } // Ready for driver partners
    var selectedLanguage by remember { mutableStateOf(DriverLanguage.ENGLISH) }

    val trans = remember(selectedLanguage) { DriverTranslations(selectedLanguage) }

    val user = currentUser
    if (user == null || user.role != UserRole.DRIVER) {
        // Fallback or force switch
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = { viewModel.switchRole(UserRole.DRIVER) }) {
                Text("Switch system to Driver Mode")
            }
        }
    } else if (!user.isApproved) {
        // Pending approval screen
        DriverPendingApprovalScreen(profile = user, onBackToHome = onBackToHome)
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("AB Quick Loader Partner", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "Fleet: ${user.vehicleType?.title ?: "Unassigned"} • ${user.vehicleNumber}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackToHome) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    actions = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        if (user.isOnline) Color(0xFF22C55E) else Color(0xFFEF4444),
                                        RoundedCornerShape(50.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (user.isOnline) trans.onlineStatusOnline else trans.onlineStatusOffline,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.Black,
                    contentColor = YellowPrimary
                ) {
                    NavigationBarItem(
                        selected = activeTab == "home",
                        onClick = { activeTab = "home" },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = YellowPrimary,
                            indicatorColor = YellowPrimary,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        ),
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                        label = { Text("Dispatches", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = activeTab == "earnings",
                        onClick = { activeTab = "earnings" },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = YellowPrimary,
                            indicatorColor = YellowPrimary,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        ),
                        icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Earnings") },
                        label = { Text("Earnings", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = activeTab == "profile",
                        onClick = { activeTab = "profile" },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = YellowPrimary,
                            indicatorColor = YellowPrimary,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        ),
                        icon = { Icon(Icons.Default.AccountBox, contentDescription = "Profile") },
                        label = { Text("Account", fontSize = 11.sp) }
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
                        DriverBookingJobsTab(
                            userProfile = user,
                            activeBooking = activeBooking,
                            trans = trans,
                            languages = DriverLanguage.values().toList(),
                            currentLang = selectedLanguage,
                            onLanguageChange = { selectedLanguage = it },
                            toggleOnline = { viewModel.toggleDriverOnline() },
                            onSimulateBooking = {
                                TransportRepository.requestRide(
                                    customerId = "C-SIM",
                                    customerName = "Priya Rajpat",
                                    customerPhone = "9988776655",
                                    vehicleType = user?.vehicleType ?: VehicleType.MINI_TRUCK,
                                    pickup = TransportRepository.rajkotLocations[2],
                                    drop = TransportRepository.rajkotLocations[3],
                                    distance = 5.6,
                                    fare = 210,
                                    payMethod = PaymentMethod.UPI
                                )
                            },
                            onProceedActiveRide = { viewModel.proceedActiveRide() },
                            onAcceptRide = { id -> viewModel.acceptRide(id, user.id) },
                            onDeclineRide = { viewModel.cancelCurrentRide() }
                        )
                    }
                    "earnings" -> {
                        DriverEarningsTab(
                            profile = user,
                            history = allHistory.filter { it.driverId == user.id },
                            trans = trans
                        )
                    }
                    "profile" -> {
                        DriverRegistrationForm(
                            viewModel = viewModel,
                            currentUser = user,
                            onBackToHome = onBackToHome
                        )
                    }
                }
            }
        }
    }
}

// DRIVER PENDING APPROVAL WARNING
@Composable
fun DriverPendingApprovalScreen(
    profile: UserProfile,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Gavel,
            contentDescription = "Pending",
            tint = Color(0xFFFACC15),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Fleet registration documents pending review and approval",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Account: ${profile.name} (${profile.phone})\nOur Rajkot verification hub expects up to 24 hours to approve Aadhaar/RC license copies.",
            color = Color.Gray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Verification Milestones:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, "Uploaded", tint = Color(0xFF22C55E), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Aadhaar Card, DL & RC Uploaded", color = Color.LightGray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HourglassBottom, "Wait", tint = Color(0xFFFACC15), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Central Admin Fleet Panel Review (Pending)", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBackToHome,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15), contentColor = Color.Black)
        ) {
            Text("Back on Main Portal", fontWeight = FontWeight.Bold)
        }
    }
}

// INCOMING DISPATCH OR ACTIVE DRIVING
@Composable
fun DriverBookingJobsTab(
    userProfile: UserProfile?,
    activeBooking: RideBooking?,
    trans: DriverTranslations,
    languages: List<DriverLanguage>,
    currentLang: DriverLanguage,
    onLanguageChange: (DriverLanguage) -> Unit,
    toggleOnline: () -> Unit,
    onSimulateBooking: () -> Unit,
    onProceedActiveRide: () -> Unit,
    onAcceptRide: (String) -> Unit,
    onDeclineRide: () -> Unit
) {
    val isOnline = userProfile?.isOnline ?: false

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        // MULTILINGUAL SELECTOR ROW
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(trans.languageSelect, fontSize = 11.sp, color = Color.LightGray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { lang ->
                            val isSelected = currentLang == lang
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onLanguageChange(lang) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFFACC15) else Color(0xFF1E1E1E)
                                )
                            ) {
                                Text(
                                    text = when(lang) {
                                        DriverLanguage.ENGLISH -> "English"
                                        DriverLanguage.HINDI -> "हिंदी"
                                        DriverLanguage.GUJARATI -> "ગુજરાતી"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // ON/OFF DUTY CONTROL PANEL
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, Color(0xFFFACC15).copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isOnline) trans.onlineStatusOnline else trans.onlineStatusOffline,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isOnline) "Ready for Rajkot commercial loads" else trans.startDuty,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isOnline,
                        onCheckedChange = { toggleOnline() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFFFACC15)
                        )
                    )
                }
            }
        }

        if (activeBooking != null) {
            // DRIVER HAS ACTIVE JOB ASSIGNED
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF22C55E).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                "DISPATCH ORDER RUNNING: ${activeBooking.status.name}",
                                color = Color(0xFF22C55E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF1E1E1E), RoundedCornerShape(50.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(activeBooking.customerName, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Mobile ID: +91 ${activeBooking.customerPhone}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)

                        // Sizing and address specs
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, "From", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(activeBooking.pickupName, fontSize = 12.sp, color = Color.White)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Default.LocationOn, "To", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(activeBooking.dropName, fontSize = 12.sp, color = Color.White)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Earn payout (Net 80%)", fontSize = 10.sp, color = Color.Gray)
                                Text("₹${(activeBooking.estimatedFare * 0.8).toInt()}", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFFFACC15))
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("Verification OTP", fontSize = 10.sp, color = Color.Gray)
                                Text(activeBooking.otp, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ACTIVE DISPATCH MILESTONE BUTTONS
                        if (activeBooking.status == RideStatus.PENDING) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onAcceptRide(activeBooking.id) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("🟢 Accept Match", fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = onDeclineRide,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Decline", fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            val nextStepTitle = when(activeBooking.status) {
                                RideStatus.ACCEPTED -> "🚩 Tap Arrived at Pickup"
                                RideStatus.ARRIVED_PICKUP -> "📦 Tap Start Active Trip"
                                RideStatus.ACTIVE_TRIP -> "🏁 Tap Complete Drop off"
                                else -> "Job Finished"
                            }

                            Button(
                                onClick = onProceedActiveRide,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15), contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(nextStepTitle, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Emergency SOS trigger
                        Button(
                            onClick = { /* Security loop triggered */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "SOS")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("🚨 EMERGENCY SOS PANIC BUTTON", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // IDLE DRIVER: NO BOOKINGS ASSIGNED
            item {
                if (isOnline) {
                    // Visual radar pulse simulator
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = Color(0xFFFACC15))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Scanning for GIDC Metoda & Kuvadva load bookings...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Nearby driver partners active online: GJ03 fleet",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Trigger simulated matching popup! (Interactive demo)
                            Button(
                                onClick = onSimulateBooking,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                            ) {
                                Text("🔔 Simulate Incoming Booking")
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.PowerOff, "Offline", tint = Color.Gray, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            trans.startDuty,
                            color = Color.LightGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// EARNINGS TRACKER AND WITHDRAWAL DASHBOARDS
@Composable
fun DriverEarningsTab(
    profile: UserProfile,
    history: List<RideBooking>,
    trans: DriverTranslations
) {
    var isWithdrawalSuccessMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, Color(0xFFFACC15).copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(trans.walletBal, color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("₹${profile.walletBalance.toInt()}", color = Color(0xFFFACC15), fontWeight = FontWeight.Black, fontSize = 32.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(trans.completedTrips, color = Color.Gray, fontSize = 10.sp)
                        Text("${profile.totalTrips} Jobs Completed", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (profile.walletBalance > 0) {
                                isWithdrawalSuccessMsg = "₹${profile.walletBalance.toInt()} transferred to verified Bank Account: State Bank of India (GJ03) successfully!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(trans.withdraw, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (isWithdrawalSuccessMsg.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = isWithdrawalSuccessMsg,
                        color = Color(0xFF22C55E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Weekly Dispatch Revenue Graph", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(8.dp))

        // Custom canvas graph drawing represent weekly earnings progress
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2E2E)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    // Drawing bar graph blocks representing last 5 days dispatch revenue
                    drawRect(Color(0xFFFACC15), size = androidx.compose.ui.geometry.Size(40f, 180f), topLeft = androidx.compose.ui.geometry.Offset(50f, size.height - 180f))
                    drawRect(Color(0xFFFACC15), size = androidx.compose.ui.geometry.Size(40f, 150f), topLeft = androidx.compose.ui.geometry.Offset(170f, size.height - 150f))
                    drawRect(Color(0xFFFACC15), size = androidx.compose.ui.geometry.Size(40f, 240f), topLeft = androidx.compose.ui.geometry.Offset(290f, size.height - 240f))
                    drawRect(Color(0xFFFACC15), size = androidx.compose.ui.geometry.Size(40f, 210f), topLeft = androidx.compose.ui.geometry.Offset(410f, size.height - 210f))
                    drawRect(Color(0xFFFACC15), size = androidx.compose.ui.geometry.Size(40f, 270f), topLeft = androidx.compose.ui.geometry.Offset(530f, size.height - 270f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("Mon", color = Color.Gray, fontSize = 10.sp)
                    Text("Tue", color = Color.Gray, fontSize = 10.sp)
                    Text("Wed", color = Color.Gray, fontSize = 10.sp)
                    Text("Thu", color = Color.Gray, fontSize = 10.sp)
                    Text("Fri", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("My Completed Bookings History", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(8.dp))

        if (history.isEmpty()) {
            Text("No bookings performed this cycle.", fontSize = 11.sp, color = Color.Gray)
        } else {
            history.forEach { job ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(text = "💰", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(job.customerName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text("${job.pickupName.take(12)} -> ${job.dropName.take(12)}", fontSize = 11.sp, color = Color.Gray)
                        }
                        Text("+₹${(job.estimatedFare * 0.8).toInt()}", fontWeight = FontWeight.ExtraBold, color = Color(0xFF22C55E))
                    }
                }
            }
        }
    }
}

// DRIVER REGISTRATION OR FLEET UPDATE FORM
@Composable
fun DriverRegistrationForm(
    viewModel: TransportViewModel,
    currentUser: UserProfile,
    onBackToHome: () -> Unit
) {
    var name by remember { mutableStateOf(currentUser.name) }
    var phone by remember { mutableStateOf(currentUser.phone) }
    var aadhaarNum by remember { mutableStateOf(currentUser.documentAadhaar.ifBlank { "" }) }
    var dlNum by remember { mutableStateOf(currentUser.documentDL.ifBlank { "" }) }
    var rcNum by remember { mutableStateOf(currentUser.documentRC.ifBlank { "" }) }
    var vehNumber by remember { mutableStateOf(currentUser.vehicleNumber.ifBlank { "GJ 03 XY 5521" }) }
    var selectedVehType by remember { mutableStateOf(currentUser.vehicleType ?: VehicleType.MINI_TRUCK) }

    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "3. Fleet Identity & Document Center",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFACC15),
                    fontSize = 16.sp
                )
                Text(
                    "Your commercial KYC credentials must be kept updated to pass Rajkot police security filters.",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Driver Partner Name") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        TextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        TextField(
            value = aadhaarNum,
            onValueChange = { aadhaarNum = it },
            label = { Text("Aadhaar Card Copy Number") },
            placeholder = { Text("xxxx-xxxx-xxxx") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        TextField(
            value = dlNum,
            onValueChange = { dlNum = it },
            label = { Text("Driving License (DL) Number") },
            placeholder = { Text("GJ03-xxxxx") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        TextField(
            value = rcNum,
            onValueChange = { rcNum = it },
            label = { Text("Registration Certificate (RC) copy") },
            placeholder = { Text("GJ-03-xx-xxxx") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        TextField(
            value = vehNumber,
            onValueChange = { vehNumber = it },
            label = { Text("Vehicle License plate value") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Active Loader Size Range", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)

        // Vehicle types grid representation
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            VehicleType.values().forEach { v ->
                val isSelected = selectedVehType == v
                Card(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                        .clickable { selectedVehType = v },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFFACC15) else Color(0xFF2D2D2D)
                    )
                ) {
                    Text(
                        "${v.iconEmoji} ${v.title}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.submitDriverRegistration(
                    name = name,
                    phone = phone,
                    aadhaar = aadhaarNum,
                    dl = dlNum,
                    rc = rcNum,
                    vehicleType = selectedVehType,
                    vehicleNum = vehNumber
                )
                isSaved = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15), contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Update KYC Documents", fontWeight = FontWeight.Bold)
        }

        if (isSaved) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Documents profile updated. Awaiting verification update.",
                color = Color(0xFF22C55E),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
