package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Athlete
import com.example.ui.viewmodel.AthleteViewModel

data class AthleteUpdate(
    val id: String,
    val athleteId: String,
    val type: String,
    val topic: String,
    val content: String,
    val inspiration: String,
    val date: String,
    val likesCount: Int
)

val athleteUpdatesList = listOf(
    AthleteUpdate(
        id = "update_srk_1",
        athleteId = "srk",
        type = "CINEMA & LIFE",
        topic = "The Grace of Relentless Discipline",
        content = "Every night before a major shoot, I read the script until the characters live inside my pulse. True passion is not about stardom—it is about honoring the craft and respecting your audience every single morning.",
        inspiration = "work like it's your first day, and dream like it's your last.",
        date = "Today, 10:00 AM",
        likesCount = 89400
    ),
    AthleteUpdate(
        id = "update_deepika_1",
        athleteId = "deepika",
        type = "WELLNESS & FOCUS",
        topic = "Mental Wellness in a Fast Digital Era",
        content = "Self-care starts with intentional quiet moments. Between film sets and brand creation, taking 10 minutes to breathe, reflect, and stay grounded is my superpower.",
        inspiration = "your inner peace is your greatest luxury.",
        date = "Today, 09:45 AM",
        likesCount = 65200
    ),
    AthleteUpdate(
        id = "update_arijit_1",
        athleteId = "arijit",
        type = "MUSICAL HARMONY",
        topic = "Acoustic Warmups in Jiaganj",
        content = "Recording raw acoustic takes early in the morning before sunrise. When vocals are unedited and direct from the heart, listeners feel the true emotion behind every note.",
        inspiration = "sing not for applause, but to touch the human soul.",
        date = "Today, 07:15 AM",
        likesCount = 74100
    ),
    AthleteUpdate(
        id = "update_carry_1",
        athleteId = "carry",
        type = "CREATOR LOG",
        topic = "Building Content Without Boundaries",
        content = "Editing the next sketch video! Creators need authentic, direct channels with fans where zero middleman algorithms distort our voices. Keep creating what you love!",
        inspiration = "authenticity always beats artificial hype.",
        date = "Today, 11:20 AM",
        likesCount = 51200
    ),
    AthleteUpdate(
        id = "update_diljit_1",
        athleteId = "diljit",
        type = "STADIUM VIBES",
        topic = "Aura of Global Live Performances",
        content = "Stadium rehearsals with full brass and traditional dhol! Bringing Punjabi rhythm and high energy to world stages. Thank you to all fans for the boundless love!",
        inspiration = "spread joy and let your roots shine globally.",
        date = "Yesterday, 08:45 PM",
        likesCount = 82300
    ),
    AthleteUpdate(
        id = "update_v_kohli_1",
        athleteId = "v_kohli",
        type = "ATHLETIC DISCIPLINE",
        topic = "Conditioning & High-Intensity Agility",
        content = "Pre-dawn gym session done: 100kg deadlifts and shuttle sprints. Consistency is doing what you must even when nobody is watching. Stay hungry!",
        inspiration = "hard work beats talent when talent doesn't work hard.",
        date = "Today, 06:30 AM",
        likesCount = 98100
    ),
    AthleteUpdate(
        id = "update_alia_1",
        athleteId = "alia",
        type = "CREATIVE VIBES",
        topic = "Storytelling & Expressive Acting",
        content = "Wrapped a powerful emotional scene today. Acting is all about empathy—putting yourself entirely in someone else's shoes and being completely present.",
        inspiration = "embrace every emotion; it fuels your artistic journey.",
        date = "Yesterday, 06:10 PM",
        likesCount = 58900
    ),
    AthleteUpdate(
        id = "update_beerbiceps_1",
        athleteId = "beerbiceps",
        type = "MINDSET & SPIRIT",
        topic = "Daily Meditation & Morning Routine",
        content = "Recorded 2 podcast episodes on ancient wisdom and modern productivity. High-performing leaders protect their attention span fiercely.",
        inspiration = "master your morning to conquer your day.",
        date = "Today, 08:10 AM",
        likesCount = 31400
    ),
    AthleteUpdate(
        id = "update_neeraj_1",
        athleteId = "neeraj",
        type = "TRAINING PROTOCOL",
        topic = "Sprinting Mechanics in Headwinds",
        content = "Today we focused entirely on crosswind javelin release angles. When throwing into headwinds, keep the tip low and pull through with direct abdominal tension.",
        inspiration = "poise under pressure is the champion's hidden signature.",
        date = "Today, 08:30 AM",
        likesCount = 24500
    ),
    AthleteUpdate(
        id = "update_sindhu_1",
        athleteId = "sindhu",
        type = "AGILITY WORKOUT",
        topic = "Multi-directional Footwork Speed",
        content = "Sprinting lunges with band resistance. We are pushing for 45 reps per minute to sustain extreme physical rally lengths in the third set.",
        inspiration = "rally through the pain; that's where gold is forged.",
        date = "Yesterday, 04:30 PM",
        likesCount = 31200
    )
)

