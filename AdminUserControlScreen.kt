package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ManagedUserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserControlScreen(
  users: List<ManagedUserEntity>,
  onUpdateUserStatus: (String, String) -> Unit,
  onToggleUserAccess: (String, Boolean) -> Unit,
  onUpdateUserRole: (String, String) -> Unit,
  onDeleteUser: (String) -> Unit,
  onCreateUser: (
    name: String,
    email: String,
    phone: String,
    targetExam: String,
    activePackage: String,
    status: String
  ) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf("ALL") } // ALL, ACTIVE, PENDING, INACTIVE, ADMIN
  var showAddUserDialog by remember { mutableStateOf(false) }
  var userPendingDelete by remember { mutableStateOf<ManagedUserEntity?>(null) }
  var statusFeedbackMessage by remember { mutableStateOf<String?>(null) }

  // Compute counts
  val totalCount = users.size
  val activeCount = users.count { it.status == "ACTIVE" }
  val pendingCount = users.count { it.status == "PENDING" }
  val inactiveCount = users.count { it.status == "INACTIVE" }
  val adminCount = users.count { it.role == "Admin" }

  // Filtered list
  val filteredUsers = users.filter { user ->
    val matchesSearch = searchQuery.isBlank() ||
      user.name.contains(searchQuery, ignoreCase = true) ||
      user.email.contains(searchQuery, ignoreCase = true) ||
      user.phone.contains(searchQuery, ignoreCase = true) ||
      user.targetExam.contains(searchQuery, ignoreCase = true)

    val matchesFilter = when (selectedFilter) {
      "ACTIVE" -> user.status == "ACTIVE"
      "PENDING" -> user.status == "PENDING"
      "INACTIVE" -> user.status == "INACTIVE"
      "ADMIN" -> user.role == "Admin"
      else -> true
    }

    matchesSearch && matchesFilter
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
  ) {
    // 1. Metric Cards Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF131C31))
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "User Control & Activation",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Activate candidates, manage mock test access & permissions",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )
        }

        // Add candidate button
        Button(
          onClick = { showAddUserDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add Candidate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4 Metric Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetricSummaryCard(
          title = "Total Users",
          count = totalCount.toString(),
          color = Color(0xFF38BDF8),
          modifier = Modifier.weight(1f)
        )
        MetricSummaryCard(
          title = "Active",
          count = activeCount.toString(),
          color = Color(0xFF34D399),
          modifier = Modifier.weight(1f)
        )
        MetricSummaryCard(
          title = "Pending",
          count = pendingCount.toString(),
          color = Color(0xFFFBBF24),
          modifier = Modifier.weight(1f)
        )
        MetricSummaryCard(
          title = "Suspended",
          count = inactiveCount.toString(),
          color = Color(0xFFF87171),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 2. Search & Filter Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF1A2238))
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by student name, email, phone or exam...", color = Color(0xFF64748B), fontSize = 12.sp) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
            }
          }
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color(0xFF0F172A),
          unfocusedContainerColor = Color(0xFF0F172A),
          focusedBorderColor = Color(0xFF38BDF8),
          unfocusedBorderColor = Color(0xFF334155),
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Filter Chips Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        StatusFilterChip(
          title = "All ($totalCount)",
          isSelected = selectedFilter == "ALL",
          activeColor = Color(0xFF2563EB)
        ) { selectedFilter = "ALL" }

        StatusFilterChip(
          title = "Active ($activeCount)",
          isSelected = selectedFilter == "ACTIVE",
          activeColor = Color(0xFF059669)
        ) { selectedFilter = "ACTIVE" }

        StatusFilterChip(
          title = "Pending Activation ($pendingCount)",
          isSelected = selectedFilter == "PENDING",
          activeColor = Color(0xFFD97706)
        ) { selectedFilter = "PENDING" }

        StatusFilterChip(
          title = "Inactive ($inactiveCount)",
          isSelected = selectedFilter == "INACTIVE",
          activeColor = Color(0xFFDC2626)
        ) { selectedFilter = "INACTIVE" }

        StatusFilterChip(
          title = "Admins ($adminCount)",
          isSelected = selectedFilter == "ADMIN",
          activeColor = Color(0xFF7C3AED)
        ) { selectedFilter = "ADMIN" }
      }
    }

    // Feedback Notice if any
    statusFeedbackMessage?.let { msg ->
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        color = Color(0xFF065F46),
        shape = RoundedCornerShape(8.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = msg, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
          IconButton(onClick = { statusFeedbackMessage = null }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(14.dp))
          }
        }
      }
    }

    // 3. User Cards List
    if (filteredUsers.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.PersonOff, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(48.dp))
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "No candidates match this filter.",
            color = Color(0xFF94A3B8),
            fontSize = 14.sp
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredUsers, key = { it.id }) { user ->
          UserControlCard(
            user = user,
            onActivateToggle = {
              val newStatus = if (user.status == "ACTIVE") "INACTIVE" else "ACTIVE"
              onUpdateUserStatus(user.id, newStatus)
              // If activating, also ensure test access is granted
              if (newStatus == "ACTIVE") {
                onToggleUserAccess(user.id, true)
              }
              statusFeedbackMessage = "Updated ${user.name} status to $newStatus"
            },
            onToggleAccess = { enabled ->
              onToggleUserAccess(user.id, enabled)
              statusFeedbackMessage = "Mock test access for ${user.name}: ${if (enabled) "Granted" else "Restricted"}"
            },
            onRoleToggle = {
              val newRole = if (user.role == "Admin") "Student" else "Admin"
              onUpdateUserRole(user.id, newRole)
              statusFeedbackMessage = "Updated ${user.name} role to $newRole"
            },
            onDelete = { userPendingDelete = user }
          )
        }
      }
    }
  }

  // Dialog: Add New Candidate
  if (showAddUserDialog) {
    AddUserDialog(
      onDismiss = { showAddUserDialog = false },
      onConfirm = { name, email, phone, targetExam, pkg, status ->
        onCreateUser(name, email, phone, targetExam, pkg, status)
        showAddUserDialog = false
        statusFeedbackMessage = "New student $name added successfully with status $status!"
      }
    )
  }

  // Dialog: Delete Confirmation
  userPendingDelete?.let { user ->
    AlertDialog(
      onDismissRequest = { userPendingDelete = null },
      containerColor = Color(0xFF1E293B),
      title = {
        Text("Remove Candidate?", color = Color.White, fontWeight = FontWeight.Bold)
      },
      text = {
        Text(
          "Are you sure you want to delete ${user.name} (${user.email})? Their test attempt history and account will be removed.",
          color = Color(0xFFCBD5E1),
          fontSize = 13.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            onDeleteUser(user.id)
            userPendingDelete = null
            statusFeedbackMessage = "Removed candidate ${user.name}."
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
        ) {
          Text("Delete", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { userPendingDelete = null }) {
          Text("Cancel", color = Color(0xFF94A3B8))
        }
      }
    )
  }
}

