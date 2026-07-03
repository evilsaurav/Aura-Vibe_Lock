package com.vibelock.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.vibelock.app.data.FriendsRepository
import com.vibelock.app.data.GlobalRepository
import com.vibelock.app.data.SquadRepository
import com.vibelock.app.engine.*
import com.vibelock.app.ui.screens.*
import com.vibelock.app.haptics.VibeHapticEngine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.vibelock.app.data.UserRepository
import com.vibelock.app.ai.GeminiEngine
import com.vibelock.app.ai.WeeklyData
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.animations.*

@Composable
fun AuraNavHost(
    hapticEngine: VibeHapticEngine,
    vibeEngine: VibeEngineWrapper,
    userRepository: UserRepository,
    friendsRepository: FriendsRepository,
    squadRepository: SquadRepository,
    globalRepository: GlobalRepository,
    auth: FirebaseAuth,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    
    val userState by userRepository.userStateFlow.collectAsState(initial = UserState())
    val globalCheckIns by globalRepository.getRecentCheckInsFlow().collectAsState(initial = emptyList())
    val friends by friendsRepository.observeFriendsList().collectAsState(initial = emptyList())
    val friendCount = friends.size
    
    // Calculate Tier
    val currentTier = AuraTier.getTierForLevel(userState.level)
    val xpRequiredForNext = vibeEngine.calculateXpRequiredForNextLevel(userState.level)

    var pendingVibe by remember { mutableStateOf("Chill") }
    var selectedSquadId by remember { mutableStateOf("") }
    var brokenStreakCount by remember { mutableStateOf(0) }
    var legalTitle by remember { mutableStateOf("") }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "HOME"

    // Routes that should NOT show the Bottom Nav Bar
    val hideBottomNavRoutes = listOf(
        "JOURNAL", "CHECK_IN", "LEVEL_UP_ORACLE", "BOX_OPENING", 
        "AI_INSIGHTS", "LOADING_WRAPPED", "WRAPPED", "STREAK_BROKEN", 
        "STREAK_SAVED", "SETTINGS", "LEGAL", "ABOUT", "SQUAD_DETAIL", "STORY"
    )

    val showBottomNav = !hideBottomNavRoutes.contains(currentRoute)

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomNavigationBar(navController = navController, currentVibe = pendingVibe)
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "HOME",
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(
                    androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideIntoContainer(
                    androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutOfContainer(
                    androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            composable("HOME") {
                HomeScreen(
                    userState = userState,
                    tier = currentTier,
                    xpRequired = xpRequiredForNext,
                    globalCheckIns = globalCheckIns,
                    friendCount = friendCount,
                    onNavigateToCheckIn = { vibe -> 
                        pendingVibe = vibe
                        navController.navigate("JOURNAL")
                    },
                    onNavigateToAI = { navController.navigate("AI_INSIGHTS") },
                    onNavigateToWrapped = { navController.navigate("WRAPPED") },
                    onNavigateToProfile = { navController.navigate("PROFILE") },
                    onNavigateToFriends = { navController.navigate("FRIENDS") },
                    onNavigateToLeaderboard = { 
                        val (newState, _) = MissionEngine.processAction(MissionType.VISIT_LEADERBOARD, userState)
                        if (newState != userState) {
                            coroutineScope.launch { userRepository.saveState(newState) }
                        }
                        navController.navigate("LEADERBOARD") 
                    },
                    onNavigateToMissions = { navController.navigate("MISSIONS") },
                    onGlobeDrag = { velocity -> hapticEngine.triggerGlobeSpin(velocity) },
                    onVibeSelect = { vibe -> hapticEngine.triggerVibeSelect(vibe) }
                )
            }

            composable("SQUADS") {
                SquadListScreen(
                    squadRepository = squadRepository,
                    friendsRepository = friendsRepository,
                    onNavigateToSquadDetail = { id ->
                        selectedSquadId = id
                        navController.navigate("SQUAD_DETAIL")
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("SQUAD_DETAIL") {
                SquadDetailScreen(
                    squadId = selectedSquadId,
                    squadRepository = squadRepository,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("LEADERBOARD") {
                LeaderboardScreen(
                    userState = userState,
                    friendsRepository = friendsRepository,
                    globalCheckIns = globalCheckIns
                )
            }

            composable("PROFILE") {
                ProfileScreen(
                    userState = userState,
                    tier = currentTier,
                    onAvatarChange = { avatar ->
                        val updatedState = userState.copy(avatarUrl = avatar)
                        coroutineScope.launch { userRepository.saveState(updatedState) }
                    },
                    onShareStory = { navController.navigate("STORY") },
                    onNavigateToAchievements = { navController.navigate("ACHIEVEMENTS") },
                    onNavigateToWrapped = { navController.navigate("WRAPPED") },
                    onNavigateToSettings = { navController.navigate("SETTINGS") }
                )
            }

            composable("SETTINGS") {
                SettingsScreen(
                    onSignOut = onSignOut,
                    onNavigateToLegal = { title ->
                        legalTitle = title
                        navController.navigate("LEGAL")
                    },
                    onNavigateToAbout = { navController.navigate("ABOUT") },
                    onBack = { navController.popBackStack() }
                )
            }

            composable("FRIENDS") {
                FriendsScreen(
                    userState = userState,
                    friendsRepository = friendsRepository,
                    onBack = { navController.popBackStack() },
                    onNavigateToSquads = { navController.navigate("SQUADS") }
                )
            }

            composable("MISSIONS") {
                MissionsScreen(
                    userState = userState,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("ACHIEVEMENTS") {
                AchievementsScreen(
                    userState = userState,
                    onClose = { navController.popBackStack() }
                )
            }

            composable("AI_INSIGHTS") {
                val ctx = LocalContext.current
                var weeklyData by remember { mutableStateOf<WeeklyData?>(null) }

                LaunchedEffect(Unit) {
                    try {
                        val db = com.vibelock.app.data.AppDatabase.getDatabase(ctx)
                        val calendar = java.util.Calendar.getInstance()
                        val end = calendar.timeInMillis
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
                        val start = calendar.timeInMillis
                        
                        val entries = db.journalDao().getEntriesInRange(start, end)
                        
                        val dominantVibe = if (entries.isNotEmpty()) {
                            entries.groupingBy { it.selectedVibe }.eachCount().maxByOrNull { it.value }?.key ?: "Chill"
                        } else {
                            "Chill"
                        }
                        
                        weeklyData = WeeklyData(
                            checkInCount = entries.size,
                            dominantVibe = dominantVibe,
                            avgCheckInTime = "Varies",
                            totalXP = userState.xp,
                            streak = userState.currentStreak,
                            missionsCompleted = (entries.size * 1.5).toInt(),
                            battlesWon = 0
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        weeklyData = WeeklyData(
                            checkInCount = 0,
                            dominantVibe = "Unknown",
                            avgCheckInTime = "N/A",
                            totalXP = userState.xp,
                            streak = userState.currentStreak,
                            missionsCompleted = 0,
                            battlesWon = 0
                        )
                    }
                }

                if (weeklyData != null) {
                    AIInsightsScreen(
                        geminiEngine = GeminiEngine(),
                        weeklyData = weeklyData!!,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = com.vibelock.app.ui.theme.AuraColors.NeonPurple)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("AURA BOT analyzing your week...", color = Color.LightGray)
                        }
                    }
                }
            }

            composable("WRAPPED") {
                var wrappedData by remember { mutableStateOf<com.vibelock.app.data.WrappedData?>(null) }
                val ctx = LocalContext.current
                val activity = ctx as? android.app.Activity
                
                LaunchedEffect(Unit) {
                    try {
                        val db = com.vibelock.app.data.AppDatabase.getDatabase(ctx)
                        val wrappedRepository = com.vibelock.app.data.WrappedRepository(db.journalDao(), db.dailyStatsDao())
                        val data = wrappedRepository.getWrappedDataForCurrentMonth(userState)
                        if (!data.isMock) {
                            val calendar = java.util.Calendar.getInstance()
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                            val start = calendar.timeInMillis
                            calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
                            val end = calendar.timeInMillis
                            val entries = db.journalDao().getEntriesInRange(start, end)
                            
                            // Check if there are actually entries, otherwise force mock mode
                            if (entries.isEmpty()) {
                                wrappedData = data.copy(
                                    isMock = true,
                                    aiMessage = "You haven't logged enough this month. Start checking in to unlock your real Aura Story!"
                                )
                            } else {
                                val analysis = GeminiEngine().generateWrappedAnalysis(entries, isRoast = false)
                                wrappedData = data.copy(themes = analysis.themes, aiMessage = analysis.aiMessage)
                            }
                        } else {
                            wrappedData = data
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Fallback if AI fails or DB crashes
                        wrappedData = com.vibelock.app.data.WrappedData(
                            totalCheckIns = 0,
                            dominantVibe = "Unknown",
                            bestStreak = userState.currentStreak,
                            startLevel = userState.level,
                            endLevel = userState.level,
                            themes = emptyList(),
                            aiMessage = "Aura Bot got dizzy calculating your vibes. Check back later!",
                            isMock = true
                        )
                    }
                }
                
                if (wrappedData != null) {
                    WrappedScreen(
                        wrappedData = wrappedData!!,
                        userState = userState,
                        onClose = { navController.popBackStack() },
                        activity = activity
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = com.vibelock.app.ui.theme.AuraColors.NeonCyan)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("AURA BOT calculating your month...", color = Color.LightGray)
                        }
                    }
                }
            }

            composable("STORY") {
                StoryShareScreen(
                    userState = userState,
                    tier = currentTier,
                    onClose = { navController.popBackStack() }
                )
            }

            composable("LEGAL") {
                LegalScreen(
                    title = legalTitle,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("ABOUT") {
                AboutScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable("JOURNAL") {
                JournalScreen(
                    vibe = pendingVibe,
                    userState = userState,
                    geminiEngine = GeminiEngine(),
                    database = com.vibelock.app.data.AppDatabase.getDatabase(LocalContext.current),
                    onCheckInComplete = {
                        navController.navigate("CHECK_IN") {
                            popUpTo("JOURNAL") { inclusive = true }
                        }
                    }
                )
            }

            composable("CHECK_IN") {
                CheckInScreen(
                    tier = currentTier,
                    onCheckInComplete = {
                        val currentTime = System.currentTimeMillis()
                        val newState = vibeEngine.processCheckIn(userState, currentTime).copy(currentVibe = pendingVibe)
                        coroutineScope.launch { userRepository.saveState(newState) }
                        
                        // Fire-and-forget squad broadcast
                        val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                            try {
                                val uid = auth.currentUser?.uid ?: ""
                                if (uid.isNotEmpty()) {
                                    val squadDocs = firestore.collection("squads")
                                        .whereArrayContains("members", uid)
                                        .get()
                                        .await()
                                    
                                    val squadIds = squadDocs.documents.map { it.id }
                                    if (squadIds.isNotEmpty()) {
                                        squadRepository.broadcastSquadVibe(squadIds, userState.auraCode, pendingVibe, currentTime)
                                    }
                                }
                            } catch (e: Exception) {
                                // Silent fail
                            }
                        }
                        
                        navController.navigate("HOME") {
                            popUpTo("HOME") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentVibe: String) {
    val items = listOf("HOME", "SQUADS", "LEADERBOARD", "PROFILE")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF0A0A0A),
        contentColor = Color.White
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen
            val onClick = {
                navController.navigate(screen) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            
            NavigationBarItem(
                selected = isSelected,
                onClick = onClick,
                icon = {
                    when (screen) {
                        "HOME" -> HomeIconAnim(selected = isSelected, currentVibe = currentVibe, onClick = onClick)
                        "SQUADS" -> SquadIconAnim(selected = isSelected, currentVibe = currentVibe, onClick = onClick)
                        "LEADERBOARD" -> TrophyIconAnim(selected = isSelected, currentVibe = currentVibe, onClick = onClick)
                        "PROFILE" -> ProfileIconAnim(selected = isSelected, currentVibe = currentVibe, onClick = onClick)
                        else -> Text("?")
                    }
                },
                label = {
                    Text(
                        text = screen.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
