package com.example.securaguardapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_main)

        val otpField = findViewById<EditText>(R.id.otpEditText)
        val otpLoginButton = findViewById<Button>(R.id.loginButton)

        otpLoginButton.setOnClickListener {
            val otp = otpField.text.toString().trim()

            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            } else if (otp.length != 6) {
                Toast.makeText(this, "OTP must be 6 digits", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
