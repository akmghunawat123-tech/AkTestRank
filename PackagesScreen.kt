package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
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
import com.example.data.model.MockTestSummary
import com.example.data.model.TestPackage
import com.example.ui.components.ExamEmblemIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesScreen(
  categoryId: String,
  categoryName: String,
  packages: List<TestPackage>,
  selectedSubCategory: String,
  onSubCategorySelect: (String) -> Unit,
  onStartTest: (String) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var expandedPackageId by remember { mutableStateOf<String?>(packages.firstOrNull()?.id) }

  // Extract unique subcategories for horizontal filter chips
  val filterOptions = remember(packages) {
    val list = mutableListOf("All")
    packages.forEach { pkg ->
      if (!list.contains(pkg.subCategory)) {
        list.add(pkg.subCategory)
      }
    }
    list
  }

  val filteredPackages = remember(packages, selectedSubCategory) {
    if (selectedSubCategory == "All") packages
    else packages.filter { it.subCategory.equals(selectedSubCategory, ignoreCase = true) }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("packages_screen"),
    containerColor = Color(0xFF16181F),
    topBar = {
      // Top bar matching Screenshot 2: Deep blue background with back arrow and Title
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
            onClick = onBack,
            modifier = Modifier.testTag("packages_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }

          Text(
            text = "$categoryName Packages",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
              .weight(1f)
              .padding(start = 8.dp)
          )

          IconButton(onClick = { /* Search/filter */ }) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = Color.White
            )
          }
        }
      }
    },
    bottomBar = {
      // Bottom Bar matching Screenshot 2: "Buy Now" or "Pass Pro Active"
      Surface(
        color = Color(0xFF0A2B78),
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Pass Pro Active • All Free & Premium Mock Tests Unlocked",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Horizontal Filter Chips (Matching Screenshot 2: Blue active pill chips)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        filterOptions.forEach { filter ->
          val isSelected = filter.equals(selectedSubCategory, ignoreCase = true)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) Color(0xFF0E43AF) else Color(0xFF222633))
              .border(
                1.dp,
                if (isSelected) Color(0xFF3B82F6) else Color(0xFF333A4D),
                RoundedCornerShape(20.dp)
              )
              .clickable { onSubCategorySelect(filter) }
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .testTag("filter_chip_$filter")
          ) {
            Text(
              text = filter,
              color = if (isSelected) Color.White else Color(0xFF94A3B8),
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }

      // Package Cards List
      LazyColumn(
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
      ) {
        items(filteredPackages, key = { it.id }) { pkg ->
          PackageCard(
            testPackage = pkg,
            categoryId = categoryId,
            isExpanded = expandedPackageId == pkg.id,
            onToggleExpand = {
              expandedPackageId = if (expandedPackageId == pkg.id) null else pkg.id
            },
            onStartTest = onStartTest
          )
        }
      }
    }
  }
}

@Composable
fun PackageCard(
  testPackage: TestPackage,
  categoryId: String,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onStartTest: (String) -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("package_card_${testPackage.id}"),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF232733)),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2F3547))
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Main card header matching Screenshot 2
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onToggleExpand)
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Emblem Icon
        ExamEmblemIcon(
          iconType = categoryId,
          size = 50.dp
        )

        Spacer(modifier = Modifier.width(14.dp))

        // Center Content
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = testPackage.title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(4.dp))

          // "1119 Total tests | 1039 Free Test"
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${testPackage.totalTests} Total tests",
              color = Color(0xFF94A3B8),
              fontSize = 12.sp
            )
            Text(
              text = " | ",
              color = Color(0xFF64748B),
              fontSize = 12.sp
            )
            Text(
              text = "${testPackage.freeTests} Free Test",
              color = Color(0xFF10B981),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // "View Test" Button from Screenshot 2
            Button(
              onClick = onToggleExpand,
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0E43AF),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(20.dp),
              contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
              modifier = Modifier
                .height(34.dp)
                .testTag("view_test_btn_${testPackage.id}")
            ) {
              Text(
                text = if (isExpanded) "Hide Tests" else "View Test",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            // Lock / Unlocked Status Badge from Screenshot 2
            Icon(
              imageVector = if (testPackage.isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
              contentDescription = "Status",
              tint = if (testPackage.isUnlocked) Color(0xFFEF4444) else Color(0xFFEF4444),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Chevron arrow on right
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
          contentDescription = "Expand",
          tint = Color(0xFF94A3B8),
          modifier = Modifier.size(16.dp)
        )
      }

      // Expanded list of specific Mock Tests inside this package
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1B1E28))
            .padding(horizontal = 14.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "Available Mock Tests in ${testPackage.subCategory}:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF38BDF8)
          )

          testPackage.tests.forEach { testSummary ->
            MockTestItemRow(
              summary = testSummary,
              onStart = { onStartTest(testSummary.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun MockTestItemRow(
  summary: MockTestSummary,
  onStart: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("test_item_${summary.id}"),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF242836)),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF32384A))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        val isRailwayTest = summary.id.startsWith("ntpc_") || summary.id.startsWith("alp_") || summary.packageId.contains("rrb") || summary.packageId.contains("railway")
        val vendorBadgeText = if (isRailwayTest) "TCS iON CBT" else "Eduquity CBT"
        val vendorBadgeColor = if (isRailwayTest) Color(0xFF1D4ED8) else Color(0xFF0284C7)

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(3.dp))
              .background(vendorBadgeColor)
              .padding(horizontal = 5.dp, vertical = 2.dp)
          ) {
            Text(vendorBadgeText, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }

          Text(
            text = summary.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Text(
            text = "${summary.totalQuestions} Qs",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
          )
          Text(
            text = "•",
            color = Color(0xFF64748B),
            fontSize = 12.sp
          )
          Text(
            text = "${summary.durationMinutes} Mins",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
          )
          Text(
            text = "•",
            color = Color(0xFF64748B),
            fontSize = 12.sp
          )
          Text(
            text = "${summary.totalMarks.toInt()} Marks",
            color = Color(0xFF38BDF8),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = "${summary.attemptsCount} Students Attempted",
          color = Color(0xFF10B981),
          fontSize = 11.sp
        )
      }

      Button(
        onClick = onStart,
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF10B981),
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier
          .height(34.dp)
          .testTag("start_test_btn_${summary.id}")
      ) {
        Text(
          text = "Start",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
