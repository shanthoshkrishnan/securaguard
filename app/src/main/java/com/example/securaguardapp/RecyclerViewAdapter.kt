package com.example.securaguardapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securaguardapp.databinding.ItemUserBinding

// Renamed User to UserModel to avoid redeclaration conflicts.
data class UserModel(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val gender: String,
    val address: String,
    val aadhaarNumber: String,
    val phoneNumber: String
)

class RecyclerViewAdapter :
    ListAdapter<UserModel, RecyclerViewAdapter.UserViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val userModel = getItem(position)
        holder.bind(userModel)
    }

    class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userModel: UserModel) {
            with(binding) {
                tvFirstName.text = userModel.firstName
                tvLastName.text = userModel.lastName
                tvAge.text = userModel.age.toString()
                tvGender.text = userModel.gender
                tvAddress.text = userModel.address
                tvAadhaar.text = userModel.aadhaarNumber
                tvPhone.text = userModel.phoneNumber
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem.aadhaarNumber == newItem.aadhaarNumber
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
}
