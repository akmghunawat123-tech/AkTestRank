package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExamEmblemIcon(
  iconType: String,
  modifier: Modifier = Modifier,
  size: Dp = 56.dp
) {
  val iconColor = when (iconType.lowercase()) {
    "ssc" -> Color(0xFFD97706) // Golden badge
    "railways" -> Color(0xFFDC2626) // Indian Railways crimson
    "ib" -> Color(0xFF0284C7) // Sky blue
    "uppbpb" -> Color(0xFFE11D48) // UP Police ruby
    "rpf" -> Color(0xFF2563EB) // Royal blue
    "delhi_police" -> Color(0xFF4F46E5) // Indigo
    "mp_police" -> Color(0xFF059669) // Emerald
    "defence" -> Color(0xFFD97706) // Gold
    "bihar_police" -> Color(0xFF9333EA) // Purple
    "rajasthan" -> Color(0xFFEA580C) // Amber-orange
    "banking" -> Color(0xFF0284C7) // SBI Cyan
    else -> Color(0xFF3B82F6)
  }

  val bgBrush = when (iconType.lowercase()) {
    "ssc" -> Brush.radialGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A)))
    "railways" -> Brush.radialGradient(listOf(Color(0xFFFEE2E2), Color(0xFFFECACA)))
    "ib" -> Brush.radialGradient(listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD)))
    "uppbpb" -> Brush.radialGradient(listOf(Color(0xFFFFE4E6), Color(0xFFFECDD3)))
    "rpf" -> Brush.radialGradient(listOf(Color(0xFFDBEAFE), Color(0xFFBFDBFE)))
    "delhi_police" -> Brush.radialGradient(listOf(Color(0xFFE0E7FF), Color(0xFFC7D2FE)))
    "mp_police" -> Brush.radialGradient(listOf(Color(0xFFD1FAE5), Color(0xFFA7F3D0)))
    "defence" -> Brush.radialGradient(listOf(Color(0xFFFEF3C7), Color(0xFFFDE68A)))
    "bihar_police" -> Brush.radialGradient(listOf(Color(0xFFF3E8FF), Color(0xFFE9D5FF)))
    "rajasthan" -> Brush.radialGradient(listOf(Color(0xFFFFEDD5), Color(0xFFFED7AA)))
    "banking" -> Brush.radialGradient(listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD)))
    else -> Brush.radialGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
  }

  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(bgBrush)
      .border(2.dp, iconColor.copy(alpha = 0.6f), CircleShape),
    contentAlignment = Alignment.Center
  ) {
    when (iconType.lowercase()) {
      "ssc" -> {
        // Staff Selection Commission emblem representation
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = "SSC Emblem",
            tint = Color(0xFFB45309),
            modifier = Modifier.size(size * 0.48f)
          )
          Text(
            text = "SSC",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB45309)
          )
        }
      }
      "railways" -> {
        // Indian Railways emblem representation
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Train,
            contentDescription = "Railways Emblem",
            tint = Color(0xFFB91C1C),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "IR",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB91C1C)
          )
        }
      }
      "ib" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "IB Emblem",
            tint = Color(0xFF0369A1),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "IB",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0369A1)
          )
        }
      }
      "uppbpb" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.LocalPolice,
            contentDescription = "UPPBPB Emblem",
            tint = Color(0xFFBE123C),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "UPP",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFBE123C)
          )
        }
      }
      "rpf" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = "RPF Emblem",
            tint = Color(0xFF1D4ED8),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "RPF",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D4ED8)
          )
        }
      }
      "delhi_police" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Delhi Police Emblem",
            tint = Color(0xFF4338CA),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "DP",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4338CA)
          )
        }
      }
      "mp_police" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Policy,
            contentDescription = "MP Police Emblem",
            tint = Color(0xFF047857),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "MPP",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF047857)
          )
        }
      }
      "defence" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.MilitaryTech,
            contentDescription = "Defence Emblem",
            tint = Color(0xFFB45309),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "MOD",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB45309)
          )
        }
      }
      "bihar_police" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.ShieldMoon,
            contentDescription = "Bihar Police Emblem",
            tint = Color(0xFF7E22CE),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "BP",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF7E22CE)
          )
        }
      }
      "rajasthan" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.Fort,
            contentDescription = "Rajasthan Emblem",
            tint = Color(0xFFC2410C),
            modifier = Modifier.size(size * 0.5f)
          )
          Text(
            text = "RAJ",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC2410C)
          )
        }
      }
      "banking" -> {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = "Banking Emblem",
            tint = Color(0xFF0369A1),
            modifier = Modifier.size(size * 0.48f)
          )
          Text(
            text = "SBI",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0369A1)
          )
        }
      }
      else -> {
        Icon(
          imageVector = Icons.Default.Folder,
          contentDescription = "Exam Category",
          tint = Color(0xFF0284C7),
          modifier = Modifier.size(size * 0.5f)
        )
      }
    }
  }
}
