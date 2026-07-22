package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Athlete
import com.example.data.model.CallLogEntry
import com.example.ui.viewmodel.AthleteViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// --- Chatroom Models ---
data class Chatroom(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val activeUsersCount: Int,
    val gradientColors: List<Color>
)

data class ChatroomMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val isSystem: Boolean = false,
    val avatarColor: Color = Color(0xFF64748B)
)

data class FloatingEmoji(
    val id: Long,
    val emoji: String,
    val xOffset: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsScreen(
    viewModel: AthleteViewModel,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val callLogs by viewModel.allCallLogs.collectAsState()

    // Screen navigation state: "Rooms", "History"
    var currentSubTab by remember { mutableStateOf("Rooms") }

    // Chatroom List
    val chatrooms = remember {
        listOf(
            Chatroom(
                id = "creative_hub",
                name = "Creative Hub & Ideas Lounge",
                description = "Discuss design, indie coding, digital art, and synth music.",
                category = "💡 CREATIVE",
                activeUsersCount = 42,
                gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
            ),
            Chatroom(
                id = "active_life",
                name = "Active Life & Daily Streak",
                description = "Share training logs, mental concentration, and raw power drills.",
                category = "🏋️ WELLNESS",
                activeUsersCount = 18,
                gradientColors = listOf(Color(0xFF10B981), Color(0xFF059669))
            ),
            Chatroom(
                id = "tech_sandbox",
                name = "The Sovereign Tech Sandbox",
                description = "Decentralized networks, encryption, privacy, and local-first apps.",
                category = "🔒 TECH",
                activeUsersCount = 29,
                gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF2563EB))
            ),
            Chatroom(
                id = "art_music",
                name = "Art & Music Fusion Lounge",
                description = "Where acoustic melodies blend with modern synthesizer beats.",
                category = "🎵 MUSIC",
                activeUsersCount = 15,
                gradientColors = listOf(Color(0xFFEC4899), Color(0xFFDB2777))
            )
        )
    }

    // Messages state for each room
    val chatroomMessagesMap = remember {
        mutableStateMapOf<String, List<ChatroomMessage>>().apply {
            put(
                "creative_hub",
                listOf(
                    ChatroomMessage("ch1", "Neeraj Chopra", "Hey everyone! Just drafted a plan for our next physical design meetup. Let's make it awesome!", System.currentTimeMillis() - 3600000, avatarColor = Color(0xFFF59E0B)),
                    ChatroomMessage("ch2", "PV Sindhu", "Count me in! I can share some details about our creative focus and recovery routines. 🏸", System.currentTimeMillis() - 1800000, avatarColor = Color(0xFFEC4899)),
                    ChatroomMessage("ch3", "Aarav Sharma", "Awesome! I'll bring some interactive prototypes of my indie game. Privacy-first, of course! 💻🕹️", System.currentTimeMillis() - 600000, avatarColor = Color(0xFF3B82F6))
                )
            )
            put(
                "active_life",
                listOf(
                    ChatroomMessage("al1", "PV Sindhu", "Completed 2 hours of agile footwork training today! Consistency is key. 🏸🏃‍♀️", System.currentTimeMillis() - 5000000, avatarColor = Color(0xFFEC4899)),
                    ChatroomMessage("al2", "Manu Bhaker", "Agreed. Mental concentration during high heart rate is super important. 🧘‍♀️🎯", System.currentTimeMillis() - 2500000, avatarColor = Color(0xFF10B981)),
                    ChatroomMessage("al3", "Mirabai Chanu", "Just hit a new squat PR! Fueling up with some high-protein diet now. 💪 Let's stay focused.", System.currentTimeMillis() - 500000, avatarColor = Color(0xFFF59E0B))
                )
            )
            put(
                "tech_sandbox",
                listOf(
                    ChatroomMessage("ts1", "Aarav Sharma", "Who here has explored local-first sync protocols using SQLite and CRDTs?", System.currentTimeMillis() - 4000000, avatarColor = Color(0xFF3B82F6)),
                    ChatroomMessage("ts2", "Riya Sen", "Yes! Combining SQLite with a custom Canvas UI makes real-time drawing so incredibly snappy and light.", System.currentTimeMillis() - 2000000, avatarColor = Color(0xFFEC4899)),
                    ChatroomMessage("ts3", "Siddharth Goel", "The code is pure poetry when there is no centralized database harvesting our private keystrokes! 🛡️🗝️", System.currentTimeMillis() - 800000, avatarColor = Color(0xFF10B981))
                )
            )
            put(
                "art_music",
                listOf(
                    ChatroomMessage("am1", "Ananya Rao", "Hey all, just recorded a session combining Indian sitar ragas with analog synthesizer filter sweeps. 🎵🌅", System.currentTimeMillis() - 3000000, avatarColor = Color(0xFFEC4899)),
                    ChatroomMessage("am2", "Riya Sen", "That sounds absolutely beautiful, Ananya! Can we use it as background music for our next concept trailer? 🎨", System.currentTimeMillis() - 1500000, avatarColor = Color(0xFFF59E0B)),
                    ChatroomMessage("am3", "Siddharth Goel", "The blend of ancient tradition with cyber synth wave is exactly what Morning Gram is about.", System.currentTimeMillis() - 400000, avatarColor = Color(0xFF3B82F6))
                )
            )
        }
    }

    // Currently opened Chatroom (null means showing room directory)
    var activeChatroom by remember { mutableStateOf<Chatroom?>(null) }

    // Live Audio Call inside active room states
    var isLiveAudioCallActive by remember { mutableStateOf(false) }
    var activeCallRoomId by remember { mutableStateOf<String?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    var activeSoundscape by remember { mutableStateOf<String?>(null) } // Ambient soundboard
    var callSeconds by remember { mutableStateOf(0) }

    // Floating reaction particles state
    var floatingEmojis by remember { mutableStateOf(listOf<FloatingEmoji>()) }
    val textInputState = remember { mutableStateOf("") }

    // Discussion Topic Prompts List
    val discussionTopics = remember {
        listOf(
            "If you could build any sovereign app in 24 hours, what would it be and why?",
            "What is your ultimate digital detox or morning routine to reclaim raw human focus?",
            "What's the most mind-bending piece of digital art or code you've written recently?",
            "How do we encourage more people to switch from addictive feeds to ad-free creative spaces?",
            "What is your secret habit for keeping your mental peace under high physical/creative pressure?",
            "Share an underrated analog tool or instrument that you absolutely cannot live without!"
        )
    }

    // Function to add messages
    fun postMessage(roomId: String, sender: String, text: String, isSystem: Boolean = false, avatarColor: Color = Color(0xFF64748B)) {
        val currentList = chatroomMessagesMap[roomId] ?: emptyList()
        chatroomMessagesMap[roomId] = currentList + ChatroomMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = sender,
            text = text,
            timestamp = System.currentTimeMillis(),
            isSystem = isSystem,
            avatarColor = avatarColor
        )
    }

    // Start Call Timer
    LaunchedEffect(isLiveAudioCallActive) {
        if (isLiveAudioCallActive) {
            callSeconds = 0
            while (isLiveAudioCallActive) {
                delay(1000)
                callSeconds += 1
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1113))
            .statusBarsPadding()
    ) {
        if (activeChatroom == null) {
            // --- ROOM DIRECTORY OR LOGS HEADER ---
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Morning Gram",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                            fontSize = 28.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sovereign Chatrooms & Group Audio",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (callLogs.isNotEmpty() && currentSubTab == "History") {
                        TextButton(
                            onClick = { viewModel.clearAllCallHistory() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear Logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Sub Tabs Selector
                TabRow(
                    selectedTabIndex = if (currentSubTab == "Rooms") 0 else 1,
                    containerColor = Color(0xFF16191C),
                    contentColor = Color(0xFFF59E0B),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp)
                ) {
                    Tab(
                        selected = currentSubTab == "Rooms",
                        onClick = { currentSubTab = "Rooms" },
                        text = { Text("Active Chatrooms", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = currentSubTab == "History",
                        onClick = { currentSubTab = "History" },
                        text = { Text("Call History Logs", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Content directory
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (currentSubTab == "Rooms") {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "GLOBAL CHATROOMS",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }

                        items(chatrooms) { room ->
                            ChatroomCard(
                                room = room,
                                onClick = { activeChatroom = room }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                } else {
                    // Call Logs History
                    if (callLogs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                                Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(64.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No call history yet", color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Your group call sessions and audio channel logs will appear here.", color = Color(0xFF64748B), fontSize = 11.sp, textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .testTag("call_logs_list"),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(callLogs) { log ->
                                CallLogItem(log)
                            }
                        }
                    }
                }
            }
        } else {
            // --- CHATROOM IS SELECTED ---
            val room = activeChatroom!!
            val messages = chatroomMessagesMap[room.id] ?: emptyList()
            val listState = rememberLazyListState()

            // Scroll to bottom when messages load or change
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Chatroom top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF16191C))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        IconButton(onClick = { activeChatroom = null }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to list", tint = Color.White)
                        }
                        
                        Spacer(modifier = Modifier.width(4.dp))

                        Column {
                            Text(
                                text = room.name,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${room.activeUsersCount} online • E2E Secured Channel",
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Start/Join Call Button
                    Button(
                        onClick = {
                            isLiveAudioCallActive = true
                            activeCallRoomId = room.id
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLiveAudioCallActive && activeCallRoomId == room.id) Color(0xFFEF4444) else Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiveAudioCallActive && activeCallRoomId == room.id) Icons.Default.CallEnd else Icons.Default.Call,
                            contentDescription = "Join Call",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isLiveAudioCallActive && activeCallRoomId == room.id) "In Stage" else "Join Stage",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Call Overlay Bar inside Chatroom
                if (isLiveAudioCallActive && activeCallRoomId == room.id) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x2610B981))
                            .drawBottomBorder(Color(0x3310B981))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Active call",
                                tint = Color(0xFF10B981),
                                modifier = Modifier
                                    .size(16.dp)
                                    .scale(1.1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Connected to Audio Stage • %02d:%02d".format(callSeconds / 60, callSeconds % 60),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Mute micro toggle
                            IconButton(
                                onClick = { isMuted = !isMuted },
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isMuted) Color(0xFFEF4444) else Color(0x26FFFFFF))
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mute",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            // Leave Stage button
                            IconButton(
                                onClick = {
                                    isLiveAudioCallActive = false
                                    activeSoundscape = null
                                    scope.launch {
                                        val log = CallLogEntry(
                                            contactName = "${room.name} Stage",
                                            contactAvatar = "user_avatar",
                                            callType = "VOICE",
                                            direction = "OUTGOING",
                                            durationSeconds = callSeconds
                                        )
                                        viewModel.addCallLog(log)
                                    }
                                },
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "Disconnect",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }

                // Chatroom Info Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1B1E22))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "📌 Category: ${room.category} • ${room.description}",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Messages area + Particle container
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp)
                    ) {
                        items(messages) { msg ->
                            if (msg.isSystem) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0x1AF59E0B), RoundedCornerShape(20.dp))
                                            .border(0.5.dp, Color(0x33F59E0B), RoundedCornerShape(20.dp))
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = msg.text,
                                            color = Color(0xFFF59E0B),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (msg.senderName == "You") Arrangement.End else Arrangement.Start
                                ) {
                                    if (msg.senderName != "You") {
                                        // Display Avatar representation
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(msg.avatarColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = msg.senderName.take(1).uppercase(),
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }

                                    Column(
                                        horizontalAlignment = if (msg.senderName == "You") Alignment.End else Alignment.Start
                                    ) {
                                        if (msg.senderName != "You") {
                                            Text(
                                                text = msg.senderName,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 12.dp,
                                                        topEnd = 12.dp,
                                                        bottomStart = if (msg.senderName == "You") 12.dp else 0.dp,
                                                        bottomEnd = if (msg.senderName == "You") 0.dp else 12.dp
                                                    )
                                                )
                                                .background(
                                                    if (msg.senderName == "You") Color(0xFFF59E0B) else Color(0xFF1E2226)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                                .widthIn(max = 260.dp)
                                        ) {
                                            Text(
                                                text = msg.text,
                                                color = if (msg.senderName == "You") Color.Black else Color.White,
                                                fontSize = 13.sp,
                                                lineHeight = 17.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Render floating emoji particles
                    floatingEmojis.forEach { p ->
                        FloatingEmojiItem(
                            emoji = p.emoji,
                            xOffset = p.xOffset,
                            onFinished = {
                                floatingEmojis = floatingEmojis.filter { it.id != p.id }
                            }
                        )
                    }
                }

                // Live Audio Call Controls Soundboard (only visible during call)
                if (isLiveAudioCallActive && activeCallRoomId == room.id) {
                    GroupSoundboardPanel(
                        activeSoundscape = activeSoundscape,
                        onSoundscapeToggle = { name ->
                            activeSoundscape = if (activeSoundscape == name) null else name
                        }
                    )
                }

                // BOTTOM ACTION BAR: Input, AI Topic Spark, Reactions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF16191C))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. AI Topic Generator (Spark Discussion Dice)
                    IconButton(
                        onClick = {
                            val topic = discussionTopics.random()
                            postMessage(
                                roomId = room.id,
                                sender = "Sovereign AI Speaker",
                                text = "🎲 CHATROOM DISCUSSION TOPIC:\n\"$topic\"",
                                isSystem = true
                            )
                            // Simulate other participants typing responses
                            scope.launch {
                                delay(1500)
                                val replies = listOf(
                                    "Neeraj Chopra" to "Oh wow, that topic is incredibly true! I find putting my smartphone in locker mode during critical throws is absolute gold.",
                                    "PV Sindhu" to "Yes, absolutely! Having offline visual journals really forces your brain to capture deep ideas clearly.",
                                    "Aarav Sharma" to "Indeed! In local-first app development, keeping your mind in a continuous state of flow is much better than constant notifications.",
                                    "Mirabai Chanu" to "True! It's all about raw discipline. Reclaiming offline energy gives you 2x power on the field."
                                )
                                val selectedReply = replies.random()
                                postMessage(
                                    roomId = room.id,
                                    sender = selectedReply.first,
                                    text = selectedReply.second,
                                    avatarColor = if (selectedReply.first == "Neeraj Chopra") Color(0xFFF59E0B) else Color(0xFFEC4899)
                                )
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x13FFFFFF))
                            .size(36.dp)
                    ) {
                        Text("🎲", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // 2. Text Input Filed
                    OutlinedTextField(
                        value = textInputState.value,
                        onValueChange = { textInputState.value = it },
                        placeholder = { Text("Encrypted message...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF0F1113),
                            unfocusedContainerColor = Color(0xFF0F1113),
                            focusedBorderColor = Color(0x33FFFFFF),
                            unfocusedBorderColor = Color(0x1AFFFFFF)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        trailingIcon = {
                            if (textInputState.value.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        val typedText = textInputState.value
                                        textInputState.value = ""
                                        postMessage(room.id, "You", typedText)

                                        // Simulated participant automatic reply delay
                                        scope.launch {
                                            delay(1500)
                                            val creatorReplies = listOf(
                                                "Aarav Sharma" to "Love that thought! Fully aligned with our sovereign space vision on Morning Gram. 🛡️🌱",
                                                "Riya Sen" to "Totally agree! Let's continue keeping our digital community authentic and beautiful. 🎨✨",
                                                "PV Sindhu" to "Got it! Thanks for keeping the energy high here. Let's do our best!",
                                                "Siddharth Goel" to "The honesty in these messages is wonderful. A true ad-free sanctuary."
                                            )
                                            val chosen = creatorReplies.random()
                                            postMessage(room.id, chosen.first, chosen.second, avatarColor = Color(0xFF3B82F6))
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // 3. Floating Reactions bar
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x0AFFFFFF))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        val reactionEmojis = listOf("🔥", "👏", "😂", "❤️", "🚀")
                        reactionEmojis.forEach { emoji ->
                            Text(
                                text = emoji,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .clickable {
                                        val newId = System.nanoTime()
                                        val randomX = Random.nextFloat() * 160f - 80f // spread particles across width
                                        floatingEmojis = floatingEmojis + FloatingEmoji(newId, emoji, randomX)
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingEmojiItem(
    emoji: String,
    xOffset: Float,
    onFinished: () -> Unit
) {
    var animTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animTrigger = true
    }

    val animatedY by animateFloatAsState(
        targetValue = if (animTrigger) -450f else 0f,
        animationSpec = tween(durationMillis = 2400, easing = LinearEasing),
        finishedListener = { onFinished() }
    )
    val animatedAlpha by animateFloatAsState(
        targetValue = if (animTrigger) 0f else 1f,
        animationSpec = tween(durationMillis = 2400, easing = EaseInExpo)
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (animTrigger) 1.6f else 0.7f,
        animationSpec = tween(durationMillis = 2400, easing = EaseOutBack)
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .offset(x = xOffset.dp, y = animatedY.dp)
                .scale(animatedScale)
                .alpha(animatedAlpha)
        ) {
            Text(text = emoji, fontSize = 28.sp)
        }
    }
}

@Composable
fun ChatroomCard(
    room: Chatroom,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF16191C))
            .border(1.dp, Color(0x0AFFFFFF), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left gradient circular icon with category emoji
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(room.gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = room.category.split(" ").firstOrNull() ?: "💬",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = room.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = room.description,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${room.activeUsersCount} active now • Open Voice Channel",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open Chatroom",
            tint = Color(0xFF64748B),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun GroupSoundboardPanel(
    activeSoundscape: String?,
    onSoundscapeToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF16191C))
            .drawTopBorder(Color(0x1AFFFFFF))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = "🔊 LIVE CALL SOUNDBOARD (BACKGROUND MOOD SYNTHESIZER)",
            color = Color(0xFF94A3B8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            val moodLoops = listOf(
                "Zen Rain" to "🌦️",
                "Binaural Focus" to "🧘‍♀️",
                "Midnight Synth" to "🌌",
                "Sitar Wave" to "🪕",
                "Deep Ocean" to "🌊"
            )

            moodLoops.forEach { (name, icon) ->
                val isActive = activeSoundscape == name
                FilterChip(
                    selected = isActive,
                    onClick = { onSoundscapeToggle(name) },
                    label = { Text("$icon $name", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF59E0B),
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0x0EFFFFFF),
                        labelColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // Display animated peer voice visualization canvas if call is on
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color(0xFF0F1113), RoundedCornerShape(8.dp))
                .border(0.5.dp, Color(0x1AFFFFFF), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Interactive Frequency Waves: ${activeSoundscape ?: "Live Micro Streams Only"}",
                color = if (activeSoundscape != null) Color(0xFFF59E0B) else Color(0xFF34D399),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )

            // Pure-compose live sine wave frequency spikes simulation
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val infiniteTransition = rememberInfiniteTransition()
                val sizes = listOf(14.dp, 22.dp, 10.dp, 28.dp, 16.dp, 24.dp, 8.dp)
                sizes.forEachIndexed { index, defaultSize ->
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400 + (index * 120), easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(defaultSize * scale)
                            .clip(CircleShape)
                            .background(
                                if (activeSoundscape != null) Color(0xFFF59E0B) else Color(0xFF34D399)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun CallLogItem(log: CallLogEntry) {
    val formatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateString = formatter.format(Date(log.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF16191C))
            .border(1.dp, Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFF59E0B), Color(0xFFEF4444), Color(0xFFA5B4FC))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = log.contactName.take(2).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = log.contactName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                    val (icon, tint) = when(log.direction) {
                        "OUTGOING" -> Pair(Icons.Default.CallMade, Color(0xFF60A5FA))
                        "INCOMING" -> Pair(Icons.Default.CallReceived, Color(0xFF34D399))
                        else -> Pair(Icons.Default.CallMissed, Color(0xFFF87171))
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = log.direction,
                        tint = tint,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${log.direction.lowercase().replaceFirstChar { it.uppercase() }} • $dateString",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Icon(
                imageVector = Icons.Default.Forum,
                contentDescription = log.callType,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (log.durationSeconds > 0) {
                    val mins = log.durationSeconds / 60
                    val secs = log.durationSeconds % 60
                    "%02d:%02d".format(mins, secs)
                } else {
                    "Cancelled"
                },
                color = Color(0xFF34D399),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Custom Helpers for elegant compose borders
fun Modifier.drawTopBorder(color: Color, strokeWidth: Float = 1f) = this.drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = strokeWidth
    )
}

fun Modifier.drawBottomBorder(color: Color, strokeWidth: Float = 1f) = this.drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidth
    )
}
