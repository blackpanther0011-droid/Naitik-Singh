package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Reel
import com.example.data.model.UserComment
import com.example.ui.viewmodel.AthleteViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReelsScreen(
    viewModel: AthleteViewModel,
    isFollowingOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val reels by (if (isFollowingOnly) viewModel.followingReels else viewModel.allReels)
        .collectAsState()
    val scope = rememberCoroutineScope()

    var showDmSelectorDialog by remember { mutableStateOf(false) }
    val activeChatRecipient by viewModel.activeChatRecipient.collectAsState()
    val activeProfileAthlete by viewModel.activeProfileAthlete.collectAsState()
    val activeColorMatrix by viewModel.activeColorMatrix.collectAsState()
    val allAthletes by viewModel.allAthletes.collectAsState()

    if (reels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0F1113)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isFollowingOnly) "Not Following Any Creators Yet" else "Loading Reels...",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isFollowingOnly) 
                        "Explore creators and hit 'Follow' to see their feeds here!"
                    else "Securing direct end-to-end encrypted creative feed...",
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        val pagerState = rememberPagerState(pageCount = { reels.size })

        Box(modifier = modifier.fillMaxSize()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val reel = reels[page]
                ReelItem(
                    reel = reel,
                    onLikeToggle = { viewModel.toggleLike(reel.id, reel.isLiked) },
                    onFollowToggle = { viewModel.toggleFollow(reel.athleteId, true) }, // If clicked from here, they want to follow
                    onCommentClick = { 
                        viewModel.setSelectedReelId(reel.id)
                    },
                    activeColorMatrix = activeColorMatrix,
                    onProfileClick = {
                        val athleteObj = allAthletes.find { it.id == reel.athleteId }
                        if (athleteObj != null) {
                            viewModel.activeProfileAthlete.value = athleteObj
                        }
                    },
                    onGiftSent = { giftTitle, points ->
                        viewModel.sendVirtualGift(giftTitle, points)
                    }
                )
            }

            // High Security & Ad-Free compliance header badge
            SecurityHeader(isFollowingOnly = isFollowingOnly, onDmClick = { showDmSelectorDialog = true })

            // Secure Banner (Footer Overlay)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp) // Fits right above bottom navigation safely
                    .background(Color(0x1A2563EB)) // Blue-600 with 10% opacity
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Encrypted End-to-End • Secure Athlete Network",
                    color = Color(0xFF93C5FD), // Blue 200 equivalent
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }

    // Comments Bottom Drawer
    val selectedReelId by viewModel.selectedReelIdForComments.collectAsState()
    val comments by viewModel.commentsForSelectedReel.collectAsState()

    if (selectedReelId != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setSelectedReelId(null) },
            containerColor = Color(0xFF1E293B),
            contentColor = Color.White,
            modifier = Modifier.fillMaxHeight(0.7f)
        ) {
            CommentsDrawerContent(
                reelId = selectedReelId!!,
                comments = comments,
                onAddComment = { commentText ->
                    viewModel.addComment(selectedReelId!!, commentText)
                }
            )
        }
    }

    if (activeChatRecipient != null) {
        EncryptedChatDialog(
            recipient = activeChatRecipient!!,
            viewModel = viewModel,
            onDismiss = { viewModel.activeChatRecipient.value = null }
        )
    }

    if (activeProfileAthlete != null) {
        AthleteProfileDialog(
            athlete = activeProfileAthlete!!,
            viewModel = viewModel,
            onDismiss = { viewModel.activeProfileAthlete.value = null }
        )
    }

    if (showDmSelectorDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDmSelectorDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.75f),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131517)),
                border = BorderStroke(1.dp, Color(0x33FFFFFF))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Secure E2EE DM Space",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { showDmSelectorDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = "Connect directly with creators globally. Cryptographic handshake keys are negotiated on-device. Morning Gram staff cannot inspect messages.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(allAthletes) { creator ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.activeChatRecipient.value = creator
                                            showDmSelectorDialog = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar circle
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(Color(0xFFF59E0B), Color(0xFF10B981))
                                                )
                                            )
                                            .padding(1.5.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .background(Color(0xFF1E293B)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = creator.name.take(1),
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(14.dp))
                                    
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = creator.name,
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (creator.isVerified) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Verified,
                                                    contentDescription = null,
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = creator.sport,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                    
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "Message",
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Divider(color = Color(0x13FFFFFF), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityHeader(isFollowingOnly: Boolean, onDmClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.85f),
                        Color.Black.copy(alpha = 0.4f),
                        Color.Transparent
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // --- Row 1: Instagram-style Brand Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Morning Gram",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        blurRadius = 4f
                    )
                ),
                modifier = Modifier.testTag("app_brand_logo_header")
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                IconButton(onClick = onDmClick, modifier = Modifier.size(28.dp).testTag("dm_inbox_header_button")) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Direct Messages",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // --- Row 2: Compliance Badge and Tabs ---
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Left Secure Shield Info to guarantee regulation compliance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SECURE",
                    color = Color(0xFF10B981),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Center Tabs matching the mockup HTML (Following & Indian Athletes)
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = "Following",
                        color = if (isFollowingOnly) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    if (isFollowingOnly) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(2.5.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 2.dp)
                ) {
                    Text(
                        text = "Indian Athletes",
                        color = if (!isFollowingOnly) Color.White else Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    if (!isFollowingOnly) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(2.5.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }

            // Search icon on the right
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(20.dp)
            )
        }
    }
}

