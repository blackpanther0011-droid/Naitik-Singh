package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontStyle
import com.example.ui.viewmodel.AthleteViewModel

@Composable
fun ProfileScreen(
    viewModel: AthleteViewModel,
    modifier: Modifier = Modifier
) {
    val signedContract by viewModel.contractSignature.collectAsState()
    val athletes by viewModel.allAthletes.collectAsState()
    val referralsCount by viewModel.referralsCount.collectAsState()
    val referredBy by viewModel.referredBy.collectAsState()
    val userReferralCode by viewModel.userReferralCode.collectAsState()
    val simulatedReferrals by viewModel.simulatedReferrals.collectAsState()
    val hasProfileHighlight by viewModel.hasProfileHighlight.collectAsState()
    val allReels by viewModel.allReels.collectAsState()

    val activeFilterName by viewModel.activeFilterName.collectAsState()
    val activeFilterDescription by viewModel.activeFilterDescription.collectAsState()
    val activeColorMatrix by viewModel.activeColorMatrix.collectAsState()
    val isGeneratingFilter by viewModel.isGeneratingFilter.collectAsState()
    val filterAuthorCode by viewModel.filterAuthorCode.collectAsState()
    val filterTrendScore by viewModel.filterTrendScore.collectAsState()

    var showPostReelDialog by remember { mutableStateOf(false) }

    val followedCount = remember(athletes) {
        athletes.count { it.isFollowing }
    }

    // Static mock metrics
    val trainingHours = 12.5f + (referralsCount * 2) // Extra training credit for referring!
    val mentalScore = minOf(100, 95 + referralsCount)

    val myMockReels = remember(signedContract) {
        val userName = signedContract?.athleteName ?: "Indian Athlete"
        listOf(
            MyReelMock("reel_my_1", "$userName - Sprint 100m", "SPRINT", "#F59E0B", "#EF4444"),
            MyReelMock("reel_my_2", "$userName - Core Workout", "WEIGHTS", "#10B981", "#3B82F6"),
            MyReelMock("reel_my_3", "$userName - Breathing Drill", "SPRINT", "#6366F1", "#A855F7")
        )
    }

    val userPostedReels = remember(allReels) {
        allReels.filter { it.athleteId == "user_athlete" }.map {
            MyReelMock(
                id = it.id,
                title = it.description,
                type = it.videoActionType,
                colorStart = it.thumbnailGradientStart,
                colorEnd = it.thumbnailGradientEnd,
                filterName = it.filterName,
                filterDescription = it.filterDescription,
                colorMatrixVals = it.colorMatrixVals
            )
        }
    }

    val combinedReels = remember(myMockReels, userPostedReels) {
        userPostedReels + myMockReels
    }

    val myReelsCount = combinedReels.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1113))
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App brand header (Cursive style matching Instagram)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Morning Gram",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "My Profile",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Card / Identity Area
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)), // Frosted translucent background
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(20.dp)) // Glass border
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar with Verification Badge and Custom Highlight Ring Overlay
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(110.dp)
                    ) {
                        if (hasProfileHighlight) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse_ring")
                            val rotationAngle by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(4000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ),
                                label = "rotate"
                            )

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(
                                        width = 3.5.dp,
                                        brush = Brush.sweepGradient(
                                            colors = listOf(
                                                Color(0xFFFF9933), // Saffron
                                                Color(0xFFEC4899), // Rose Pink
                                                Color(0xFF3B82F6), // Blue
                                                Color(0xFF10B981), // Emerald
                                                Color(0xFFFF9933)  // Saffron to close
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Color(0xFF3B82F6), Color(0xFF1E1B4B))
                                    )
                                )
                                .border(
                                    width = if (hasProfileHighlight) 1.5.dp else 2.dp,
                                    color = if (hasProfileHighlight) Color.White else Color(0xFF334155),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (signedContract != null) signedContract!!.athleteName.take(2).uppercase() else "IN",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Verification Checkmark icon if signed
                    if (signedContract != null) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                                .border(2.dp, Color(0xFF1E293B), CircleShape)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified Identity",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                val currentUserName by viewModel.currentUserName.collectAsState()
                val currentUserHandle by viewModel.currentUserHandle.collectAsState()
                val currentUserRole by viewModel.currentUserRole.collectAsState()

                Spacer(modifier = Modifier.height(12.dp))

                // Profile Name
                Text(
                    text = currentUserName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                // Sub-label
                Text(
                    text = "$currentUserHandle • $currentUserRole",
                    color = Color(0xFFF59E0B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Custom User Bio
                val userSpecialization by viewModel.userSpecialization.collectAsState()
                val userBio = remember(userSpecialization) {
                    "Devoted to creativity, digital focus, and direct celebrity-fan connections on Morning Gram. 🌐✨"
                }
                Text(
                    text = userBio,
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sign Out / Switch Account Button
                OutlinedButton(
                    onClick = { viewModel.logoutUser() },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33EF4444)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("logout_button")
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign Out / Switch Account", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1310B981))
                        .border(1.dp, Color(0x3310B981), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Verified Creator Account",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Direct E2E Encrypted Creator Connection Active",
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Stats Indicators Row (Optimized for 4 items in a clean responsive row)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard("Training", "${String.format(java.util.Locale.US, "%.1f", trainingHours)}h", Icons.Default.DirectionsRun, Modifier.weight(1f))
            MetricCard("Motive", "$mentalScore%", Icons.Default.LocalFireDepartment, Modifier.weight(1f))
            MetricCard("Followers", "${1200 + simulatedReferrals.size * 250}", Icons.Default.Star, Modifier.weight(1f))
            MetricCard("Following", "$followedCount", Icons.Default.People, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- TROPHY WALL & DIGITAL MILESTONES SECTION ---
        TrophyWallSection(viewModel = viewModel, trainingHours = trainingHours)

        Spacer(modifier = Modifier.height(16.dp))

        // --- REFER AN ATHLETE SECTION ---
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)), // Frosted translucent background
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(20.dp)) // Glass border
                .testTag("refer_an_athlete_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header with Sparkle/Invite icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x26FBBF24)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Refer a Friend Program",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grow Morning Gram organically & earn rewards!",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User's referral code block
                Text(
                    text = "YOUR EXCLUSIVE INVITATION CODE",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                
                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                var isCopied by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x0DFFFFFF))
                        .border(1.dp, Color(0x13FFFFFF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userReferralCode.ifEmpty { "GENERATING..." },
                        color = Color(0xFFFBBF24),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.testTag("referral_code_text")
                    )

                    Button(
                        onClick = {
                            if (userReferralCode.isNotEmpty()) {
                                val shareMessage = "Hey! Join me on Morning Gram, the premium ad-free social platform! Showcase your creative reels and use my referral code: $userReferralCode to instantly unlock the exclusive Pro Highlight Ring and Star badge! 🚀✨"
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(shareMessage))
                                isCopied = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCopied) Color(0xFF10B981) else Color(0xFF3B82F6),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("copy_referral_code_button")
                    ) {
                        Icon(
                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isCopied) "Copied!" else "Copy Invite", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Temporary reset of copied status
                LaunchedEffect(isCopied) {
                    if (isCopied) {
                        kotlinx.coroutines.delay(2000)
                        isCopied = false
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Enter referral code area
                if (referredBy == null) {
                    var inputCode by remember { mutableStateOf("") }
                    var applyError by remember { mutableStateOf<String?>(null) }
                    var applySuccess by remember { mutableStateOf(false) }

                    Text(
                        text = "WERE YOU INVITED BY A FRIEND?",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputCode,
                            onValueChange = {
                                inputCode = it
                                applyError = null
                            },
                            placeholder = { Text("Enter code (e.g. NEERAJ-1234)", fontSize = 12.sp, color = Color(0xFF64748B)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0x26FFFFFF),
                                focusedContainerColor = Color(0x05FFFFFF),
                                unfocusedContainerColor = Color(0x05FFFFFF)
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("referral_code_input")
                        )

                        Button(
                            onClick = {
                                val codeToApply = inputCode.trim()
                                if (codeToApply.isEmpty()) {
                                    applyError = "Please enter a code"
                                } else if (codeToApply.uppercase() == userReferralCode.uppercase()) {
                                    applyError = "Cannot refer yourself"
                                } else {
                                    val success = viewModel.enterReferralCode(codeToApply)
                                    if (success) {
                                        applySuccess = true
                                        inputCode = ""
                                    } else {
                                        applyError = "Invalid code input"
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("apply_referral_code_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0x33FFFFFF))
                        ) {
                            Text("Apply", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (applyError != null) {
                        Text(
                            text = applyError!!,
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                        )
                    }
                } else {
                    // Display who referred them
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1310B981))
                            .border(1.dp, Color(0x2610B981), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Referred by Member Code: $referredBy • Highlight Unlocked! ✔",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- REWARDS & BADGES TITLE ---
                Text(
                    text = "UNLOCKED REWARDS & BADGES",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Row of 3 badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val ambassadorUnlocked = referralsCount >= 1
                    val recruiterUnlocked = referralsCount >= 3
                    val influencerUnlocked = referralsCount >= 5

                    BadgeItem(
                        title = "Ambassador",
                        subtitle = "1 Referral",
                        icon = Icons.Default.EmojiEvents,
                        isUnlocked = ambassadorUnlocked,
                        unlockedColor = Color(0xFFFF9933),
                        modifier = Modifier.weight(1f)
                    )

                    BadgeItem(
                        title = "Recruiter",
                        subtitle = "3 Referrals",
                        icon = Icons.Default.Whatshot,
                        isUnlocked = recruiterUnlocked,
                        unlockedColor = Color(0xFFEC4899),
                        modifier = Modifier.weight(1f)
                    )

                    BadgeItem(
                        title = "Influencer",
                        subtitle = "5+ Referrals",
                        icon = Icons.Default.WorkspacePremium,
                        isUnlocked = influencerUnlocked,
                        unlockedColor = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Profile Highlight unlocked notification
                if (hasProfileHighlight) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
                                )
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.sweepGradient(
                                    colors = listOf(Color(0xFFFF9933), Color(0xFFEC4899), Color(0xFFFF9933))
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Active Pro Highlight Ring",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Your profile picture now has a premium glowing gradient border!",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- LIVE SIMULATOR FOR AUDIENCE GROWTH ---
                Text(
                    text = "SANDBOX: SIMULATE PEER JOINING",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Since this is an offline sandbox environment, you can simulate peer athletes registering with your code to instantly see level-ups!",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                var peerInputName by remember { mutableStateOf("") }
                var simulateSuccessText by remember { mutableStateOf<String?>(null) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = peerInputName,
                        onValueChange = {
                            peerInputName = it
                            simulateSuccessText = null
                        },
                        placeholder = { Text("Friend's Athlete Name", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color(0x26FFFFFF),
                            focusedContainerColor = Color(0x05FFFFFF),
                            unfocusedContainerColor = Color(0x05FFFFFF)
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("simulate_peer_input")
                    )

                    Button(
                        onClick = {
                            val name = peerInputName.trim()
                            if (name.isNotEmpty()) {
                                val added = viewModel.simulateReferral(name)
                                if (added) {
                                    simulateSuccessText = "Successfully registered athlete: $name!"
                                    peerInputName = ""
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("simulate_peer_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Join", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (simulateSuccessText != null) {
                    Text(
                        text = simulateSuccessText!!,
                        color = Color(0xFF10B981),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                // List of referrals so far
                if (simulatedReferrals.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "YOUR REFERRALS (${simulatedReferrals.size}):",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x08FFFFFF))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Signed up: " + simulatedReferrals.joinToString(", "),
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    }
                }

                // Small subtle reset button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Reset Progress",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                viewModel.resetReferralProgress()
                                simulateSuccessText = "All referral metrics reset"
                            }
                            .padding(4.dp)
                            .testTag("reset_referral_progress_button")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Status & Regulations Center (Hack-Free, Ad-Free confirmation panel)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)), // Frosted translucent background
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp)) // Glass border
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Encrypted Security Firewall Status",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SecurityStatusRow("Prerecorded Logs Immunity (Hack-Free)", "SECURELY SEALED", Color(0xFF10B981))
                    SecurityStatusRow("Data Ad-Server Filter", "ENGAGED (0 ADS INJECTED)", Color(0xFF10B981))
                    SecurityStatusRow("Ministry Regulations Checksum", "ALIGNED ✔", Color(0xFFF59E0B))
                    SecurityStatusRow("User Verification System", "VERIFIED ✔", Color(0xFF10B981))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // State for detail dialog of clicked reel
        var selectedDetailReel by remember { mutableStateOf<MyReelMock?>(null) }

        // My Personal Reels Feed (Grid of published reels thumbnails)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Uploaded Reels (${combinedReels.size})",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { showPostReelDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("post_reel_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Post with AI Filter", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            combinedReels.chunked(3).forEach { rowReels ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowReels.forEach { reel ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.75f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(android.graphics.Color.parseColor(reel.colorStart)),
                                            Color(android.graphics.Color.parseColor(reel.colorEnd))
                                        )
                                    )
                                )
                                .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
                                .clickable {
                                    selectedDetailReel = reel
                                },
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (reel.type == "SPRINT") Icons.Default.DirectionsRun else Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.25f),
                                    modifier = Modifier
                                        .size(44.dp)
                                        .align(Alignment.Center)
                                )

                                // Video view badge on top-right of the grid card
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(Color(0x66000000), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${(1..5).random()}.${(0..9).random()}K",
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (reel.filterName != null) {
                                    // Glow AI Badge at top-left
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .background(Color(0xCC1E3A8A), RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0x993B82F6), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color(0xFF60A5FA),
                                            modifier = Modifier.size(8.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = reel.filterName,
                                            color = Color(0xFF93C5FD),
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = reel.title,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                            }
                        }
                    }
                    // Complete empty spaces if chunk is incomplete
                    val remaining = 3 - rowReels.size
                    if (remaining > 0) {
                        for (i in 0 until remaining) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Dialogs for posting and viewing detail details
        if (showPostReelDialog) {
            var reelDesc by remember { mutableStateOf("") }
            var songName by remember { mutableStateOf("") }
            var selectedActionType by remember { mutableStateOf("SPRINT") }
            var aiPromptText by remember { mutableStateOf("") }
            
            AlertDialog(
                onDismissRequest = {
                    showPostReelDialog = false
                    viewModel.clearActiveFilter()
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "New Reel with Gemini Filter",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = reelDesc,
                            onValueChange = { reelDesc = it },
                            label = { Text("Reel Caption / Description", color = Color(0xFF94A3B8)) },
                            placeholder = { Text("Describe your training moment...", color = Color(0xFF64748B)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0x26FFFFFF)
                            ),
                            singleLine = false,
                            modifier = Modifier.fillMaxWidth().testTag("reel_desc_input")
                        )

                        OutlinedTextField(
                            value = songName,
                            onValueChange = { songName = it },
                            label = { Text("Background Song Name", color = Color(0xFF94A3B8)) },
                            placeholder = { Text("e.g. Unstoppable Rhythm", color = Color(0xFF64748B)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0x26FFFFFF)
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("song_name_input")
                        )

                        Text(
                            text = "Athlete Action Type:",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("SPRINT", "WEIGHTS", "BADMINTON", "WRESTLE", "CRICKET").forEach { action ->
                                FilterChip(
                                    selected = selectedActionType == action,
                                    onClick = { selectedActionType = action },
                                    label = { Text(action, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        labelColor = Color(0xFF94A3B8),
                                        selectedLabelColor = Color.White,
                                        selectedContainerColor = Color(0x403B82F6)
                                    )
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0x1AFFFFFF), thickness = 1.dp)

                        Text(
                            text = "Gemini AI Visual Filter Generator:",
                            color = Color(0xFFF59E0B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Input any creative mood or visual theme. Gemini will generate a custom 4x5 color-matrix rendering configuration for this Reel!",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )

                        OutlinedTextField(
                            value = aiPromptText,
                            onValueChange = { aiPromptText = it },
                            label = { Text("Filter Prompt (e.g. Vintage Sunset Glow)", color = Color(0xFF94A3B8)) },
                            placeholder = { Text("Explain visual style, color tones, warm or cyber vibes...", color = Color(0xFF64748B)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFF59E0B),
                                unfocusedBorderColor = Color(0x26FFFFFF)
                            ),
                            singleLine = false,
                            modifier = Modifier.fillMaxWidth().testTag("ai_filter_prompt_input")
                        )

                        Button(
                            onClick = {
                                if (aiPromptText.isNotBlank()) {
                                    viewModel.generateGeminiFilter(aiPromptText)
                                }
                            },
                            enabled = !isGeneratingFilter && aiPromptText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth().testTag("generate_filter_button")
                        ) {
                            if (isGeneratingFilter) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gemini is designing...", color = Color.White, fontSize = 12.sp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate AI Visual Matrix", color = Color.White, fontSize = 12.sp)
                            }
                        }

                        if (activeFilterName != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x13FFFFFF))
                                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "✨ ${activeFilterName}",
                                            color = Color(0xFFF59E0B),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "${filterAuthorCode ?: "GEMINI-PRO"}",
                                                color = Color(0xFF60A5FA),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "🔥 ${filterTrendScore ?: 85}% Trend",
                                                color = Color(0xFF34D399),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = activeFilterDescription ?: "",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    val matrix = activeColorMatrix
                                    if (matrix != null && matrix.size >= 20) {
                                        val swatchColor = remember(matrix) {
                                            val r = matrix[0]
                                            val g = matrix[6]
                                            val b = matrix[12]
                                            when {
                                                r > b && r > g -> Color(0xFFFF9933).copy(alpha = 0.2f)
                                                b > r && b > g -> Color(0xFF00F2FE).copy(alpha = 0.2f)
                                                else -> Color(0xFFF35588).copy(alpha = 0.2f)
                                            }
                                        }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(swatchColor)
                                                .padding(6.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "VISUAL MATRIX ENGAGED ✔ [${matrix.take(4).joinToString(", ") { "%.1f".format(it) }}...]",
                                                color = Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (reelDesc.isNotBlank()) {
                                viewModel.postReel(
                                    description = reelDesc,
                                    songName = songName,
                                    actionType = selectedActionType,
                                    filterName = activeFilterName,
                                    filterDesc = activeFilterDescription,
                                    colorMatrix = activeColorMatrix
                                )
                                viewModel.clearActiveFilter()
                                showPostReelDialog = false
                            }
                        },
                        enabled = reelDesc.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        modifier = Modifier.testTag("submit_post_button")
                    ) {
                        Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showPostReelDialog = false
                            viewModel.clearActiveFilter()
                        }
                    ) {
                        Text("Cancel", color = Color(0xFF64748B))
                    }
                },
                containerColor = Color(0xFF15191C)
            )
        }

        if (selectedDetailReel != null) {
            val reel = selectedDetailReel!!
            val matrix = remember(reel.colorMatrixVals) {
                if (!reel.colorMatrixVals.isNullOrBlank()) {
                    try {
                        val parts = reel.colorMatrixVals.split(",")
                        if (parts.size == 20) {
                            FloatArray(20) { parts[it].trim().toFloat() }
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                } else null
            }

            AlertDialog(
                onDismissRequest = { selectedDetailReel = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (reel.type == "SPRINT") Icons.Default.DirectionsRun else Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Athlete Reel Details",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Caption:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = reel.title,
                            color = Color.White,
                            fontSize = 13.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0x33F59E0B), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "ACTION: ${reel.type}",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0x1AFFFFFF), thickness = 1.dp)

                        if (reel.filterName != null) {
                            Text(
                                text = "✨ Gemini AI Visual Filter Config:",
                                color = Color(0xFF60A5FA),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x0AFFFFFF))
                                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = reel.filterName,
                                            color = Color(0xFF60A5FA),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "GEMINI-PRO",
                                            color = Color(0xFF34D399),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    if (reel.filterDescription != null) {
                                        Text(
                                            text = reel.filterDescription,
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (matrix != null && matrix.size == 20) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "4x5 Color Transformation Matrix:",
                                            color = Color(0xFF64748B),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(2.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0x1A000000), RoundedCornerShape(4.dp))
                                                .padding(6.dp)
                                        ) {
                                            for (row in 0 until 4) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceEvenly
                                                ) {
                                                    for (col in 0 until 5) {
                                                        val idx = row * 5 + col
                                                        Text(
                                                            text = "%.2f".format(matrix[idx]),
                                                            color = Color(0xFF93C5FD),
                                                            fontSize = 8.sp,
                                                            modifier = Modifier.weight(1f),
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Standard Filter (No AI applied)",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { selectedDetailReel = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("OK", color = Color.White)
                    }
                },
                containerColor = Color(0xFF15191C)
            )
        }
    }
}

@Composable
fun MetricCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)), // Frosted translucent background
        modifier = modifier
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(14.dp)) // Glass border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SecurityStatusRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color(0xFFCBD5E1), fontSize = 11.sp)
        Text(text = value, color = valueColor, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun BadgeItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isUnlocked: Boolean,
    unlockedColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) unlockedColor.copy(alpha = 0.15f) else Color(0x08FFFFFF)
        ),
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isUnlocked) unlockedColor.copy(alpha = 0.4f) else Color(0x1AFFFFFF),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) unlockedColor.copy(alpha = 0.2f) else Color(0x0DFFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isUnlocked) unlockedColor else Color(0xFF475569),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = if (isUnlocked) Color.White else Color(0xFF475569),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                color = if (isUnlocked) unlockedColor else Color(0xFF475569),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class MyReelMock(
    val id: String,
    val title: String,
    val type: String,
    val colorStart: String,
    val colorEnd: String,
    val filterName: String? = null,
    val filterDescription: String? = null,
    val colorMatrixVals: String? = null
)

data class DigitalMilestone(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val badgeColor: Color,
    val currentProgress: Int,
    val requiredProgress: Int,
    val unit: String
)

@Composable
fun TrophyWallSection(
    viewModel: AthleteViewModel,
    trainingHours: Float
) {
    val trainingStreakDays by viewModel.trainingStreakDays.collectAsState()
    val claimedTrophies by viewModel.claimedTrophies.collectAsState()
    val totalDuetsCreated by viewModel.totalDuetsCreated.collectAsState()
    val totalGiftsSent by viewModel.totalGiftsSent.collectAsState()
    val vibeMatchPercentage by viewModel.vibeMatchPercentage.collectAsState()

    var selectedMilestone by remember { mutableStateOf<DigitalMilestone?>(null) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val milestones = listOf(
        DigitalMilestone(
            id = "trophy_1_sprint_warrior",
            title = "7-Day Sprint Warrior",
            category = "Training Consistency",
            description = "Maintained a 7-day streak following athlete morning workout programs.",
            icon = Icons.Default.DirectionsRun,
            badgeColor = Color(0xFFF59E0B),
            currentProgress = trainingStreakDays,
            requiredProgress = 7,
            unit = "Days Streak"
        ),
        DigitalMilestone(
            id = "trophy_2_training_master",
            title = "Program Veteran",
            category = "Workout Volume",
            description = "Completed 10+ hours of structured training and athletic conditioning.",
            icon = Icons.Default.FitnessCenter,
            badgeColor = Color(0xFF10B981),
            currentProgress = trainingHours.toInt(),
            requiredProgress = 10,
            unit = "Hours Drills"
        ),
        DigitalMilestone(
            id = "trophy_3_duet_legend",
            title = "Co-Creator Legend",
            category = "Daily Duets",
            description = "Recorded live interactive duets with star creators in Aura Studio.",
            icon = Icons.Default.Mic,
            badgeColor = Color(0xFFEC4899),
            currentProgress = totalDuetsCreated,
            requiredProgress = 3,
            unit = "Duets Recorded"
        ),
        DigitalMilestone(
            id = "trophy_4_vibe_champion",
            title = "Aura Vibe Champion",
            category = "Daily Engagement",
            description = "Synchronized daily creative vibe with morning celebrity aura.",
            icon = Icons.Default.AutoAwesome,
            badgeColor = Color(0xFF8B5CF6),
            currentProgress = vibeMatchPercentage,
            requiredProgress = 80,
            unit = "% Vibe Alignment"
        ),
        DigitalMilestone(
            id = "trophy_5_golden_supporter",
            title = "Golden Fan Supporter",
            category = "Fan Loyalty",
            description = "Sent virtual trophies and gift points to boost athlete morale.",
            icon = Icons.Default.CardGiftcard,
            badgeColor = Color(0xFF3B82F6),
            currentProgress = totalGiftsSent,
            requiredProgress = 5,
            unit = "Gifts Sent"
        )
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(20.dp))
            .testTag("trophy_wall_section")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row with Trophy Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy Wall",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Trophy Wall",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Earn digital milestones & consistency rewards",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                // Active Streak Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x22F59E0B),
                    border = BorderStroke(1.dp, Color(0x66F59E0B))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${trainingStreakDays}d Streak",
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Workout Consistency Action Button
            Button(
                onClick = {
                    viewModel.incrementTrainingStreak()
                    toastMessage = "🔥 Daily Training Logged! Streak increased to ${trainingStreakDays + 1} days."
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF59E0B)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("workout_checkin_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Complete Today's Athlete Program Drills (+1 Day Streak)",
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (toastMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = toastMessage!!,
                    color = Color(0xFF10B981),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                LaunchedEffect(toastMessage) {
                    kotlinx.coroutines.delay(2500)
                    toastMessage = null
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of Milestones
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                milestones.forEach { milestone ->
                    val isUnlocked = milestone.currentProgress >= milestone.requiredProgress
                    val isClaimed = claimedTrophies.contains(milestone.id)

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isClaimed) Color(0x1EF59E0B) else if (isUnlocked) Color(0x1310B981) else Color(0x08FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (isClaimed) Color(0x88F59E0B) else if (isUnlocked) Color(0x6610B981) else Color(0x1AFFFFFF),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedMilestone = milestone }
                            .testTag("milestone_card_${milestone.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge Icon Container
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) milestone.badgeColor.copy(alpha = 0.25f)
                                        else Color(0x0DFFFFFF)
                                    )
                                    .border(
                                        1.dp,
                                        if (isUnlocked) milestone.badgeColor else Color(0x33FFFFFF),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = milestone.icon,
                                    contentDescription = milestone.title,
                                    tint = if (isUnlocked) milestone.badgeColor else Color(0xFF64748B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Details & Progress Bar
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = milestone.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    if (isClaimed) {
                                        Text(
                                            text = "🏆 CLAIMED",
                                            color = Color(0xFFF59E0B),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    } else if (isUnlocked) {
                                        Text(
                                            text = "✨ READY TO CLAIM",
                                            color = Color(0xFF10B981),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    } else {
                                        Text(
                                            text = "${milestone.currentProgress}/${milestone.requiredProgress} ${milestone.unit}",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                Text(
                                    text = milestone.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Progress Indicator Bar
                                val progressFraction = (milestone.currentProgress.toFloat() / milestone.requiredProgress.toFloat()).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = if (isUnlocked) milestone.badgeColor else Color(0xFF475569),
                                    trackColor = Color(0x22FFFFFF)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal for viewing and claiming milestone detail
    if (selectedMilestone != null) {
        val milestone = selectedMilestone!!
        val isUnlocked = milestone.currentProgress >= milestone.requiredProgress
        val isClaimed = claimedTrophies.contains(milestone.id)

        AlertDialog(
            onDismissRequest = { selectedMilestone = null },
            containerColor = Color(0xFF181C20),
            shape = RoundedCornerShape(24.dp),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(milestone.badgeColor, Color(0xFF0F1113))
                                )
                            )
                            .border(2.dp, milestone.badgeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = milestone.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = milestone.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Category: ${milestone.category}",
                        color = milestone.badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = milestone.description,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Seal
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x13FFFFFF),
                        border = BorderStroke(1.dp, Color(0x22FFFFFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "OFFICIAL DIGITAL CERTIFICATE",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Morning Gram Verified Athlete Network",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Progress: ${milestone.currentProgress} / ${milestone.requiredProgress} ${milestone.unit}",
                                color = if (isUnlocked) Color(0xFF10B981) else Color(0xFFF59E0B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (isUnlocked && !isClaimed) {
                    Button(
                        onClick = {
                            viewModel.claimTrophy(milestone.id)
                            selectedMilestone = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = milestone.badgeColor),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("claim_trophy_confirm_button")
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Claim Digital Milestone", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = { selectedMilestone = null },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0x44FFFFFF))
                    ) {
                        Text(if (isClaimed) "Trophy Saved in Profile" else "Close", color = Color.White, fontSize = 12.sp)
                    }
                }
            },
            dismissButton = {
                if (isUnlocked && !isClaimed) {
                    TextButton(onClick = { selectedMilestone = null }) {
                        Text("Later", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            }
        )
    }
}
