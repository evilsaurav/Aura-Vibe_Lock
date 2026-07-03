package com.vibelock.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.components.NeonButton

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF1E1E1E))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ABOUT AURA",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // About Project Section
        Text("🌌 About Project: AURA", color = Color(0xFF8B5CF6), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "AURA is not just a habit tracker; it is a real-time social gamification engine built for the modern grind. Wrapped in a premium, neon-drenched cyberpunk interface, it turns your daily mood and mental energy into an interactive RPG. Whether you are hustling, chilling, or surviving chaos, your vibe now has a level, a streak, and a global presence.\n\nLock in your aura, spin the 4D matrix globe, and see who else is grinding with you across the world.",
            color = Color.LightGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Note from Creator Section
        Text("💻 Why I Built This", color = Color(0xFF10B981), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("(A Note from the Creator)", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E1E1E))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "\"We track our screen time, our server pings, and our daily tasks, but we rarely track the most important metric: our own Vibe.\n\nThe idea for AURA came to me during those endless, late-night coding sessions. When you are staring at a screen until 3 AM, pushing boundaries, and trying to level up in real life, standard to-do lists and boring calendar apps just don't cut it. I wanted my daily grind to feel as rewarding as a massive multiplayer game.\n\nI built this app because I wanted to turn self-reflection into a high-energy experience. Fueled by too much caffeine, complex math algorithms, and my dog Brownie sleeping next to my chair while I compiled the codebase, AURA was born. Operating under the code name Insomniac, I designed this for the night owls, the hustlers, and anyone who wants to visually see their 'Aura' level up as they conquer their days.\"",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "— Saurav Kumar (Code Name: Insomniac)",
                    color = Color(0xFFF59E0B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Core Features
        Text("⚡ The Arsenal (Core Features)", color = Color(0xFF3B82F6), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        FeatureItem("🌍 The 4D Aura Globe", "A custom-engineered, fully interactive 3D holographic matrix. Spin the globe with physical haptic resistance and watch real-time check-ins glow across the world.")
        FeatureItem("🧬 Gamified Vibe Engine", "Your check-ins are converted into raw XP. Rank up from a Novice to a Legend, unlock dynamic loot boxes, and protect your progress with Streak Shields.")
        FeatureItem("🧠 Gemini AI Brain", "Powered by next-gen AI, get brutally honest weekly vibe-checks, personalized roasts, and hyper-customized psychology insights based on your check-in history.")
        FeatureItem("🏆 Social Leaderboards & Aura Codes", "Add your squad using your unique 6-character Aura Code. Flex your highest streak, claim daily mission XP, and dominate the global rankings.")
        FeatureItem("📸 Story/Flex Generator", "Export your daily stats into a sleek, Instagram-ready 9:16 poster. Show the world your current Aura, AI-generated quote, and daily grind in a single tap.")
        FeatureItem("📳 Deep Hardware Haptics", "Every click, spin, and level-up is synced with your phone's native vibration motor, delivering heavy bass drops and mechanical ticks for a pure 4D experience.")

        Spacer(modifier = Modifier.height(32.dp))

        // Other Projects & Portfolio
        Text("🚀 Other Projects & Portfolio", color = Color(0xFFEC4899), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Beyond AURA, I am constantly building tools that bridge the gap between complex technology and everyday utility.", color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        NeonButton(
            text = "VIEW BCABUDDY",
            color = Color(0xFF3B82F6),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://kind-sea-0b41fb700.2.azurestaticapps.net/"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        NeonButton(
            text = "VIEW PORTFOLIO",
            color = Color(0xFF8B5CF6),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://portfolio-three-psi-76.vercel.app/"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        NeonButton(
            text = "GITHUB: @evilsaurav",
            color = Color(0xFF10B981),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/evilsaurav"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun FeatureItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = "• $title", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = desc, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 12.dp, top = 4.dp))
    }
}
