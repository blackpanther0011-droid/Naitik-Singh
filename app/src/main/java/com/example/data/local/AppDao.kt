package com.example.data.local

import androidx.room.*
import com.example.data.model.Athlete
import com.example.data.model.ContractSignature
import com.example.data.model.Reel
import com.example.data.model.UserComment
import com.example.data.model.AppNote
import com.example.data.model.CallLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Athletes
    @Query("SELECT * FROM athletes")
    fun getAllAthletes(): Flow<List<Athlete>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthletes(athletes: List<Athlete>)

    @Query("UPDATE athletes SET isFollowing = :isFollowing, followerCount = followerCount + :countDelta WHERE id = :athleteId")
    suspend fun updateAthleteFollowing(athleteId: String, isFollowing: Boolean, countDelta: Int)

    @Query("UPDATE athletes SET isPendingRequest = :isPending WHERE id = :athleteId")
    suspend fun updateAthletePendingRequest(athleteId: String, isPending: Boolean)

    @Query("UPDATE athletes SET isPrivate = :isPrivate WHERE id = :athleteId")
    suspend fun updateAthletePrivate(athleteId: String, isPrivate: Boolean)

    // Reels
    @Query("SELECT * FROM reels")
    fun getAllReels(): Flow<List<Reel>>

    @Query("SELECT reels.* FROM reels INNER JOIN athletes ON reels.athleteId = athletes.id WHERE athletes.isFollowing = 1")
    fun getFollowingReels(): Flow<List<Reel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<Reel>)

    @Query("UPDATE reels SET isLiked = :isLiked, likesCount = likesCount + :countDelta WHERE id = :reelId")
    suspend fun updateReelLiked(reelId: String, isLiked: Boolean, countDelta: Int)

    // Contract Signatures
    @Query("SELECT * FROM contracts LIMIT 1")
    fun getContractSignature(): Flow<ContractSignature?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContractSignature(contract: ContractSignature)

    @Query("DELETE FROM contracts")
    suspend fun deleteContractSignature()

    // Comments
    @Query("SELECT * FROM user_comments WHERE reelId = :reelId ORDER BY timestamp DESC")
    fun getCommentsForReel(reelId: String): Flow<List<UserComment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: UserComment)

    // Notes features (Useful to all people)
    @Query("SELECT * FROM app_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<AppNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: AppNote)

    @Query("DELETE FROM app_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    // Calling System Log Features
    @Query("SELECT * FROM call_logs ORDER BY timestamp DESC")
    fun getAllCallLogs(): Flow<List<CallLogEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallLog(callLog: CallLogEntry)

    @Query("DELETE FROM call_logs")
    suspend fun clearCallLogs()
}

