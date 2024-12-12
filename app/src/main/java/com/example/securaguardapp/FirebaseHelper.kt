package com.example.securaguardapp

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseHelper {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

    fun addUser(userId: String, user: User, callback: (Boolean) -> Unit) {
        database.child(userId).setValue(user).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    fun fetchUsers(callback: (List<User>) -> Unit) {
        val usersList = mutableListOf<User>()

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        usersList.add(it)
                    }
                }
                callback(usersList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors that might occur when fetching data
                callback(emptyList())
            }
        })
    }
}
