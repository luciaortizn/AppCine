package com.example.appcine.ui.filmsInfo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.appcine.R
import com.squareup.picasso.Picasso

class FilmsInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films_infomation)

        supportActionBar?.hide()

        val apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzYzM2MDY4YjUxOTQ4MTJmODM2N2RhMWQxZmY3YjNmNyIsInN1YiI6IjY1YTBmOTkyNDQ3ZjljMDEyMjVhNjE1NiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._Y48hLg92ILHsg8MsWJR6p01JUIRrRpF_m6bgI66pbo"

        val movieId = intent.getIntExtra("movieID", -1)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val overviewTextView: TextView = findViewById(R.id.overview)
        val trailerView: TextView = findViewById(R.id.trailer)

        val buttonCastCrew: Button = findViewById(R.id.buttonCastCrew)
        val buttonGenre: Button = findViewById(R.id.buttonGenre)

        val layoutCast: LinearLayout = findViewById(R.id.layoutCast)
        val layoutCrew: LinearLayout = findViewById(R.id.layoutCrew)
        val layoutGeneros: LinearLayout = findViewById(R.id.layoutGeneros)

        backButton.setOnClickListener {
            onBackPressed()
            finish()
        }

        MovieApi.getMovieBackdrop(movieId, apiKey) { backdropPath ->
            runOnUiThread {
                backdropPath?.let {
                    val movieBackdrop: ImageView = findViewById(R.id.movieBackdrop)
                    val backdropUrl = "https://image.tmdb.org/t/p/w500/$backdropPath"
                    Picasso.get().load(backdropUrl).into(movieBackdrop)
                }
            }
        }

        MovieApi.getMoviePoster(movieId, apiKey) { posterPath ->
            runOnUiThread {
                posterPath?.let {
                    val moviePoster: ImageView = findViewById(R.id.moviePoster)
                    val posterUrl = "https://image.tmdb.org/t/p/w500/$posterPath"
                    Picasso.get().load(posterUrl).into(moviePoster)
                }
            }
        }

        MovieApi.getMovieTitle(movieId, apiKey) { title ->
            runOnUiThread {
                val movieTitle: TextView = findViewById(R.id.movieTitle)
                movieTitle.text = "$title"
            }
        }

        MovieApi.getMovieOriginalTitle(movieId, apiKey) { originalTitle ->
            runOnUiThread {
                val movieOriginalTitle: TextView = findViewById(R.id.originalTitle)
                movieOriginalTitle.text = "$originalTitle"
            }
        }

        MovieApi.getDirector(movieId, apiKey) { directorName ->
            runOnUiThread {
                val directorNameTextView: TextView = findViewById(R.id.directorName)
                directorNameTextView.text = directorName ?: "Director no encontrado"
            }
        }

        MovieApi.getReleaseYear(movieId, apiKey) { year ->
            runOnUiThread {
                val releaseYear: TextView = findViewById(R.id.releaseYear)
                releaseYear.text = "$year"
            }
        }

        MovieApi.getRuntime(movieId, apiKey) { runtime ->
            runOnUiThread {
                val time: TextView = findViewById(R.id.movieDuration)
                time.text = "$runtime min"
            }
        }

        MovieApi.getTagline(movieId, apiKey) { tagline ->
            runOnUiThread {
                val taglineTextView: TextView = findViewById(R.id.tagline)
                if (!tagline.isNullOrBlank()) {
                    taglineTextView.text = tagline.toUpperCase()
                } else {
                    // Si no hay tagline, ocultar el TextView y ajustar el margen del Overview
                    taglineTextView.visibility = View.GONE
                    val overviewTextView: TextView = findViewById(R.id.overview)
                    val layoutParams = overviewTextView.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.overview_no_tagline)
                    overviewTextView.layoutParams = layoutParams
                }
            }
        }

        MovieApi.getOverview(movieId, apiKey) { overview ->
            runOnUiThread {
                val over: TextView = findViewById(R.id.overview)
                over.text = "$overview"
            }
        }

        overviewTextView.setOnClickListener {
            if (overviewTextView.maxLines == 3) {
                overviewTextView.maxLines = Int.MAX_VALUE
                overviewTextView.ellipsize = null
            } else {
                overviewTextView.maxLines = 3
                overviewTextView.ellipsize = TextUtils.TruncateAt.END
            }
        }

        trailerView.setOnClickListener {
            MovieApi.getOfficialTrailer(movieId, apiKey) { trailerKey ->
                runOnUiThread {
                    trailerKey?.let { key ->
                        // Abrir el trailer en YouTube
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$key"))
                        startActivity(intent)
                    }
                }
            }
        }

        buttonCastCrew.setOnClickListener {
            // Muestra los layouts de cast y crew
            //Oculat el genero
            layoutCast.visibility = View.VISIBLE
            layoutCrew.visibility = View.VISIBLE
            layoutGeneros.visibility = View.GONE
        }

        MovieApi.getActors(movieId, apiKey) { actors ->
            runOnUiThread {
                actors?.let { actorList ->
                    val castLayout: LinearLayout = findViewById(R.id.castLayout)

                    // Limpiar cualquier vista previa en el layout de actores
                    castLayout.removeAllViews()

                    for (actor in actorList) {
                        val actorLinearLayout = LinearLayout(this@FilmsInformation)
                        actorLinearLayout.orientation = LinearLayout.VERTICAL
                        actorLinearLayout.gravity = Gravity.CENTER_HORIZONTAL
                        actorLinearLayout.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(0, 0, 0, 25)
                        actorLinearLayout.layoutParams = layoutParams

                        val actorImageView = ImageView(this@FilmsInformation)

                        Picasso.get().load("https://image.tmdb.org/t/p/w200/${actor.profilePath}").into(actorImageView,
                            object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    // Convertir la imagen en un BitmapDrawable
                                    val originalDrawable: Drawable = actorImageView.drawable
                                    val originalBitmap: Bitmap = (originalDrawable as BitmapDrawable).bitmap

                                    // Crear un Bitmap con un tamaño fijo
                                    val outputBitmap = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888)

                                    // Calcular el radio del círculo en función del tamaño del Bitmap
                                    val radius = Math.min(outputBitmap.width, outputBitmap.height) / 2f

                                    // Crear un Canvas y dibujar un círculo en el Bitmap
                                    val canvas = Canvas(outputBitmap)
                                    val paint = Paint().apply {
                                        isAntiAlias = true
                                        shader = BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                                    }
                                    canvas.drawCircle(outputBitmap.width / 2f, outputBitmap.height / 2f, radius, paint)

                                    // Establecer el Bitmap modificado como imagen del ImageView
                                    actorImageView.setImageBitmap(outputBitmap)
                                }

                                override fun onError(e: Exception?) {
                                    // Manejar el error, si es necesario
                                    e?.printStackTrace()
                                }
                            })

                        val actorNameTextView = TextView(this@FilmsInformation)
                        actorNameTextView.text = actor.name
                        actorNameTextView.gravity = Gravity.CENTER
                        actorNameTextView.setTextColor(ContextCompat.getColor(this@FilmsInformation, R.color.black))

                        val characterTextView = TextView(this@FilmsInformation)
                        characterTextView.text = actor.character
                        characterTextView.gravity = Gravity.CENTER
                        characterTextView.setTypeface(null, Typeface.ITALIC)
                        characterTextView.setTextColor(ContextCompat.getColor(this@FilmsInformation, R.color.black))

                        // Agregar vistas al LinearLayout del actor
                        actorLinearLayout.addView(actorImageView)
                        actorLinearLayout.addView(actorNameTextView)
                        actorLinearLayout.addView(characterTextView)

                        // Agregar el LinearLayout del actor al layout de actores
                        castLayout.addView(actorLinearLayout)
                    }
                }
            }
        }

        MovieApi.getRest(movieId, apiKey) { rest ->
            runOnUiThread {
                rest?.let { restList ->
                    val crewLayout: LinearLayout = findViewById(R.id.crewLayout)

                    // Limpiar cualquier vista previa en el layout de actores
                    crewLayout.removeAllViews()

                    for (rest in restList) {
                        val restLinearLayout = LinearLayout(this@FilmsInformation)
                        restLinearLayout.orientation = LinearLayout.VERTICAL
                        restLinearLayout.gravity = Gravity.CENTER_HORIZONTAL

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(0, 0, 0, 25)
                        restLinearLayout.layoutParams = layoutParams

                        val restImageView = ImageView(this@FilmsInformation)

                        Picasso.get().load("https://image.tmdb.org/t/p/w200/${rest.profilePath}").into(restImageView,
                            object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    // Convertir la imagen en un BitmapDrawable
                                    val originalDrawable: Drawable = restImageView.drawable
                                    val originalBitmap: Bitmap = (originalDrawable as BitmapDrawable).bitmap

                                    // Crear un Bitmap con un tamaño fijo
                                    val outputBitmap = Bitmap.createBitmap(250, 250, Bitmap.Config.ARGB_8888)

                                    // Calcular el radio del círculo en función del tamaño del Bitmap
                                    val radius = Math.min(outputBitmap.width, outputBitmap.height) / 2f

                                    // Crear un Canvas y dibujar un círculo en el Bitmap
                                    val canvas = Canvas(outputBitmap)
                                    val paint = Paint().apply {
                                        isAntiAlias = true
                                        shader = BitmapShader(originalBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                                    }
                                    canvas.drawCircle(outputBitmap.width / 2f, outputBitmap.height / 2f, radius, paint)

                                    // Establecer el Bitmap modificado como imagen del ImageView
                                    restImageView.setImageBitmap(outputBitmap)
                                }

                                override fun onError(e: Exception?) {
                                    // Manejar el error, si es necesario
                                    e?.printStackTrace()
                                }
                            })

                        val restNameTextView = TextView(this@FilmsInformation)
                        restNameTextView.text = rest.name
                        restNameTextView.gravity = Gravity.CENTER
                        restNameTextView.setTextColor(ContextCompat.getColor(this@FilmsInformation, R.color.black))

                        val jobTextView = TextView(this@FilmsInformation)
                        jobTextView.text = rest.job
                        jobTextView.gravity = Gravity.CENTER
                        jobTextView.setTypeface(null, Typeface.ITALIC)
                        jobTextView.setTextColor(ContextCompat.getColor(this@FilmsInformation, R.color.black))

                        // Agregar vistas al LinearLayout del actor
                        restLinearLayout.addView(restImageView)
                        restLinearLayout.addView(restNameTextView)
                        restLinearLayout.addView(jobTextView)

                        // Agregar el LinearLayout del actor al layout de actores
                        crewLayout.addView(restLinearLayout)
                    }
                }
            }
        }

        buttonGenre.setOnClickListener {
            //Muestra los generos
            // Oculta los layouts de cast y crew
            layoutGeneros.visibility = View.VISIBLE
            layoutCast.visibility = View.GONE
            layoutCrew.visibility = View.GONE

        }

        MovieApi.getGenre(movieId, apiKey) { genreList ->
            runOnUiThread {
                val layoutGeneros = findViewById<LinearLayout>(R.id.layoutGeneros)

                layoutGeneros.removeAllViews()

                if (genreList != null && genreList.isNotEmpty()) {
                    for (genre in genreList) {
                        val genreTextView = TextView(this@FilmsInformation)
                        genreTextView.text = genre.name.uppercase()
                        val textSizeInPixels = resources.getDimensionPixelSize(R.dimen.text_size)
                        genreTextView.textSize = textSizeInPixels.toFloat()
                        genreTextView.setTextColor(ContextCompat.getColor(this@FilmsInformation, R.color.black))
                        genreTextView.gravity = Gravity.LEFT
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(0, 0, 0, 16)
                        genreTextView.layoutParams = layoutParams

                        layoutGeneros.addView(genreTextView)
                    }
                } else {
                    // Si la lista de géneros es nula o está vacía, establecer un mensaje predeterminado
                    val textViewGeneros = TextView(this@FilmsInformation)
                    textViewGeneros.text = "No se encontraron géneros para esta película"
                    textViewGeneros.setTextColor(ContextCompat.getColor(this@FilmsInformation, R.color.black))
                    textViewGeneros.gravity = Gravity.CENTER
                    layoutGeneros.addView(textViewGeneros)
                }
            }
        }
    }
}
