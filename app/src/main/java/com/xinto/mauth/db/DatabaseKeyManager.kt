@file:Suppress("DEPRECATION") // EncryptedSharedPreferences

package com.xinto.mauth.db

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

/**
 * Manages the per-device SQLCipher passphrase.
 * The passphrase is randomly generated on the first launch and then persisted,
 * encrypted at rest, inside [EncryptedSharedPreferences] backed by the Android Keystore.
 */
internal object DatabaseKeyManager {

    private const val PREF_FILE = "db_security_prefs"
    private const val KEY_PASSPHRASE = "db_passphrase"

    fun getOrCreateKey(context: Context): ByteArray {
        val masterKey = MasterKey(context = context)
        val prefs = EncryptedSharedPreferences.create(
            context,
            PREF_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        val existing = prefs.getString(KEY_PASSPHRASE, null)
        if (existing != null) {
            return Base64.decode(existing, Base64.NO_WRAP)
        }
        val key = ByteArray(32)
        SecureRandom().nextBytes(key)
        prefs.edit().putString(KEY_PASSPHRASE, Base64.encodeToString(key, Base64.NO_WRAP)).apply()
        return key
    }
}
