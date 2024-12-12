package com.example.securaguardapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity

class FaceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_main)  // Shows activity_face_main.xml

        // Find the capture button by its ID
        val captureButton: Button = findViewById(R.id.button)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        // Set up a click listener to start FaceCaptureActivity
        captureButton.setOnClickListener {
            val intent = Intent(this, FaceCaptureActivity::class.java) // Start FaceCaptureActivity
            startActivity(intent)
        }
        // Handle Logout Button click
        logoutButton.setOnClickListener {
            // Clear user session
            val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            // Navigate back to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startCameraActivity() {
        // Implement logic to connect with the device's camera for face capture
        Toast.makeText(this, "Starting camera for face capture...", Toast.LENGTH_SHORT).show()

        // Example: Navigate to CameraActivity (if implemented separately)
        // val intent = Intent(this, CameraActivity::class.java)
        // startActivity(intent)
    }
}

