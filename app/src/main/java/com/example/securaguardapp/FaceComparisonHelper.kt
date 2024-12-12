package com.example.securaguardapp

import com.example.securaguardapp.KeyManager
import android.content.Context
import android.util.Log
import java.io.File
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.nio.charset.StandardCharsets
import java.util.Base64

class FaceComparisonHelper(private val context: Context) {
    private val keyManager = KeyManager(context) // Use KeyManager for key operations

    /**
     * Placeholder method to compare faces.
     * Replace with actual face comparison logic using a library or API.
     */
    fun compareFace(capturedImage: ByteArray): Boolean {
        Log.d("FaceComparisonHelper", "Comparing face...")
        // Replace with actual face recognition logic
        return true // Return true if faces match
    }

    /**
     * Encrypts the comparison result along with IMEI using the private key.
     */
    fun encryptResultWithIMEI(capturedImage: ByteArray): String {
        try {
            // Retrieve IMEI using KeyManager
            val imei = keyManager.getDeviceIMEI()

            // Combine IMEI with captured image data (you can customize this format)
            val combinedData = "$imei:${Base64.getEncoder().encodeToString(capturedImage)}"
            val encryptedData = encryptUsingPrivateKey(combinedData.toByteArray(StandardCharsets.UTF_8))

            return Base64.getEncoder().encodeToString(encryptedData)
        } catch (e: Exception) {
            Log.e("FaceComparisonHelper", "Failed to encrypt data: ${e.message}")
            return ""
        }
    }

    /**
     * Encrypts the given data using the private key from KeyManager.
     */
    private fun encryptUsingPrivateKey(data: ByteArray): ByteArray {
        try {
            // Use KeyManager to retrieve the private key and encrypt data
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            val privateKey = keyManager.getPublicKey() // Retrieve public key for encryption
            cipher.init(Cipher.ENCRYPT_MODE, privateKey)
            return cipher.doFinal(data)
        } catch (e: Exception) {
            Log.e("FaceComparisonHelper", "Encryption failed: ${e.message}")
            throw e
        }
    }

    /**
     * Deletes the captured authentication photo after it is processed.
     * @param capturedImagePath Path to the captured photo file.
     */
    fun deleteCapturedPhoto(capturedImagePath: String) {
        try {
            val file = File(capturedImagePath)
            if (file.exists()) {
                if (file.delete()) {
                    Log.d("FaceComparisonHelper", "Captured authentication photo deleted successfully.")
                } else {
                    Log.e("FaceComparisonHelper", "Failed to delete the captured authentication photo.")
                }
            } else {
                Log.w("FaceComparisonHelper", "Captured authentication photo file does not exist.")
            }
        } catch (e: Exception) {
            Log.e("FaceComparisonHelper", "Error deleting captured authentication photo: ${e.message}")
        }
    }
}
