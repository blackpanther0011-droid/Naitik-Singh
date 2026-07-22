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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AthleteViewModel
import kotlinx.coroutines.delay

data class StarStory(
    val id: String,
    val name: String,
    val role: String,
    val initial: String,
    val isUnread: Boolean,
    val storyQuote: String,
    val gradientColors: List<Color>
)

val starStoriesList = listOf(
    StarStory("story_srk", "Shah Rukh Khan", "Actor", "SRK", true, "Picture abhi baaki hai... Never stop dreaming big!", listOf(Color(0xFF3A1C71), Color(0xFFD76D77))),
    StarStory("story_arijit", "Arijit Singh", "Singer", "AS", true, "New unplugged acoustic session recorded live at 6 AM.", listOf(Color(0xFFF5AF19), Color(0xFFF12711))),
    StarStory("story_deepika", "Deepika Padukone", "Actress", "DP", true, "Mindful stretch routine before shooting. Stay calm!", listOf(Color(0xFFFF512F), Color(0xFFDD2476))),
    StarStory("story_vkohli", "Virat Kohli", "Cricket", "VK", true, "High intensity shuttle sprints. Hard work wins games!", listOf(Color(0xFF1F4037), Color(0xFF99F2C8))),
    StarStory("story_diljit", "Diljit Dosanjh", "Singer", "DD", false, "Sold out stadium tour in Vancouver! Born to shine!", listOf(Color(0xFFF2994A), Color(0xFFF2C94C))),
    StarStory("story_carry", "CarryMinati", "Creator", "CM", true, "Editing the new comedy sketch! Direct connect with fans.", listOf(Color(0xFF00F2FE), Color(0xFF4FACFE)))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AthleteViewModel,
    onNavigateToReels: () -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.currentUserName.collectAsState()
    val userRole by viewModel.currentUserRole.collectAsState()
    val activeVibe by viewModel.activeVibe.collectAsState()
    val vibeMatchPercentage by viewModel.vibeMatchPercentage.collectAsState()
    val vibeMatchedStar by viewModel.vibeMatchedStar.collectAsState()
    val isPlayingVoiceNote by viewModel.isPlayingStarVoiceNote.collectAsState()
    val totalDuetsCreated by viewModel.totalDuetsCreated.collectAsState()
    val allReels by viewModel.allReels.collectAsState()

    var activeStoryModal by remember { mutableStateOf<StarStory?>(null) }
    var showDuetRecorderModal by remember { mutableStateOf(false) }
    var selectedDialogueForDuet by remember { mutableStateOf("Picture abhi baaki hai... (SRK)") }
    var isRecordingDuet by remember { mutableStateOf(false) }
    var recordingProgress by remember { mutableFloatStateOf(0f) }

    val vibesList = listOf("Cinematic Drama", "Acoustic Melody", "High Intensity Energy", "Global Beats", "Deep Focus")

    val starDialogues = listOf(
        "Picture abhi baaki hai... (SRK)" to "Shah Rukh Khan",
        "Kesariya tera ishq hai piya (Arijit Chorus)" to "Arijit Singh",
        "Your inner peace is your true superpower" to "Deepika Padukone",
        "Chak de! Dedication is a daily habit" to "Virat Kohli"
    )

    // Pulse animation for recording
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Good Day, $userName",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "$userRole • $activeVibe",
                        color = Color(0xFFF59E0B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Vibe Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x22F59E0B))
                        .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { onNavigateToExplore() }
                        .testTag("home_vibe_badge")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Aura Sync", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // SECTION 1: Daily Star Sparks & Stories Bar
            Text(
                text = "DAILY STAR SPARKS",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                starStoriesList.forEach { story ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { activeStoryModal = story }
                            .testTag("story_avatar_${story.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.sweepGradient(
                                        if (story.isUnread) listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFEC4899), Color(0xFFF59E0B))
                                        else listOf(Color(0x33FFFFFF), Color(0x33FFFFFF))
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
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(story.gradientColors)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = story.initial,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = story.name.split(" ").first(),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 2: IMAGINATIVE FEATURE #1 — Aura Vibe Matcher & Star Voice Capsule
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33F59E0B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aura Vibe Sync & Star Capsule",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = Color(0x3310B981),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$vibeMatchPercentage% Match",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Match your daily creative vibration with Indian icons to unlock exclusive morning voice notes.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vibe Selectors
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        vibesList.forEach { vibe ->
                            val isSelected = activeVibe == vibe
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateVibe(vibe) },
                                label = { Text(vibe, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFF59E0B),
                                    selectedLabelColor = Color.Black,
                                    containerColor = Color(0x22FFFFFF),
                                    labelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice Note Player Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF334155), Color(0xFF0F172A))
                                )
                            )
                            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleVoiceCapsule() },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B))
                                    .testTag("play_voice_capsule_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlayingVoiceNote) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play Star Voice Note",
                                    tint = Color.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Daily 15s Capsule • $vibeMatchedStar",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Simulated Audio Waveform Visualizer Bars
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    modifier = Modifier.height(20.dp)
                                ) {
                                    listOf(8, 14, 20, 12, 18, 22, 10, 16, 24, 14, 8, 18, 12, 20, 15).forEach { barHeight ->
                                        val height = if (isPlayingVoiceNote) (barHeight * ((barHeight % 5) * 0.12f + 0.8f)).dp else (barHeight / 2).dp
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(height)
                                                .clip(CircleShape)
                                                .background(if (isPlayingVoiceNote) Color(0xFFF59E0B) else Color(0xFF64748B))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 3: IMAGINATIVE FEATURE #2 — Fan Co-Creator Duet Studio
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3338BDF8)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Co-Creator Fan Duet Studio",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = Color(0x3338BDF8),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "$totalDuetsCreated Recorded",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Perform a live 5-second acoustic or dialogue response with SRK, Arijit, or Deepika to create your own Duet Reel!",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showDuetRecorderModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("open_duet_studio_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Record Live Fan Duet", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 4: Featured Celebrity Reels Spotlight
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FEATURED CELEBRITY REELS",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "View All Reels →",
                    color = Color(0xFFF59E0B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToReels() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                allReels.take(5).forEach { reel ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .width(180.dp)
                            .height(240.dp)
                            .clickable { onNavigateToReels() }
                            .testTag("featured_reel_card_${reel.id}")
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Gradient representation of video
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color(0xFF334155), Color(0xFF0F172A))
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayCircleFilled,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = reel.athleteName,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Column {
                                    Text(
                                        text = reel.description,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${reel.likesCount}", color = Color(0xFFCBD5E1), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal 1: Star Story Popup
    if (activeStoryModal != null) {
        val story = activeStoryModal!!
        AlertDialog(
            onDismissRequest = { activeStoryModal = null },
            confirmButton = {
                Button(
                    onClick = { activeStoryModal = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Text("Close Spark", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(story.gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(story.initial, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(story.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(story.role, color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            },
            text = {
                Column {
                    Text(
                        text = "“${story.storyQuote}”",
                        color = Color(0xFFE2E8F0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Broadcasted directly via Morning Gram Sovereign Node • Zero Ad Interruption",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp
                    )
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }

    // Modal 2: Duet Studio Recorder
    if (showDuetRecorderModal) {
        AlertDialog(
            onDismissRequest = {
                showDuetRecorderModal = false
                isRecordingDuet = false
            },
            title = { Text("Fan Duet Recording Studio", color = Color.White) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Select dialogue line to duet with:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    starDialogues.forEach { (dialogue, star) ->
                        val isSelected = selectedDialogueForDuet == dialogue
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0x3338BDF8) else Color(0x11FFFFFF))
                                .border(1.dp, if (isSelected) Color(0xFF38BDF8) else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable { selectedDialogueForDuet = dialogue }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedDialogueForDuet = dialogue },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF38BDF8))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(dialogue, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recording waveform animation simulation
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .scale(if (isRecordingDuet) pulseScale else 1f)
                            .background(if (isRecordingDuet) Color(0xFFEF4444) else Color(0xFF38BDF8))
                            .clickable {
                                isRecordingDuet = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecordingDuet) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = "Record",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isRecordingDuet) "Recording Fan Audio Overlay... Speak Now!" else "Tap Mic to Start 5s Duet",
                        color = if (isRecordingDuet) Color(0xFFEF4444) else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val starName = starDialogues.find { it.first == selectedDialogueForDuet }?.second ?: "Shah Rukh Khan"
                        viewModel.createFanDuetSpark(starName, selectedDialogueForDuet)
                        showDuetRecorderModal = false
                        isRecordingDuet = false
                        onNavigateToReels()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                ) {
                    Text("Publish Duet Reel", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDuetRecorderModal = false
                    isRecordingDuet = false
                }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}
