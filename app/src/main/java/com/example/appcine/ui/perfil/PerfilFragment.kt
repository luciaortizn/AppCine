package com.example.appcine.ui.perfil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appcine.R
import com.example.appcine.databinding.FragmentUserBinding
import com.example.appcine.ui.editUser.EditUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root

        getUserData()

        binding.btnEditProfile.setOnClickListener{
            val intent = Intent(activity, EditUser::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        return  view
    }

    private fun getUserData() {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("uid", "")

        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java)
                val firstName = snapshot.child("firstName").getValue(String::class.java)

                if (username != null && firstName != null) {
                    println("Username: $username")
                    println("First Name: $firstName")

                    val formattedUsername = "@$username"

                    binding.textFirstName.text = firstName
                    binding.textUsername.text = formattedUsername
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
