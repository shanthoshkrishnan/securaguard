package com.example.securaguardapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)

        val firstNameField = findViewById<EditText>(R.id.firstName)
        val lastNameField = findViewById<EditText>(R.id.lastName)
        val dobField = findViewById<EditText>(R.id.dateOfBirth)
        val genderSpinner = findViewById<Spinner>(R.id.genderSpinner)
        val addressField = findViewById<EditText>(R.id.address)
        val aadhaarField = findViewById<EditText>(R.id.aadhaarNumber)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Gender Spinner setup
        val genderOptions = arrayOf("Select Gender", "Male", "Female", "Other")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter

        // Date of Birth Picker setup
        val calendar = Calendar.getInstance()
        dobField.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                    dobField.setText(sdf.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Submit Button logic
        submitButton.setOnClickListener {
            val firstName = firstNameField.text.toString().trim()
            val lastName = lastNameField.text.toString().trim()
            val dob = dobField.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()
            val address = addressField.text.toString().trim()
            val aadhaar = aadhaarField.text.toString().trim()

            when {
                firstName.isEmpty() -> showToast("First Name is required")
                lastName.isEmpty() -> showToast("Last Name is required")
                dob.isEmpty() -> showToast("Date of Birth is required")
                gender == "Select Gender" -> showToast("Please select your gender")
                address.isEmpty() -> showToast("Address is required")
                aadhaar.length != 12 || !aadhaar.all { it.isDigit() } -> {
                    showToast("Aadhaar must be a valid 12-digit number")
                }
                else -> {
                    // Get IMEI from the actual method
                    val imei = getIMEI()

                    // Proceed without showing IMEI retrieval message
                    sendUserDataToServer(firstName, lastName, dob, gender, address, aadhaar, imei)
                }
            }
        }
    }

    private fun sendUserDataToServer(
        firstName: String, lastName: String, dob: String, gender: String,
        address: String, aadhaar: String, imei: String
    ) {
        val json = """
            {
                "firstName": "$firstName",
                "lastName": "$lastName",
                "dob": "$dob",
                "gender": "$gender",
                "address": "$address",
                "aadhaar": "$aadhaar",
                "imei": "$imei"
            }
        """.trimIndent()

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url("https://your-backend-api.com/verify-user")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    runOnUiThread {
                        showToast("User verified successfully.")
                        // Navigate to FaceCaptureActivity
                        val intent = Intent(this@UserActivity, FaceActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        showToast("User verification failed. Please try again.")
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showToast("Error communicating with server: ${e.message}")
                }
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Correct implementation of getIMEI function
    private fun getIMEI(): String {
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, return a default value
            return "IMEI not available"
        }

        return try {
            when {
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q -> {
                    telephonyManager.imei ?: "IMEI not available"

                }
                else -> {
                    @Suppress("DEPRECATION")
                    telephonyManager.deviceId ?: "IMEI not available"
                }
            }
        } catch (e: Exception) {
            // Handle errors gracefully and return a default value
            "IMEI not available"
        }
    }
    private fun startFaceCaptureActivity() {
        Log.d("MainActivity", "Starting FaceCaptureActivity")
        val intent = Intent(this, FaceCaptureActivity::class.java)
        startActivity(intent)
    }
}
