@file:Suppress("DEPRECATION") // AndroidX Crypto

package com.xinto.mauth.core.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.xinto.mauth.core.settings.model.ColorSetting
import com.xinto.mauth.core.settings.model.SortSetting
import com.xinto.mauth.core.settings.model.ThemeSetting
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class DefaultSettings(context: Context) : Settings {

    private val Context.preferences by preferencesDataStore("preferences")
    private val preferences = context.preferences

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "webdav_prefs",
        MasterKey(context = context),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override fun getSecureMode(): Flow<Boolean> {
        return preferences.data.map {
            it[KEY_SECURE_MODE] ?: false
        }
    }

    override fun getUseBiometrics(): Flow<Boolean> {
        return preferences.data.map {
            it[KEY_USE_BIOMETRICS] ?: false
        }
    }

    override fun getSortMode(): Flow<SortSetting> {
        return preferences.data.map {
            it[KEY_SORT_MODE]?.let { name ->
                SortSetting.valueOf(name)
            } ?: SortSetting.DEFAULT
        }
    }

    override fun getTheme(): Flow<ThemeSetting> {
        return preferences.data.map { preferences ->
            preferences[KEY_THEME]?.let { name ->
                ThemeSetting.entries.find { it.name == name }
            } ?: ThemeSetting.DEFAULT
        }
    }

    override fun getColor(): Flow<ColorSetting> {
        return preferences.data.map { preferences ->
            preferences[KEY_COLOR]?.let { name ->
                ColorSetting.entries.find { it.name == name }
            } ?: ColorSetting.DEFAULT
        }
    }

    override fun getThemeSeedColor(): Flow<Long> {
        return preferences.data.map { it[KEY_THEME_SEED_COLOR] ?: 0xFFFFFFFFL }
    }

    override fun getWebDavUrl(): Flow<String> = encryptedPrefFlow(KEY_WEBDAV_URL)

    override fun getWebDavUsername(): Flow<String> = encryptedPrefFlow(KEY_WEBDAV_USERNAME)

    override fun getWebDavPassword(): Flow<String> = encryptedPrefFlow(KEY_WEBDAV_PASSWORD)

    override suspend fun setSecureMode(value: Boolean) {
        preferences.edit {
            it[KEY_SECURE_MODE] = value
        }
    }

    override suspend fun setUseBiometrics(value: Boolean) {
        preferences.edit {
            it[KEY_USE_BIOMETRICS] = value
        }
    }

    override suspend fun setSortMode(value: SortSetting) {
        preferences.edit {
            it[KEY_SORT_MODE] = value.name
        }
    }

    override suspend fun setTheme(value: ThemeSetting) {
        preferences.edit {
            it[KEY_THEME] = value.name
        }
    }

    override suspend fun setColor(value: ColorSetting) {
        preferences.edit {
            it[KEY_COLOR] = value.name
        }
    }

    override suspend fun setThemeSeedColor(value: Long) {
        preferences.edit { it[KEY_THEME_SEED_COLOR] = value }
    }

    override suspend fun setWebDavUrl(value: String) {
        encryptedPrefs.edit { putString(KEY_WEBDAV_URL, value) }
    }

    override suspend fun setWebDavUsername(value: String) {
        encryptedPrefs.edit { putString(KEY_WEBDAV_USERNAME, value) }
    }

    override suspend fun setWebDavPassword(value: String) {
        encryptedPrefs.edit { putString(KEY_WEBDAV_PASSWORD, value) }
    }

    private fun encryptedPrefFlow(key: String): Flow<String> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, changedKey ->
            if (changedKey == key) {
                trySend(prefs.getString(key, "") ?: "")
            }
        }
        send(encryptedPrefs.getString(key, "") ?: "")
        encryptedPrefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { encryptedPrefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    private companion object {
        val KEY_SECURE_MODE = booleanPreferencesKey("private_mode")
        val KEY_USE_BIOMETRICS = booleanPreferencesKey("use_biometrics")
        val KEY_SORT_MODE = stringPreferencesKey("sort_mode")
        val KEY_THEME = stringPreferencesKey("theme")
        val KEY_COLOR = stringPreferencesKey("color")
        val KEY_THEME_SEED_COLOR = longPreferencesKey("theme_seed_color")
        const val KEY_WEBDAV_URL = "webdav_url"
        const val KEY_WEBDAV_USERNAME = "webdav_username"
        const val KEY_WEBDAV_PASSWORD = "webdav_password"
    }

}