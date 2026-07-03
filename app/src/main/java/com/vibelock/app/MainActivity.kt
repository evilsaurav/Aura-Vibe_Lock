package com.vibelock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vibelock.app.data.AppDatabase
import com.vibelock.app.data.DailyStatsEntity
import com.vibelock.app.ui.screens.OnboardingScreen
import com.vibelock.app.data.UserRepository
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.engine.UserState
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.engine.VibeEngineWrapper
import com.vibelock.app.data.SquadRepository
import kotlinx.coroutines.tasks.await
import com.vibelock.app.engine.MissionEngine
import com.vibelock.app.engine.MissionType
import com.vibelock.app.data.FriendsRepository
import com.vibelock.app.data.GlobalRepository
import com.vibelock.app.engine.StreakProtectionEngine
import com.vibelock.app.ai.GeminiEngine
import com.vibelock.app.ai.WeeklyData
import com.vibelock.app.haptics.VibeHapticEngine
import com.vibelock.app.ui.components.GlassCard
import com.vibelock.app.ui.components.HolographicBorderContainer
import com.vibelock.app.ui.components.JellyButton
import com.vibelock.app.ui.components.NeonProgressBar
import com.vibelock.app.ui.components.ParticleCannon
import com.vibelock.app.ui.theme.PremiumBlack
import com.vibelock.app.ui.theme.VibeLockTheme
import kotlinx.coroutines.launch
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import java.util.concurrent.TimeUnit
import com.vibelock.app.worker.GeminiNotificationWorker
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var hapticEngine: VibeHapticEngine
    private val vibeEngine = VibeEngineWrapper()
    private lateinit var userRepository: UserRepository
    private lateinit var friendsRepository: com.vibelock.app.data.FriendsRepository
    private lateinit var squadRepository: com.vibelock.app.data.SquadRepository
    private lateinit var globalRepository: com.vibelock.app.data.GlobalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        hapticEngine = VibeHapticEngine(this)
        
        val database = AppDatabase.getDatabase(this)
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        userRepository = UserRepository(database.userDao(), auth, firestore)
        friendsRepository = com.vibelock.app.data.FriendsRepository(firestore, auth)
        squadRepository = com.vibelock.app.data.SquadRepository(firestore, auth)
        globalRepository = com.vibelock.app.data.GlobalRepository(firestore, fusedLocationClient)

        // Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        // Schedule AI Timely Notifications (Every 12 Hours)
        val workRequest = PeriodicWorkRequestBuilder<GeminiNotificationWorker>(12, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AuraAI_Reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        setContent {
            VibeLockTheme {
                // Shared preferences for first boot
                val context = LocalContext.current
                val sharedPrefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                val hasSeenOnboarding = sharedPrefs.getBoolean("hasSeenOnboarding", false)

                // Firebase check karega ki user pehle se login hai ya nahi
                val auth = FirebaseAuth.getInstance()
                
                var startDestination by remember { 
                    mutableStateOf(
                        if (auth.currentUser != null) "HOME" 
                        else if (!hasSeenOnboarding) "ONBOARDING" 
                        else "LOGIN"
                    ) 
                }
                
                var showWelcomeNote by remember {
                    mutableStateOf(auth.currentUser != null && !sharedPrefs.getBoolean("hasSeenWelcomeNote", false))
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PremiumBlack
                ) {
                    if (showWelcomeNote) {
                        androidx.compose.ui.window.Dialog(onDismissRequest = { 
                            showWelcomeNote = false
                            sharedPrefs.edit().putBoolean("hasSeenWelcomeNote", true).apply()
                        }) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFF1E1E1E))
                                    .padding(24.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Welcome to AURA! 🌌", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Track your vibes, unlock 4D trophies, and level up in real life.\n\nSpin the globe and join the grind!",
                                        color = Color.LightGray,
                                        fontSize = 14.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    com.vibelock.app.ui.components.NeonButton(
                                        text = "LET'S GO",
                                        color = com.vibelock.app.ui.theme.AuraColors.NeonCyan,
                                        onClick = {
                                            showWelcomeNote = false
                                            sharedPrefs.edit().putBoolean("hasSeenWelcomeNote", true).apply()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    when (startDestination) {
                        "ONBOARDING" -> {
                            OnboardingScreen(
                                onFinish = {
                                    sharedPrefs.edit().putBoolean("hasSeenOnboarding", true).apply()
                                    startDestination = "LOGIN"
                                }
                            )
                        }
                        "LOGIN" -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    startDestination = "HOME" // Login hote hi Home pe bhej do
                                }
                            )
                        }
                        else -> {
                            // Ye aapki aage banne wali main app screen hai
                            AuraMainScreen(
                                hapticEngine = hapticEngine,
                                vibeEngine = vibeEngine,
                                userRepository = userRepository,
                                friendsRepository = friendsRepository,
                                squadRepository = squadRepository,
                                globalRepository = globalRepository,
                                firestore = firestore,
                                auth = auth,
                                onSignOut = { 
                                    auth.signOut()
                                    startDestination = "LOGIN" 
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        hapticEngine.release()
    }
}

@Composable
fun AuraMainScreen(
    hapticEngine: VibeHapticEngine,
    vibeEngine: VibeEngineWrapper,
    userRepository: UserRepository,
    friendsRepository: FriendsRepository,
    squadRepository: com.vibelock.app.data.SquadRepository,
    globalRepository: GlobalRepository,
    firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    onSignOut: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val userState by userRepository.userStateFlow.collectAsState(initial = UserState())
    
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }
    
    // Evaluate Streak Misses on launch
    var checkedStreak by remember { mutableStateOf(false) }

    LaunchedEffect(userState.lastCheckInTimestamp) {
        if (!checkedStreak && userState.lastCheckInTimestamp > 0) {
            val (newState, status) = StreakProtectionEngine.evaluateMissedDays(userState, System.currentTimeMillis())
            if (status == "STREAK_BROKEN" || status == "SHIELD_USED") {
                userRepository.saveState(newState)
            }
            checkedStreak = true
        }
    }

    // Embed the new NavHost!
    com.vibelock.app.ui.navigation.AuraNavHost(
        hapticEngine = hapticEngine,
        vibeEngine = vibeEngine,
        userRepository = userRepository,
        friendsRepository = friendsRepository,
        squadRepository = squadRepository,
        globalRepository = globalRepository,
        auth = auth,
        onSignOut = onSignOut
    )
}


