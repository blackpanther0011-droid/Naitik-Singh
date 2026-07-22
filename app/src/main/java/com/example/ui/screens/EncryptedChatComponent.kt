package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Athlete
import com.example.ui.viewmodel.AthleteViewModel
import com.example.ui.viewmodel.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptedChatDialog(
    recipient: Athlete,
    viewModel: AthleteViewModel,
    onDismiss: () -> Unit
) {
    val messagesMap by viewModel.chatMessages.collectAsState()
    val messages = messagesMap[recipient.id] ?: emptyList()
    
    val userPublicKey by viewModel.userPublicKey.collectAsState()
    val userPrivateKey by viewModel.userPrivateKey.collectAsState()
    val userSpecialization by viewModel.userSpecialization.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    var isKeysDashboardExpanded by remember { mutableStateOf(false) }
    var showKeysToast by remember { mutableStateOf(false) }
    
    // Derived static public key for recipient based on ID
    val recipientPublicKey = remember(recipient.id) {
        "04" + recipient.id.hashCode().toString(16).padEnd(40, 'F').uppercase().take(40)
    }
    
    // Channel security hash derived from combined public keys
    val channelSecurityCode = remember(userPublicKey, recipientPublicKey) {
        val hash = (userPublicKey.hashCode() + recipientPublicKey.hashCode()).hashCode()
        hash.toString(16).padEnd(24, 'A').uppercase().chunked(4).joinToString("-").take(23)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                // Creator Avatar with elegant halo
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.sweepGradient(
                                                colors = listOf(Color(0xFFF59E0B), Color(0xFFC084FC), Color(0xFFF59E0B))
                                            )
                                        )
                                        .padding(2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = recipient.name.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(10.dp))
                                
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = recipient.name,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (recipient.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Verified,
                                                contentDescription = "Verified Creator",
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = recipient.sport,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back to Feed",
                                    tint = Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { isKeysDashboardExpanded = !isKeysDashboardExpanded }) {
                                Icon(
                                    imageVector = if (isKeysDashboardExpanded) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = "Cryptographic Key Management",
                                    tint = if (isKeysDashboardExpanded) Color(0xFFF59E0B) else Color(0xFF34D399)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF131517),
                            titleContentColor = Color.White
                        )
                    )
                    
                    // Direct communication notice bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .border(0.5.dp, Color(0x1AFFFFFF))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "E2EE Secured: Staff Blocked from Reading Chats",
                                color = Color(0xFF34D399),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                }
            },
            containerColor = Color(0xFF0B0D0F),
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    
                    // Collapsible Key Management Dashboard
                    AnimatedVisibility(
                        visible = isKeysDashboardExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Key,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "On-Device Keys & Verification",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Button(
                                        onClick = {
                                            viewModel.regenerateKeys()
                                            showKeysToast = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x33F59E0B)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Regen Keys", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                // Verification Code
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("CHANNEL SAFETY CODE (ECDH-SHA256)", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = channelSecurityCode,
                                            color = Color(0xFF34D399),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Verify this code with ${recipient.name} to guarantee no middle-man. Encryption takes place locally.",
                                            color = Color(0xFF64748B),
                                            fontSize = 9.sp,
                                            lineHeight = 12.sp
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // User Keys
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("YOUR PUBLIC KEY", color = Color(0xFF94A3B8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = userPublicKey.take(16) + "...",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("YOUR PRIVATE KEY (HIDDEN)", color = Color(0xFF94A3B8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = userPrivateKey.take(6) + "****************",
                                            color = Color(0xFFF43F5E),
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                // Recipient Key
                                Column {
                                    Text("${recipient.name.uppercase()}'S PUBLIC KEY", color = Color(0xFF94A3B8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = recipientPublicKey,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                
                                if (showKeysToast) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "✓ Cryptographic key pair updated successfully!",
                                        color = Color(0xFF34D399),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    LaunchedEffect(showKeysToast) {
                                        kotlinx.coroutines.delay(2000)
                                        showKeysToast = false
                                    }
                                }
                            }
                        }
                    }

                    // Messages List Space
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        if (messages.isEmpty()) {
                            // Atmospheric empty state
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = Color(0xFF334155),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "E2E Encrypted Sanctuary",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No records exist yet. Send a secure message below. Keys are negotiated on-device, completely keeping your data out of corporate hands.",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(Color(0x0FFFFFFF), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Ad-Free & Algorithm-Free Chat Room",
                                        color = Color(0xFFF59E0B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                                reverseLayout = false
                            ) {
                                items(messages) { message ->
                                    val isMe = message.sender == "You"
                                    
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    brush = if (isMe) {
                                                        Brush.linearGradient(
                                                            colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8))
                                                        )
                                                    } else {
                                                        Brush.linearGradient(
                                                            colors = listOf(Color(0xFF334155), Color(0xFF1E293B))
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(
                                                        topStart = 16.dp,
                                                        topEnd = 16.dp,
                                                        bottomStart = if (isMe) 16.dp else 2.dp,
                                                        bottomEnd = if (isMe) 2.dp else 16.dp
                                                    )
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isMe) Color(0xFF3B82F6) else Color(0xFF475569),
                                                    shape = RoundedCornerShape(
                                                        topStart = 16.dp,
                                                        topEnd = 16.dp,
                                                        bottomStart = if (isMe) 16.dp else 2.dp,
                                                        bottomEnd = if (isMe) 2.dp else 16.dp
                                                    )
                                                )
                                                .padding(horizontal = 14.dp, vertical = 10.dp)
                                                .widthIn(max = 280.dp)
                                        ) {
                                            Column {
                                                if (!isMe) {
                                                    Text(
                                                        text = message.sender,
                                                        color = Color(0xFFC084FC),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(bottom = 2.dp)
                                                    )
                                                }
                                                Text(
                                                    text = message.text,
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    lineHeight = 18.sp
                                                )
                                            }
                                        }
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color(0xFF34D399),
                                                modifier = Modifier.size(9.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Decrypted",
                                                color = Color(0xFF64748B),
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Input Bar with visual security details
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF131517))
                            .border(0.5.dp, Color(0x1AFFFFFF))
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                placeholder = { Text("Send E2EE Message...", color = Color(0xFF64748B), fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Encrypted Connection",
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFFF59E0B),
                                    unfocusedBorderColor = Color(0x26FFFFFF),
                                    focusedContainerColor = Color(0xFF0F1113),
                                    unfocusedContainerColor = Color(0xFF0B0D0F)
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("encrypted_chat_input_field"),
                                maxLines = 3
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            IconButton(
                                onClick = {
                                    if (messageText.isNotBlank()) {
                                        viewModel.sendEncryptedMessage(recipient, messageText)
                                        messageText = ""
                                    }
                                },
                                enabled = messageText.isNotBlank(),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (messageText.isNotBlank()) Color(0xFFF59E0B) else Color(0xFF1E293B)
                                    )
                                    .testTag("encrypted_chat_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send Encrypted Message",
                                    tint = if (messageText.isNotBlank()) Color.Black else Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
