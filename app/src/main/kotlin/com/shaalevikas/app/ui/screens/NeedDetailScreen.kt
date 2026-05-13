package com.shaalevikas.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shaalevikas.app.ui.components.*
import com.shaalevikas.app.ui.theme.*
import com.shaalevikas.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedDetailScreen(
    needId: String,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val need by viewModel.observeNeed(needId).collectAsState(initial = null)
    val pledges by viewModel.observePledgesForNeed(needId).collectAsState(initial = emptyList())
    var hasPledged by remember { mutableStateOf(false) }
    var showPledgeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(needId) {
        hasPledged = viewModel.hasPledged(needId)
    }

    if (showPledgeDialog && need != null) {
        PledgeDialog(
            need = need!!,
            onDismiss = { showPledgeDialog = false },
            onPledge = { amount ->
                showPledgeDialog = false
                hasPledged = true
                viewModel.submitPledge(need!!, amount)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Need Detail", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
            )
        }
    ) { padding ->
        if (need == null) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
            return@Scaffold
        }

        val item = need!!

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(16.dp)
            ) {
                Text(item.title, color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // White badge for priority
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(White.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(item.priority, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("${pledges.size} alumni pledged", color = White.copy(0.8f), fontSize = 13.sp)
                }
            }

            // Completed banner
            if (item.completed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TealLight)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓ This project is completed!", color = Teal,
                        fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // Progress card
            Card(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row {
                        Text("₹${item.raisedAmount} raised", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = GreenPrimary,
                            modifier = Modifier.weight(1f))
                        Text("${item.progressPercent()}%", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = GreenPrimary)
                    }
                    Spacer(Modifier.height(8.dp))
                    AnimatedLinearProgress(item.progressPercent())
                    Spacer(Modifier.height(6.dp))
                    Text("Target: ₹${item.targetAmount}", fontSize = 13.sp, color = TextSecondary)
                }
            }

            // Description card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("ABOUT THIS NEED", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        color = TextSecondary, letterSpacing = 0.8.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(item.description, fontSize = 15.sp, lineHeight = 22.sp)
                }
            }

            // Before photo
            if (item.photoUrl.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("📸 Before Photo", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = Coral,
                            modifier = Modifier.padding(bottom = 8.dp))
                        AsyncImage(
                            model = item.photoUrl,
                            contentDescription = "Before photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            // After photo
            if (item.afterPhotoUrl.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("✅ After Photo — Work Done!", fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, color = Teal,
                            modifier = Modifier.padding(bottom = 8.dp))
                        AsyncImage(
                            model = item.afterPhotoUrl,
                            contentDescription = "After photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            // Pledge button
            if (!item.completed) {
                Spacer(Modifier.height(4.dp))
                if (hasPledged) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GreenLight)
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓ You have pledged!", color = GreenMid,
                            fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                } else {
                    PrimaryButton(
                        text = "Pledge to Contribute",
                        onClick = { showPledgeDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
