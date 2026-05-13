package com.shaalevikas.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shaalevikas.app.ui.components.PrimaryButton
import com.shaalevikas.app.ui.components.OutlineButton
import com.shaalevikas.app.ui.theme.*
import kotlinx.coroutines.delay

// ─── Splash Screen ────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(onReady: (String) -> Unit, role: String) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(1800)
        onReady(role)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenPrimary),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + scaleIn(initialScale = 0.8f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // School emoji in white circle
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏫", fontSize = 42.sp)
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Shaale-Vikas",
                    color = White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "School-Alumni Bridge",
                    color = White.copy(alpha = 0.75f),
                    fontSize = 15.sp
                )

                Spacer(Modifier.height(60.dp))

                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

// ─── Role Select Screen ───────────────────────────────────────────────────────

@Composable
fun RoleSelectScreen(
    onAlumni: () -> Unit,
    onAdmin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏫", fontSize = 60.sp)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Shaale-Vikas",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Empowering alumni to support their school",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Divider(
            modifier = Modifier
                .width(60.dp)
                .padding(vertical = 16.dp),
            color = GreenAccent,
            thickness = 2.dp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Who are you?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            text = "🎓  I am an Alumni",
            onClick = onAlumni,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(14.dp))

        OutlineButton(
            text = "🏫  I am the Headmaster",
            onClick = onAdmin,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(48.dp))

        Text(
            text = "Shaale-Vikas v1.0 — Building bridges",
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}
