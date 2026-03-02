package com.xinto.mauth.core.settings

import com.xinto.mauth.core.settings.model.ColorSetting
import com.xinto.mauth.core.settings.model.SortSetting
import com.xinto.mauth.core.settings.model.ThemeSetting
import kotlinx.coroutines.flow.Flow

interface Settings {
    fun getSecureMode(): Flow<Boolean>
    fun getUseBiometrics(): Flow<Boolean>
    fun getSortMode(): Flow<SortSetting>
    fun getTheme(): Flow<ThemeSetting>
    fun getColor(): Flow<ColorSetting>
    fun getThemeSeedColor(): Flow<Long>
    fun getWebDavUrl(): Flow<String>
    fun getWebDavUsername(): Flow<String>
    fun getWebDavPassword(): Flow<String>

    suspend fun setSecureMode(value: Boolean)
    suspend fun setUseBiometrics(value: Boolean)
    suspend fun setSortMode(value: SortSetting)
    suspend fun setTheme(value: ThemeSetting)
    suspend fun setColor(value: ColorSetting)
    suspend fun setThemeSeedColor(value: Long)
    suspend fun setWebDavUrl(value: String)
    suspend fun setWebDavUsername(value: String)
    suspend fun setWebDavPassword(value: String)
}