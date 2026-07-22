package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.Athlete
import com.example.data.model.ContractSignature
import com.example.data.model.Reel
import com.example.data.model.UserComment
import com.example.data.model.AppNote
import com.example.data.model.CallLogEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AthleteRepository(private val appDao: AppDao) {

    val allAthletes: Flow<List<Athlete>> = appDao.getAllAthletes()
    val allReels: Flow<List<Reel>> = appDao.getAllReels()
    val followingReels: Flow<List<Reel>> = appDao.getFollowingReels()
    val contractSignature: Flow<ContractSignature?> = appDao.getContractSignature()
    val allNotes: Flow<List<AppNote>> = appDao.getAllNotes()
    val allCallLogs: Flow<List<CallLogEntry>> = appDao.getAllCallLogs()


    suspend fun toggleFollow(athleteId: String, currentFollowing: Boolean) {
        val nextFollowing = !currentFollowing
        val delta = if (nextFollowing) 1 else -1
        appDao.updateAthleteFollowing(athleteId, nextFollowing, delta)
    }

    suspend fun updateAthletePendingRequest(athleteId: String, isPending: Boolean) {
        appDao.updateAthletePendingRequest(athleteId, isPending)
    }

    suspend fun updateAthletePrivate(athleteId: String, isPrivate: Boolean) {
        appDao.updateAthletePrivate(athleteId, isPrivate)
    }

    suspend fun toggleLike(reelId: String, currentLiked: Boolean) {
        val nextLiked = !currentLiked
        val delta = if (nextLiked) 1 else -1
        appDao.updateReelLiked(reelId, nextLiked, delta)
    }

    suspend fun signContract(name: String, signature: String) {
        val contract = ContractSignature(
            athleteName = name,
            signatureText = signature,
            signedAt = System.currentTimeMillis()
        )
        appDao.insertContractSignature(contract)
    }

    suspend fun removeContract() {
        appDao.deleteContractSignature()
    }

    fun getComments(reelId: String): Flow<List<UserComment>> = appDao.getCommentsForReel(reelId)

    suspend fun addComment(reelId: String, commentText: String) {
        if (commentText.isBlank()) return
        val comment = UserComment(
            reelId = reelId,
            commentText = commentText
        )
        appDao.insertComment(comment)
    }

    suspend fun checkAndPopulateMockData() {
        val existingAthletes = allAthletes.first()
        val existingReels = allReels.first()
        if (existingAthletes.size >= 20 && existingReels.size >= 10) return

        val mockAthletes = listOf(
            // Bollywood Stars
            Athlete("srk", "Shah Rukh Khan", "Bollywood Actor & Icon", "srk_avatar", isVerified = true, isFollowing = true, followerCount = 45000000, bio = "The King of Bollywood. Passion, cinema, and connecting directly with millions of fans nationwide. 👑🎬", isPrivate = false),
            Athlete("deepika", "Deepika Padukone", "Bollywood Actress & Icon", "deepika_avatar", isVerified = true, isFollowing = true, followerCount = 38000000, bio = "Actor, producer & mental wellness founder. Spreading positivity, elegance, and focus. 🌸✨", isPrivate = false),
            Athlete("alia", "Alia Bhatt", "Bollywood Actress", "alia_avatar", isVerified = true, isFollowing = false, followerCount = 35000000, bio = "Actor, entrepreneur & storyteller. Living life one scene at a time. 🎬💛", isPrivate = false),
            Athlete("ranveer_s", "Ranveer Singh", "Bollywood Actor", "ranveer_s_avatar", isVerified = true, isFollowing = false, followerCount = 31000000, bio = "Pure energy, high fashion & cinematic passion! ⚡🔥 Live unapologetically.", isPrivate = false),
            Athlete("priyanka", "Priyanka Chopra", "Global Actress & Producer", "priyanka_avatar", isVerified = true, isFollowing = false, followerCount = 42000000, bio = "Bridging East and West. Global actor, author, and producer. 🌍✨", isPrivate = false),
            Athlete("ranbir", "Ranbir Kapoor", "Bollywood Actor", "ranbir_avatar", isVerified = true, isFollowing = false, followerCount = 28000000, bio = "Focused on pure craft & cinema. Direct connection with true cinema lovers. 🎥", isPrivate = false),
            Athlete("katrina", "Katrina Kaif", "Bollywood Actress", "katrina_avatar", isVerified = true, isFollowing = false, followerCount = 29000000, bio = "Actor, Kay Beauty founder & fitness enthusiast. 💄✨", isPrivate = false),

            // Top Singers & Musicians
            Athlete("arijit", "Arijit Singh", "Playback Singer & Composer", "arijit_avatar", isVerified = true, isFollowing = true, followerCount = 39000000, bio = "Music is emotion in sound. Expressing soul through melody. 🎤🎶", isPrivate = false),
            Athlete("shreya", "Shreya Ghoshal", "Playback Singer", "shreya_avatar", isVerified = true, isFollowing = true, followerCount = 27000000, bio = "Nightingale of melody. Connecting hearts across generations through vocal mastery. 🎵❤️", isPrivate = false),
            Athlete("diljit", "Diljit Dosanjh", "Global Singer & Actor", "diljit_avatar", isVerified = true, isFollowing = true, followerCount = 24000000, bio = "Bhangra, urban beats & global stadium tours! Born to shine. 🌾🎤⚡", isPrivate = false),
            Athlete("badshah", "Badshah", "Hip-Hop Artist & Rapper", "badshah_avatar", isVerified = true, isFollowing = false, followerCount = 19000000, bio = "Rhythm, beatmaking & hip-hop pioneer. Elevating Indian street music. 🎧🔥", isPrivate = false),
            Athlete("neha", "Neha Kakkar", "Pop Singer & Creator", "neha_avatar", isVerified = true, isFollowing = false, followerCount = 33000000, bio = "Spreading smiles, energetic pop hits, and musical joy! 💖🎵", isPrivate = false),
            Athlete("ar_rahman", "A.R. Rahman", "Composer & Music Director", "ar_rahman_avatar", isVerified = true, isFollowing = false, followerCount = 18000000, bio = "Music without boundaries. Exploring sonic landscapes and classical fusion. 🎹🎼", isPrivate = false),

            // Top Content Creators & Influencers
            Athlete("carry", "CarryMinati / Ajey Nagar", "Content Creator & Gamer", "carry_avatar", isVerified = true, isFollowing = true, followerCount = 32000000, bio = "India's premier video creator & livestream gamer. Keep grinding! 🎮🔥", isPrivate = false),
            Athlete("beerbiceps", "Ranveer Allahbadia", "Podcaster & Fitness Influencer", "beerbiceps_avatar", isVerified = true, isFollowing = true, followerCount = 12000000, bio = "Host of The Ranveer Show. Mindset, fitness, spirituality, and relentless growth. 🎙️💪", isPrivate = false),
            Athlete("mostly_sane", "Prajakta Koli", "Creator & Actress", "mostly_sane_avatar", isVerified = true, isFollowing = false, followerCount = 9500000, bio = "MostlySane. Storytelling, relatable comedy, and digital positivity. 🌟😄", isPrivate = false),
            Athlete("flying_beast", "Gaurav Taneja", "Fitness Influencer & Pilot", "flying_beast_avatar", isVerified = true, isFollowing = false, followerCount = 11000000, bio = "Flying Beast. Fitness, aviation, family vlogs & core discipline. ✈️🏋️‍♂️", isPrivate = false),
            Athlete("tech_burner", "Shlok Srivastava", "Tech Creator & Influencer", "tech_burner_avatar", isVerified = true, isFollowing = false, followerCount = 10500000, bio = "Tech Burner. Gadget reviews, tech innovations & creative energy! 📱🚀", isPrivate = false),

            // Sports Champions
            Athlete("v_kohli", "Virat Kohli", "Cricket Champion", "v_kohli_avatar", isVerified = true, isFollowing = true, followerCount = 50000000, bio = "Cricket icon. Fitness, intensity, and relentless pursuit of excellence. 🏏🔥", isPrivate = false),
            Athlete("neeraj", "Neeraj Chopra", "Javelin Throw", "neeraj_avatar", isVerified = true, isFollowing = true, followerCount = 1450000, bio = "Olympic Gold Medalist. Driven by precision, speed, and dedication.", isPrivate = false),
            Athlete("sindhu", "PV Sindhu", "Badminton", "sindhu_avatar", isVerified = true, isFollowing = false, followerCount = 890000, bio = "Two-time Olympic Medalist. Agility, passion, and relentless hard work.", isPrivate = true),
            Athlete("manu", "Manu Bhaker", "Shooting", "manu_avatar", isVerified = true, isFollowing = true, followerCount = 320000, bio = "Olympic shooting medalist. Mental concentration and absolute grit. 🎯", isPrivate = false),
            Athlete("sunil", "Sunil Chhetri", "Football", "sunil_avatar", isVerified = true, isFollowing = false, followerCount = 2100000, bio = "Captain. Leader. Legend. Dedicated to lifting Indian football.", isPrivate = false),
            Athlete("smriti", "Smriti Mandhana", "Cricket Champion", "smriti_avatar", isVerified = true, isFollowing = false, followerCount = 7800000, bio = "Indian Cricket Team opener. Timing, power, and elegant strokes. 🏏✨", isPrivate = false),
            Athlete("mirabai", "Mirabai Chanu", "Weightlifting", "mirabai_avatar", isVerified = true, isFollowing = false, followerCount = 450000, bio = "Olympic Weightlifting Medalist. Power, technique, and core resilience.", isPrivate = true),

            // Indie Creators
            Athlete("aarav", "Aarav Sharma", "Indie Game Dev", "aarav_avatar", isVerified = true, isFollowing = true, followerCount = 8200, bio = "Indie game developer & tech designer. Building decentralized & privacy-first gaming modules. 💻🎮", isPrivate = false),
            Athlete("riya", "Riya Sen", "Digital Artist", "riya_avatar", isVerified = true, isFollowing = false, followerCount = 14300, bio = "Digital artist, concept illustrator. Cyberpunk futurism & traditional Indian aesthetics. 🎨🖌️", isPrivate = false),
            Athlete("ananya", "Ananya Rao", "Fusion Musician", "ananya_avatar", isVerified = true, isFollowing = true, followerCount = 22100, bio = "Fusion sitar player & sonic synth explorer. Bridging ancient ragas with ambient electronic beats. 🎵", isPrivate = false)
        )
        appDao.insertAthletes(mockAthletes)

        val mockReels = listOf(
            Reel(
                id = "reel_srk_1",
                athleteId = "srk",
                athleteName = "Shah Rukh Khan",
                athleteSport = "Bollywood Actor & Icon",
                athleteAvatar = "srk_avatar",
                description = "Over 30 years of cinema and love from all over India and the world! Always keep dreaming big and working with absolute passion. 👑🎬✨",
                songName = "Jawan Title Track",
                songArtist = "Anirudh Ravichander",
                likesCount = 125000,
                commentsCount = 3400,
                isLiked = true,
                thumbnailGradientStart = "#3A1C71",
                thumbnailGradientEnd = "#D76D77",
                videoActionType = "SPRINT"
            ),
            Reel(
                id = "reel_arijit_1",
                athleteId = "arijit",
                athleteName = "Arijit Singh",
                athleteSport = "Playback Singer & Composer",
                athleteAvatar = "arijit_avatar",
                description = "Early morning acoustic melody session live from the studio. Raw vocals without auto-tune. Let the music heal your spirit. 🎤🎶",
                songName = "Kesariya (Unplugged)",
                songArtist = "Arijit Singh",
                likesCount = 98000,
                commentsCount = 2100,
                isLiked = true,
                thumbnailGradientStart = "#F5AF19",
                thumbnailGradientEnd = "#F12711",
                videoActionType = "CRICKET"
            ),
            Reel(
                id = "reel_deepika_1",
                athleteId = "deepika",
                athleteName = "Deepika Padukone",
                athleteSport = "Bollywood Actress & Icon",
                athleteAvatar = "deepika_avatar",
                description = "Mindful morning stretch and meditation routine. Wellness begins from within! Take time for your mental peace today. 🌸✨",
                songName = "Deewani Mastani instrumental",
                songArtist = "Sanjay Leela Bhansali",
                likesCount = 82000,
                commentsCount = 1500,
                isLiked = false,
                thumbnailGradientStart = "#FF512F",
                thumbnailGradientEnd = "#DD2476",
                videoActionType = "BADMINTON"
            ),
            Reel(
                id = "reel_v_kohli_1",
                athleteId = "v_kohli",
                athleteName = "Virat Kohli",
                athleteSport = "Cricket Champion",
                athleteAvatar = "v_kohli_avatar",
                description = "High intensity agility drills & cover drive practice in the nets! Excellence is a daily habit. Push your boundaries every single day. 🏏🔥",
                songName = "Chak De India (Title Anthem)",
                songArtist = "Sukhwinder Singh",
                likesCount = 145000,
                commentsCount = 4200,
                isLiked = true,
                thumbnailGradientStart = "#1f4037",
                thumbnailGradientEnd = "#99f2c8",
                videoActionType = "CRICKET"
            ),
            Reel(
                id = "reel_carry_1",
                athleteId = "carry",
                athleteName = "CarryMinati / Ajey Nagar",
                athleteSport = "Content Creator & Gamer",
                athleteAvatar = "carry_avatar",
                description = "Late night gaming live stream highlights! Connecting directly with fans without middleman algorithms. Thank you all for the love! 🎮🔥",
                songName = "Yalgaar Anthem",
                songArtist = "CarryMinati & Wily Frenzy",
                likesCount = 67000,
                commentsCount = 1900,
                isLiked = false,
                thumbnailGradientStart = "#00F2FE",
                thumbnailGradientEnd = "#4FACFE",
                videoActionType = "SPRINT"
            ),
            Reel(
                id = "reel_diljit_1",
                athleteId = "diljit",
                athleteName = "Diljit Dosanjh",
                athleteSport = "Global Singer & Actor",
                athleteAvatar = "diljit_avatar",
                description = "Sold out global stadium concert tour! Bringing Punjabi beat culture to millions worldwide. Born to shine! 🌾🎤⚡",
                songName = "Lover (Live Stadium Tour)",
                songArtist = "Diljit Dosanjh",
                likesCount = 112000,
                commentsCount = 2800,
                isLiked = true,
                thumbnailGradientStart = "#F2994A",
                thumbnailGradientEnd = "#F2C94C",
                videoActionType = "CRICKET"
            ),
            Reel(
                id = "reel_neeraj_1",
                athleteId = "neeraj",
                athleteName = "Neeraj Chopra",
                athleteSport = "Javelin Throw",
                athleteAvatar = "neeraj_avatar",
                description = "Chasing 90 meters with pure rotation & launch speed! 🇮🇳🔥 Practice makes perfect. Dedicated to all Indian youth seeking focus.",
                songName = "Kar Har Maidan Fateh",
                songArtist = "Sukhwinder Singh",
                likesCount = 45200,
                commentsCount = 892,
                isLiked = true,
                thumbnailGradientStart = "#FF9933",
                thumbnailGradientEnd = "#128807",
                videoActionType = "SPRINT"
            ),
            Reel(
                id = "reel_alia_1",
                athleteId = "alia",
                athleteName = "Alia Bhatt",
                athleteSport = "Bollywood Actress & Icon",
                athleteAvatar = "alia_avatar",
                description = "Behind the scenes look at the latest festive couture shoot! ✨ Bright pastel hues and summer glow vibes. Love to everyone! 🌸💄",
                songName = "Kesariya (Acoustic Remix)",
                songArtist = "Arijit Singh & Pritam",
                likesCount = 189000,
                commentsCount = 4120,
                isLiked = true,
                thumbnailGradientStart = "#FF758C",
                thumbnailGradientEnd = "#FF7EB3",
                videoActionType = "BADMINTON"
            ),
            Reel(
                id = "reel_katrina_1",
                athleteId = "katrina",
                athleteName = "Katrina Kaif",
                athleteSport = "Bollywood Actress",
                athleteAvatar = "katrina_avatar",
                description = "Post-workout glow & Kay Beauty summer touchup routine! 💪✨ Consistency is key, whether in fitness or self-care. Stay glowing!",
                songName = "Kamli (Dance Remix)",
                songArtist = "Sunidhi Chauhan",
                likesCount = 154000,
                commentsCount = 3890,
                isLiked = false,
                thumbnailGradientStart = "#F857A6",
                thumbnailGradientEnd = "#FF5858",
                videoActionType = "SPRINT"
            ),
            Reel(
                id = "reel_neha_1",
                athleteId = "neha",
                athleteName = "Neha Kakkar",
                athleteSport = "Pop Singer & Creator",
                athleteAvatar = "neha_avatar",
                description = "Trending dance routine with my team backstage before taking the main stage! 💖🎶 Keep dancing and singing your heart out!",
                songName = "O Saki Saki (Live Beat)",
                songArtist = "Neha Kakkar & B Praak",
                likesCount = 210000,
                commentsCount = 5400,
                isLiked = true,
                thumbnailGradientStart = "#EC4899",
                thumbnailGradientEnd = "#8B5CF6",
                videoActionType = "CRICKET"
            ),
            Reel(
                id = "reel_smriti_1",
                athleteId = "smriti",
                athleteName = "Smriti Mandhana",
                athleteSport = "Cricket Champion",
                athleteAvatar = "smriti_avatar",
                description = "Early morning net session! Master the cover drive through repetition and timing. Dedicated to all aspiring women cricketers 🏏🔥",
                songName = "Chak De India (Remix)",
                songArtist = "Salim-Sulaiman",
                likesCount = 132000,
                commentsCount = 2900,
                isLiked = true,
                thumbnailGradientStart = "#2563EB",
                thumbnailGradientEnd = "#3B82F6",
                videoActionType = "CRICKET"
            ),
            Reel(
                id = "reel_mostly_1",
                athleteId = "mostly_sane",
                athleteName = "Prajakta Koli",
                athleteSport = "Creator & Actress",
                athleteAvatar = "mostly_sane_avatar",
                description = "When you try to take a aesthetic morning aesthetic reel but your cat steals the spotlight! 🐱😂 Relatable morning struggles!",
                songName = "Trending Lofi Beats",
                songArtist = "Chillhop Music",
                likesCount = 94000,
                commentsCount = 1870,
                isLiked = false,
                thumbnailGradientStart = "#F59E0B",
                thumbnailGradientEnd = "#10B981",
                videoActionType = "BADMINTON"
            )
        )
        appDao.insertReels(mockReels)
    }

    suspend fun insertReel(reel: Reel) {
        appDao.insertReels(listOf(reel))
    }

    suspend fun insertNote(note: AppNote) {
        appDao.insertNote(note)
    }

    suspend fun deleteNoteById(id: Int) {
        appDao.deleteNoteById(id)
    }

    suspend fun insertCallLog(callLog: CallLogEntry) {
        appDao.insertCallLog(callLog)
    }

    suspend fun clearCallLogs() {
        appDao.clearCallLogs()
    }
}

