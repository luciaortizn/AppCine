package com.example.appcine

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.appcine.R

class Splash : AppCompatActivity() {
    private val splashTime: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        Handler().postDelayed({
            // Verificar si el usuario ya ha iniciado sesión
            if (isLoggedIn()) {
                val intent = Intent(this@Splash, MainActivity2::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@Splash, Charge::class.java)
                startActivity(intent)
            }
        }, splashTime)
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLogged", false)
    }
}