@Composable
fun MetricSummaryCard(
  title: String,
  count: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    shape = RoundedCornerShape(8.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = count,
        color = color,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold
      )
      Text(
        text = title,
        color = Color(0xFF94A3B8),
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}

@Composable
fun StatusFilterChip(
  title: String,
  isSelected: Boolean,
  activeColor: Color,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    color = if (isSelected) activeColor else Color(0xFF0F172A),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isSelected) activeColor else Color(0xFF334155)
    )
  ) {
    Text(
      text = title,
      color = if (isSelected) Color.White else Color(0xFF94A3B8),
      fontSize = 11.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
  }
}

@Composable
fun UserControlCard(
  user: ManagedUserEntity,
  onActivateToggle: () -> Unit,
  onToggleAccess: (Boolean) -> Unit,
  onRoleToggle: () -> Unit,
  onDelete: () -> Unit
) {
  val statusColor = when (user.status) {
    "ACTIVE" -> Color(0xFF10B981)
    "PENDING" -> Color(0xFFF59E0B)
    else -> Color(0xFFEF4444)
  }

  val dateStr = remember(user.joinedAt) {
    try {
      val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
      sdf.format(Date(user.joinedAt))
    } catch (e: Exception) {
      "Recently"
    }
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("user_card_${user.id}"),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF161F33)),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334F))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row: Avatar + Name + Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Avatar circle with status dot
        Box(contentAlignment = Alignment.BottomEnd) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(
                if (user.role == "Admin") Color(0xFF7C3AED) else Color(0xFF1D4ED8)
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = user.name.take(2).uppercase(),
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Small status dot
          Box(
            modifier = Modifier
              .size(12.dp)
              .clip(CircleShape)
              .background(statusColor)
              .border(2.dp, Color(0xFF161F33), CircleShape)
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = user.name,
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            if (user.role == "Admin") {
              Surface(
                color = Color(0xFF6D28D9),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "ADMIN",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.ExtraBold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }
          }

          Text(
            text = user.email,
            color = Color(0xFF94A3B8),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Status Tag
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = statusColor.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(statusColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = user.status,
              color = statusColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // User Details Grid
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF0F172A))
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Target Exam:", color = Color(0xFF64748B), fontSize = 11.sp)
          Text(text = user.targetExam, color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Contact Phone:", color = Color(0xFF64748B), fontSize = 11.sp)
          Text(text = user.phone, color = Color(0xFFCBD5E1), fontSize = 11.sp)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Registered:", color = Color(0xFF64748B), fontSize = 11.sp)
          Text(text = dateStr, color = Color(0xFFCBD5E1), fontSize = 11.sp)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Tests Attempted / Avg Score:", color = Color(0xFF64748B), fontSize = 11.sp)
          Text(
            text = "${user.testsAttempted} tests (${user.avgScore} pts)",
            color = Color(0xFF34D399),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Active Package:", color = Color(0xFF64748B), fontSize = 11.sp)
          Text(
            text = user.activePackage,
            color = Color(0xFFFBBF24),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Controls Strip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // 1. Direct Activation Button (Primary control requested by user)
        Button(
          onClick = onActivateToggle,
          modifier = Modifier.weight(1.3f),
          colors = ButtonDefaults.buttonColors(
            containerColor = when (user.status) {
              "ACTIVE" -> Color(0xFFB45309) // Amber/deactivate
              "PENDING" -> Color(0xFF059669) // Green/activate
              else -> Color(0xFF2563EB) // Blue/reactivate
            }
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = when (user.status) {
              "ACTIVE" -> Icons.Default.PauseCircle
              "PENDING" -> Icons.Default.CheckCircle
              else -> Icons.Default.PlayCircle
            },
            contentDescription = null,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = when (user.status) {
              "ACTIVE" -> "Suspend / Deactivate"
              "PENDING" -> "Activate Candidate"
              else -> "Reactivate User"
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // 2. Full Test Access Toggle
        OutlinedButton(
          onClick = { onToggleAccess(!user.hasFullTestAccess) },
          modifier = Modifier.weight(1f),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (user.hasFullTestAccess) Color(0xFF0F3628) else Color(0xFF2D1616),
            contentColor = if (user.hasFullTestAccess) Color(0xFF34D399) else Color(0xFFF87171)
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (user.hasFullTestAccess) Color(0xFF059669) else Color(0xFFDC2626)
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = if (user.hasFullTestAccess) Icons.Default.LockOpen else Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (user.hasFullTestAccess) "Test Pass: ON" else "Test Pass: OFF",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // 3. Promote/Demote Role
        IconButton(
          onClick = onRoleToggle,
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
        ) {
          Icon(
            imageVector = if (user.role == "Admin") Icons.Default.Person else Icons.Default.AdminPanelSettings,
            contentDescription = "Switch Role",
            tint = Color(0xFFA78BFA),
            modifier = Modifier.size(18.dp)
          )
        }

        // 4. Delete User
        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF3F1B1B))
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete",
            tint = Color(0xFFF87171),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}

@Composable
fun AddUserDialog(
  onDismiss: () -> Unit,
  onConfirm: (
    name: String,
    email: String,
    phone: String,
    targetExam: String,
    activePackage: String,
    status: String
  ) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+91 ") }
  var targetExam by remember { mutableStateOf("SSC CGL (Tier 1 & 2)") }
  var activePackage by remember { mutableStateOf("All India Live Mock Test Pass") }
  var initialStatus by remember { mutableStateOf("ACTIVE") } // ACTIVE or PENDING
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val examOptions = listOf(
    "SSC CGL (Tier 1 & 2)",
    "RRB NTPC (CBT 1 & 2)",
    "SSC CHSL 10+2",
    "RRB ALP & Technician",
    "SSC CPO SI",
    "SSC MTS & Havaldar",
    "Banking - IBPS PO"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF131C31),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add New Candidate", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Candidate Full Name", color = Color(0xFF94A3B8), fontSize = 12.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF0F172A),
            unfocusedContainerColor = Color(0xFF0F172A),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email Address", color = Color(0xFF94A3B8), fontSize = 12.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF0F172A),
            unfocusedContainerColor = Color(0xFF0F172A),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Mobile Phone Number", color = Color(0xFF94A3B8), fontSize = 12.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF0F172A),
            unfocusedContainerColor = Color(0xFF0F172A),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Text("Target Exam:", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          examOptions.forEach { exam ->
            val isSel = exam == targetExam
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { targetExam = exam },
              color = if (isSel) Color(0xFF2563EB) else Color(0xFF1E293B),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = exam,
                color = if (isSel) Color.White else Color(0xFF94A3B8),
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
              )
            }
          }
        }

        Text("Initial Activation Status:", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .clickable { initialStatus = "ACTIVE" },
            color = if (initialStatus == "ACTIVE") Color(0xFF065F46) else Color(0xFF1E293B),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (initialStatus == "ACTIVE") Color(0xFF34D399) else Color(0xFF334155)
            )
          ) {
            Column(
              modifier = Modifier.padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("ACTIVE", color = if (initialStatus == "ACTIVE") Color(0xFF34D399) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text("Instant Test Access", color = Color(0xFFCBD5E1), fontSize = 9.sp)
            }
          }

          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .clickable { initialStatus = "PENDING" },
            color = if (initialStatus == "PENDING") Color(0xFF78350F) else Color(0xFF1E293B),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (initialStatus == "PENDING") Color(0xFFFBBF24) else Color(0xFF334155)
            )
          ) {
            Column(
              modifier = Modifier.padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("PENDING", color = if (initialStatus == "PENDING") Color(0xFFFBBF24) else Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text("Awaiting Approval", color = Color(0xFFCBD5E1), fontSize = 9.sp)
            }
          }
        }

        errorMessage?.let { err ->
          Text(text = err, color = Color(0xFFEF4444), fontSize = 11.sp)
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || email.isBlank()) {
            errorMessage = "Name and Email are required!"
            return@Button
          }
          onConfirm(name.trim(), email.trim(), phone.trim(), targetExam, activePackage, initialStatus)
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
      ) {
        Text("Save & Add Candidate", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel", color = Color(0xFF94A3B8))
      }
    }
  )
}
