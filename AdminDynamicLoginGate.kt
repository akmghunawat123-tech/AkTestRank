package com.example.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdminDynamicLoginGate(
  onUnlock: (String) -> Boolean,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var loginMode by remember { mutableStateOf(0) } // 0 = 4-Digit Security PIN, 1 = Master Admin ID/Password
  var pinCode by remember { mutableStateOf("") }
  var usernameInput by remember { mutableStateOf("admin") }
  var passwordInput by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var isSuccessUnlocked by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  // Pulsing animation for cyber shield glow
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  fun submitAttempt(key: String) {
    if (key.isBlank()) {
      errorMessage = "Please enter your security key / PIN."
      return
    }
    val success = onUnlock(key.trim())
    if (success) {
      isSuccessUnlocked = true
      errorMessage = null
    } else {
      errorMessage = "Access Denied: Invalid Security Key / PIN! Hint: Use 7860"
      coroutineScope.launch {
        delay(3500)
        if (errorMessage != null) {
          errorMessage = "Master Demo Key available: Click '7860' below"
        }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF070B19),
            Color(0xFF0F172A),
            Color(0xFF06132D)
          )
        )
      )
      .testTag("admin_security_gate")
  ) {
    // Top glowing circuit decor lines
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .background(
          Brush.radialGradient(
            colors = listOf(
              Color(0xFF1D4ED8).copy(alpha = 0.28f),
              Color.Transparent
            )
          )
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Navigation Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(18.dp)
          )
        }

        // Live Security Status Chip
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color(0xFF0F2547),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "ADMIN PORTAL ONLINE",
              color = Color(0xFF60A5FA),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.6.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Dynamic Shield Emblem with Glowing Rings
      Box(
        modifier = Modifier.size(92.dp),
        contentAlignment = Alignment.Center
      ) {
        // Outer glowing ring
        Box(
          modifier = Modifier
            .size(90.dp)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(
                  Color(0xFF2563EB).copy(alpha = glowAlpha * 0.45f),
                  Color(0xFF38BDF8).copy(alpha = 0.05f)
                )
              )
            )
            .border(
              width = 1.5.dp,
              color = Color(0xFF38BDF8).copy(alpha = glowAlpha),
              shape = CircleShape
            )
        )

        // Inner Shield Container
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                colors = listOf(
                  Color(0xFF1D4ED8),
                  Color(0xFF0F172A)
                )
              )
            )
            .border(1.5.dp, Color(0xFF60A5FA), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isSuccessUnlocked) Icons.Default.LockOpen else Icons.Default.Security,
            contentDescription = "Admin Security Shield",
            tint = if (isSuccessUnlocked) Color(0xFF34D399) else Color.White,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Admin Command Center",
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.3.sp
      )

      Text(
        text = "Role-Based Access Control • Question Studio & User Activation",
        color = Color(0xFF94A3B8),
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
      )

      // Dynamic Mode Switcher (PIN Mode vs Credentials Mode)
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp)),
        color = Color(0xFF1E293B).copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(9.dp))
              .background(if (loginMode == 0) Color(0xFF2563EB) else Color.Transparent)
              .clickable {
                loginMode = 0
                errorMessage = null
              }
              .padding(vertical = 9.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Pin,
                contentDescription = null,
                tint = if (loginMode == 0) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Security PIN Key",
                color = if (loginMode == 0) Color.White else Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = if (loginMode == 0) FontWeight.Bold else FontWeight.Medium
              )
            }
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(9.dp))
              .background(if (loginMode == 1) Color(0xFF2563EB) else Color.Transparent)
              .clickable {
                loginMode = 1
                errorMessage = null
              }
              .padding(vertical = 9.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Key,
                contentDescription = null,
                tint = if (loginMode == 1) Color.White else Color(0xFF94A3B8),
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Admin Credentials",
                color = if (loginMode == 1) Color.White else Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = if (loginMode == 1) FontWeight.Bold else FontWeight.Medium
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Main Dynamic Auth Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
          1.2.dp,
          if (errorMessage != null) Color(0xFFEF4444) else Color(0xFF2A3A5D)
        )
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (loginMode == 0) {
            // MODE 0: 4-DIGIT PIN GATE WITH DYNAMIC DOTS & NUMERIC KEYPAD
            Text(
              text = "ENTER 4-DIGIT SECURITY KEY",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic PIN Code Display Dots
            Row(
              horizontalArrangement = Arrangement.spacedBy(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              for (i in 0 until 4) {
                val isFilled = i < pinCode.length
                val isCurrent = i == pinCode.length
                val dotColor by animateColorAsState(
                  targetValue = when {
                    isFilled -> Color(0xFF38BDF8)
                    isCurrent -> Color(0xFF60A5FA)
                    else -> Color(0xFF1E293B)
                  },
                  label = "dot_color"
                )

                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(
                      width = if (isFilled || isCurrent) 2.dp else 1.dp,
                      color = if (isFilled) Color(0xFF38BDF8) else Color(0xFF475569),
                      shape = CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (isFilled) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Standard text field option (also allows direct keyboard entry)
            OutlinedTextField(
              value = pinCode,
              onValueChange = {
                if (it.length <= 6) {
                  pinCode = it
                  errorMessage = null
                  if (it.length == 4) {
                    submitAttempt(it)
                  }
                }
              },
              placeholder = { Text("Or type key directly (7860)", color = Color(0xFF64748B), fontSize = 12.sp) },
              visualTransformation = PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_pin_input"),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Integrated Touch Numpad (1-9, Clear, 0, Backspace)
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              val keypadRows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "⌫")
              )

              for (row in keypadRows) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  for (btn in row) {
                    Button(
                      onClick = {
                        errorMessage = null
                        when (btn) {
                          "C" -> pinCode = ""
                          "⌫" -> if (pinCode.isNotEmpty()) pinCode = pinCode.dropLast(1)
                          else -> {
                            if (pinCode.length < 6) {
                              pinCode += btn
                              if (pinCode.length == 4) {
                                submitAttempt(pinCode)
                              }
                            }
                          }
                        }
                      },
                      modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                      shape = RoundedCornerShape(10.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = when (btn) {
                          "C" -> Color(0xFF451A1A)
                          "⌫" -> Color(0xFF1E293B)
                          else -> Color(0xFF1E2842)
                        }
                      ),
                      contentPadding = PaddingValues(0.dp)
                    ) {
                      Text(
                        text = btn,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (btn) {
                          "C" -> Color(0xFFFCA5A5)
                          "⌫" -> Color(0xFF94A3B8)
                          else -> Color.White
                        },
                        fontFamily = FontFamily.Monospace
                      )
                    }
                  }
                }
              }
            }

          } else {
            // MODE 1: MASTER ADMIN USERNAME + PASSWORD
            Text(
              text = "MASTER ADMIN CREDENTIALS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = usernameInput,
              onValueChange = {
                usernameInput = it
                errorMessage = null
              },
              label = { Text("Admin Identifier / Username", color = Color(0xFF94A3B8), fontSize = 12.sp) },
              leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
              },
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth(),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = passwordInput,
              onValueChange = {
                passwordInput = it
                errorMessage = null
              },
              label = { Text("Password or Master PIN", color = Color(0xFF94A3B8), fontSize = 12.sp) },
              leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(18.dp))
              },
              trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                  Icon(
                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = "Toggle password visibility",
                    tint = Color(0xFF94A3B8)
                  )
                }
              },
              visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F172A),
                unfocusedContainerColor = Color(0xFF0F172A),
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth(),
              singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = {
                submitAttempt(passwordInput.ifBlank { "7860" })
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
              Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Sign In to Admin Portal", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
          }

          // Error Message Banner with dynamic animation
          AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
              color = Color(0xFF450A0A),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = errorMessage ?: "",
                  color = Color(0xFFFEE2E2),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // ⚡ Dynamic One-Tap Quick Access Demo Chip
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                pinCode = "7860"
                passwordInput = "7860"
                submitAttempt("7860")
              },
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color(0xFFFACC15),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "⚡ Instant Access Key: Tap 7860 (Auto-Fill & Unlock)",
                color = Color(0xFF38BDF8),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Bottom Security Telemetry & Return Button
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        OutlinedButton(
          onClick = onBack,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
          Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Return to Student Dashboard", fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.GppGood,
            contentDescription = null,
            tint = Color(0xFF34D399),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "TLS 1.3 End-to-End Encrypted Session • Local Secure Sandbox",
            color = Color(0xFF64748B),
            fontSize = 10.sp
          )
        }
      }
    }
  }
}
