package com.vibelock.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.data.FriendRequest
import com.vibelock.app.data.FriendsRepository
import com.vibelock.app.data.UserProfile
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.engine.UserState
import com.vibelock.app.ui.components.AuraGlassCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    userState: UserState,
    friendsRepository: FriendsRepository,
    onBack: () -> Unit,
    onNavigateToSquads: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val friendsList by friendsRepository.observeFriendsList().collectAsState(initial = emptyList())
    val friendRequests by friendsRepository.observeFriendRequests().collectAsState(initial = emptyList())
    
    var searchQuery by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<UserProfile?>(null) }
    var showSearchResult by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(16.dp)
    ) {
        Text(
            text = "YOUR CREW",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("< Back", color = Color.Gray)
            }
            Text("YOUR CIRCLE", color = Color(0xFF00E5FF), fontSize = 24.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.width(64.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Squads Entry Point
        Button(
            onClick = onNavigateToSquads,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("ENTER SQUADS 🔥", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // PENDING REQUESTS
        AnimatedVisibility(visible = friendRequests.isNotEmpty()) {
            Column {
                Text("PENDING REQUESTS", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                friendRequests.forEach { request ->
                    FriendRequestCard(
                        request = request,
                        onAccept = {
                            coroutineScope.launch {
                                friendsRepository.acceptFriendRequest(request.fromUid)
                                Toast.makeText(context, "Added to Crew!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDecline = {
                            coroutineScope.launch {
                                friendsRepository.declineFriendRequest(request.fromUid)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // ADD FRIEND SECTION
        AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("YOUR AURA CODE", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = userState.auraCode.ifEmpty { "LOADING..." },
                        color = Color(0xFF8B5CF6),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Row {
                        TextButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Aura Code", userState.auraCode))
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Copy", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Join my Crew on AURA! Use my code: ${userState.auraCode}")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share your Aura Code"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }
                    }
                }
                
                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it.uppercase() },
                    placeholder = { Text("Enter their Aura Code", color = Color.Gray) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        containerColor = Color(0xFF121212)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    searchResult = friendsRepository.findUserByAuraCode(searchQuery)
                                    showSearchResult = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp).padding(end = 8.dp)
                        ) {
                            Text("Find", fontSize = 12.sp)
                        }
                    }
                )
                
                if (showSearchResult) {
                    Spacer(modifier = Modifier.height(12.dp))
                    if (searchResult != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp)).padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(searchResult!!.displayName, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("${searchResult!!.currentStreak}-day streak", color = Color(0xFF10B981), fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        friendsRepository.sendFriendRequest(searchResult!!.uid)
                                        Toast.makeText(context, "Request Sent!", Toast.LENGTH_SHORT).show()
                                        showSearchResult = false
                                        searchQuery = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("Add", fontSize = 12.sp)
                            }
                        }
                    } else {
                        Text("User not found.", color = Color.Red, fontSize = 12.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // FRIENDS LIST
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("YOUR CREW (${friendsList.size})", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Weekly XP", color = Color.Gray, fontSize = 12.sp)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (friendsList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your crew is empty 😔", color = Color.LightGray)
                    Text("Share your Aura Code to build your crew.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(friendsList) { friend ->
                    FriendCard(friend, userState)
                }
            }
        }
    }
}

@Composable
fun FriendRequestCard(request: FriendRequest, onAccept: () -> Unit, onDecline: () -> Unit) {
    AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("⚔️ ${request.fromUsername} wants to join", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Code: ${request.fromAuraCode}", color = Color.Gray, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onDecline, modifier = Modifier.size(36.dp).background(Color(0xFFE53935).copy(alpha = 0.2f), CircleShape)) {
                    Icon(Icons.Default.Close, contentDescription = "Decline", tint = Color(0xFFE53935))
                }
                IconButton(onClick = onAccept, modifier = Modifier.size(36.dp).background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape)) {
                    Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color(0xFF10B981))
                }
            }
        }
    }
}

@Composable
fun FriendCard(friend: UserProfile, currentUserState: UserState) {
    val friendTier = AuraTier.getTierForLevel(friend.level)
    val hasCheckedInToday = (System.currentTimeMillis() - friend.lastCheckInTimestamp) < 24 * 60 * 60 * 1000
    
    // FOMO Triggers
    val isHigherStreak = friend.currentStreak > currentUserState.currentStreak
    val isAheadInXP = friend.weeklyXP > currentUserState.xp

    AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(friendTier.colorHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(friend.displayName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    if (hasCheckedInToday) {
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(0xFF10B981)).align(Alignment.BottomEnd))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Info
                Column {
                    Text(friend.displayName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(friendTier.name.replace("_", " "), color = Color(friendTier.colorHex), fontSize = 12.sp)
                    
                    // FOMO Element
                    if (isHigherStreak) {
                        Text("🔥 ${friend.currentStreak}-day streak", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    } else if (hasCheckedInToday) {
                        Text("Vibe locked ✓", color = Color(0xFF10B981), fontSize = 10.sp)
                    } else {
                        Text("Not in yet", color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }
            
            // XP Score
            Column(horizontalAlignment = Alignment.End) {
                Text(friend.weeklyXP.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                if (isAheadInXP) {
                    Text("↑ Ahead of you", color = Color(0xFFE53935), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
