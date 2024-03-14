package com.example.appcine.ui.perfil

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.appcine.R
import com.example.appcine.databinding.FragmentUserBinding
import com.example.appcine.ui.editUser.EditUser
import com.example.appcine.ui.filmsInfo.FilmsInformation
import com.example.appcine.ui.filmsInfo.MovieApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class PerfilFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val client = OkHttpClient()

    private val apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzYzM2MDY4YjUxOTQ4MTJmODM2N2RhMWQxZmY3YjNmNyIsInN1YiI6IjY1YTBmOTkyNDQ3ZjljMDEyMjVhNjE1NiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._Y48hLg92ILHsg8MsWJR6p01JUIRrRpF_m6bgI66pbo"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnEditProfile.setOnClickListener{
            val intent = Intent(activity, EditUser::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
        val movies = mutableListOf<Int>()
        val blackColor = ContextCompat.getColor(requireContext(), R.color.black)
        val pistachioColor = ContextCompat.getColor(requireContext(), R.color.pistachio)

        binding.buttonLike.setOnClickListener {
            binding.buttonLike.setTextColor(pistachioColor)
            binding.buttonWatchlist.setTextColor(blackColor)
            Log.d("BotonLike", "Le has dado al boton Like")
            val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("uid", "")

            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!).child("moviesLiked")

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.movieFavFilmas.removeAllViews()


                    snapshot.children.forEach { movieSnapshot ->
                        val movieIdString = movieSnapshot.value as String
                        val movieId = movieIdString.toInt()
                        movies.add(movieId)
                    }

                    val chunkedMovies = movies.chunked(2)
                    chunkedMovies.forEach { chunk ->
                        val rowLayout = LinearLayout(requireContext())
                        rowLayout.orientation = LinearLayout.HORIZONTAL
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        rowLayout.layoutParams = layoutParams

                        chunk.forEach { movieId ->
                            obtenerPosterPeliculaFavorita(movieId, apiKey) { posterPath ->
                                posterPath?.let {
                                    requireActivity().runOnUiThread {
                                        val imageView = ImageView(requireContext())
                                        val posterUrl = "https://image.tmdb.org/t/p/w500/$posterPath"

                                        Picasso.get().load(posterUrl).into(imageView)

                                        val layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        val margin = 2
                                        layoutParams.setMargins(margin, 0, margin, 10)
                                        imageView.layoutParams = layoutParams

                                        imageView.setOnClickListener {
                                            val intent = Intent(activity, FilmsInformation::class.java)
                                            intent.putExtra("movieID", movieId)
                                            startActivity(intent)
                                        }
                                        rowLayout.addView(imageView)
                                    }
                                }
                            }
                        }
                        binding.movieFavFilmas.addView(rowLayout)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("DatabaseError", "Error: ${error.message}")
                }
            })
        }

        binding.buttonWatchlist.setOnClickListener{
            binding.buttonLike.setTextColor(blackColor)
            binding.buttonWatchlist.setTextColor(pistachioColor)
            movies.clear()


        }
    }

    private fun obtenerPosterPeliculaFavorita(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/$movieId")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callback(null)
                        return
                    }

                    val responseData = response.body()?.string()
                    responseData?.let {
                        val jsonObject = JSONObject(it)
                        val posterPath = jsonObject.optString("poster_path")
                        callback(posterPath)
                    } ?: run {
                        callback(null)
                    }
                }
            }
        })
    }


    private fun getUserData() {
        val sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("uid", "")

        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java)
                val firstName = snapshot.child("firstName").getValue(String::class.java)

                if (username != null && firstName != null) {
                    val formattedUsername = "@$username"

                    binding.textFirstName.text = firstName
                    binding.textUsername.text = formattedUsername
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