@Composable
fun ExploreScreen(
    viewModel: AthleteViewModel,
    modifier: Modifier = Modifier
) {
    val athletes by viewModel.allAthletes.collectAsState()
    val followedAthletes = athletes.filter { it.isFollowing }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var activeSubTab by remember { mutableStateOf("directory") } // "directory" or "updates"

    // Sessions reaction counts state map to allow live increments to persist nicely
    val updateReactions = remember {
        mutableStateMapOf<String, Int>().apply {
            athleteUpdatesList.forEach { update ->
                this["${update.id}_power"] = update.likesCount
                this["${update.id}_grit"] = update.likesCount / 2
                this["${update.id}_support"] = update.likesCount / 4
            }
        }
    }
    
    // User click tracker so they can toggle their reactions and see increments/decrements
    val userReactionToggles = remember { mutableStateMapOf<String, Boolean>() }

    val categories = listOf(
        "All", "Bollywood", "Singers & Music", "Sports", "Influencers", "Game & Tech Dev", "Art & Design"
    )

    // Dynamic youth inspirational quotes
    val inspirationQuotes = remember {
        listOf(
            "“Work like it's your first day, and dream like it's your last.” — Shah Rukh Khan",
            "“Your inner peace is your greatest luxury.” — Deepika Padukone",
            "“Sing not for applause, but to touch the human soul.” — Arijit Singh",
            "“Authenticity always beats artificial hype.” — CarryMinati",
            "“Hard work beats talent when talent doesn't work hard.” — Virat Kohli",
            "“Spread joy and let your roots shine globally.” — Diljit Dosanjh",
            "“When you want to succeed as bad as you want to breathe, then you will be successful.” — Neeraj Chopra"
        )
    }
    var activeQuoteIndex by remember { mutableStateOf(0) }

    // Gemini Filter states
    val activeFilterName by viewModel.activeFilterName.collectAsState()
    val activeFilterDescription by viewModel.activeFilterDescription.collectAsState()
    val activeColorMatrix by viewModel.activeColorMatrix.collectAsState()
    val isGeneratingFilter by viewModel.isGeneratingFilter.collectAsState()
    val filterAuthorCode by viewModel.filterAuthorCode.collectAsState()
    val filterTrendScore by viewModel.filterTrendScore.collectAsState()

    var filterPrompt by remember { mutableStateOf("") }
    val visualVibes = listOf("Cyberpunk", "Retro Sunset", "Arctic Breeze", "Midnight Noir")

    val activeChatRecipient by viewModel.activeChatRecipient.collectAsState()
    val activeProfileAthlete by viewModel.activeProfileAthlete.collectAsState()

    // Filter creators
    val filteredAthletes = athletes.filter { athlete ->
        val matchesSearch = athlete.name.contains(searchQuery, ignoreCase = true) ||
                athlete.sport.contains(searchQuery, ignoreCase = true) ||
                athlete.bio.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "Bollywood" -> athlete.sport.contains("Actor", ignoreCase = true) || athlete.sport.contains("Actress", ignoreCase = true) || athlete.sport.contains("Bollywood", ignoreCase = true)
            "Singers & Music" -> athlete.sport.contains("Singer", ignoreCase = true) || athlete.sport.contains("Music", ignoreCase = true) || athlete.sport.contains("Rapper", ignoreCase = true) || athlete.sport.contains("Composer", ignoreCase = true)
            "Sports" -> athlete.sport in listOf("Javelin Throw", "Badminton", "Shooting", "Football", "Weightlifting", "Boxing", "Wrestling", "Cricket Champion") || athlete.sport.contains("Cricket", ignoreCase = true)
            "Influencers" -> athlete.sport.contains("Influencer", ignoreCase = true) || athlete.sport.contains("Creator", ignoreCase = true) || athlete.sport.contains("Gamer", ignoreCase = true) || athlete.sport.contains("Podcaster", ignoreCase = true)
            "Game & Tech Dev" -> athlete.sport.contains("Dev", ignoreCase = true) || athlete.sport.contains("Tech", ignoreCase = true)
            "Art & Design" -> athlete.sport.contains("Artist", ignoreCase = true) || athlete.sport.contains("Poetry", ignoreCase = true) || athlete.sport.contains("Design", ignoreCase = true)
            else -> athlete.sport.contains(selectedCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1113))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // App brand header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
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
                modifier = Modifier.size(20.dp)
            )
        }

        // Custom sub-tabs switcher bar matching modern M3 visual vibes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(Color(0x13FFFFFF), RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val followedAthletesCount = followedAthletes.size
            
            // Sub-Tab 1: Creators Directory
            val isDirActive = activeSubTab == "directory"
            Button(
                onClick = { activeSubTab = "directory" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDirActive) Color(0xFFF59E0B) else Color.Transparent,
                    contentColor = if (isDirActive) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .height(36.dp)
                    .testTag("sub_tab_directory"),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isDirActive) Color.Black else Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Directory", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Sub-Tab 2: Following Updates
            val isUpdatesActive = activeSubTab == "updates"
            Button(
                onClick = { activeSubTab = "updates" },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUpdatesActive) Color(0xFFF59E0B) else Color.Transparent,
                    contentColor = if (isUpdatesActive) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1.8f)
                    .height(36.dp)
                    .testTag("sub_tab_updates"),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isUpdatesActive) Color.Black else Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Following Updates", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    if (followedAthletesCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(if (isUpdatesActive) Color.Black else Color(0xFFF59E0B), CircleShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = followedAthletesCount.toString(),
                                color = if (isUpdatesActive) Color(0xFFF59E0B) else Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (activeSubTab == "directory") {
                item {
                Column {
                    Text(
                        text = "Global Youth Directory",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Sovereign ad-free social sanctuary for world-class talents.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // Inspiration Corner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            activeQuoteIndex = (activeQuoteIndex + 1) % inspirationQuotes.size
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0x13FFFFFF), Color(0x24FFFFFF))
                                )
                            )
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TipsAndUpdates,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DAILY SPARKS",
                                        color = Color(0xFFF59E0B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Text(
                                    text = "Tap ↻",
                                    color = Color(0xFFC084FC),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = inspirationQuotes[activeQuoteIndex],
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // --- GEMINI TRENDING FILTER EFFECT LAB ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_filter_lab_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131517)),
                    border = BorderStroke(1.dp, Color(0x26FFFFFF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFA855F7),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GEMINI TRENDING FILTER LAB",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Design custom visual themes using Gemini AI. Generated filters style your media feed globally.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Input Field
                        OutlinedTextField(
                            value = filterPrompt,
                            onValueChange = { filterPrompt = it },
                            placeholder = { Text("Describe a visual atmosphere (e.g. vintage warm)", color = Color(0xFF64748B), fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFA855F7),
                                unfocusedBorderColor = Color(0x1EFFFFFF),
                                focusedContainerColor = Color(0x13FFFFFF),
                                unfocusedContainerColor = Color(0x0AFFFFFF)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("gemini_filter_input"),
                            singleLine = true,
                            trailingIcon = {
                                if (filterPrompt.isNotEmpty()) {
                                    IconButton(onClick = { filterPrompt = "" }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick suggestions Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            visualVibes.forEach { vibe ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x1AFFFFFF))
                                        .clickable { filterPrompt = vibe }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = vibe, color = Color(0xFFCBD5E1), fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Generate Button
                        Button(
                            onClick = { viewModel.generateGeminiFilter(filterPrompt) },
                            enabled = filterPrompt.isNotBlank() && !isGeneratingFilter,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA855F7),
                                disabledContainerColor = Color(0x33A855F7)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("generate_filter_button")
                        ) {
                            if (isGeneratingFilter) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generating matrices...", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate with Gemini-3.5-Flash", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        // Active Filter Preview Dashboard
                        if (activeFilterName != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0x13FFFFFF))
                            Spacer(modifier = Modifier.height(10.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0x0AFFFFFF), RoundedCornerShape(8.dp))
                                    .border(0.5.dp, Color(0x2210B981), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = activeFilterName!!,
                                        color = Color(0xFF10B981),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Bolt, contentDescription = "Trending", tint = Color(0xFFF59E0B), modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = "Trend: ${filterTrendScore ?: 95}%", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = activeFilterDescription ?: "Visual atmosphere successfully applied globally.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                // Real Color Matrix inspection values
                                if (activeColorMatrix != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Active Matrix [4x5 Vectors]:\n" +
                                                activeColorMatrix!!.take(10).map { String.format("%.2f", it) }.joinToString(", ") + "...",
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                        color = Color(0xFF64748B),
                                        fontSize = 8.sp,
                                        lineHeight = 11.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Engine: ${filterAuthorCode ?: "GEMINI-PRO"}", color = Color(0xFF64748B), fontSize = 9.sp)
                                    Text(
                                        text = "Reset Original",
                                        color = Color(0xFFEF4444),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clickable { viewModel.clearActiveFilter() }
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Search Bar & Filter Headers
            item {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search youth creators...", color = Color(0xFFCBD5E1)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFCBD5E1)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0x1EFFFFFF),
                            focusedContainerColor = Color(0x1EFFFFFF),
                            unfocusedContainerColor = Color(0x13FFFFFF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("athlete_search_bar")
                    )

                    // Categories list Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            val isSelected = category == selectedCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) Color(0xFFF59E0B) else Color(0x13FFFFFF))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color.Transparent else Color(0x1AFFFFFF),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedCategory = category }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = category,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Lazy Grid representation of Creators inside LazyColumn
            if (filteredAthletes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SentimentDissatisfied,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No creators found matching search",
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                // Chunk grid entries of 2 manually to fit inside parent scrollable Column cleanly!
                val chunks = filteredAthletes.chunked(2)
                chunks.forEach { rowItems ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { creator ->
                                Box(modifier = Modifier.weight(1f)) {
                                    AthleteGridCard(
                                        athlete = creator,
                                        onFollowToggle = { viewModel.toggleFollow(creator.id, creator.isFollowing, creator.isPrivate) },
                                        onChatClick = { viewModel.activeChatRecipient.value = creator },
                                        onProfileClick = { viewModel.activeProfileAthlete.value = creator }
                                    )
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            } else {
                // FOLLOWED CREATORS UPDATES TAB
                if (followedAthletes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)),
                            border = BorderStroke(1.dp, Color(0x26FFFFFF))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(Color(0x1AF59E0B), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "No Followed Creators Yet",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Follow world-class athletes, musicians, and designers in the Creators Directory to unlock their direct E2E updates and daily inspiration here!",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { activeSubTab = "directory" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF59E0B),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Text("Explore Directory", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                } else {
                    // Daily AI Inspiration card
                    item {
                        val activeInspirationChallenge by viewModel.activeInspirationChallenge.collectAsState()
                        val isGeneratingInspiration by viewModel.isGeneratingInspiration.collectAsState()
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))), RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16191C))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0x22F59E0B), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "AI",
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Daily Sovereign AI Guide",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Powered by Gemini 3.5 Flash • Custom Grit Flow",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(14.dp))
                                
                                if (activeInspirationChallenge != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
                                            .padding(14.dp)
                                    ) {
                                        Text(
                                            text = activeInspirationChallenge!!,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                } else {
                                    Text(
                                        text = "Request a custom athletic focus guide and training challenge dynamically optimized for the athletes and sports you currently follow.",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                }
                                
                                Button(
                                    onClick = {
                                        val sportsList = followedAthletes.joinToString(", ") { it.sport }
                                        viewModel.generateInspirationChallenge(sportsList)
                                    },
                                    enabled = !isGeneratingInspiration,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF59E0B),
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    if (isGeneratingInspiration) {
                                        CircularProgressIndicator(
                                            color = Color.Black,
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Synthesizing Daily Protocol...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (activeInspirationChallenge != null) "Regenerate Protocol" else "Ask Gemini for Inspiration",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Feed Updates list
                    val followedUpdates = athleteUpdatesList.filter { update ->
                        followedAthletes.any { it.id == update.athleteId }
                    }
                    
                    items(followedUpdates) { update ->
                        val athleteObj = followedAthletes.find { it.id == update.athleteId }
                        if (athleteObj != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    // Header Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Avatar Ring
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .border(1.5.dp, Color(0xFFF59E0B), CircleShape)
                                                .clickable { viewModel.activeProfileAthlete.value = athleteObj }
                                                .padding(2.dp),
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
                                                    text = athleteObj.name.take(2).uppercase(),
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.width(10.dp))
                                        
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = athleteObj.name,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.clickable { viewModel.activeProfileAthlete.value = athleteObj }
                                                )
                                                if (athleteObj.isVerified) {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.Verified,
                                                        contentDescription = "Verified",
                                                        tint = Color(0xFF3B82F6),
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                            Text(
                                                text = athleteObj.sport,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 10.sp
                                            )
                                        }
                                        
                                        // Time Label and Badge
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = update.date,
                                                color = Color(0xFF64748B),
                                                fontSize = 9.sp
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0x1A10B981), RoundedCornerShape(6.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "E2E Secure",
                                                    color = Color(0xFF10B981),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Topic and Category tag
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0x1AFFFFFF), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = update.type,
                                                color = Color(0xFFF59E0B),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    
                                    Text(
                                        text = update.topic,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    
                                    Text(
                                        text = update.content,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    
                                    // Inspiration Tip block
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0x06FFFFFF), RoundedCornerShape(8.dp))
                                            .border(BorderStroke(0.5.dp, Color(0x13FFFFFF)), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row {
                                            Box(
                                                modifier = Modifier
                                                    .width(3.dp)
                                                    .height(32.dp)
                                                    .background(Color(0xFFF59E0B), RoundedCornerShape(1.5.dp))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "💡 DAILY CREATOR INSPIRATION",
                                                    color = Color(0xFFF59E0B),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 1.sp
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = update.inspiration,
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 11.sp,
                                                    fontStyle = FontStyle.Italic,
                                                    lineHeight = 15.sp
                                                )
                                            }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Divider(color = Color(0x0DFFFFFF), thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    // Interaction Bar
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Reaction 1: Power (Fire)
                                            val fireKey = "${update.id}_power"
                                            val isFireActive = userReactionToggles[fireKey] ?: false
                                            val fireCount = updateReactions[fireKey] ?: update.likesCount
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isFireActive) Color(0x33FF5722) else Color(0x0AFFFFFF))
                                                    .clickable {
                                                        val nextToggle = !isFireActive
                                                        userReactionToggles[fireKey] = nextToggle
                                                        updateReactions[fireKey] = fireCount + if (nextToggle) 1 else -1
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("🔥", fontSize = 11.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = fireCount.toString(),
                                                        color = if (isFireActive) Color(0xFFFF5722) else Color(0xFF94A3B8),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            
                                            // Reaction 2: Grit (Applause)
                                            val gritKey = "${update.id}_grit"
                                            val isGritActive = userReactionToggles[gritKey] ?: false
                                            val gritCount = updateReactions[gritKey] ?: (update.likesCount / 2)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isGritActive) Color(0x33FF9800) else Color(0x0AFFFFFF))
                                                    .clickable {
                                                        val nextToggle = !isGritActive
                                                        userReactionToggles[gritKey] = nextToggle
                                                        updateReactions[gritKey] = gritCount + if (nextToggle) 1 else -1
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("👏", fontSize = 11.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = gritCount.toString(),
                                                        color = if (isGritActive) Color(0xFFFF9800) else Color(0xFF94A3B8),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            
                                            // Reaction 3: Support (Heart)
                                            val supportKey = "${update.id}_support"
                                            val isSupportActive = userReactionToggles[supportKey] ?: false
                                            val supportCount = updateReactions[supportKey] ?: (update.likesCount / 4)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSupportActive) Color(0x33E91E63) else Color(0x0AFFFFFF))
                                                    .clickable {
                                                        val nextToggle = !isSupportActive
                                                        userReactionToggles[supportKey] = nextToggle
                                                        updateReactions[supportKey] = supportCount + if (nextToggle) 1 else -1
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("❤️", fontSize = 11.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = supportCount.toString(),
                                                        color = if (isSupportActive) Color(0xFFE91E63) else Color(0xFF94A3B8),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Direct Chat with Creator button
                                        IconButton(
                                            onClick = { viewModel.activeChatRecipient.value = athleteObj },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Send,
                                                contentDescription = "Message Creator",
                                                tint = Color(0xFF10B981),
                                                modifier = Modifier.size(16.dp)
                                            )
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
}

@Composable
fun AthleteGridCard(
    athlete: Athlete,
    onFollowToggle: () -> Unit,
    onChatClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x13FFFFFF)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp))
            .clickable { onProfileClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Ring & Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .border(2.dp, Color(0xFFF59E0B), CircleShape)
                    .padding(3.dp),
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Creator Name & Verified
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = athlete.name,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified",
                    tint = Color(0xFF3B82F6),
                    modifier = Modifier.size(12.dp)
                )
            }

            // Sport/Specialization Label
            Text(
                text = athlete.sport,
                color = Color(0xFF94A3B8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Followers label
            Text(
                text = "${formatFollowers(athlete.followerCount)} followers",
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Row of Follow and E2EE Chat buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isFollowing = athlete.isFollowing
                val isPending = athlete.isPendingRequest
                val isPrivate = athlete.isPrivate
                val canChat = !isPrivate || isFollowing

                Button(
                    onClick = onFollowToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing) {
                            Color(0x33FFFFFF)
                        } else if (isPending) {
                            Color(0x22F59E0B)
                        } else {
                            Color(0xFFF59E0B)
                        },
                        contentColor = if (isFollowing) {
                            Color.White
                        } else if (isPending) {
                            Color(0xFFF59E0B)
                        } else {
                            Color.Black
                        }
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text(
                        text = if (isFollowing) "Following" else if (isPending) "Requested" else "Follow",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { if (canChat) onChatClick() },
                    enabled = canChat,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canChat) Color(0xFF10B981) else Color(0x13FFFFFF),
                        contentColor = if (canChat) Color.White else Color(0xFF64748B)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (canChat) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(8.dp),
                            tint = if (canChat) Color.White else Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = if (canChat) "Chat" else "Locked",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun formatFollowers(count: Int): String {
    return if (count >= 1000000) {
        "${String.format("%.1f", count / 1000000f)}M"
    } else if (count >= 1000) {
        "${String.format("%.1f", count / 1000f)}K"
    } else {
        "$count"
    }
}
