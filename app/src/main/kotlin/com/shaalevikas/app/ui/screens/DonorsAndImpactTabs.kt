package com.shaalevikas.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.shaalevikas.app.model.AlumniUser
import com.shaalevikas.app.model.NeedItem
import com.shaalevikas.app.ui.components.*
import com.shaalevikas.app.ui.theme.*
import com.shaalevikas.app.viewmodel.AppViewModel

// ─── Donors Tab ───────────────────────────────────────────────────────────────

@Composable
fun DonorsTab(viewModel: AppViewModel) {
    val donors by viewModel.donors.collectAsState()

    Column(Modifier.fillMaxSize()) {
        GreenBanner("🏆 Hall of Fame", "Alumni who pledged to support their school")

        if (donors.isEmpty()) {
            EmptyState("No donors yet.\nBe the first to pledge! 🙏")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(donors, key = { _, d -> d.uid }) { index, donor ->
                    DonorCard(donor, index)
                }
            }
        }
    }
}

@Composable
fun DonorCard(donor: AlumniUser, index: Int) {
    val rankLabel = when (index) {
        0 -> "👑"; 1 -> "🥈"; 2 -> "🥉"
        else -> "#${index + 1}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Text(rankLabel, fontSize = if (index < 3) 22.sp else 15.sp,
                fontWeight = FontWeight.Bold, color = Gold,
                modifier = Modifier.width(36.dp))

            Spacer(Modifier.width(8.dp))

            // Avatar
            AvatarCircle(
                initials = donor.initials(),
                color = AvatarColors[index % AvatarColors.size]
            )

            Spacer(Modifier.width(12.dp))

            // Info
            Column(Modifier.weight(1f)) {
                Text(donor.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(donor.city, fontSize = 12.sp, color = TextSecondary)
                Text(
                    "${donor.pledgeCount} pledge${if (donor.pledgeCount != 1) "s" else ""}",
                    fontSize = 12.sp, color = GreenPrimary
                )
            }

            // Amount
            Text(
                "₹${donor.totalPledged}",
                fontSize = 13.sp, color = GreenMid,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Impact Tab ───────────────────────────────────────────────────────────────

@Composable
fun ImpactTab(viewModel: AppViewModel) {
    val completed by viewModel.completedNeeds.collectAsState()

    Column(Modifier.fillMaxSize()) {
        GreenBanner("📸 Before & After", "${completed.size} projects completed")

        if (completed.isEmpty()) {
            EmptyState("No completed projects yet.\nCheck back soon! 🔨")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(completed.size) { index ->
                    ImpactCard(completed[index])
                }
            }
        }
    }
}

@Composable
fun ImpactCard(need: NeedItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(need.title, fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = GreenPrimary)
            Text("₹${need.raisedAmount} raised by alumni",
                fontSize = 12.sp, color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Before
                Column(Modifier.weight(1f)) {
                    Text("BEFORE", fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, color = Coral,
                        modifier = Modifier.padding(bottom = 4.dp))
                    PhotoBox(need.photoUrl, CoralLight, "Before photo")
                }
                // After
                Column(Modifier.weight(1f)) {
                    Text("AFTER ✓", fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, color = Teal,
                        modifier = Modifier.padding(bottom = 4.dp))
                    PhotoBox(need.afterPhotoUrl, TealLight, "After photo")
                }
            }
        }
    }
}

@Composable
fun PhotoBox(
    url: String?,
    placeholderColor: androidx.compose.ui.graphics.Color,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(placeholderColor),
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrEmpty()) {
            AsyncImage(
                model = url,
                contentDescription = description,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("No photo yet", fontSize = 12.sp, color = TextSecondary)
        }
    }
}
