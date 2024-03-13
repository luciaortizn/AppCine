package com.example.appcine.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.appcine.R

class Charge : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charge)

        supportActionBar?.hide()

        // Encuentra los botones por ID
        val loginButton: AppCompatButton = findViewById(R.id.login_button)
        val registerButton: AppCompatButton = findViewById(R.id.register_button)

        loginButton.setOnClickListener {
            // Intent para abrir la actividad de inicio de sesión
            //Log.d("ChargeActivity", "Login button clicked")
            val intent = Intent(this@Charge, Login::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

        registerButton.setOnClickListener {
            // Intent para abrir la actividad de registro
            //Log.d("ChargeActivity", "Register button clicked")
            val intent = Intent(this@Charge, Register::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }
    }
}