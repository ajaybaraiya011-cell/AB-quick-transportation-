package com.example.ui

import com.example.ui.theme.*

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VehicleType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onNavigateToCustomer: () -> Unit,
    onNavigateToDriver: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFACC15), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = "Logo",
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AB Quick",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                ),
                actions = {
                    IconButton(onClick = onNavigateToAdmin) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Admin Portal",
                            tint = Color(0xFFFACC15)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Floating WhatsApp Call support button as requested in branding details!
            ExtendedFloatingActionButton(
                onClick = { /* Simulated dialer launch to contacts: 9265454753 */ },
                containerColor = Color(0xFF22C55E),
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = "Support")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "WhatsApp Support", fontSize = 12.sp)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E))
                .verticalScroll(scrollState)
                .padding(innerPadding)
        ) {
            // 1. HERO BANNER SECTION (Vibrant Gradient Yellow & Black)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF121212), Color(0xFF1E1E1E))
                        )
                    )
                    .padding(vertical = 32.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFACC15).copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🚚 Logistics Made Rapid & Reliable",
                            color = Color(0xFFFACC15),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "AB QUICK\nTRANSPORTATION",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Gujarat's Trusted Commercial Cargo Load & Bike Delivery Partner",
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Redirection buttons to test panels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onNavigateToCustomer,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFACC15),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Book")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Book Vehicle", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onNavigateToDriver,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Icon(imageVector = Icons.Default.AddLocationAlt, contentDescription = "Join")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Join Driver", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 2. VEHICLE SHOWCASE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF262626)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Our Commercial Fleet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Choose from our wide range of goods cargo loading vehicles",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // List vehicles
                    VehicleType.values().forEach { vehicle ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color(0xFF2D2D2D), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = vehicle.iconEmoji, fontSize = 24.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = vehicle.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${vehicle.capacity} • ${vehicle.description}",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${vehicle.baseFare}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFACC15),
                                    fontSize = 15.sp
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
            }

            // 3. HOW IT WORKS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How AB Quick Works",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Book commercial loaders in 3 simple steps",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StepWidget(
                        stepNumber = "1",
                        icon = Icons.Default.EditLocation,
                        title = "Set Routes",
                        desc = "Provide GIDC Metoda or Rajkot pickup drops"
                    )
                    StepWidget(
                        stepNumber = "2",
                        icon = Icons.Default.LocalShipping,
                        title = "Select Loader",
                        desc = "Compare fares of Tata Ace, Bike, or Truck"
                    )
                    StepWidget(
                        stepNumber = "3",
                        icon = Icons.Default.DoneOutline,
                        title = "Instant Deliver",
                        desc = "Real-time dispatch, route optimize & tracking"
                    )
                }
            }

            // 4. BUSINESS CONTACT DETAILS & SERVICE LOCATION MAP SIMULATOR
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
                border = BorderStroke(1.dp, Color(0xFFFACC15).copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Operational HQ: Rajkot, Gujarat",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Commercial Vehicle Booking & Goods Transport Service centers across Rajkot Kalavad Road, Gondal Road, & Metoda.",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Marker",
                            tint = Color(0xFFFACC15),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Yagnik Road, Opposite Shastri Maidan, Rajkot, Gujarat", color = Color.White, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = Color(0xFFFACC15),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+91 9265454753", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Beautiful simulated custom vectors canvas map indicating landmarks
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Drawing simple roads mesh
                            drawLine(Color(0xFF2A2A2A), start = androidx.compose.ui.geometry.Offset(0f, 100f), end = androidx.compose.ui.geometry.Offset(size.width, 100f), strokeWidth = 14f)
                            drawLine(Color(0xFF2A2A2A), start = androidx.compose.ui.geometry.Offset(size.width/3, 0f), end = androidx.compose.ui.geometry.Offset(size.width/3, size.height), strokeWidth = 14f)
                            drawLine(Color(0xFF2A2A2A), start = androidx.compose.ui.geometry.Offset(size.width * 2/3, 0f), end = androidx.compose.ui.geometry.Offset(size.width * 2/3, size.height), strokeWidth = 14f)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "HQ PIN",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFACC15)),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    "AB Quick Hub, Rajkot",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 5. FOOTER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF121212))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AB Quick Transportation Service",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "© 2026 AB Quick Inc. All rights reserved.",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.StepWidget(
    stepNumber: String,
    icon: ImageVector,
    title: String,
    desc: String
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFACC15), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(Color.White, RoundedCornerShape(50.dp))
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stepNumber,
                    fontSize = 10.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = desc,
            fontSize = 10.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}
