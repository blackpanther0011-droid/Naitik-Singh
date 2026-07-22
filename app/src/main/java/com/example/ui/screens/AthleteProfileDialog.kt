package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Athlete
import com.example.data.model.Reel
import com.example.ui.viewmodel.AthleteViewModel

@Composable
fun AthleteProfileDialog(
    athlete: Athlete,
    viewModel: AthleteViewModel,
    onDismiss: () -> Unit
) {
    val allReels by viewModel.allReels.collectAsState()
    val athleteReels = remember(athlete, allReels) {
        allReels.filter { it.athleteId == athlete.id }
    }
    val followedStatus = athlete.isFollowing

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131517)), // Custom slate background
            border = BorderStroke(1.dp, Color(0x33FFFFFF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with back/close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Member Profile",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Divider(color = Color(0x13FFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar area
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .border(2.dp, Color(0xFFF59E0B), CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF3B82F6), Color(0xFF10B981))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = athlete.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                        if (athlete.isVerified) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3B82F6))
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Verified",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // Name
                    Text(
                        text = athlete.name,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    // Sport
                    Text(
                        text = if (athlete.isPrivate) "🔒 Private Profile • ${athlete.sport}" else "🌐 Public Profile • ${athlete.sport}",
                        color = Color(0xFFF59E0B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bio (Full description layout as requested)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x0AFFFFFF)),
                        border = BorderStroke(0.5.dp, Color(0x13FFFFFF))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "BIOGRAPHY",
                                color = Color(0xFF64748B),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = athlete.bio,
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Metrics Row (Follower count & following status)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Followers Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x0AFFFFFF)),
                            border = BorderStroke(0.5.dp, Color(0x13FFFFFF))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatFollowers(athlete.followerCount),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Followers",
                                    color = Color(0xFF64748B),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Reels Count Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x0AFFFFFF)),
                            border = BorderStroke(0.5.dp, Color(0x13FFFFFF))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${athleteReels.size}",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Reels",
                                    color = Color(0xFF64748B),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Interaction Buttons: Follow & DM Chat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isPending = athlete.isPendingRequest
                        val isPrivate = athlete.isPrivate
                        Button(
                            onClick = { viewModel.toggleFollow(athlete.id, athlete.isFollowing, athlete.isPrivate) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (followedStatus) {
                                    Color(0x1EFFFFFF)
                                } else if (isPending) {
                                    Color(0x33F59E0B)
                                } else {
                                    Color(0xFFF59E0B)
                                },
                                contentColor = if (followedStatus) {
                                    Color.White
                                } else if (isPending) {
                                    Color(0xFFF59E0B)
                                } else {
                                    Color.Black
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = if (followedStatus || isPending) BorderStroke(0.5.dp, Color(0x33FFFFFF)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isPending && !followedStatus) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Color(0xFFF59E0B)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Requested",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = if (followedStatus) "Following" else if (isPrivate) "Request Follow" else "Follow",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        val canChat = !isPrivate || followedStatus
                        Button(
                            onClick = {
                                if (canChat) {
                                    viewModel.activeChatRecipient.value = athlete
                                    onDismiss()
                                }
                            },
                            enabled = canChat,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (canChat) Color(0xFF10B981) else Color(0x1EFFFFFF),
                                contentColor = if (canChat) Color.White else Color(0xFF64748B)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = if (!canChat) BorderStroke(0.5.dp, Color(0x1AFFFFFF)) else null,
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (canChat) Icons.Default.Chat else Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = if (canChat) Color.White else Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (canChat) "Private Chat" else "Chat Locked",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Published Reels Grid (The requested reels gallery grid layout)
                    Text(
                        text = "Published Reels (${athleteReels.size})",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, bottom = 8.dp),
                        textAlign = TextAlign.Start
                    )

                    val isLocked = athlete.isPrivate && !athlete.isFollowing
                    if (isLocked) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(Color(0x0AFFFFFF), RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0x13FFFFFF), RoundedCornerShape(14.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Private Profile",
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "This Account is Private",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Follow this creator to see their published reels and start a secure encrypted chat conversation.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    } else if (athleteReels.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No reels published yet.",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            athleteReels.chunked(3).forEach { rowReels ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowReels.forEach { reel ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(0.75f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color(android.graphics.Color.parseColor(reel.thumbnailGradientStart)),
                                                            Color(android.graphics.Color.parseColor(reel.thumbnailGradientEnd))
                                                        )
                                                    )
                                                )
                                                .border(0.5.dp, Color(0x13FFFFFF), RoundedCornerShape(10.dp)),
                                            contentAlignment = Alignment.BottomStart
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (reel.videoActionType == "SPRINT") Icons.Default.DirectionsRun else Icons.Default.FitnessCenter,
                                                    contentDescription = null,
                                                    tint = Color.White.copy(alpha = 0.25f),
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .align(Alignment.Center)
                                                )

                                                // Play icon overlay badge
                                                Row(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .background(Color(0x66000000), RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Favorite,
                                                        contentDescription = null,
                                                        tint = Color(0xFFEF4444),
                                                        modifier = Modifier.size(8.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = formatFollowers(reel.likesCount),
                                                        color = Color.White,
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }

                                                Text(
                                                    text = reel.description,
                                                    color = Color.White,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.align(Alignment.BottomStart)
                                                )
                                            }
                                        }
                                    }
                                    // Complete spacing if chunk is incomplete
                                    val remaining = 3 - rowReels.size
                                    if (remaining > 0) {
                                        for (i in 0 until remaining) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
