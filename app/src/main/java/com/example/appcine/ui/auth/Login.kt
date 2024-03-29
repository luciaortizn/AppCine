package com.example.appcine.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appcine.ui.common.MainActivity2
import com.example.appcine.R
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

    /**
     *
     *  Autenticación de un usuario comparando credenciales
     */
    private fun loginUser(username: String, password: String) {
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userData = userSnapshot.getValue(UserData::class.java)
                       // Si las credenciales son válidas, se inicia sesión y se almacenan datos del usuario
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
                                saveUserData(userData)
                                return
                            }
                        }
                    }
                    //no coincide la contraseña se muestra alert
                    showInvalidPasswordAlert()
                } else {
                    // el usuario no se encuentra se muestra alert
                    showInvalidUsernameAlert()
                }
            }



            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Login, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //alerta cuando el usuario deja campos en blanco
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

    //alerta cuando la contraseña es inválida
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

    //alerta cuando el nombre de usuario no es válido
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

    fun passwordVisibility(view: View) {
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val passwordVisibilityToggle = findViewById<ImageView>(R.id.passwordVisibilityToggle)

        // Guarda el tipo de letra actual
        val currentTypeface = passwordEditText.typeface

        // Determina si la contraseña es visible o no
        val isPasswordVisible = passwordEditText.transformationMethod == null

        // Cambia la visibilidad de la contraseña y el icono
        if (isPasswordVisible) {
            // Oculta la contraseña
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            passwordVisibilityToggle.setImageResource(R.drawable.ic_hidden_24) //CAMBIAR CON EL DE NO VISIBLE
        } else {
            passwordEditText.transformationMethod = null
            passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_24)
        }

        passwordEditText.typeface = currentTypeface

        passwordEditText.setSelection(passwordEditText.text.length)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    //guardar el estado (logeado)
    private fun saveLoginState(isLogged: Boolean) {
        val sharedPreferences = getSharedPreferences("myPrefs",  Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLogged", isLogged)
        editor.apply()
    }

    //guardar datos de usuario en Shared Preferences, aún con la aplicación cerrada
    private fun saveUserData(userData: UserData) {
        //clave valor
        val sharedPreferences =
            getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("uid", userData.uid)
        editor.putString("email", userData.email)
        editor.putString("username", userData.username)
        editor.putString("firstName", userData.firstName)
        editor.putString("password", userData.password)

        // Guarda la lista de películas que le gustan al usuario
        if (userData.moviesLiked != null) {
            // Convierte la lista a un String para almacenarla en Shared Preferences
            val moviesLikedString = userData.moviesLiked.joinToString(",")
            editor.putString("moviesLiked", moviesLikedString)
        }

        editor.apply()
    }
}