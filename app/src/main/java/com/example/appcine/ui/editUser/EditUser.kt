package com.example.appcine.ui.editUser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcine.R
import com.example.appcine.ui.perfil.PerfilFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditUser : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getString("uid", "")
        val username = sharedPreferences.getString("username", "")
        val firstName = sharedPreferences.getString("firstName", "")

        val editTextUser = findViewById<EditText>(R.id.editTextNombre)
        val editTextUsername = findViewById<EditText>(R.id.editTextNombreUser)
        val saveChangeButton = findViewById<ImageView>(R.id.saveChange)

        editTextUser.setText(firstName)
        editTextUsername.setText(username)

        saveChangeButton.setOnClickListener {
            val newUsername = editTextUsername.text.toString()
            val newFirstName = editTextUser.text.toString()

            if (newUsername.isNotEmpty() && newFirstName.isNotEmpty()) {
                val userRef = databaseReference.child("-NscI2nqNov0WVVuIrCj")

                val userMap = HashMap<String, Any>()
                userMap["username"] = newUsername
                userMap["firstName"] = newFirstName

                userRef.updateChildren(userMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@EditUser, "Datos actualizados", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@EditUser, PerfilFragment::class.java).apply {
                                putExtra("username", newUsername)
                                putExtra("firstName", newFirstName)
                            }
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@EditUser, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this@EditUser, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
