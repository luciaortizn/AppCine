package com.example.appcine.ui.editUser

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appcine.Charge
import com.example.appcine.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditUser : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    private val CAMERA_PERMISSION_CODE = 101
    private val TAKE_PHOTO_REQUEST_CODE = 102

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
        val textEditPhoto = findViewById<TextView>(R.id.textEditPhoto)
        val saveChangeButton = findViewById<ImageView>(R.id.saveChange)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)
        val btnEliminarCuenta = findViewById<Button>(R.id.btnEliminarCuenta)

        editTextUser.setText(firstName)
        editTextUsername.setText(username)

        textEditPhoto.setOnClickListener {
            showPhotoOptions(it)
        }

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

    fun showPhotoOptions(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.photo_options_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.takePhoto -> {
                    if (checkCameraPermission()) {
                        openCamera()
                    }
                    true
                }
                R.id.choosePhoto -> {
                    // Acción cuando el usuario elige "Elegir foto existente"
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun checkCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestCameraPermission()
            false
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            val circleImageView = findViewById<ImageView>(R.id.circle)

            // Recortar la imagen en forma circular
            val scaledBitmap = Bitmap.createScaledBitmap(imageBitmap!!, circleImageView.width, circleImageView.height, true)
            circleImageView.setImageBitmap(scaledBitmap)
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawOval(rectF, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }


}
