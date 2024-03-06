package com.example.appcine.ui.perfil

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.appcine.R
import com.example.appcine.databinding.FragmentUserBinding
import com.example.appcine.ui.editUser.EditUser

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

        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "")
        val firstName = sharedPreferences.getString("firstName", "")

        if (username != null && firstName != null) {
            println("Username: $username")
            println("First Name: $firstName")
        }

        val formattedUsername = "@$username"

        binding.textFirstName.text = firstName
        binding.textUsername.text = formattedUsername

        binding.btnEditProfile.setOnClickListener{
            val intent = Intent(activity, EditUser::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        return  view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}