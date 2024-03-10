package com.example.appcine.ui.editUser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appcine.Charge
import com.example.appcine.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        val btnEliminarCuenta = findViewById<Button>(R.id.btnEliminarCuenta)

        editTextUser.setText(firstName)
        editTextUsername.setText(username)

        saveChangeButton.setOnClickListener {
            val newUsername = editTextUsername.text.toString()
            val newFirstName = editTextUser.text.toString()

            if (newUsername.isNotEmpty() && newFirstName.isNotEmpty()) {
                val userRef = databaseReference.child(userId!!) //"-NscI2nqNov0WVVuIrCj"

                databaseReference.orderByChild("username").equalTo(newUsername)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                // El nombre de usuario no está en uso, se puede guardar el cambio
                                val userMap = HashMap<String, Any>()
                                userMap["username"] = newUsername
                                userMap["firstName"] = newFirstName

                                userRef.updateChildren(userMap)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this@EditUser, "Datos actualizados", Toast.LENGTH_SHORT).show()

                                            sharedPreferences.edit().apply {
                                                putString("username", newUsername)
                                                putString("firstName", newFirstName)
                                                apply()
                                            }

                                            finish()
                                        } else {
                                            Toast.makeText(this@EditUser, "Error al actualizar", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                // El nombre de usuario está en uso
                                Toast.makeText(this@EditUser, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Manejar error en la consulta
                            Toast.makeText(this@EditUser, "Error al consultar la base de datos", Toast.LENGTH_SHORT).show()
                        }
                    })

            } else {
                Toast.makeText(this@EditUser, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnCerrarSesion.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this@EditUser, Charge::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

        btnEliminarCuenta.setOnClickListener{
            val userRef = databaseReference.child(userId!!)
            userRef.removeValue()

            sharedPreferences.edit().clear().apply()

            val intent = Intent(this@EditUser, Charge::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

    }
}
