package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppNote
import com.example.ui.viewmodel.AthleteViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(
    viewModel: AthleteViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.allNotes.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    
    // States for Adding/Editing Notes
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var noteCategory by remember { mutableStateOf("Personal") }

    val categories = listOf("Personal", "Work", "Idea", "Diary", "Training", "Reminder")

    val filteredNotes = remember(notes, searchQuery, selectedCategoryFilter) {
        notes.filter { note ->
            val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
                    note.content.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategoryFilter == "All" || note.category.equals(selectedCategoryFilter, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1113))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // App header (cursive cursive style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
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
                    text = "Personal Notes & Journals",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(
                onClick = { showAddNoteDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444))))
                    .size(40.dp)
                    .testTag("add_note_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note",
                    tint = Color.White
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search notes...", color = Color(0xFF64748B), fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF16191C),
                unfocusedContainerColor = Color(0xFF16191C),
                focusedBorderColor = Color(0xFFF59E0B),
                unfocusedBorderColor = Color(0x26FFFFFF)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("notes_search_input")
        )

        // Category Horizontal Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("All") + categories
            filters.forEach { filter ->
                val isSelected = selectedCategoryFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFFF59E0B) else Color(0xFF16191C))
                        .clickable { selectedCategoryFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.Black else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Notes Grid
        if (filteredNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedCategoryFilter != "All") "No notes match your criteria" else "Your notepad is empty",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the '+' button above to jot down thoughts, diaries, or training logs!",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("notes_grid"),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredNotes) { note ->
                    NoteCard(
                        note = note,
                        onDeleteClick = { viewModel.deleteNote(note.id) }
                    )
                }
            }
        }
    }

    // Add Note Dialog
    if (showAddNoteDialog) {
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Write a New Note",
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
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("Title", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0x26FFFFFF)
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_note_title")
                    )

                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("Note content...", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFF59E0B),
                            unfocusedBorderColor = Color(0x26FFFFFF)
                        ),
                        singleLine = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("add_note_content")
                    )

                    Text(
                        text = "Choose Category:",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = noteCategory == cat,
                                onClick = { noteCategory = cat },
                                label = { Text(cat, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    labelColor = Color(0xFF94A3B8),
                                    selectedLabelColor = Color.White,
                                    selectedContainerColor = Color(0x40F59E0B)
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addNote(
                            title = noteTitle,
                            content = noteContent,
                            category = noteCategory
                        )
                        // Reset states
                        noteTitle = ""
                        noteContent = ""
                        noteCategory = "Personal"
                        showAddNoteDialog = false
                    },
                    enabled = noteContent.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    modifier = Modifier.testTag("submit_note_button")
                ) {
                    Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddNoteDialog = false }
                ) {
                    Text("Discard", color = Color(0xFF64748B))
                }
            },
            containerColor = Color(0xFF15191C)
        )
    }
}

@Composable
fun NoteCard(
    note: AppNote,
    onDeleteClick: () -> Unit
) {
    val formatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val dateString = formatter.format(Date(note.timestamp))

    // Accent color based on category
    val accentColor = when (note.category.lowercase()) {
        "personal" -> Color(0xFFA5B4FC) // Lavender
        "work" -> Color(0xFF93C5FD) // Sky Blue
        "idea" -> Color(0xFFFDE047) // Soft Yellow
        "diary" -> Color(0xFFF472B6) // Soft Pink
        "training" -> Color(0xFF34D399) // Mint Green
        "reminder" -> Color(0xFFFCA5A5) // Soft Coral
        else -> Color(0xFFCBD5E1)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF16191C))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = note.category.uppercase(),
                        color = accentColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(18.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Note",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Text(
                text = note.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = note.content,
                color = Color(0xFFCBD5E1),
                fontSize = 11.sp,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
                modifier = Modifier.weight(1f, fill = false)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dateString,
                color = Color(0xFF64748B),
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
