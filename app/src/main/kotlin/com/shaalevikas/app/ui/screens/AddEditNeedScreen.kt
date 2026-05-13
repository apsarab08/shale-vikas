package com.shaalevikas.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shaalevikas.app.model.NeedItem
import com.shaalevikas.app.ui.components.*
import com.shaalevikas.app.ui.theme.*
import com.shaalevikas.app.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNeedScreen(
    viewModel: AppViewModel,
    existingNeed: NeedItem? = null,   // null = add mode
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val isEdit  = existingNeed != null

    var title       by remember { mutableStateOf(existingNeed?.title ?: "") }
    var description by remember { mutableStateOf(existingNeed?.description ?: "") }
    var amount      by remember { mutableStateOf(existingNeed?.targetAmount?.toString() ?: "") }
    var priority    by remember { mutableStateOf(existingNeed?.priority ?: "Urgent") }
    var beforeUri   by remember { mutableStateOf<Uri?>(null) }
    var afterUri    by remember { mutableStateOf<Uri?>(null) }
    var error       by remember { mutableStateOf("") }
    var isSaving    by remember { mutableStateOf(false) }

    val priorities = listOf("Urgent", "Medium", "Planned")

    val beforePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri -> beforeUri = uri }

    val afterPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) { uri -> afterUri = uri }

    // Navigate back after save
    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            if (msg.contains("added") || msg.contains("updated")) {
                isSaving = false
                onDone()
            } else if (msg.contains("Error") || msg.contains("failed")) {
                isSaving = false
                error = msg
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEdit) "Edit Need" else "Add New Need",
                        color = White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            AppTextField(title, { title = it }, "Need Title *")
            Spacer(Modifier.height(14.dp))

            AppTextField(
                description, { description = it },
                "Description / Details *",
                singleLine = false
            )
            Spacer(Modifier.height(14.dp))

            AppTextField(amount, { amount = it }, "Estimated Cost (₹) *",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            Spacer(Modifier.height(14.dp))

            // Priority selector
            Text("Priority Level", fontSize = 13.sp, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                priorities.forEach { p ->
                    val selected = priority == p
                    val (bg, fg) = when (p) {
                        "Urgent" -> if (selected) ErrorRed to White else ErrorLight to ErrorRed
                        "Medium" -> if (selected) Amber to White else AmberLight to AmberDark
                        else     -> if (selected) GreenPrimary to White else GreenLight to GreenMid
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bg)
                            .clickable { priority = p }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(p, color = fg, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Before photo
            Text("Before Photo (optional)", fontSize = 13.sp, color = TextSecondary)
            Spacer(Modifier.height(6.dp))
            OutlineButton("📷 Pick Before Photo", onClick = { beforePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth())
            if (beforeUri != null) {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = beforeUri,
                    contentDescription = "Before preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else if (!existingNeed?.photoUrl.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = existingNeed?.photoUrl,
                    contentDescription = "Existing before photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            Spacer(Modifier.height(16.dp))

            // After photo
            Text("After Photo (when work is done)", fontSize = 13.sp, color = TextSecondary)
            Spacer(Modifier.height(6.dp))
            OutlineButton("📷 Pick After Photo", onClick = { afterPicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth())
            if (afterUri != null) {
                Spacer(Modifier.height(8.dp))
                AsyncImage(
                    model = afterUri,
                    contentDescription = "After preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }

            ErrorText(error)
            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                text = if (isEdit) "Update Need" else "Add Need",
                isLoading = isSaving,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (title.isBlank() || description.isBlank() || amount.isBlank()) {
                        error = "Title, description and amount are required"
                        return@PrimaryButton
                    }
                    val amt = amount.toLongOrNull()
                    if (amt == null || amt <= 0) {
                        error = "Enter a valid amount"
                        return@PrimaryButton
                    }
                    isSaving = true
                    error = ""

                    val needId = existingNeed?.id

                    if (isEdit && needId != null) {
                        viewModel.updateNeed(needId, mapOf(
                            "title"        to title,
                            "description"  to description,
                            "targetAmount" to amt,
                            "priority"     to priority
                        ))
                        // Upload photos if selected
                        beforeUri?.let { viewModel.uploadPhoto(context, needId, "before", it) {} }
                        afterUri?.let  { viewModel.uploadPhoto(context, needId, "after",  it) {} }
                        // message listener in LaunchedEffect will call onDone
                    } else {
                        val adminUid = viewModel.sessionUid.value
                        val newNeed  = NeedItem(title = title, description = description,
                            priority = priority, targetAmount = amt, addedBy = adminUid)
                        viewModel.addNeed(newNeed)
                        // After add, upload photos if any
                        // Note: For new needs, photo upload happens after key is known via message
                    }
                }
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
