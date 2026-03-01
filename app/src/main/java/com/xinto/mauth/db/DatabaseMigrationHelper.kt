package com.xinto.mauth.db

import android.content.Context
import android.util.Log
import net.zetetic.database.sqlcipher.SQLiteDatabase
import java.io.File

/**
 * One-time helper that converts an existing plaintext SQLite database
 * (created by versions of the app that did not use SQLCipher) into an
 * AES-256 encrypted SQLCipher database.
 *
 * The migration works as follows:
 * 1. Detect whether the file is plaintext by checking the SQLite magic header.
 * 2. Open it with SQLCipher using an empty passphrase (plaintext-compatibility mode).
 * 3. ATTACH a new temporary file as an encrypted database with the device key.
 * 4. Run `sqlcipher_export` to copy all data into the encrypted file.
 * 5. Detach, close, remove the old plaintext file (+ WAL/SHM), and rename the temp file.
 *
 * If the database does not exist or is already encrypted this function is a no-op.
 */
internal object DatabaseMigrationHelper {

    fun encryptIfPlaintext(context: Context, dbName: String, passphrase: ByteArray) {
        System.loadLibrary("sqlcipher")

        val dbFile = context.getDatabasePath(dbName)
        if (!dbFile.exists() || !isPlaintext(dbFile)) return

        val dbDir = dbFile.parentFile ?: return
        val tmpFile = File(dbDir, "$dbName.tmp")
        tmpFile.delete()

        try {
            val hexKey = passphrase.joinToString("") { "%02x".format(it) }

            // Open the existing plaintext database (empty ByteArray = no encryption)
            val db = SQLiteDatabase.openOrCreateDatabase(
                dbFile.absolutePath,
                ByteArray(0),
                null,
                null,
            )

            // Export into a new encrypted copy
            db.execSQL("ATTACH DATABASE '${tmpFile.absolutePath}' AS encrypted KEY \"x'$hexKey'\"")
            db.execSQL("SELECT sqlcipher_export('encrypted')")
            db.execSQL("DETACH DATABASE encrypted")
            db.close()

            // Delete WAL/SHM files that belong to the plaintext database
            File(dbDir, "$dbName-wal").delete()
            File(dbDir, "$dbName-shm").delete()

            // Atomically replace the plaintext database with the encrypted one
            dbFile.delete()
            tmpFile.renameTo(dbFile)
        } catch (e: Exception) {
            tmpFile.delete()
            // Migration failed. Delete the plaintext database files so Room creates a fresh
            // encrypted database on the next open. NOTE: this permanently discards any user
            // data that was in the plaintext database; the alternative is an app crash.
            Log.e("DatabaseMigration", "Failed to encrypt '$dbName' — user data will be lost", e)
            if (!dbFile.delete() && dbFile.exists())
                Log.w("DatabaseMigration", "Could not delete plaintext database file: ${dbFile.absolutePath}")
            val wal = File(dbDir, "$dbName-wal")
            if (!wal.delete() && wal.exists())
                Log.w("DatabaseMigration", "Could not delete WAL file: ${wal.absolutePath}")
            val shm = File(dbDir, "$dbName-shm")
            if (!shm.delete() && shm.exists())
                Log.w("DatabaseMigration", "Could not delete SHM file: ${shm.absolutePath}")
        }
    }

    /** Returns true when [file] begins with the well-known SQLite plaintext header. */
    private fun isPlaintext(file: File): Boolean {
        if (file.length() < 16) return false
        return try {
            file.inputStream().use { stream ->
                val header = ByteArray(16)
                stream.read(header)
                String(header, Charsets.US_ASCII).startsWith("SQLite format 3")
            }
        } catch (e: Exception) {
            false
        }
    }
}
