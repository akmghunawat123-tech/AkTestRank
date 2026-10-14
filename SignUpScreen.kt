package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserProfileEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
  userProfile: UserProfileEntity?,
  onSaveProfile: (name: String, email: String, phone: String, targetExam: String, lang: String, role: String) -> Unit,
  onBack: () -> Unit,
  onAdminClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isSignUpTab by remember { mutableStateOf(userProfile == null) }
  var name by remember { mutableStateOf(userProfile?.name ?: "Asharam Meena") }
  var email by remember { mutableStateOf(userProfile?.email ?: "asharammeena2338@gmail.com") }
  var phone by remember { mutableStateOf(userProfile?.phone ?: "9876543210") }
  var password by remember { mutableStateOf("pass1234") }
  var isPasswordVisible by remember { mutableStateOf(false) }

  var selectedTargetExam by remember { mutableStateOf(userProfile?.targetExam ?: "SSC CGL 2026") }
  var selectedLanguage by remember { mutableStateOf(userProfile?.preferredLanguage ?: "English") }
  var selectedRole by remember { mutableStateOf(userProfile?.role ?: "Student") }

  var showSuccessNotice by remember { mutableStateOf(false) }

  val targetExamsList = listOf(
    "SSC CGL 2026",
    "SSC CHSL 2026",
    "Railways NTPC",
    "UP Police / SI",
    "Delhi Police",
    "Banking SBI PO",
    "Defence NDA/CDS"
  )

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("signup_screen"),
    containerColor = Color(0xFF131620),
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
            .padding(horizontal = 8.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("signup_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }

          Text(
            text = if (userProfile != null && userProfile.isLoggedIn) "Aspirant Profile" else "Account & Onboarding",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
              .weight(1f)
              .padding(start = 8.dp)
          )

          // Admin button shortcut
          TextButton(
            onClick = onAdminClick,
            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF38BDF8))
          ) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Admin", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Hero Header Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2436)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C3852))
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(Color(0xFF0E43AF)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (userProfile != null && userProfile.isLoggedIn) userProfile.name else "Welcome Aspirant!",
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = if (userProfile != null && userProfile.isLoggedIn) "Target: ${userProfile.targetExam} • ${userProfile.role}" else "Join 1.2M+ competitive exam candidates",
              color = Color(0xFF38BDF8),
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }

      // Success notification if just saved
      if (showSuccessNotice) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Profile and credentials saved successfully!",
              color = Color.White,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // Tab switcher: Sign Up vs Sign In
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF1B2030))
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSignUpTab) Color(0xFF0E43AF) else Color.Transparent)
            .clickable { isSignUpTab = true }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Create Account",
            color = if (isSignUpTab) Color.White else Color(0xFF94A3B8),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (!isSignUpTab) Color(0xFF0E43AF) else Color.Transparent)
            .clickable { isSignUpTab = false }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Sign In",
            color = if (!isSignUpTab) Color.White else Color(0xFF94A3B8),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Form Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2333)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C354E))
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (isSignUpTab) {
            // Full Name
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Full Name", color = Color(0xFF94A3B8)) },
              leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF94A3B8)) },
              modifier = Modifier.fillMaxWidth().testTag("signup_name_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF151824),
                unfocusedContainerColor = Color(0xFF151824),
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF333D55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp),
              singleLine = true
            )
          }

          // Email
          OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth().testTag("signup_email_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF151824),
              unfocusedContainerColor = Color(0xFF151824),
              focusedBorderColor = Color(0xFF38BDF8),
              unfocusedBorderColor = Color(0xFF333D55),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
          )

          if (isSignUpTab) {
            // Mobile Number
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("Phone Number", color = Color(0xFF94A3B8)) },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF94A3B8)) },
              modifier = Modifier.fillMaxWidth().testTag("signup_phone_input"),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF151824),
                unfocusedContainerColor = Color(0xFF151824),
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF333D55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp),
              singleLine = true
            )
          }

          // Password
          OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF94A3B8)) },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Toggle password",
                  tint = Color(0xFF94A3B8)
                )
              }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color(0xFF151824),
              unfocusedContainerColor = Color(0xFF151824),
              focusedBorderColor = Color(0xFF38BDF8),
              unfocusedBorderColor = Color(0xFF333D55),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
          )

          if (isSignUpTab) {
            // Target Examination
            Text(
              text = "Target Examination:",
              color = Color(0xFFCBD5E1),
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(top = 4.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              targetExamsList.forEach { exam ->
                val isSelected = exam == selectedTargetExam
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) Color(0xFF0E43AF) else Color(0xFF242A3D))
                    .clickable { selectedTargetExam = exam }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = isSelected,
                    onClick = { selectedTargetExam = exam },
                    colors = RadioButtonDefaults.colors(
                      selectedColor = Color.White,
                      unselectedColor = Color(0xFF64748B)
                    ),
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(text = exam, color = Color.White, fontSize = 13.sp)
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Preferred Test Language
            Text(
              text = "Preferred Language Medium:",
              color = Color(0xFFCBD5E1),
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf("English", "हिन्दी (Hindi)").forEach { lang ->
                val isSel = lang.startsWith(selectedLanguage.take(3))
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) Color(0xFF10B981) else Color(0xFF252B3E))
                    .clickable { selectedLanguage = if (lang.startsWith("Eng")) "English" else "Hindi" }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = lang,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Role selection: Student vs Admin
            Text(
              text = "Account Role:",
              color = Color(0xFFCBD5E1),
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              listOf("Student", "Admin").forEach { r ->
                val isSel = r == selectedRole
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSel) Color(0xFF8B5CF6) else Color(0xFF252B3E))
                    .clickable { selectedRole = r }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = if (r == "Admin") Icons.Default.AdminPanelSettings else Icons.Default.School,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (r == "Admin") "Admin (Creator)" else "Student (Aspirant)",
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Submit Button
          Button(
            onClick = {
              onSaveProfile(
                if (isSignUpTab) name else (userProfile?.name ?: email.substringBefore("@")),
                email,
                phone,
                selectedTargetExam,
                selectedLanguage,
                selectedRole
              )
              showSuccessNotice = true
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("signup_submit_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF0E43AF),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = if (isSignUpTab) "Create Free Account" else "Sign In & Continue",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Guest / Skip button
          OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(42.dp),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569))
          ) {
            Text("Continue as Guest / Return to Home", color = Color(0xFFCBD5E1), fontSize = 13.sp)
          }
        }
      }
    }
  }
}
