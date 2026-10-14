package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
  bookmarks: List<BookmarkEntity>,
  onRemoveBookmark: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isEnglish by remember { mutableStateOf(true) }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("bookmarks_screen"),
    containerColor = Color(0xFF141722),
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
            .padding(horizontal = 8.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }

          Text(
            text = "Saved Questions (${bookmarks.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.weight(1f)
          )

          TextButton(
            onClick = { isEnglish = !isEnglish },
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFF1E3A8A))
              .height(32.dp)
          ) {
            Text(
              text = if (isEnglish) "ENG | हिन्" else "हिन् | ENG",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  ) { paddingValues ->
    if (bookmarks.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(54.dp))
          Spacer(modifier = Modifier.height(12.dp))
          Text("No Bookmarked Questions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
          Text(
            "Bookmark important or tricky questions during mock tests to review them here anytime.",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(bookmarks, key = { it.questionId }) { bookmark ->
          val options = (if (isEnglish) bookmark.optionsEnJoined else bookmark.optionsHiJoined).split("|||")

          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3852))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${bookmark.testTitle} • ${bookmark.topic}",
                  color = Color(0xFF38BDF8),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )

                IconButton(
                  onClick = { onRemoveBookmark(bookmark.questionId) },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = if (isEnglish) bookmark.questionEn else bookmark.questionHi,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Options
              val labels = listOf("A", "B", "C", "D")
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                options.forEachIndexed { idx, opt ->
                  val isCorrect = idx == bookmark.correctOptionIndex
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isCorrect) Color(0xFF064E3B) else Color(0xFF242A3D))
                      .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(if (isCorrect) Color(0xFF10B981) else Color(0xFF334155)),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(labels.getOrElse(idx) { "$idx" }, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(opt, color = Color.White, fontSize = 12.sp)
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Explanation Box
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF192033))
                  .padding(10.dp)
              ) {
                Text(
                  text = "Explanation:",
                  color = Color(0xFFF59E0B),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (isEnglish) bookmark.explanationEn else bookmark.explanationHi,
                  color = Color(0xFFCBD5E1),
                  fontSize = 12.sp,
                  lineHeight = 16.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
