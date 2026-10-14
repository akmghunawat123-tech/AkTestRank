package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.ExamCategory
import com.example.ui.components.ExamEmblemIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(
  categories: List<ExamCategory>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  onCategorySelected: (String) -> Unit,
  onPracticeClick: () -> Unit,
  onMyTestsClick: () -> Unit,
  onAnalyticsClick: () -> Unit,
  onStudyPlanClick: () -> Unit,
  onBookmarksClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredCategories = remember(categories, searchQuery) {
    if (searchQuery.isBlank()) categories
    else categories.filter {
      it.name.contains(searchQuery, ignoreCase = true) ||
      it.subtitle.contains(searchQuery, ignoreCase = true) ||
      it.filterTags.any { tag -> tag.contains(searchQuery, ignoreCase = true) }
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("category_selection_screen"),
    containerColor = Color(0xFF16181F),
    topBar = {
      // Header matching Screenshot 1: Deep blue toolbar with back arrow and title "Test Series"
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF0A2B78))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { /* Home root */ },
            modifier = Modifier.testTag("back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Text(
            text = "Test Series",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
              .weight(1f)
              .padding(start = 8.dp)
          )

          IconButton(
            onClick = onBookmarksClick,
            modifier = Modifier.testTag("bookmarks_nav_button")
          ) {
            Icon(
              imageVector = Icons.Default.BookmarkBorder,
              contentDescription = "Bookmarks",
              tint = Color.White
            )
          }
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF16181F))
          .navigationBarsPadding()
      ) {
        // "Purchased" / "Pass Active" Green Button from Screenshot 1
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Button(
            onClick = { /* Pass status info */ },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("purchased_pass_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF159648),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Active Pass",
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Purchased (Pass Pro Active)",
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp
            )
          }
        }

        // Navigation Bar tabs
        NavigationBar(
          containerColor = Color(0xFF12141A),
          tonalElevation = 0.dp,
          modifier = Modifier.height(64.dp)
        ) {
          NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.ListAlt, contentDescription = "Test Series") },
            label = { Text("Tests", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              selectedTextColor = Color(0xFF38BDF8),
              indicatorColor = Color(0xFF1E3A8A),
              unselectedIconColor = Color(0xFF64748B),
              unselectedTextColor = Color(0xFF64748B)
            )
          )
          NavigationBarItem(
            selected = false,
            onClick = onPracticeClick,
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
            onClick = onAnalyticsClick,
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
            label = { Text("Analytics", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              unselectedIconColor = Color(0xFF94A3B8),
              unselectedTextColor = Color(0xFF94A3B8)
            )
          )
          NavigationBarItem(
            selected = false,
            onClick = onStudyPlanClick,
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Study Plan") },
            label = { Text("AI Plan", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
              unselectedIconColor = Color(0xFF94A3B8),
              unselectedTextColor = Color(0xFF94A3B8)
            )
          )
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Search exam, e.g. SSC CGL, RRB...", color = Color(0xFF64748B)) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF94A3B8))
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChange("") }) {
              Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF94A3B8))
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp)
          .testTag("exam_search_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF1E222D),
          unfocusedContainerColor = Color(0xFF1E222D),
          focusedBorderColor = Color(0xFF3B82F6),
          unfocusedBorderColor = Color(0xFF2E3547),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true
      )

      // Grid of Categories (Matches 2-column cards layout from Screenshot 1)
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredCategories, key = { it.id }) { cat ->
          CategoryGridCard(
            category = cat,
            onClick = { onCategorySelected(cat.id) }
          )
        }
      }
    }
  }
}

@Composable
fun CategoryGridCard(
  category: ExamCategory,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(118.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(Color(0xFF242833))
      .border(1.dp, Color(0xFF323746), RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .testTag("category_card_${category.id}")
      .padding(14.dp)
  ) {
    // Title on top left
    Column(
      modifier = Modifier
        .align(Alignment.TopStart)
        .fillMaxWidth(0.65f)
    ) {
      Text(
        text = category.name,
        color = Color(0xFFEAEFF8),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2
      )
      Text(
        text = "${category.totalTests} Tests",
        color = Color(0xFF22C55E),
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 2.dp)
      )
    }

    // Emblem Icon on bottom right (matching Screenshot 1)
    Box(
      modifier = Modifier.align(Alignment.BottomEnd)
    ) {
      ExamEmblemIcon(
        iconType = category.iconType,
        size = 52.dp
      )
    }
  }
}
