package com.example.securaguardapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyProperties
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.X509Certificate
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class KeyManager(private val context: Context) {

    companion object {
        private const val KEY_ALIAS = "SecureCameraKey"
        private const val KEY_EXPIRATION_DAYS = 365 // 1 year validity
        private const val AES_ALGORITHM = "AES/CBC/PKCS5Padding"
        private const val RSA_ALGORITHM = "RSA/ECB/PKCS1Padding"
        private const val RSA_MAX_SIZE = 245 // Maximum data size for RSA encryption with a 2048-bit key
    }

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("Users")
    }

    init {
        ensureKeyValidity()
    }

    private fun ensureKeyValidity() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val existingKey = keyStore.getCertificate(KEY_ALIAS)
        if (existingKey == null || isKeyExpired((existingKey as X509Certificate).notAfter)) {
            generateKeyPair()
        }
    }

    private fun isKeyExpired(expirationDate: Date): Boolean {
        return Date().after(expirationDate)
    }

    private fun generateKeyPair() {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")

        val builder = android.security.keystore.KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .setKeyValidityEnd(Date(System.currentTimeMillis() + KEY_EXPIRATION_DAYS * 24L * 60L * 60L * 1000L))

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                builder.setIsStrongBoxBacked(true)
            }
        } catch (e: Exception) {
            // Fallback to default TEE-backed key if StrongBox is unavailable
        }

        keyPairGenerator.initialize(builder.build())
        keyPairGenerator.generateKeyPair()
    }

    fun getPublicKey(): PublicKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        return entry.certificate.publicKey
    }

    private fun getPrivateKey(): PrivateKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        return entry.privateKey
    }

    fun encryptFaceData(faceData: ByteArray): Pair<ByteArray, ByteArray> {
        val aesKey = generateAESKey()
        val encryptedFaceData = encryptWithAES(faceData, aesKey)
        val encryptedAESKey = encryptWithRSA(aesKey)
        return Pair(encryptedFaceData, encryptedAESKey)
    }

    private fun encryptWithRSA(aesKey: SecretKey): ByteArray {
        val publicKey = getPublicKey()
        val cipher = Cipher.getInstance(RSA_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val aesKeyBytes = aesKey.encoded
        if (aesKeyBytes.size > RSA_MAX_SIZE) {
            throw IllegalArgumentException("AES key size exceeds RSA max encryptable size")
        }

        return cipher.doFinal(aesKeyBytes)
    }

    private fun encryptWithAES(data: ByteArray, aesKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)

        val encryptedData = cipher.doFinal(data)
        val iv = cipher.iv
        return iv + encryptedData
    }

    private fun decryptAESKey(encryptedAESKey: ByteArray): SecretKey {
        val privateKey = getPrivateKey()
        val cipher = Cipher.getInstance(RSA_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedAESKey = cipher.doFinal(encryptedAESKey)

        return SecretKeySpec(decryptedAESKey, "AES")
    }

    fun decryptFaceData(encryptedFaceData: ByteArray, encryptedAESKey: ByteArray): ByteArray {
        val aesKey = decryptAESKey(encryptedAESKey)
        return decryptWithAES(encryptedFaceData, aesKey)
    }

    private fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    private fun decryptWithAES(encryptedData: ByteArray, aesKey: SecretKey): ByteArray {
        val iv = encryptedData.copyOfRange(0, 16)
        val encryptedFaceData = encryptedData.copyOfRange(16, encryptedData.size)

        val cipher = Cipher.getInstance(AES_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, aesKey, javax.crypto.spec.IvParameterSpec(iv))

        return cipher.doFinal(encryptedFaceData)
    }

    fun getDeviceIMEI(): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, return a default value or an empty string
            return "IMEI not available"
        }

        return try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    telephonyManager.imei ?: "IMEI not available"
                }
                else -> {
                    @Suppress("DEPRECATION")
                    telephonyManager.deviceId ?: "IMEI not available"
                }
            }
        } catch (e: Exception) {
            // Catch any exception and return a default message
            "IMEI not accessible"
        }
    }

    fun saveIMEIToFirebase(userId: String) {
        val imei = getDeviceIMEI()
        // Proceed to save IMEI (even if it's a default message or "IMEI not available")
        database.child(userId).child("imei").setValue(imei)
            .addOnSuccessListener {
                println("IMEI saved successfully for user $userId")
            }
            .addOnFailureListener {
                println("Failed to save IMEI: ${it.message}")
            }
    }

}

