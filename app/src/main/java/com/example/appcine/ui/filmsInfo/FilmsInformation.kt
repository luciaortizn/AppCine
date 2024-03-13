package com.example.appcine.ui.filmsInfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.appcine.R

class FilmsInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films_infomation)

        val apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzYzM2MDY4YjUxOTQ4MTJmODM2N2RhMWQxZmY3YjNmNyIsInN1YiI6IjY1YTBmOTkyNDQ3ZjljMDEyMjVhNjE1NiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._Y48hLg92ILHsg8MsWJR6p01JUIRrRpF_m6bgI66pbo"

        val movieId = intent.getIntExtra("movieID", -1)

        MovieApi.getMovieTitle(movieId, apiKey) { title ->
            runOnUiThread {
                val movieTitle: TextView = findViewById(R.id.movieTitle)
                movieTitle.text = "$title"
            }
        }
    }
}