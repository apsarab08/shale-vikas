package com.shaalevikas.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shaalevikas.app.ui.theme.*

// ─── Green filled button ──────────────────────────────────────────────────────

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
        shape = RoundedCornerShape(10.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Outlined button ─────────────────────────────────────────────────────────

@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GreenPrimary),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

// ─── Animated progress bar ───────────────────────────────────────────────────

@Composable
fun AnimatedLinearProgress(percent: Int, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = percent / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )
    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = GreenPrimary,
        trackColor = GreenLight
    )
}

// ─── Priority badge ───────────────────────────────────────────────────────────

@Composable
fun PriorityBadge(priority: String) {
    val (bg, fg) = when (priority) {
        "Urgent" -> ErrorLight to ErrorRed
        "Medium" -> AmberLight to AmberDark
        else     -> GreenLight to GreenMid
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(priority, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = fg)
    }
}

// ─── Avatar circle ────────────────────────────────────────────────────────────

@Composable
fun AvatarCircle(
    initials: String,
    color: Color,
    size: Int = 46,
    fontSize: Int = 16
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = White,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── Section header ───────────────────────────────────────────────────────────

@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        letterSpacing = 0.8.sp,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// ─── Green top stats bar ─────────────────────────────────────────────────────

@Composable
fun StatBar(stats: List<Pair<String, String>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        stats.forEachIndexed { index, (value, label) ->
            if (index > 0) {
                Divider(
                    color = White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .height(36.dp)
                        .width(1.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(value, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(label, color = White.copy(alpha = 0.7f), fontSize = 11.sp)
            }
        }
    }
}

// ─── Green header banner ─────────────────────────────────────────────────────

@Composable
fun GreenBanner(title: String, subtitle: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        if (subtitle != null) {
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = White.copy(alpha = 0.7f), fontSize = 13.sp)
        }
    }
}

// ─── Error text ───────────────────────────────────────────────────────────────

@Composable
fun ErrorText(message: String) {
    if (message.isNotEmpty()) {
        Text(
            text = message,
            color = ErrorRed,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// ─── Loading screen ───────────────────────────────────────────────────────────

@Composable
fun FullScreenLoader() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = GreenPrimary)
    }
}

// ─── Empty state ─────────────────────────────────────────────────────────────

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = TextSecondary, fontSize = 15.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
