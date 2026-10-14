package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TestAttemptEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.ExamCategory
import com.example.ui.components.ExamEmblemIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  categories: List<ExamCategory>,
  latestAttempt: TestAttemptEntity?,
  userProfile: UserProfileEntity?,
  onNavigateToCategory: (String) -> Unit,
  onViewAllCategories: () -> Unit,
  onStartLiveMock: () -> Unit,
  onStartPracticeDrill: (String) -> Unit,
  onViewAnalytics: (Long) -> Unit,
  onSignUpClick: () -> Unit,
  onAdminClick: () -> Unit,
  onBookmarksClick: () -> Unit,
  onMyTestsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val displayName = userProfile?.name ?: "Aspirant"
  val targetExam = userProfile?.targetExam ?: "SSC CGL 2026"

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen"),
    containerColor = Color(0xFF11141E),
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0A2B78))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // App Logo / Title
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E3A8A)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "TestRank",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Text(
                text = "Target: $targetExam",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF38BDF8)
              )
            }
          }

          // Admin Studio Action
          IconButton(
            onClick = onAdminClick,
            modifier = Modifier.testTag("home_admin_icon_btn")
          ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Studio", tint = Color(0xFFF59E0B))
          }

          // Bookmarks
          IconButton(onClick = onBookmarksClick) {
            Icon(Icons.Default.BookmarkBorder, contentDescription = "Saved Questions", tint = Color.White)
          }

          // Profile / Avatar
          IconButton(
            onClick = onSignUpClick,
            modifier = Modifier.testTag("home_profile_icon_btn")
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E3A8A))
                .border(1.dp, Color(0xFF38BDF8), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = displayName.take(1).uppercase(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    },
    bottomBar = {
      // Bottom Navigation Bar
      NavigationBar(
        containerColor = Color(0xFF11141E),
        tonalElevation = 0.dp,
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
          .height(64.dp)
      ) {
        NavigationBarItem(
          selected = true,
          onClick = { },
          icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
          label = { Text("Home", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color(0xFF38BDF8),
            indicatorColor = Color(0xFF0E43AF),
            unselectedIconColor = Color(0xFF64748B),
            unselectedTextColor = Color(0xFF64748B)
          )
        )
        NavigationBarItem(
          selected = false,
          onClick = onViewAllCategories,
          icon = { Icon(Icons.Default.ListAlt, contentDescription = "Test Series") },
          label = { Text("Tests", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = Color(0xFF94A3B8),
            unselectedTextColor = Color(0xFF94A3B8)
          )
        )
        NavigationBarItem(
          selected = false,
          onClick = { onStartPracticeDrill("cgl_drill_quant") },
          icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Practice") },
          label = { Text("Practice", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = Color(0xFF94A3B8),
            unselectedTextColor = Color(0xFF94A3B8)
          )
        )
        NavigationBarItem(
          selected = false,
          onClick = onMyTestsClick,
          icon = { Icon(Icons.Default.History, contentDescription = "My Tests") },
          label = { Text("My Tests", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = Color(0xFF94A3B8),
            unselectedTextColor = Color(0xFF94A3B8)
          )
        )
        NavigationBarItem(
          selected = false,
          onClick = onSignUpClick,
          icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
          label = { Text("Profile", fontSize = 11.sp) },
          colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = Color(0xFF94A3B8),
            unselectedTextColor = Color(0xFF94A3B8)
          )
        )
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Streak & Notification Banner
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF1E2436))
          .border(1.dp, Color(0xFF2E3A54), RoundedCornerShape(10.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("🔥", fontSize = 18.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text("Day 7 Daily Streak!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("Keep practicing to boost RankIQ percentile", color = Color(0xFF94A3B8), fontSize = 11.sp)
          }
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0E43AF))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text("+50 XP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
      }

      // Hero Card: All-India Live Mega Mock Test
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2236)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF2C3C63))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFF0F2B6E), Color(0xFF151928))
              )
            )
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFEF4444))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text("LIVE NOW", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text("42,150 Aspirants Joined", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "SSC CGL 2026 – All India Live Full Mock 1",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "100 Questions • 200 Marks • 60 Mins • All-India Rank & Percentile Report",
            color = Color(0xFFCBD5E1),
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = onStartLiveMock,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("home_start_live_mock_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF10B981),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Start Free Live Mock Test", fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Official CBT Interfaces Showcase: Eduquity for SSC & TCS iON for Railways
      Text(
        text = "Official CBT Consoles / वास्तविक परीक्षा इंटरफ़ेस",
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // SSC Eduquity Card
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { onStartPracticeDrill("cgl_mock_01") }
            .testTag("home_launch_eduquity_card"),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2B57)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF0284C7))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("Eduquity", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
              ExamEmblemIcon(iconType = "ssc", size = 26.dp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("SSC Exams", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("CGL, CHSL, CPO\nEduquity Official CBT", color = Color(0xFF94A3B8), fontSize = 11.sp, lineHeight = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = { onStartPracticeDrill("cgl_mock_01") },
              modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .testTag("home_play_eduquity_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text("Eduquity Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Railways TCS iON Card
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { onStartPracticeDrill("ntpc_mock_01") }
            .testTag("home_launch_tcs_card"),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2240)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(Color(0xFF1D4ED8))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("TCS iON", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
              ExamEmblemIcon(iconType = "railways", size = 26.dp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Railways RRB", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("NTPC, ALP, Group D\nTCS iON Classic CBT", color = Color(0xFF94A3B8), fontSize = 11.sp, lineHeight = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = { onStartPracticeDrill("ntpc_mock_01") },
              modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .testTag("home_play_tcs_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text("TCS iON Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Recent Scorecard Card (if user has taken any test)
      if (latestAttempt != null) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2234)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3752))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Recent Performance", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
              TextButton(onClick = { onViewAnalytics(latestAttempt.id) }) {
                Text("Full Report", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("All India Rank", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Text("#${latestAttempt.rank}", color = Color(0xFFF59E0B), fontSize = 18.sp, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Score", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Text("${latestAttempt.totalScore}/${latestAttempt.maxScore.toInt()}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Accuracy", color = Color(0xFF94A3B8), fontSize = 11.sp)
                Text("${latestAttempt.accuracyPercentage}%", color = Color(0xFF10B981), fontSize = 18.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Exam Categories Section (Horizontal chips & cards)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Popular Test Series", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        TextButton(onClick = onViewAllCategories) {
          Text("View All (${categories.size})", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      // Horizontal Scroll of Category Cards
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        categories.take(6).forEach { cat ->
          Card(
            modifier = Modifier
              .width(135.dp)
              .height(115.dp)
              .clickable { onNavigateToCategory(cat.id) },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF222634)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF31384C))
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = cat.name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
              ) {
                Text(
                  text = "${cat.totalTests} Tests",
                  color = Color(0xFF10B981),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
                ExamEmblemIcon(iconType = cat.iconType, size = 36.dp)
              }
            }
          }
        }
      }

      // Quick Practice Drills
      Text("Subject Speed Boosters", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DrillQuickCard("Quant Drill", "25 Qs", Color(0xFF2563EB), Modifier.weight(1f)) {
          onStartPracticeDrill("cgl_drill_quant")
        }
        DrillQuickCard("Reasoning", "25 Qs", Color(0xFF8B5CF6), Modifier.weight(1f)) {
          onStartPracticeDrill("cgl_drill_reasoning")
        }
        DrillQuickCard("English", "25 Qs", Color(0xFF0284C7), Modifier.weight(1f)) {
          onStartPracticeDrill("cgl_drill_english")
        }
        DrillQuickCard("General GK", "25 Qs", Color(0xFF10B981), Modifier.weight(1f)) {
          onStartPracticeDrill("cgl_drill_gk")
        }
      }

      // Admin Studio Banner Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onAdminClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F30)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3D60))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF1E3A8A)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.PostAdd, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("Admin Question Studio", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Create custom questions, explanations & shortcut tricks", color = Color(0xFF94A3B8), fontSize = 12.sp)
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
        }
      }
    }
  }
}

@Composable
fun DrillQuickCard(
  title: String,
  subtitle: String,
  color: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(Color(0xFF1E2333))
      .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
      .clickable(onClick = onClick)
      .padding(vertical = 10.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
      Spacer(modifier = Modifier.height(2.dp))
      Text(subtitle, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
  }
}
