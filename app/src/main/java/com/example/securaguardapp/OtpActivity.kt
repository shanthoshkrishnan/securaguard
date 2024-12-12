package com.example.securaguardapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val otpEditText = findViewById<EditText>(R.id.otpEditText)
        val submitButton = findViewById<Button>(R.id.submitOtpButton)

        val aadhaar = intent.getStringExtra("AadhaarNumber")
        val phone = intent.getStringExtra("PhoneNumber")

        submitButton.setOnClickListener {
            val otp = otpEditText.text.toString().trim()

            if (otp.isEmpty() || otp.length != 6) {
                Toast.makeText(this, "Enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show()
            } else {
                // Simulate OTP validation (replace with actual OTP validation logic)
                val isOtpValid = otp == "123456" // Replace with actual OTP validation logic

                if (isOtpValid) {
                    Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()

                    // Check if the user is already registered and go directly to FaceActivity
                    val sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    val isUserRegistered = sharedPreferences.getBoolean("isUserRegistered", false)

                    if (isUserRegistered) {
                        // User is registered, go to FaceActivity (user account page)
                        val intent = Intent(this, FaceActivity::class.java).apply {
                            putExtra("AadhaarNumber", aadhaar)
                            putExtra("PhoneNumber", phone)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        // Navigate to UserActivity if the user is not registered
                        val intent = Intent(this, UserActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
