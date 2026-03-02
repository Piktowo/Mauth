package com.xinto.mauth.ui.screen.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xinto.mauth.core.settings.model.ThemeSetting
import com.xinto.mauth.domain.SettingsRepository
import com.xinto.mauth.ui.theme.DEFAULT_THEME_SEED_ARGB
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme = settingsRepository.getTheme()
        .stateIn(
            scope = viewModelScope,
            initialValue = ThemeSetting.DEFAULT,
            started = SharingStarted.WhileSubscribed(5000)
        )

    val themeSeedColor = settingsRepository.getThemeSeedColor()
        .stateIn(
            scope = viewModelScope,
            initialValue = DEFAULT_THEME_SEED_ARGB,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun updateTheme(newTheme: ThemeSetting) {
        viewModelScope.launch {
            settingsRepository.setTheme(newTheme)
        }
    }

    fun updateThemeSeedColor(argb: Long) {
        viewModelScope.launch {
            settingsRepository.setThemeSeedColor(argb)
        }
    }

    fun resetThemeSeedColor() {
        viewModelScope.launch {
            settingsRepository.setThemeSeedColor(DEFAULT_THEME_SEED_ARGB)
        }
    }
}
