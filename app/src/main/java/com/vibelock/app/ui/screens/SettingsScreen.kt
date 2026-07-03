package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onSignOut: () -> Unit,
    onNavigateToLegal: (String) -> Unit,
    onNavigateToAbout: () -> Unit,
    onBack: () -> Unit
) {
    val isHapticsEnabled by viewModel.isHapticsEnabled.collectAsState()
    val isRoastMode by viewModel.isRoastMode.collectAsState()
    
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack, modifier = Modifier.padding(end = 16.dp)) {
                Text("< Back", color = Color.Gray)
            }
            Text("SETTINGS", color = AuraColors.NeonCyan, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        Text("APP PREFERENCES", color = AuraColors.NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        
        SettingToggleRow(
            title = "Haptic Feedback",
            subtitle = "Enable vibration for interactions",
            checked = isHapticsEnabled,
            onCheckedChange = { viewModel.setHapticsEnabled(it) }
        )
        
        SettingToggleRow(
            title = "AI Persona (Roast Mode)",
            subtitle = "Enable aggressive AI roasting instead of hype",
            checked = isRoastMode,
            onCheckedChange = { viewModel.setRoastMode(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("INFORMATION", color = AuraColors.NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        
        SettingRowClickable(title = "About AURA", onClick = onNavigateToAbout)
        SettingRowClickable(title = "Terms of Service", onClick = { onNavigateToLegal("Terms of Service") })
        SettingRowClickable(title = "Privacy Policy", onClick = { onNavigateToLegal("Privacy Policy") })
        SettingRowClickable(title = "Help Center", onClick = { onNavigateToLegal("Help Center") })

        Spacer(modifier = Modifier.height(32.dp))

        Text("ACCOUNT & PROFILE", color = AuraColors.NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        
        // Personal Profile Info Block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF111111))
                .padding(16.dp)
        ) {
            Text("User ID", color = Color.Gray, fontSize = 10.sp)
            Text(currentUser?.uid ?: "Not Logged In", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Email", color = Color.Gray, fontSize = 10.sp)
            Text(currentUser?.email ?: "No Email", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Display Name", color = Color.Gray, fontSize = 10.sp)
            Text(currentUser?.displayName ?: "No Name Set", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A0808)),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("SIGN OUT", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF111111))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AuraColors.NeonPurple,
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = Color.DarkGray
            )
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun SettingRowClickable(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF111111))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(">", color = Color.Gray, fontSize = 16.sp)
    }
    Spacer(modifier = Modifier.height(8.dp))
}
