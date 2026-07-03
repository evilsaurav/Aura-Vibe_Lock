package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import com.vibelock.app.data.Squad
import com.vibelock.app.data.SquadRepository
import com.vibelock.app.data.FriendsRepository
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.theme.AuraColors
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadListScreen(
    squadRepository: SquadRepository,
    friendsRepository: FriendsRepository,
    onNavigateToSquadDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    var squads by remember { mutableStateOf<List<Squad>>(emptyList()) }
    var showCreateDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        squadRepository.getUserSquads().collect { list ->
            squads = list
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("< Back", color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text("YOUR SQUADS", color = AuraColors.NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create Squad", tint = AuraColors.NeonPurple)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        if (squads.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("You have no squads. Gather your gang.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(squads) { squad ->
                    SquadListItem(squad = squad, onClick = { onNavigateToSquadDetail(squad.squadId) })
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateSquadDialog(
            friendsRepository = friendsRepository,
            onDismiss = { showCreateDialog = false },
            onCreate = { name, uids, auraCodes ->
                coroutineScope.launch {
                    val auth = FirebaseAuth.getInstance()
                    val myUid = auth.currentUser?.uid ?: return@launch
                    val fullUids = mutableListOf(myUid).apply { addAll(uids) }
                    squadRepository.createSquad(name, fullUids, auraCodes)
                    showCreateDialog = false
                }
            }
        )
    }
}

@Composable
fun SquadListItem(squad: Squad, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF111111))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column {
            Text(squad.name.uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${squad.members.size}/5 Members", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSquadDialog(
    friendsRepository: FriendsRepository,
    onDismiss: () -> Unit,
    onCreate: (String, List<String>, List<String>) -> Unit
) {
    var squadName by remember { mutableStateOf("") }
    var friendCode by remember { mutableStateOf("") }
    var addedFriends by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } // Pair of UID and AuraCode
    var errorText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isSearching by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF151515))
                .padding(24.dp)
        ) {
            Column {
                Text("CREATE A SQUAD", color = AuraColors.NeonCyan, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = squadName,
                    onValueChange = { squadName = it },
                    label = { Text("Squad Name") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = AuraColors.NeonCyan,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = friendCode,
                        onValueChange = { friendCode = it.uppercase() },
                        label = { Text("Friend Aura Code") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = AuraColors.NeonPurple,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (addedFriends.size >= 4) {
                                errorText = "Squad is full (Max 5 including you)"
                                return@Button
                            }
                            isSearching = true
                            coroutineScope.launch {
                                val user = friendsRepository.findUserByAuraCode(friendCode)
                                isSearching = false
                                if (user != null) {
                                    if (addedFriends.any { it.first == user.uid }) {
                                        errorText = "Already added"
                                    } else {
                                        addedFriends = addedFriends + (user.uid to user.auraCode)
                                        friendCode = ""
                                        errorText = ""
                                    }
                                } else {
                                    errorText = "User not found"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraColors.NeonPurple),
                        enabled = !isSearching && friendCode.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
                
                if (errorText.isNotEmpty()) {
                    Text(errorText, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (addedFriends.isNotEmpty()) {
                    Text("Members: ${addedFriends.joinToString { it.second }}", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onCreate(squadName, addedFriends.map { it.first }, addedFriends.map { it.second }) },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraColors.NeonCyan),
                        enabled = squadName.isNotBlank()
                    ) {
                        Text("Create", color = Color.Black)
                    }
                }
            }
        }
    }
}
