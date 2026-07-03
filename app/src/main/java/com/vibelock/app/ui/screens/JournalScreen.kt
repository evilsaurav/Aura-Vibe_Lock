package com.vibelock.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ai.GeminiEngine
import com.vibelock.app.ai.JournalAnalysis
import com.vibelock.app.data.AppDatabase
import com.vibelock.app.data.JournalEntity
import com.vibelock.app.haptics.VibeHapticEngine
import com.vibelock.app.ui.components.AuraBotCompanion
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.engine.UserState
import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JournalScreen(
    vibe: String,
    userState: UserState,
    geminiEngine: GeminiEngine,
    database: AppDatabase,
    onCheckInComplete: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<JournalAnalysis?>(null) }
    
    val context = LocalContext.current
    val hapticEngine = remember { VibeHapticEngine(context) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Aura Bot matches the vibe
        AuraBotCompanion(vibe = vibe, modifier = Modifier.size(100.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Locked in: $vibe",
            color = AuraColors.NeonCyan,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(targetState = analysisResult, label = "journal_state") { result ->
            if (result == null) {
                // Input Mode
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Bata, aaj kya chal raha hai? Kuch bhi likh...",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = rawText,
                        onValueChange = { rawText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF1E1E1E),
                            unfocusedContainerColor = Color(0xFF1E1E1E),
                            focusedBorderColor = AuraColors.NeonPurple,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isSubmitting) {
                        CircularProgressIndicator(color = AuraColors.NeonCyan)
                        Text("Aura is analyzing...", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        LaunchedEffect(Unit) {
                            while(isSubmitting) {
                                hapticEngine.triggerAILoading()
                                delay(1000)
                            }
                        }
                    } else {
                        NeonButton(
                            text = "SUBMIT",
                            color = AuraColors.NeonPurple,
                            onClick = {
                                hapticEngine.triggerJournalSubmit()
                                isSubmitting = true
                                coroutineScope.launch {
                                    val recentEntries = database.journalDao().getAllEntries()
                                    val sharedPrefs = context.getSharedPreferences("aura_prefs", Context.MODE_PRIVATE)
                                    val isRoast = sharedPrefs.getBoolean("ai_tone_roast", false)
                                    
                                    val finalResult = geminiEngine.generateVibeJournalAnalysis(
                                        vibe = vibe,
                                        userText = rawText,
                                        userState = userState,
                                        recentEntries = recentEntries,
                                        isRoast = isRoast
                                    )
                                    val entry = JournalEntity(
                                        timestamp = System.currentTimeMillis(),
                                        selectedVibe = vibe,
                                        rawText = rawText,
                                        aiSummary = "Energy: ${finalResult.energyLevel} | Mood: ${finalResult.mood} | Theme: ${finalResult.coreTheme}",
                                        aiPerspective = finalResult.auraTake,
                                        aiSuggestion = finalResult.suggestion
                                    )
                                    database.journalDao().insertEntry(entry)
                                    isSubmitting = false
                                    analysisResult = finalResult
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "SKIP",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                onCheckInComplete()
                            }.padding(8.dp)
                        )
                    }
                }
            } else {
                // Result Mode
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF121212))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("📊 Aaj ka Vibe Analysis:", color = AuraColors.NeonCyan, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Energy Level: ${result.energyLevel}", color = Color.LightGray, fontSize = 14.sp)
                            Text("Mood: ${result.mood}", color = Color.LightGray, fontSize = 14.sp)
                            Text("Core theme: ${result.coreTheme}", color = Color.LightGray, fontSize = 14.sp)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text("💡 Aura's Take:", color = AuraColors.NeonPink, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(result.auraTake, color = Color.White, fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text("🎯 Aaj ke liye suggestion:", color = AuraColors.NeonPurple, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(result.suggestion, color = Color.LightGray, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    NeonButton(
                        text = "LOCK IN",
                        color = AuraColors.NeonCyan,
                        onClick = {
                            hapticEngine.triggerLevelUp()
                            onCheckInComplete()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
