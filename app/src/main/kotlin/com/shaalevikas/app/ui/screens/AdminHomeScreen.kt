package com.shaalevikas.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shaalevikas.app.model.NeedItem
import com.shaalevikas.app.ui.components.*
import com.shaalevikas.app.ui.theme.*
import com.shaalevikas.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    viewModel: AppViewModel,
    onAddNeed: () -> Unit,
    onEditNeed: (String) -> Unit,
    onLogout: () -> Unit
) {
    val needs  by viewModel.allNeeds.collectAsState()
    val stats  by viewModel.adminStats.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.message.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Dashboard", fontSize = 17.sp,
                            fontWeight = FontWeight.Bold, color = White)
                        Text("ZP School, Dharwad", fontSize = 12.sp,
                            color = White.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenDark)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNeed,
                containerColor = GreenPrimary,
                contentColor = White,
                icon = { Icon(Icons.Default.Add, "Add Need") },
                text = { Text("Add Need", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenDark)
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AdminStat("${stats.totalNeeds}", "total needs")
                Divider(color = White.copy(0.3f), modifier = Modifier.height(36.dp).width(1.dp))
                AdminStat("₹${stats.totalRaised}", "raised", color = GreenAccent)
                Divider(color = White.copy(0.3f), modifier = Modifier.height(36.dp).width(1.dp))
                AdminStat("${needs.count { it.completed }}", "completed")
            }

            SectionHeader("Manage School Needs")

            if (needs.isEmpty()) {
                EmptyState("No needs yet.\nTap + to add your first need.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(needs, key = { it.id }) { need ->
                        AdminNeedCard(
                            need = need,
                            onEdit = { onEditNeed(need.id) },
                            onDelete = { viewModel.deleteNeed(need.id) },
                            onToggleComplete = { viewModel.markComplete(need.id, !need.completed) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStat(value: String, label: String, color: androidx.compose.ui.graphics.Color = White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = White.copy(0.7f), fontSize = 11.sp)
    }
}

@Composable
fun AdminNeedCard(
    need: NeedItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Need", fontWeight = FontWeight.Bold) },
            text = { Text("Delete \"${need.title}\"? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; onDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    val borderColor = if (need.completed) Teal else androidx.compose.ui.graphics.Color.Transparent
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = if (need.completed) BorderStroke(2.dp, Teal) else null
    ) {
        Column(Modifier.padding(16.dp)) {
            // Title + Priority
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(need.title, fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, modifier = Modifier.weight(1f))
                PriorityBadge(need.priority)
            }

            Spacer(Modifier.height(4.dp))

            // Status
            Text(
                if (need.completed) "✓ Completed" else "● Active",
                color = if (need.completed) Teal else GreenPrimary,
                fontSize = 12.sp, fontWeight = FontWeight.Bold
            )

            Text(need.description, fontSize = 13.sp, color = TextSecondary,
                maxLines = 2, modifier = Modifier.padding(top = 4.dp))

            Text("Target: ₹${need.targetAmount}  |  Raised: ₹${need.raisedAmount}",
                fontSize = 12.sp, color = GreenPrimary, modifier = Modifier.padding(top = 6.dp))

            // Progress
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedLinearProgress(need.progressPercent(), Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Text("${need.progressPercent()}%", fontSize = 12.sp,
                    color = GreenPrimary, fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Divider)

            // Action buttons row 1
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlineButton("✏ Edit", onClick = onEdit, modifier = Modifier.weight(1f))
                // Delete button
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = BorderStroke(1.dp, ErrorRed),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("🗑 Delete", fontSize = 14.sp) }
            }

            Spacer(Modifier.height(8.dp))

            // Action buttons row 2
            PrimaryButton(
                text = if (need.completed) "Reopen Need" else "✓ Mark Complete",
                onClick = onToggleComplete,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
