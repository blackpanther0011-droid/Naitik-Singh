package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "athletes")
data class Athlete(
    @PrimaryKey val id: String,
    val name: String,
    val sport: String,
    val avatarUrl: String,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false,
    val followerCount: Int = 1200,
    val bio: String = "National-class creator training for excellence in digital spaces on Morning Gram.",
    val isPrivate: Boolean = false,
    val isPendingRequest: Boolean = false
)

@Entity(tableName = "reels")
data class Reel(
    @PrimaryKey val id: String,
    val athleteId: String,
    val athleteName: String,
    val athleteSport: String,
    val athleteAvatar: String,
    val description: String,
    val songName: String,
    val songArtist: String,
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val thumbnailGradientStart: String,
    val thumbnailGradientEnd: String,
    val videoActionType: String, // e.g. "SPRINT", "WRESTLE", "CRICKET", "BADMINTON", "WEIGHTS"
    val filterName: String? = null,
    val filterDescription: String? = null,
    val colorMatrixVals: String? = null
)

@Entity(tableName = "contracts")
data class ContractSignature(
    @PrimaryKey val id: String = "athlete_regulatory_contract",
    val athleteName: String,
    val signatureText: String,
    val signedAt: Long,
    val isGovernmentRegulationCompliant: Boolean = true,
    val isCompanyDisclaimersAccepted: Boolean = true
)

@Entity(tableName = "user_comments")
data class UserComment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reelId: String,
    val userName: String = "You (Athlete)",
    val commentText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_notes")
data class AppNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "General", // e.g., "Personal", "Work", "Idea", "Training", "Diary"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "call_logs")
data class CallLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contactName: String,
    val contactAvatar: String,
    val callType: String, // "VOICE" or "VIDEO"
    val direction: String, // "INCOMING", "OUTGOING", "MISSED"
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis()
)
