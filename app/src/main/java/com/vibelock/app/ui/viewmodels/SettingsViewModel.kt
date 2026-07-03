package com.vibelock.app.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("aura_prefs", Context.MODE_PRIVATE)

    private val _isHapticsEnabled = MutableStateFlow(prefs.getBoolean("haptics_enabled", true))
    val isHapticsEnabled: StateFlow<Boolean> = _isHapticsEnabled.asStateFlow()

    private val _isRoastMode = MutableStateFlow(prefs.getBoolean("is_roast_mode", false))
    val isRoastMode: StateFlow<Boolean> = _isRoastMode.asStateFlow()

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
        _isHapticsEnabled.value = enabled
    }

    fun setRoastMode(enabled: Boolean) {
        prefs.edit().putBoolean("is_roast_mode", enabled).apply()
        _isRoastMode.value = enabled
    }
}
