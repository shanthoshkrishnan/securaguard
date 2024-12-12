package com.example.securaguardapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securaguardapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecyclerViewAdapter
    private val firebaseHelper = FirebaseHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupAddUserButton()
        fetchUsersAndUpdateRecyclerView()
        handleDeepLink()
    }

    /**
     * Sets up RecyclerView with a LinearLayoutManager and an adapter.
     */
    private fun setupRecyclerView() {
        adapter = RecyclerViewAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    /**
     * Configures the Add User button click listener.
     */
    private fun setupAddUserButton() {
        binding.btnAddUser.setOnClickListener {
            val user = captureUserInput() ?: return@setOnClickListener
            addUserToFirebase(user)
        }
    }

    /**
     * Captures user input and returns a [User] instance if all fields are valid.
     * Shows a toast if validation fails.
     */
    private fun captureUserInput(): User? {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val age = binding.etAge.text.toString().toIntOrNull() ?: 0
        val gender = binding.etGender.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val aadhaarNumber = binding.etAadhaar.text.toString().trim()
        val phoneNumber = binding.etPhone.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty() || gender.isEmpty() ||
            address.isEmpty() || aadhaarNumber.isEmpty() || phoneNumber.isEmpty()
        ) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return null
        }

        return User(
            firstName = firstName,
            lastName = lastName,
            age = age,
            gender = gender,
            address = address,
            aadhaarNumber = aadhaarNumber,
            phoneNumber = phoneNumber
        )
    }

    /**
     * Adds a user to Firebase and shows appropriate feedback to the user.
     */
    private fun addUserToFirebase(user: User) {
        val userId = "USER_${System.currentTimeMillis()}"
        firebaseHelper.addUser(userId, user) { success ->
            if (success) {
                Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show()
                fetchUsersAndUpdateRecyclerView() // Refresh the list
            } else {
                Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Fetches users from Firebase and updates the RecyclerView.
     */
    private fun fetchUsersAndUpdateRecyclerView() {
        firebaseHelper.fetchUsers { users ->
            if (users.isNotEmpty()) {
                val userModels = users.map { user ->
                    UserModel(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        age = user.age,
                        gender = user.gender,
                        address = user.address,
                        aadhaarNumber = user.aadhaarNumber,
                        phoneNumber = user.phoneNumber
                    )
                }
                adapter.submitList(userModels)
            } else {
                Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Handles deep link for OTP verification.
     */
    private fun handleDeepLink() {
        val otp = intent?.data?.getQueryParameter("otp")
        if (!otp.isNullOrEmpty()) {
            Log.d("MainActivity", "Received OTP via deep link: $otp")
            verifyOTP(otp)
        }
    }

    /**
     * Verifies the OTP and proceeds to the next activity if valid.
     */
    private fun verifyOTP(otp: String) {
        Log.d("MainActivity", "Verifying OTP: $otp")
        val isOtpValid = true // Simulated OTP verification logic

        if (isOtpValid) {
            Toast.makeText(this, "OTP verified successfully", Toast.LENGTH_SHORT).show()
            startFaceCaptureActivity()
        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Starts the FaceCaptureActivity.
     */
    private fun startFaceCaptureActivity() {
        Log.d("MainActivity", "Starting FaceCaptureActivity")
        val intent = Intent(this, FaceCaptureActivity::class.java)
        startActivity(intent)
    }
}
