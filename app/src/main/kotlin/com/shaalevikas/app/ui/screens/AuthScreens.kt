package com.shaalevikas.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shaalevikas.app.model.AlumniUser
import com.shaalevikas.app.ui.components.*
import com.shaalevikas.app.ui.theme.*
import com.shaalevikas.app.viewmodel.AuthState
import com.shaalevikas.app.viewmodel.AppViewModel

// ─── Shared styled text field ─────────────────────────────────────────────────

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    singleLine: Boolean = true
) {
    var passVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !passVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passVisible = !passVisible }) {
                    Icon(
                        imageVector = if (passVisible) Icons.Default.Visibility
                                      else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = GreenPrimary
                    )
                }
            }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenPrimary,
            focusedLabelColor = GreenPrimary,
            cursorColor = GreenPrimary
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

// ─── Alumni Login ─────────────────────────────────────────────────────────────

@Composable
fun AlumniLoginScreen(
    viewModel: AppViewModel,
    onSuccess: () -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.AlumniSuccess) {
            viewModel.resetAuthState()
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Back
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GreenPrimary)
        }

        Spacer(Modifier.height(16.dp))

        Text("Welcome back!", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(
            "Login to see your school's current needs",
            fontSize = 14.sp, color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
        )

        AppTextField(email, { email = it }, "Email Address", keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(14.dp))
        AppTextField(password, { password = it }, "Password", isPassword = true)

        // Error
        if (authState is AuthState.Error) {
            ErrorText((authState as AuthState.Error).message)
        }

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = "Login",
            onClick = { viewModel.loginAlumni(email, password) },
            modifier = Modifier.fillMaxWidth(),
            isLoading = authState is AuthState.Loading
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("New alumni? ", color = TextSecondary, fontSize = 14.sp)
            Text(
                "Register here",
                color = GreenPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onRegister() }
            )
        }
    }
}

// ─── Alumni Register ──────────────────────────────────────────────────────────

@Composable
fun AlumniRegisterScreen(
    viewModel: AppViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var city     by remember { mutableStateOf("") }
    var year     by remember { mutableStateOf("") }
    var phone    by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.AlumniSuccess) {
            viewModel.resetAuthState()
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GreenPrimary)
        }

        Spacer(Modifier.height(8.dp))
        Text("Create your account", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        AppTextField(name, { name = it }, "Full Name *")
        Spacer(Modifier.height(12.dp))
        AppTextField(email, { email = it }, "Email Address *", keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        AppTextField(password, { password = it }, "Password (min 6 chars) *", isPassword = true)
        Spacer(Modifier.height(12.dp))
        AppTextField(city, { city = it }, "Current City *")
        Spacer(Modifier.height(12.dp))
        AppTextField(year, { year = it }, "Graduation Year (optional)", keyboardType = KeyboardType.Number)
        Spacer(Modifier.height(12.dp))
        AppTextField(phone, { phone = it }, "Phone (optional)", keyboardType = KeyboardType.Phone)

        if (authState is AuthState.Error) {
            ErrorText((authState as AuthState.Error).message)
        }

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = "Create Account",
            onClick = {
                viewModel.registerAlumni(
                    email, password,
                    AlumniUser(name = name, email = email, city = city,
                               graduationYear = year, phone = phone)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isLoading = authState is AuthState.Loading
        )
    }
}

// ─── Admin Login ──────────────────────────────────────────────────────────────

@Composable
fun AdminLoginScreen(
    viewModel: AppViewModel,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.AdminSuccess) {
            viewModel.resetAuthState()
            onSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                }
                Text(
                    "Admin Login",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(end = 48.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Text("🏫", fontSize = 40.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text("Headmaster Portal", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(
                "Manage school needs & updates",
                color = White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Card form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    AppTextField(email, { email = it }, "Admin Email", keyboardType = KeyboardType.Email)
                    Spacer(Modifier.height(14.dp))
                    AppTextField(password, { password = it }, "Password", isPassword = true)

                    if (authState is AuthState.Error) {
                        ErrorText((authState as AuthState.Error).message)
                    }

                    Spacer(Modifier.height(20.dp))

                    PrimaryButton(
                        text = "Login as Headmaster",
                        onClick = { viewModel.loginAdmin(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = authState is AuthState.Loading
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Admin accounts are created by the school IT admin.\nContact support if you need access.",
                color = White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
