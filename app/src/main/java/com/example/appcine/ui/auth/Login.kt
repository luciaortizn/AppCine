package com.example.appcine.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appcine.MainActivity2
import com.example.appcine.PasswordEncrypt
import com.example.appcine.R
import com.example.appcine.UserData
import com.example.appcine.databinding.ActivityLoginBinding
import com.google.firebase.database.*

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseDatabase = FirebaseDatabase.getInstance()

        databaseReference = firebaseDatabase.reference.child("users")

        binding.btnLogIn.setOnClickListener {
            val username = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (username.isBlank() || password.isBlank()) {
                showEmptyFieldsAlert()
            } else {
                loginUser(username, password)
            }
        }

        binding.changeSignIn.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }

    private fun loginUser(username: String, password: String) {
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userData = userSnapshot.getValue(UserData::class.java)

                        if (userData != null) {
                            val storedHash = userData.password
                            val enteredHash = PasswordEncrypt.hashPassword(password)

                            println("Stored Hash: $storedHash")
                            println("Entered Hash: $enteredHash")

                            if (storedHash == enteredHash) {
                                // Login successful
                                val intent = Intent(this@Login, MainActivity2::class.java)
                                startActivity(intent)
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                finish()
                                saveLoginState(true)
                                return
                            }
                        }
                    }
                    // Password doesn't match
                    showInvalidPasswordAlert()
                } else {
                    // User not found in the database
                    showInvalidUsernameAlert()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Login, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEmptyFieldsAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Empty Fields")
        alertDialogBuilder.setMessage("Please fill in all fields.")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showInvalidPasswordAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Invalid Password")
        alertDialogBuilder.setMessage("Invalid password.")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showInvalidUsernameAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Invalid Username")
        alertDialogBuilder.setMessage("Invalid username.")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun saveLoginState(isLogged: Boolean) {
        val sharedPreferences = getSharedPreferences("myPrefs",  Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLogged", isLogged)
        editor.apply()
    }
}