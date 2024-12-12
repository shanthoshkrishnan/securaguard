package com.example.securaguardapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val aadhaarEditText = findViewById<EditText>(R.id.aadharnumber)
        val phoneEditText = findViewById<EditText>(R.id.phonenumber)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<TextView>(R.id.registerButton)

        // Handle Login button click
        loginButton.setOnClickListener {
            val aadhaar = aadhaarEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (aadhaar.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else if (aadhaar.length != 12 || !aadhaar.all { it.isDigit() } || phone.length != 10 || !phone.all { it.isDigit() }) {
                Toast.makeText(this, "Invalid Aadhaar or Phone Number", Toast.LENGTH_SHORT).show()
            } else {
                // Navigate to OTP Activity
                val intent = Intent(this, OtpActivity::class.java).apply {
                    putExtra("AadhaarNumber", aadhaar)
                    putExtra("PhoneNumber", phone)
                }
                startActivity(intent)
                finish() // Close LoginActivity
            }
        }

        // Handle Register button click
        registerButton.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
    }
}
