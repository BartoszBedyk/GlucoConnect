package pl.example.databasemodule.database.security

import android.content.Context
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.SecureRandom

object EncryptedKeyProvider {

    private const val SHARED_PREF_NAME = "gluco_db_secure_prefs"
    private const val ENCRYPTED_KEY = "sqlcipher_encryption_key"

    @RequiresApi(Build.VERSION_CODES.M)
    fun getOrCreateDatabasePassphrase(context: Context): ByteArray {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val prefs = EncryptedSharedPreferences.create(
            SHARED_PREF_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val existingKey = prefs.getString(ENCRYPTED_KEY, null)
        Log.d("EncryptedKeyProvider", "Existing key: $existingKey")

        if (existingKey != null) {
            val decoded = Base64.decode(existingKey, Base64.DEFAULT)
            Log.d("EncryptedKeyProvider", "Decoded key size: ${decoded.size}")
            return decoded
        } else {
            val newKey = ByteArray(32)
            SecureRandom().nextBytes(newKey)
            prefs.edit {
                putString(ENCRYPTED_KEY, Base64.encodeToString(newKey, Base64.DEFAULT))
            }
            Log.d("EncryptedKeyProvider", "Generated new key: ${Base64.encodeToString(newKey, Base64.DEFAULT)}")
            return newKey
        }
    }
}