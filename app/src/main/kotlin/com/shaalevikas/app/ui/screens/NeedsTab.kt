package com.shaalevikas.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shaalevikas.app.model.NeedItem
import com.shaalevikas.app.ui.components.*
import com.shaalevikas.app.ui.theme.*
import com.shaalevikas.app.viewmodel.AppViewModel

@Composable
fun NeedsTab(viewModel: AppViewModel, onNeedDetail: (String) -> Unit) {

    val needs by viewModel.activeNeeds.collectAsState()
    val completed by viewModel.completedNeeds.collectAsState()
    val donors by viewModel.donors.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Stats Bar
        StatBar(
            listOf(
                "₹${needs.sumOf { it.raisedAmount } / 1000}K" to "raised",
                "${donors.size}" to "donors",
                "${completed.size}" to "done"
            )
        )

        if (needs.isEmpty()) {

            EmptyState(
                "No current needs.\nYour school is doing well! 🎉"
            )

        } else {

            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // SAFER VERSION
                items(needs) { need ->

                    NeedCard(
                        need = need,
                        viewModel = viewModel,
                        onClick = { id ->

                            if (id.isNotEmpty()) {
                                onNeedDetail(id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NeedCard(
    need: NeedItem,
    viewModel: AppViewModel,
    onClick: (String) -> Unit
) {

    var hasPledged by remember {
        mutableStateOf(false)
    }

    var showPledgeDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(need.id) {

        try {
            hasPledged = viewModel.hasPledged(need.id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (showPledgeDialog) {

        PledgeDialog(
            need = need,

            onDismiss = {
                showPledgeDialog = false
            },

            onPledge = { amount ->

                showPledgeDialog = false
                hasPledged = true

                viewModel.submitPledge(
                    need,
                    amount
                )
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()

            .clickable {

                if (need.id.isNotEmpty()) {
                    onClick(need.id)
                }
            },

        shape = RoundedCornerShape(12.dp),

        colors = CardDefaults.cardColors(
            containerColor = White
        ),

        elevation = CardDefaults.cardElevation(2.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Title Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = need.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )

                PriorityBadge(need.priority)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = need.description,
                fontSize = 13.sp,
                color = TextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Est. cost: ₹${need.targetAmount}",
                fontSize = 13.sp,
                color = GreenPrimary,
                fontWeight = FontWeight.SemiBold
            )

            // Progress
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "₹${need.raisedAmount} raised",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${need.progressPercent()}%",
                    fontSize = 12.sp,
                    color = GreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            AnimatedLinearProgress(
                need.progressPercent()
            )

            // Button
            Spacer(modifier = Modifier.height(14.dp))

            if (hasPledged) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(GreenLight)
                        .padding(vertical = 12.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "✓ Pledged — Thank you!",
                        color = GreenMid,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

            } else {

                Button(
                    onClick = {
                        showPledgeDialog = true
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),

                    shape = RoundedCornerShape(10.dp)
                ) {

                    Text(
                        text = "Pledge to Contribute",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun PledgeDialog(
    need: NeedItem,
    onDismiss: () -> Unit,
    onPledge: (Long) -> Unit
) {

    val options = listOf(
        500L,
        1000L,
        2000L,
        5000L,
        10000L
    )

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text(
                text = "Pledge for: ${need.title}",
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column {

                Text(
                    text = "Choose a commitment amount. No real money is collected in-app.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                options.forEach { amt ->

                    OutlinedButton(

                        onClick = {
                            onPledge(amt)
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),

                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = GreenPrimary
                        ),

                        border = BorderStroke(
                            1.dp,
                            GreenPrimary
                        ),

                        shape = RoundedCornerShape(8.dp)
                    ) {

                        Text(
                            text = "₹$amt",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },

        confirmButton = {},

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text(
                    text = "Cancel",
                    color = TextSecondary
                )
            }
        }
    )
}