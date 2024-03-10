package com.example.appcine.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appcine.PasswordEncrypt
import com.example.appcine.R
import com.example.appcine.UserData
import com.example.appcine.databinding.ActivityRegisterBinding
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseRerence: DatabaseReference

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseRerence = firebaseDatabase.reference.child("users")

        mAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {

            val email = binding.editTextEmail.text.toString()
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextRepeatPassword.text.toString()
            val firstName = binding.editTextFirstName.text.toString()

            registerUser(email, username, firstName, password, confirmPassword)
        }

        binding.changeLogIn.setOnClickListener {
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

    }

    private fun registerUser(
        email: String,
        username: String,
        firstName: String,
        password: String,
        confirmPassword: String
    ) {
        val validationResult = validateFields(email, username, firstName, password, confirmPassword)

        if (validationResult == ValidationResult.SUCCESS) {

            //VALIDAMOS QUE EL NOMBRE DE USUARIO NO ESTÉ YA REGISTRADO
            databaseRerence.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (!dataSnapshot.exists()){
                        val id = databaseRerence.push().key

                        val userData = UserData(id, email, username, firstName, PasswordEncrypt.hashPassword(password))

                        databaseRerence.child(id!!).setValue(userData)

                        //Toast.makeText(this@Register, "SingUp Successfull", Toast.LENGTH_SHORT).show()

                        //Change interface
                        val intent = Intent(this@Register, Login::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        finish()
                    } else {
                        showUserExistsAlert()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@Register, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }

            })

        } else {
            showInvalidFieldsAlert(validationResult)
        }
    }

    private fun validateFields(email: String, username: String, firstName: String,  password: String, confirmPassword: String): Any {
        return when {
            isAnyFieldEmpty(email, username, firstName, password, confirmPassword) -> ValidationResult.EMPTY_FIELD
            containsWhiteSpace(email, username, password, confirmPassword) -> ValidationResult.WHITESPACE_IN_FIELD
            !isEmailValid(email) -> ValidationResult.INVALID_EMAIL
            !doPasswordsMatch(password, confirmPassword) -> ValidationResult.PASSWORDS_NOT_MATCH
            !isPasswordStrongEnough(password) -> ValidationResult.WEAK_PASSWORD
            else -> ValidationResult.SUCCESS
        }
    }

    private fun containsWhiteSpace(vararg fields: String): Boolean {
        return fields.any { it.contains(" ") }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    private fun isAnyFieldEmpty(vararg fields: String): Boolean {
        return fields.any { it.isBlank() }
    }

    private fun containsNumbers(vararg fields: String): Boolean {
        val regex = Regex("\\d")
        return fields.any { field ->
            regex.containsMatchIn(field)
        }
    }

    private fun doPasswordsMatch(password: String, repeatPassword: String): Boolean {
        return password == repeatPassword
    }

    private fun isPasswordStrongEnough(password: String): Boolean {
        val digitRegex = Regex("\\d")
        val upperCaseRegex = Regex("[A-Z]")
        val lowerCaseRegex = Regex("[a-z]")
        val specialCharacterRegex = Regex("[^A-Za-z0-9]")

        return password.length >= 8 &&
                digitRegex.containsMatchIn(password) &&
                upperCaseRegex.containsMatchIn(password) &&
                lowerCaseRegex.containsMatchIn(password) &&
                specialCharacterRegex.containsMatchIn(password) &&
                !password.contains(" ")
    }

    private fun showInvalidFieldsAlert(validationResult: Any) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Invalid Fields")

        val errorMessage = when (validationResult) {
            ValidationResult.EMPTY_FIELD -> "Please fill in all fields."
            ValidationResult.INVALID_EMAIL -> "Please enter a valid email address."
            ValidationResult.PASSWORDS_NOT_MATCH -> "Passwords do not match."
            ValidationResult.WEAK_PASSWORD -> "Password does not meet the minimum requirements."
            else -> "Please check your entries."
        }

        alertDialogBuilder.setMessage(errorMessage)
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showUserExistsAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("User Exists")
        alertDialogBuilder.setMessage("User already exists")
        alertDialogBuilder.setPositiveButton("OK") { _, _ ->
            // Dismiss the alert dialog if OK is pressed
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    enum class ValidationResult {
        SUCCESS,
        INVALID_EMAIL,
        EMPTY_FIELD,
        PASSWORDS_NOT_MATCH,
        WEAK_PASSWORD,
        WHITESPACE_IN_FIELD
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
            passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_24) //CAMBIAR CON EL DE NO VISIBLE
        } else {
            passwordEditText.transformationMethod = null
            passwordVisibilityToggle.setImageResource(R.drawable.ic_visibility_24)
        }

        passwordEditText.typeface = currentTypeface

        passwordEditText.setSelection(passwordEditText.text.length)
    }

    fun repeatPasswordVisibility(view: View) {
        val repeatPasswordEditText = findViewById<EditText>(R.id.editTextRepeatPassword)
        val repeatPasswordVisibilityToggle = findViewById<ImageView>(R.id.repeatPasswordVisibilityToggle)

        val isRepeatPasswordVisible = repeatPasswordEditText.transformationMethod == null

        if (isRepeatPasswordVisible) {
            repeatPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            repeatPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility_24) //CAMBIAR CON EL DE NO VISIBLE
        } else {
            repeatPasswordEditText.transformationMethod = null
            repeatPasswordVisibilityToggle.setImageResource(R.drawable.ic_visibility_24)
        }

        repeatPasswordEditText.setSelection(repeatPasswordEditText.text.length)
    }

}