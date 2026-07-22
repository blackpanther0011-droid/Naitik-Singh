package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AthleteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AthleteViewModel,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpTab by remember { mutableStateOf(false) }

    // Form fields
    var emailInput by remember { mutableStateOf("creator.fan@morninggram.in") }
    var passwordInput by remember { mutableStateOf("MorningGram#2026") }
    var nameInput by remember { mutableStateOf("Aarav Sharma") }
    var handleInput by remember { mutableStateOf("@aarav_creator") }
    var selectedRole by remember { mutableStateOf("Cinema & Music Enthusiast") }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordModal by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successToast by remember { mutableStateOf<String?>(null) }

    val roles = listOf("Cinema & Music Enthusiast", "Content Creator", "Indie Game & Tech Dev", "Digital Artist", "Sports Enthusiast")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Brand Logo Badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFEC4899))
                        )
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Morning Gram Logo",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Morning Gram",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Direct Celebrity & Fan Community Platform",
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tab Switcher: Log In vs Sign Up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(25.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(21.dp))
                        .background(if (!isSignUpTab) Color(0xFFF59E0B) else Color.Transparent)
                        .clickable {
                            isSignUpTab = false
                            errorMessage = null
                        }
                        .testTag("login_tab_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log In",
                        color = if (!isSignUpTab) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(21.dp))
                        .background(if (isSignUpTab) Color(0xFFF59E0B) else Color.Transparent)
                        .clickable {
                            isSignUpTab = true
                            errorMessage = null
                        }
                        .testTag("signup_tab_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign Up",
                        color = if (isSignUpTab) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Fields Container
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x1F222938)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AnimatedVisibility(visible = isSignUpTab) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Full Name Field
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Full Name", color = Color(0xFF94A3B8)) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFF59E0B)) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFFF59E0B),
                                    unfocusedBorderColor = Color(0x33FFFFFF)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_name_input")
                            )

                            // Handle Field
                            OutlinedTextField(
                                value = handleInput,
                                onValueChange = { handleInput = it },
                                label = { Text("Unique Handle (@username)", color = Color(0xFF94A3B8)) },
                                leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Color(0xFFF59E0B)) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFFF59E0B),
                                    unfocusedBorderColor = Color(0x33FFFFFF)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_handle_input")
                            )

                            // Role Category Selector
                            Text("Your Creator / Fan Role:", color = Color(0xFFCBD5E1), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                roles.forEach { role ->
                                    val isSelected = selectedRole == role
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedRole = role },
                                        label = { Text(role, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFF59E0B),
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color(0x22FFFFFF),
                                            labelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address / Phone Number", color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFF59E0B)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    // Password Field
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Security Password", color = Color(0xFF94A3B8)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFF59E0B)) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Password Visibility",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    if (!isSignUpTab) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showForgotPasswordModal = true }
                            )
                        }
                    }

                    // Error Banner
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Submit Primary Button
                    Button(
                        onClick = {
                            if (emailInput.isBlank() || passwordInput.isBlank()) {
                                errorMessage = "Please enter your email and password."
                            } else if (isSignUpTab && (nameInput.isBlank() || handleInput.isBlank())) {
                                errorMessage = "Please enter your name and handle."
                            } else {
                                errorMessage = null
                                if (isSignUpTab) {
                                    viewModel.signupUser(emailInput, nameInput, handleInput, selectedRole)
                                } else {
                                    viewModel.loginUser(emailInput, nameInput, selectedRole)
                                }
                                successToast = "Welcome to Morning Gram!"
                                onAuthSuccess()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button")
                    ) {
                        Text(
                            text = if (isSignUpTab) "Create Account" else "Log In to Feed",
                            color = Color.Black,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Or Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color(0x33FFFFFF))
                Text(
                    text = " OR ",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Divider(modifier = Modifier.weight(1f), color = Color(0x33FFFFFF))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Guest Login Option
            OutlinedButton(
                onClick = {
                    viewModel.loginAsGuest()
                    onAuthSuccess()
                },
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44F59E0B)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("guest_login_button")
            ) {
                Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue as Guest Fan", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "E2E Encrypted • Zero Ad Tracking • Sovereign Fan Storage",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
            }
        }
    }

    // Forgot Password Modal
    if (showForgotPasswordModal) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordModal = false },
            title = { Text("Reset Security Password", color = Color.White) },
            text = {
                Text(
                    "Enter your email address to receive an instant verification link and reset code.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotPasswordModal = false
                        successToast = "Password reset link sent to $emailInput"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Text("Send Link", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordModal = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}