@Composable
fun ReelItem(
    reel: Reel,
    onLikeToggle: () -> Unit,
    onFollowToggle: () -> Unit,
    onCommentClick: () -> Unit,
    activeColorMatrix: FloatArray? = null,
    onProfileClick: () -> Unit,
    onGiftSent: (String, Int) -> Unit = { _, _ -> }
) {
    val scope = rememberCoroutineScope()
    var isDoubleTapLikedAnimate by remember { mutableStateOf(false) }

    val parsedColorMatrix = remember(reel.colorMatrixVals) {
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

    val filterToApply = parsedColorMatrix ?: activeColorMatrix

    // Start-End gradient color parse
    val colorStart = remember { Color(android.graphics.Color.parseColor(reel.thumbnailGradientStart)) }
    val colorEnd = remember { Color(android.graphics.Color.parseColor(reel.thumbnailGradientEnd)) }

    // Continuous athletic action simulator inside Canvas
    val infiniteTransition = rememberInfiniteTransition(label = "ActionSimulator")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        isDoubleTapLikedAnimate = true
                        if (!reel.isLiked) {
                            onLikeToggle()
                        }
                    },
                    onTap = {
                        // Single tap shows overlay or pauses music mock
                    }
                )
            }
    ) {
        // Full screen pulsing sports background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorStart, colorEnd, Color.Black)
                    )
                )
        ) {
            // Draw interactive athletic field/tracks or javelin paths dynamically!
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw background energy rays
                drawCircle(
                    color = Color.White.copy(alpha = 0.03f),
                    radius = (animOffset % width) + 100f,
                    center = Offset(width / 2, height / 2)
                )

                // Sports Specific Visual Simulation
                when (reel.videoActionType) {
                    "SPRINT" -> {
                        // Drawing dynamic Olympic Running tracks
                        val trackSpacing = 40f
                        for (i in 0..4) {
                            val x = (width * 0.15f) + (i * trackSpacing)
                            drawLine(
                                color = Color.White.copy(alpha = 0.15f),
                                start = Offset(x, 0f),
                                end = Offset(x - 100f, height),
                                strokeWidth = 3f
                            )
                        }
                        // Moving training block
                        drawCircle(
                            color = Color(0xFFF59E0B).copy(alpha = 0.7f),
                            radius = 25f,
                            center = Offset(width / 2, (animOffset * 1.2f) % height)
                        )
                    }
                    "BADMINTON" -> {
                        // Drawing Court Net pattern at the bottom
                        val netY = height * 0.7f
                        drawLine(
                            color = Color.White.copy(alpha = 0.3f),
                            start = Offset(0f, netY),
                            end = Offset(width, netY),
                            strokeWidth = 6f
                        )
                        // Moving shuttlecock arc
                        val progress = (animOffset % 1000f) / 1000f
                        val sx = width * progress
                        val sy = netY - 200f * (1f - (2f * progress - 1f) * (2f * progress - 1f)) // arc
                        drawCircle(
                            color = Color.White.copy(alpha = 0.9f),
                            radius = 15f,
                            center = Offset(sx, sy)
                        )
                    }
                    "WRESTLE" -> {
                        // Drawing Wrestling inner Ring
                        drawCircle(
                            color = Color(0xFFEF4444).copy(alpha = 0.3f),
                            radius = 350f,
                            center = Offset(width / 2, height / 2),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                        )
                        drawCircle(
                            color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                            radius = 180f,
                            center = Offset(width / 2, height / 2),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                        )
                    }
                    "WEIGHTS" -> {
                        // Drawing bar bell lifter power aura
                        for (i in 1..3) {
                            drawCircle(
                                color = Color(0xFF10B981).copy(alpha = 0.15f / i),
                                radius = 100f * i + (animOffset % 100f),
                                center = Offset(width / 2, height * 0.45f)
                            )
                        }
                    }
                    else -> {
                        // CRICKET or other
                        // Draw spinning cricket ball seams
                        val angle = (animOffset * 0.1f) % 360f
                        drawCircle(
                            color = Color(0xFFEF4444).copy(alpha = 0.4f),
                            radius = 150f,
                            center = Offset(width / 2, height * 0.45f)
                        )
                    }
                }
            }
        }

        // Glassmorphic Center Card displaying the Live Action Silhouette Placeholder
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.46f)
                .background(Color(0x1EFFFFFF), RoundedCornerShape(24.dp)) // Frosted translucent white
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp)), // Glass border
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Moving silhouette icon mimicking sports
                val actionIcon = when (reel.videoActionType) {
                    "SPRINT" -> Icons.Default.DirectionsRun
                    "BADMINTON" -> Icons.Default.SportsTennis
                    "WRESTLE" -> Icons.Default.SportsKabaddi
                    "WEIGHTS" -> Icons.Default.FitnessCenter
                    else -> Icons.Default.SportsCricket
                }

                val scale by animateFloatAsState(
                    targetValue = if (animOffset % 400 < 200) 1.15f else 0.9f,
                    animationSpec = tween(500), label = ""
                )

                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "LIVE PERFORMANCE DEMO",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                
                Spacer(modifier = Modifier.height(10.dp))

                // Safety/Regulation Badge (Emerald Green match)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0x2610B981), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0x4D10B981), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified Secure",
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DIRECT CREATOR FEED • VERIFIED E2E",
                        color = Color(0xFF6EE7B7),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Bottom Details (Metadata, Song, Description)
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 90.dp)
        ) {
            // Athlete Avatar, Name, and Follow Action
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { onProfileClick() }
            ) {
                // Circular Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reel.athleteName.take(2).uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reel.athleteName,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified Athlete",
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "🇮🇳 ${reel.athleteSport}",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Follow Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x33FFFFFF))
                        .clickable { onFollowToggle() }
                        .border(0.5.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Follow",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Reel Description
            Text(
                text = reel.description,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (reel.filterName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x333B82F6))
                        .border(1.dp, Color(0x663B82F6), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Visual Filter",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Filter: ${reel.filterName}",
                        color = Color(0xFF93C5FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Indian Athletic Song Scroller
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x26000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Animated looping soundtrack text
                Text(
                    text = "${reel.songName} - ${reel.songArtist}",
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.widthIn(max = 200.dp)
                )
            }
        }

        // Right Side Interactive Social Bar (Likes, Comments, Security Shield, etc)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Like Action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLikeToggle,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0x1EFFFFFF), CircleShape)
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (reel.isLiked) Color(0xFFEF4444) else Color.White
                    )
                }
                Text(
                    text = formatLikes(reel.likesCount),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Comment Action
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onCommentClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0x1EFFFFFF), CircleShape)
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Comments",
                        tint = Color.White
                    )
                }
                Text(
                    text = "${reel.commentsCount}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Share Action (Encrypted secure link simulation)
            var showEncryptedToast by remember { mutableStateOf(false) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        showEncryptedToast = true
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0x1EFFFFFF), CircleShape)
                        .border(1.dp, Color(0x33FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Secure Share",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Share",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Inspire Action (High Contrast Accent Spark Button)
            var showInspireSpark by remember { mutableStateOf(false) }
            val inspirationalSparks = remember {
                listOf(
                    "🔥 Dream bigger. Train harder. Deliver gold!",
                    "💪 Absolute dedication is the true athletic contract.",
                    "⚡ Mental strength conquers physical fatigue.",
                    "🇮🇳 Represent with pride. Lead with passion!",
                    "🌟 No shortcuts. Every millisecond counts."
                )
            }
            var activeSparkIndex by remember { mutableStateOf(0) }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        activeSparkIndex = (activeSparkIndex + 1) % inspirationalSparks.size
                        showInspireSpark = true
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFD1E4FF), CircleShape)
                        .border(1.dp, Color(0x4DD1E4FF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Inspire Spark",
                        tint = Color(0xFF001D36)
                    )
                }
                Text(
                    text = "Inspire",
                    color = Color(0xFFD1E4FF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Virtual Fan Gifting / Tip Jar Button
            var showGiftSheet by remember { mutableStateOf(false) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { showGiftSheet = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0x33F59E0B), CircleShape)
                        .border(1.dp, Color(0xFFF59E0B), CircleShape)
                        .testTag("reel_gift_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Send Virtual Fan Gift",
                        tint = Color(0xFFF59E0B)
                    )
                }
                Text(
                    text = "Gift Star",
                    color = Color(0xFFF59E0B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Virtual Fan Gift Dialog
            if (showGiftSheet) {
                var giftSuccessToast by remember { mutableStateOf<String?>(null) }
                AlertDialog(
                    onDismissRequest = { showGiftSheet = false },
                    title = { Text("Send Virtual Fan Trophy to ${reel.athleteName}", color = Color.White) },
                    text = {
                        Column {
                            Text("Support your favorite celebrity directly with fan tokens:", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(14.dp))

                            val gifts = listOf(
                                "🎤 Golden Mic" to 50,
                                "🏆 Oscar Trophy" to 100,
                                "🔥 Sitar Flame" to 200,
                                "🏏 Cricket Bat" to 150
                            )

                            gifts.forEach { (giftTitle, points) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x22FFFFFF))
                                        .clickable {
                                            onGiftSent(giftTitle, points)
                                            giftSuccessToast = "Sent $giftTitle to ${reel.athleteName}!"
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(giftTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Surface(color = Color(0x33F59E0B), shape = RoundedCornerShape(8.dp)) {
                                        Text("$points PTS", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (giftSuccessToast != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(giftSuccessToast!!, color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showGiftSheet = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                        ) {
                            Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = Color(0xFF1E293B)
                )
            }

            // Hack-Free encryption shield badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0x2610B981), CircleShape)
                    .border(1.dp, Color(0x8010B981), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Secure Connection Status",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Rotating Music Cover Art
            val rotationTransition = rememberInfiniteTransition(label = "musicRotation")
            val angleRotation by rotationTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(6000, easing = LinearEasing)
                ), label = ""
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .rotate(angleRotation)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFF9933), Color.White, Color(0xFF128807))
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF0F1113),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Shared Link Encryption alert
            LaunchedEffect(showEncryptedToast) {
                if (showEncryptedToast) {
                    delay(2000)
                    showEncryptedToast = false
                }
            }
            AnimatedVisibility(
                visible = showEncryptedToast,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 }),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFF0F1113), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0x3310B981), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Encrypted Link Copied!", color = Color(0xFF10B981), fontSize = 10.sp)
                }
            }

            // Spark quote dialog
            LaunchedEffect(showInspireSpark) {
                if (showInspireSpark) {
                    delay(3000)
                    showInspireSpark = false
                }
            }
            AnimatedVisibility(
                visible = showInspireSpark,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 100 }),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xCC1A1C1E), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x66D1E4FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .widthIn(max = 240.dp)
                ) {
                    Text(
                        text = inspirationalSparks[activeSparkIndex],
                        color = Color(0xFFD1E4FF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Overlay double-tap big heart animation
        AnimatedVisibility(
            visible = isDoubleTapLikedAnimate,
            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(100.dp)
            )
            LaunchedEffect(isDoubleTapLikedAnimate) {
                if (isDoubleTapLikedAnimate) {
                    delay(800)
                    isDoubleTapLikedAnimate = false
                }
            }
        }

        // --- Generative Color Filter Blending Tint Overlay ---
        if (filterToApply != null) {
            val tintColor = remember(filterToApply) {
                val r = filterToApply[0]
                val g = filterToApply[6]
                val b = filterToApply[12]
                when {
                    r > b && r > g -> Color(0xFFF5AF19).copy(alpha = 0.12f) // Golden Hour warm
                    b > r && b > g -> Color(0xFF00F2FE).copy(alpha = 0.10f) // Blue Cyber cold
                    else -> Color(0xFFF35588).copy(alpha = 0.12f) // Cyberpunk magenta/pink
                }
            }
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("generative_color_filter_overlay")
            ) {
                drawRect(color = tintColor)
            }
        }
    }
}

@Composable
fun CommentsDrawerContent(
    reelId: String,
    comments: List<UserComment>,
    onAddComment: (String) -> Unit
) {
    var newCommentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Athlete Discussions (${comments.size})",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Divider(color = Color(0x1AFFFFFF), modifier = Modifier.padding(bottom = 12.dp))

        // Comments List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No comments yet. Start the athlete motivation!",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                items(comments) { comment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = comment.userName.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = comment.userName,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Verified",
                                    color = Color(0xFF3B82F6),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = comment.commentText,
                                color = Color(0xFFCBD5E1),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                placeholder = { Text("Encourage this athlete...", color = Color(0xFF94A3B8)) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF334155),
                    unfocusedContainerColor = Color(0xFF334155),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("comment_input_field")
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (newCommentText.isNotBlank()) {
                        onAddComment(newCommentText)
                        newCommentText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Post", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun formatLikes(count: Int): String {
    return if (count >= 1000) "${String.format("%.1f", count / 1000f)}k" else "$count"
}

// Extension to facilitate sizing calculations
fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)
