package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Athlete
import com.example.data.model.ContractSignature
import com.example.data.model.Reel
import com.example.data.model.UserComment
import com.example.data.model.AppNote
import com.example.data.model.CallLogEntry

import com.example.data.repository.AthleteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

data class ChatMessage(
    val sender: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AthleteViewModel(
    private val repository: AthleteRepository,
    private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("morning_gram_referral_prefs", Context.MODE_PRIVATE)

    // Authentication System States
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", true)) // default logged in or active session
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUserName = MutableStateFlow(prefs.getString("current_user_name", "Aarav Sharma") ?: "Aarav Sharma")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private val _currentUserEmail = MutableStateFlow(prefs.getString("current_user_email", "aarav.creator@morninggram.in") ?: "aarav.creator@morninggram.in")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentUserHandle = MutableStateFlow(prefs.getString("current_user_handle", "@aarav_sharma") ?: "@aarav_sharma")
    val currentUserHandle: StateFlow<String> = _currentUserHandle.asStateFlow()

    private val _currentUserRole = MutableStateFlow(prefs.getString("current_user_role", "Creator & Cinema Lover") ?: "Creator & Cinema Lover")
    val currentUserRole: StateFlow<String> = _currentUserRole.asStateFlow()

    // Imaginative Feature States: Aura Vibe Sync & Star Voice Capsule
    private val _activeVibe = MutableStateFlow(prefs.getString("active_vibe", "Cinematic Drama") ?: "Cinematic Drama")
    val activeVibe: StateFlow<String> = _activeVibe.asStateFlow()

    private val _vibeMatchPercentage = MutableStateFlow(88)
    val vibeMatchPercentage: StateFlow<Int> = _vibeMatchPercentage.asStateFlow()

    private val _vibeMatchedStar = MutableStateFlow("Shah Rukh Khan")
    val vibeMatchedStar: StateFlow<String> = _vibeMatchedStar.asStateFlow()

    private val _isPlayingStarVoiceNote = MutableStateFlow(false)
    val isPlayingStarVoiceNote: StateFlow<Boolean> = _isPlayingStarVoiceNote.asStateFlow()

    private val _totalGiftsSent = MutableStateFlow(prefs.getInt("total_gifts_sent", 12))
    val totalGiftsSent: StateFlow<Int> = _totalGiftsSent.asStateFlow()

    private val _totalDuetsCreated = MutableStateFlow(prefs.getInt("total_duets_created", 3))
    val totalDuetsCreated: StateFlow<Int> = _totalDuetsCreated.asStateFlow()

    private val _trainingStreakDays = MutableStateFlow(prefs.getInt("training_streak_days", 7))
    val trainingStreakDays: StateFlow<Int> = _trainingStreakDays.asStateFlow()

    private val _claimedTrophies = MutableStateFlow(
        prefs.getStringSet("claimed_trophies", setOf("trophy_1_sprint_warrior")) ?: setOf("trophy_1_sprint_warrior")
    )
    val claimedTrophies: StateFlow<Set<String>> = _claimedTrophies.asStateFlow()

    private val _referralsCount = MutableStateFlow(prefs.getInt("referrals_count", 0))
    val referralsCount: StateFlow<Int> = _referralsCount.asStateFlow()

    private val _referredBy = MutableStateFlow<String?>(prefs.getString("referred_by", null))
    val referredBy: StateFlow<String?> = _referredBy.asStateFlow()

    private val _userReferralCode = MutableStateFlow(prefs.getString("user_referral_code", "") ?: "")
    val userReferralCode: StateFlow<String> = _userReferralCode.asStateFlow()

    private val _simulatedReferrals = MutableStateFlow<List<String>>(
        prefs.getString("simulated_referrals", "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
    )
    val simulatedReferrals: StateFlow<List<String>> = _simulatedReferrals.asStateFlow()

    // Cryptographic Keys & E2E Encrypted Chats
    val userPublicKey = MutableStateFlow("")
    val userPrivateKey = MutableStateFlow("")
    val activeChatRecipient = MutableStateFlow<Athlete?>(null)
    val activeProfileAthlete = MutableStateFlow<Athlete?>(null)
    val chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())

    // Custom Youth Track (Specialization / Interests)
    private val _userSpecialization = MutableStateFlow(prefs.getString("user_specialization", "Indie Creator / Tech") ?: "Indie Creator / Tech")
    val userSpecialization: StateFlow<String> = _userSpecialization.asStateFlow()

    // Gemini Filter Generator States
    val activeFilterName = MutableStateFlow<String?>(null)
    val activeFilterDescription = MutableStateFlow<String?>(null)
    val activeColorMatrix = MutableStateFlow<FloatArray?>(null)
    val isGeneratingFilter = MutableStateFlow(false)
    val filterAuthorCode = MutableStateFlow<String?>(null)
    val filterTrendScore = MutableStateFlow<Int?>(null)

    // Followed Athletes Updates & Inspiration AI states
    val activeInspirationChallenge = MutableStateFlow<String?>(null)
    val isGeneratingInspiration = MutableStateFlow(false)

    val hasProfileHighlight: StateFlow<Boolean> = combine(referralsCount, referredBy) { count, refBy ->
        count > 0 || refBy != null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = prefs.getInt("referrals_count", 0) > 0 || prefs.getString("referred_by", null) != null
    )

    val allAthletes: StateFlow<List<Athlete>> = repository.allAthletes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allReels: StateFlow<List<Reel>> = repository.allReels
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val followingReels: StateFlow<List<Reel>> = repository.followingReels
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val contractSignature: StateFlow<ContractSignature?> = repository.contractSignature
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Notes Persistence States (Useful for all people)
    val allNotes: StateFlow<List<AppNote>> = repository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Call Log States
    val allCallLogs: StateFlow<List<CallLogEntry>> = repository.allCallLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active Calling States
    val activeCallContact = MutableStateFlow<Athlete?>(null)
    val activeCallType = MutableStateFlow<String?>(null) // "VOICE" or "VIDEO"
    val isCallActive = MutableStateFlow(false)
    val isCallRinging = MutableStateFlow(false)
    val callDurationSeconds = MutableStateFlow(0)
    val isMuted = MutableStateFlow(false)
    val isCameraOff = MutableStateFlow(false)
    private var callTimerJob: kotlinx.coroutines.Job? = null


    val activeProfileAthleteReels: StateFlow<List<Reel>> = combine(activeProfileAthlete, allReels) { athlete, reels ->
        if (athlete == null) emptyList()
        else reels.filter { it.athleteId == athlete.id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        regenerateKeys()
        viewModelScope.launch {
            repository.checkAndPopulateMockData()
        }

        // Dynamically monitor contract signatures to update user referral code matching real name
        viewModelScope.launch {
            contractSignature.collect { signature ->
                val baseName = signature?.athleteName ?: "GUEST"
                val cleaned = baseName.uppercase().replace("\\s+".toRegex(), "")
                val currentCode = prefs.getString("user_referral_code", "") ?: ""
                if (currentCode.isEmpty() || (currentCode.startsWith("GUEST") && signature != null)) {
                    val code = "${cleaned}-${(1000..9999).random()}"
                    prefs.edit().putString("user_referral_code", code).apply()
                    _userReferralCode.value = code
                } else if (currentCode.isEmpty()) {
                    val code = "GUEST-${(1000..9999).random()}"
                    prefs.edit().putString("user_referral_code", code).apply()
                    _userReferralCode.value = code
                }
            }
        }
    }

    private val _selectedReelIdForComments = MutableStateFlow<String?>(null)
    val selectedReelIdForComments: StateFlow<String?> = _selectedReelIdForComments.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val commentsForSelectedReel: StateFlow<List<UserComment>> = _selectedReelIdForComments
        .flatMapLatest { reelId ->
            if (reelId != null) {
                repository.getComments(reelId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleLike(reelId: String, currentLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleLike(reelId, currentLiked)
        }
    }

    fun toggleFollow(athleteId: String, currentFollowing: Boolean, isPrivate: Boolean = false) {
        viewModelScope.launch {
            if (isPrivate && !currentFollowing) {
                val currentList = allAthletes.value
                val athlete = currentList.find { it.id == athleteId }
                val isPending = athlete?.isPendingRequest ?: false
                val nextPending = !isPending
                repository.updateAthletePendingRequest(athleteId, nextPending)
                
                // For a highly interactive live demo: auto-approve the request after 3 seconds with an encrypted greeting message!
                if (nextPending) {
                    launch {
                        kotlinx.coroutines.delay(3000)
                        // Verify that they are still pending
                        val currentAthlete = repository.allAthletes.first().find { it.id == athleteId }
                        if (currentAthlete?.isPendingRequest == true) {
                            repository.updateAthletePendingRequest(athleteId, false)
                            repository.toggleFollow(athleteId, false)
                            
                            // Send welcome chat message
                            val welcomeText = "Hey there! I just approved your follow request on Morning Gram. Thanks for respecting my private space. Let's connect here E2E encrypted! 🛡️🗝️"
                            val replyMessage = ChatMessage(currentAthlete.name, welcomeText)
                            val updatedMap = chatMessages.value.toMutableMap()
                            val updatedList = updatedMap[athleteId]?.toMutableList() ?: mutableListOf()
                            updatedList.add(replyMessage)
                            updatedMap[athleteId] = updatedList
                            chatMessages.value = updatedMap
                        }
                    }
                }
            } else {
                repository.toggleFollow(athleteId, currentFollowing)
                if (currentFollowing) {
                    repository.updateAthletePendingRequest(athleteId, false)
                }
            }
        }
    }

    fun approveFollowRequest(athleteId: String) {
        viewModelScope.launch {
            repository.updateAthletePendingRequest(athleteId, false)
            repository.toggleFollow(athleteId, false)
        }
    }

    fun signContract(name: String, signature: String) {
        viewModelScope.launch {
            repository.signContract(name, signature)
        }
    }

    fun removeContract() {
        viewModelScope.launch {
            repository.removeContract()
        }
    }

    fun setSelectedReelId(reelId: String?) {
        _selectedReelIdForComments.value = reelId
    }

    fun addComment(reelId: String, commentText: String) {
        viewModelScope.launch {
            repository.addComment(reelId, commentText)
        }
    }

    // --- Referral System Actions ---

    fun enterReferralCode(code: String): Boolean {
        val trimmed = code.trim().uppercase()
        if (trimmed.isEmpty()) return false
        
        // Cannot refer yourself
        if (trimmed == _userReferralCode.value.uppercase()) return false
        
        // Cannot enter code if already referred
        if (_referredBy.value != null) return false

        prefs.edit().putString("referred_by", trimmed).apply()
        _referredBy.value = trimmed
        return true
    }

    fun simulateReferral(peerName: String): Boolean {
        val trimmed = peerName.trim()
        if (trimmed.isEmpty()) return false

        val currentList = _simulatedReferrals.value.toMutableList()
        currentList.add(trimmed)
        _simulatedReferrals.value = currentList

        val nextCount = _referralsCount.value + 1
        _referralsCount.value = nextCount

        prefs.edit()
            .putInt("referrals_count", nextCount)
            .putString("simulated_referrals", currentList.joinToString(","))
            .apply()

        return true
    }

    fun resetReferralProgress() {
        prefs.edit().clear().apply()
        _referralsCount.value = 0
        _referredBy.value = null
        _simulatedReferrals.value = emptyList()
        
        val signature = contractSignature.value
        val baseName = signature?.athleteName ?: "GUEST"
        val cleaned = baseName.uppercase().replace("\\s+".toRegex(), "")
        val code = if (signature != null) "${cleaned}-${(1000..9999).random()}" else "GUEST-${(1000..9999).random()}"
        prefs.edit().putString("user_referral_code", code).apply()
        _userReferralCode.value = code
    }

    // --- Cryptography & E2E Encrypted Chat Helpers ---

    private fun generateRandomHex(length: Int): String {
        val chars = "0123456789ABCDEF"
        return (1..length).map { chars.random() }.joinToString("")
    }

    fun regenerateKeys() {
        userPublicKey.value = "04" + generateRandomHex(40)
        userPrivateKey.value = generateRandomHex(32)
    }

    fun sendEncryptedMessage(recipient: Athlete, text: String) {
        val recipientId = recipient.id
        val newMessage = ChatMessage("You", text)
        val currentMap = chatMessages.value.toMutableMap()
        val currentList = currentMap[recipientId]?.toMutableList() ?: mutableListOf()
        currentList.add(newMessage)
        currentMap[recipientId] = currentList
        chatMessages.value = currentMap

        // Trigger automatic encrypted reply
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            val replyText = when (recipientId) {
                "neeraj" -> "Got your encrypted ping! This E2EE is perfect. Focusing on my next throw without Instagram tracking my location! 🇮🇳 Javelin launch keys verified."
                "sindhu" -> "Received with perfect safety! 🏸 The team cannot see our private chats here. Reclaiming our digital rights as youth!"
                "aarav" -> "Hey! Cryptographic key-pair verified on my end. This is how we defeat the monopoly of Instagram. No data harvesting, just direct E2E encrypted connection! 💻🚀"
                "riya" -> "Beautifully received! My ECDH public key matched your signature. Our creative collabs stay 100% private. 🎨🌌"
                "ananya" -> "E2EE works flawlessly! The Morning Gram team cannot peek into our messages. Let's reclaim our music spaces without algorithmic manipulation. 🎻🌅"
                "siddharth" -> "The poetry of encryption: two keys, one secure bridge. Absolute private sanctuary on Morning Gram! 🕊️✍️"
                else -> "Key exchange successful! Message decrypted. Let's build a safe, ad-free youth community together."
            }
            val replyMessage = ChatMessage(recipient.name, replyText)
            val updatedMap = chatMessages.value.toMutableMap()
            val updatedList = updatedMap[recipientId]?.toMutableList() ?: mutableListOf()
            updatedList.add(replyMessage)
            updatedMap[recipientId] = updatedList
            chatMessages.value = updatedMap
        }
    }

    // --- Custom Specialization Settings ---

    fun setUserSpecialization(spec: String) {
        prefs.edit().putString("user_specialization", spec).apply()
        _userSpecialization.value = spec
    }

    // --- Gemini Filter Generator Action ---

    fun generateGeminiFilter(prompt: String) {
        if (prompt.isBlank()) return
        isGeneratingFilter.value = true
        
        viewModelScope.launch {
            val apiKey = com.example.BuildConfig.GEMINI_API_KEY
            var success = false
            
            if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                try {
                    val client = okhttp3.OkHttpClient.Builder()
                        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
                        
                    val jsonRequest = JSONObject().apply {
                        put("contents", JSONArray().apply {
                            put(JSONObject().apply {
                                put("parts", JSONArray().apply {
                                    put(JSONObject().apply {
                                        put("text", "Generate a trending visual effect filter based on this user prompt: '$prompt'. Respond ONLY with a valid single JSON object with these keys: 'name' (catchy trendy filter name), 'description' (1-sentence description), 'colorMatrix' (an array of exactly 20 float numbers representing a 4x5 color matrix for Android. Changing these floats shifts colors to fit the prompt's aesthetic. E.g. for cyberpunk: [1.2, 0.2, -0.1, 0, 0, -0.1, 1.3, 0.2, 0, 0, 0.2, -0.1, 1.4, 0, 0, 0, 0, 0, 1, 0]), 'trendScore' (Int between 82 and 99), 'authorCode' (e.g. 'GEMINI-PRO'). No markdown formatting, no ```json tags, just raw JSON text.")
                                    })
                                })
                            })
                        })
                        put("generationConfig", JSONObject().apply {
                            put("responseMimeType", "application/json")
                        })
                    }
                    
                    val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
                    val request = okhttp3.Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                        .post(body)
                        .build()
                        
                    val response = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        client.newCall(request).execute()
                    }
                    
                    response.use { resp ->
                        if (resp.isSuccessful) {
                            val respStr = resp.body?.string() ?: ""
                            val jsonResponse = JSONObject(respStr)
                            val candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0)
                            val textContent = candidate.getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                            
                            val parsedFilter = JSONObject(textContent.trim())
                            val name = parsedFilter.getString("name")
                            val desc = parsedFilter.getString("description")
                            val trend = parsedFilter.getInt("trendScore")
                            val author = parsedFilter.getString("authorCode")
                            val matrixJson = parsedFilter.getJSONArray("colorMatrix")
                            val matrix = FloatArray(20)
                            for (i in 0 until 20) {
                                matrix[i] = matrixJson.getDouble(i).toFloat()
                            }
                            
                            activeFilterName.value = name
                            activeFilterDescription.value = desc
                            activeColorMatrix.value = matrix
                            filterTrendScore.value = trend
                            filterAuthorCode.value = author
                            success = true
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AthleteViewModel", "Gemini API failed, falling back to local simulation: ${e.message}")
                }
            }
            
            if (!success) {
                // Generative local color filter simulation if API fails or key is missing
                kotlinx.coroutines.delay(1200) // Beautiful lifelike delay
                
                val lowerPrompt = prompt.lowercase()
                val name: String
                val desc: String
                val author = "GEMINI-LOCAL"
                val trend = (85..99).random()
                val matrix: FloatArray
                
                when {
                    lowerPrompt.contains("cyber") || lowerPrompt.contains("neon") || lowerPrompt.contains("punk") -> {
                        name = "Neon Cyberpunk"
                        desc = "Electric cyans and neon purples with boosted high-contrast highlights."
                        matrix = floatArrayOf(
                            1.2f, 0.0f, 0.3f, 0.0f, 15f,
                            -0.1f, 1.3f, 0.0f, 0.0f, -10f,
                            0.2f, -0.1f, 1.6f, 0.0f, 30f,
                            0.0f, 0.0f, 0.0f, 1.0f, 0f
                        )
                    }
                    lowerPrompt.contains("vintage") || lowerPrompt.contains("retro") || lowerPrompt.contains("polaroid") || lowerPrompt.contains("old") -> {
                        name = "Retro Polaroid"
                        desc = "Faded warm sepia tone with desaturated shadows and soft film highlights."
                        matrix = floatArrayOf(
                            0.95f, 0.05f, 0.0f, 0.0f, 10f,
                            0.05f, 0.85f, 0.0f, 0.0f, 5f,
                            0.0f, 0.05f, 0.70f, 0.0f, -15f,
                            0.0f, 0.0f, 0.0f, 1.0f, 0f
                        )
                    }
                    lowerPrompt.contains("golden") || lowerPrompt.contains("hour") || lowerPrompt.contains("sunset") || lowerPrompt.contains("warm") -> {
                        name = "Himalayan Golden Sunset"
                        desc = "Bathe your media in rich golden-orange sunset glow with soft warm contours."
                        matrix = floatArrayOf(
                            1.3f, 0.1f, 0.0f, 0.0f, 20f,
                            0.1f, 1.1f, 0.0f, 0.0f, 10f,
                            -0.2f, 0.0f, 0.8f, 0.0f, -20f,
                            0.0f, 0.0f, 0.0f, 1.0f, 0f
                        )
                    }
                    lowerPrompt.contains("cold") || lowerPrompt.contains("blue") || lowerPrompt.contains("freeze") || lowerPrompt.contains("winter") -> {
                        name = "Arctic Breeze"
                        desc = "Cool high-latitude blues with crisp white highlights and desaturated warm tones."
                        matrix = floatArrayOf(
                            0.8f, 0.0f, 0.0f, 0.0f, -10f,
                            0.0f, 0.9f, 0.1f, 0.0f, -5f,
                            0.1f, 0.1f, 1.4f, 0.0f, 20f,
                            0.0f, 0.0f, 0.0f, 1.0f, 0f
                        )
                    }
                    lowerPrompt.contains("dark") || lowerPrompt.contains("midnight") || lowerPrompt.contains("goth") || lowerPrompt.contains("mono") -> {
                        name = "Midnight Noir"
                        desc = "Deep shadow monochrome style with stark silver highlights for dramatic contrast."
                        matrix = floatArrayOf(
                            0.3f, 0.59f, 0.11f, 0.0f, -20f,
                            0.3f, 0.59f, 0.11f, 0.0f, -20f,
                            0.3f, 0.59f, 0.11f, 0.0f, -20f,
                            0.0f, 0.0f, 0.0f, 1.0f, 0f
                        )
                    }
                    else -> {
                        name = "Vibrant Youth Pop"
                        desc = "Generative aesthetic boost with enriched colors and crisp global lighting."
                        matrix = floatArrayOf(
                            1.15f, 0.0f, 0.05f, 0.0f, 5f,
                            0.05f, 1.15f, 0.0f, 0.0f, 5f,
                            0.0f, 0.05f, 1.15f, 0.0f, 5f,
                            0.0f, 0.0f, 0.0f, 1.0f, 0f
                        )
                    }
                }
                
                activeFilterName.value = name
                activeFilterDescription.value = desc
                activeColorMatrix.value = matrix
                filterTrendScore.value = trend
                filterAuthorCode.value = author
            }
            isGeneratingFilter.value = false
        }
    }

    fun clearActiveFilter() {
        activeFilterName.value = null
        activeFilterDescription.value = null
        activeColorMatrix.value = null
        filterTrendScore.value = null
        filterAuthorCode.value = null
    }

    fun generateInspirationChallenge(sportsList: String) {
        isGeneratingInspiration.value = true
        viewModelScope.launch {
            val apiKey = com.example.BuildConfig.GEMINI_API_KEY
            var success = false
            
            if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
                try {
                    val client = okhttp3.OkHttpClient.Builder()
                        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
                        
                    val jsonRequest = org.json.JSONObject().apply {
                        put("contents", org.json.JSONArray().apply {
                            put(org.json.JSONObject().apply {
                                put("parts", org.json.JSONArray().apply {
                                    put(org.json.JSONObject().apply {
                                        put("text", "Generate an elite sovereign athletic/mental focus training challenge for today based on these followed sports/interests: '$sportsList'. Respond ONLY with a valid single JSON object with these keys: 'title' (catchy title), 'challenge' (2-sentence specific challenge description), 'inspiration' (1-sentence elite motivational quote). No markdown formatting, no ```json tags, just raw JSON text.")
                                    })
                                })
                            })
                        })
                        put("generationConfig", org.json.JSONObject().apply {
                            put("responseMimeType", "application/json")
                        })
                    }
                    
                    val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
                    val request = okhttp3.Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                        .post(body)
                        .build()
                        
                    val response = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        client.newCall(request).execute()
                    }
                    
                    response.use { resp ->
                        if (resp.isSuccessful) {
                            val respStr = resp.body?.string() ?: ""
                            val jsonResponse = org.json.JSONObject(respStr)
                            val candidate = jsonResponse.getJSONArray("candidates").getJSONObject(0)
                            val textContent = candidate.getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                            
                            val parsed = org.json.JSONObject(textContent.trim())
                            val title = parsed.getString("title")
                            val challenge = parsed.getString("challenge")
                            val inspiration = parsed.getString("inspiration")
                            
                            activeInspirationChallenge.value = "🎯 **$title**\n\n$challenge\n\n*“$inspiration”*"
                            success = true
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AthleteViewModel", "Gemini inspiration failed: ${e.message}")
                }
            }
            
            if (!success) {
                // Local premium simulation
                kotlinx.coroutines.delay(1200)
                val titles = listOf(
                    "Sovereign Concentration Drill",
                    "Explosive Stamina Sprint",
                    "The Digital Silence Breathe",
                    "Unshakable Focus Protocol"
                )
                val challenges = listOf(
                    "Sit completely quiet for 5 minutes before your workout. Align your breath and visual focus on a single point without moving.",
                    "Integrate 4 sets of 50-meter max speed sprints into your training. Experience the raw propulsion of uninhibited speed.",
                    "Practice a 4-7-8 breathing loop for 3 minutes whenever you feel the urge to scroll. Reclaim your mental sovereign space.",
                    "Train for 30 minutes in absolute digital silence. Focus entirely on the muscle activation, joint feedback, and natural sound."
                )
                val quotes = listOf(
                    "Gold medals are forged in the quiet seconds of absolute internal dedication.",
                    "The strongest stabilizer muscle in any athlete is a sovereign, focused mind.",
                    "Do not seek validation from an app; seek validation from your own relentless progress.",
                    "You are the architect of your own focus. Clear the static, conquer the path."
                )
                val idx = (0..3).random()
                activeInspirationChallenge.value = "🎯 **${titles[idx]}**\n\n${challenges[idx]}\n\n*“${quotes[idx]}”*"
            }
            isGeneratingInspiration.value = false
        }
    }

    fun postReel(description: String, songName: String, actionType: String, filterName: String?, filterDesc: String?, colorMatrix: FloatArray?) {
        viewModelScope.launch {
            val signedName = contractSignature.value?.athleteName ?: "Guest Athlete"
            val newId = "reel_user_${System.currentTimeMillis()}"
            val colorMatrixStr = colorMatrix?.joinToString(",")
            
            val newReel = Reel(
                id = newId,
                athleteId = "user_athlete",
                athleteName = signedName,
                athleteSport = _userSpecialization.value,
                athleteAvatar = "user_avatar",
                description = description,
                songName = if (songName.isBlank()) "Unstoppable Rhythm" else songName,
                songArtist = signedName,
                likesCount = 0,
                commentsCount = 0,
                isLiked = false,
                thumbnailGradientStart = "#F59E0B",
                thumbnailGradientEnd = "#EF4444",
                videoActionType = actionType,
                filterName = filterName,
                filterDescription = filterDesc,
                colorMatrixVals = colorMatrixStr
            )
            repository.insertReel(newReel)
        }
    }

    // Note Management Methods (Useful to all people for journals, lists, dairy, reminders, etc.)
    fun addNote(title: String, content: String, category: String) {
        viewModelScope.launch {
            val note = AppNote(
                title = title.ifBlank { "Untitled Note" },
                content = content,
                category = category.ifBlank { "General" }
            )
            repository.insertNote(note)
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            repository.deleteNoteById(id)
        }
    }

    // Call System Management Methods
    fun initiateCall(athlete: Athlete, type: String) {
        // Starts a call to an Athlete/Creator
        activeCallContact.value = athlete
        activeCallType.value = type
        isCallRinging.value = true
        isCallActive.value = false
        callDurationSeconds.value = 0
        isMuted.value = false
        isCameraOff.value = false

        // Insert call log immediately as OUTGOING
        viewModelScope.launch {
            val log = CallLogEntry(
                contactName = athlete.name,
                contactAvatar = athlete.avatarUrl,
                callType = type,
                direction = "OUTGOING",
                durationSeconds = 0
            )
            repository.insertCallLog(log)
        }
    }

    fun acceptCall() {
        if (isCallRinging.value) {
            isCallRinging.value = false
            isCallActive.value = true
            startCallTimer()
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (isCallActive.value) {
                kotlinx.coroutines.delay(1000)
                callDurationSeconds.value += 1
            }
        }
    }

    fun endCall() {
        val contact = activeCallContact.value
        val type = activeCallType.value
        val duration = callDurationSeconds.value
        val wasRinging = isCallRinging.value

        callTimerJob?.cancel()
        isCallActive.value = false
        isCallRinging.value = false
        activeCallContact.value = null
        activeCallType.value = null

        // If contact was present, log final call history entry
        if (contact != null && type != null) {
            viewModelScope.launch {
                val log = CallLogEntry(
                    contactName = contact.name,
                    contactAvatar = contact.avatarUrl,
                    callType = type,
                    direction = if (wasRinging) "MISSED" else "OUTGOING",
                    durationSeconds = duration
                )
                repository.insertCallLog(log)
            }
        }
    }

    fun toggleMute() {
        isMuted.value = !isMuted.value
    }

    fun toggleCamera() {
        isCameraOff.value = !isCameraOff.value
    }

    fun addCallLog(log: CallLogEntry) {
        viewModelScope.launch {
            repository.insertCallLog(log)
        }
    }

    fun clearAllCallHistory() {
        viewModelScope.launch {
            repository.clearCallLogs()
        }
    }

    // Authentication Functions
    fun loginUser(email: String, name: String = "", role: String = "Cinema & Music Enthusiast") {
        val userName = if (name.isNotBlank()) name else email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        val handle = "@" + userName.lowercase().replace(" ", "_")
        _currentUserName.value = userName
        _currentUserEmail.value = email
        _currentUserHandle.value = handle
        _currentUserRole.value = role
        _isLoggedIn.value = true

        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("current_user_name", userName)
            .putString("current_user_email", email)
            .putString("current_user_handle", handle)
            .putString("current_user_role", role)
            .apply()
    }

    fun signupUser(email: String, name: String, handle: String, role: String) {
        val finalHandle = if (handle.startsWith("@")) handle else "@$handle"
        _currentUserName.value = name
        _currentUserEmail.value = email
        _currentUserHandle.value = finalHandle
        _currentUserRole.value = role
        _isLoggedIn.value = true

        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("current_user_name", name)
            .putString("current_user_email", email)
            .putString("current_user_handle", finalHandle)
            .putString("current_user_role", role)
            .apply()
    }

    fun loginAsGuest() {
        val guestName = "Guest Fan"
        val guestEmail = "guest@morninggram.in"
        val guestHandle = "@guest_fan"
        _currentUserName.value = guestName
        _currentUserEmail.value = guestEmail
        _currentUserHandle.value = guestHandle
        _currentUserRole.value = "Guest Visitor"
        _isLoggedIn.value = true

        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("current_user_name", guestName)
            .putString("current_user_email", guestEmail)
            .putString("current_user_handle", guestHandle)
            .putString("current_user_role", "Guest Visitor")
            .apply()
    }

    fun logoutUser() {
        _isLoggedIn.value = false
        prefs.edit().putBoolean("is_logged_in", false).apply()
    }

    // Imaginative Feature Functions
    fun updateVibe(newVibe: String) {
        _activeVibe.value = newVibe
        prefs.edit().putString("active_vibe", newVibe).apply()

        // Calculate imaginative Star Vibe match
        when (newVibe) {
            "Cinematic Drama" -> {
                _vibeMatchedStar.value = "Shah Rukh Khan"
                _vibeMatchPercentage.value = (85..99).random()
            }
            "Acoustic Melody" -> {
                _vibeMatchedStar.value = "Arijit Singh"
                _vibeMatchPercentage.value = (88..98).random()
            }
            "High Intensity Energy" -> {
                _vibeMatchedStar.value = "Virat Kohli"
                _vibeMatchPercentage.value = (90..100).random()
            }
            "Global Beats" -> {
                _vibeMatchedStar.value = "Diljit Dosanjh"
                _vibeMatchPercentage.value = (87..99).random()
            }
            else -> {
                _vibeMatchedStar.value = "Deepika Padukone"
                _vibeMatchPercentage.value = (82..95).random()
            }
        }
    }

    fun toggleVoiceCapsule() {
        _isPlayingStarVoiceNote.value = !_isPlayingStarVoiceNote.value
    }

    fun sendVirtualGift(giftName: String, points: Int) {
        val newTotal = _totalGiftsSent.value + 1
        _totalGiftsSent.value = newTotal
        prefs.edit().putInt("total_gifts_sent", newTotal).apply()
    }

    fun createFanDuetSpark(starName: String, dialogueTitle: String) {
        val newTotal = _totalDuetsCreated.value + 1
        _totalDuetsCreated.value = newTotal
        prefs.edit().putInt("total_duets_created", newTotal).apply()

        // Insert custom generated Duet Reel into Room database
        viewModelScope.launch {
            val user = currentUserName.value
            val newReel = Reel(
                id = "duet_${System.currentTimeMillis()}",
                athleteId = "user_me",
                athleteName = user,
                athleteSport = "Co-Creator Duet with $starName",
                athleteAvatar = "user_avatar",
                description = "🎙️ Co-Creator Fan Duet: '$dialogueTitle' performed with $starName! Recorded live on Morning Gram Aura Studio.",
                songName = "$dialogueTitle (Duet Mix)",
                songArtist = "$starName x $user",
                likesCount = 1,
                commentsCount = 0,
                isLiked = true,
                thumbnailGradientStart = "#8A2387",
                thumbnailGradientEnd = "#E94057",
                videoActionType = "SPRINT"
            )
            repository.insertReel(newReel)
        }
    }

    fun claimTrophy(trophyId: String) {
        val updatedSet = _claimedTrophies.value + trophyId
        _claimedTrophies.value = updatedSet
        prefs.edit().putStringSet("claimed_trophies", updatedSet).apply()
    }

    fun incrementTrainingStreak() {
        val newStreak = _trainingStreakDays.value + 1
        _trainingStreakDays.value = newStreak
        prefs.edit().putInt("training_streak_days", newStreak).apply()
    }
}


class AthleteViewModelFactory(
    private val repository: AthleteRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AthleteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AthleteViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
