package com.example.appcine.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcine.R
import com.example.appcine.databinding.FragmentHomeBinding
import com.example.appcine.ui.filmsInfo.FilmsInformation
import com.squareup.picasso.Picasso
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val client = OkHttpClient()

    private lateinit var  recyclerView: RecyclerView
    private lateinit var filmList:ArrayList<Films>
    lateinit var imageList:Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageList = arrayOf()

        recyclerView = view.findViewById(R.id.recycler_view)
        val numColumnas = 2
        // GridLayoutManager con 2 columnas
        val layoutManager = GridLayoutManager(context, numColumnas)
        // Establezco el layoutManager en el RecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        filmList = arrayListOf<Films>()
        getData()



    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getData() {

        fetchMovies()

        val homeAdapter = HomeAdapter(filmList)

        recyclerView.adapter = homeAdapter


    }

    private fun fetchMovies() {
        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/discover/movie?include_video=true&page=1&sort_by=popularity.desc")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzYzM2MDY4YjUxOTQ4MTJmODM2N2RhMWQxZmY3YjNmNyIsInN1YiI6IjY1YTBmOTkyNDQ3ZjljMDEyMjVhNjE1NiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._Y48hLg92ILHsg8MsWJR6p01JUIRrRpF_m6bgI66pbo")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Errores
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body()?.string()
                    responseData?.let {
                        // Parse JSON data
                        val jsonObject = JSONObject(it)
                        val results = jsonObject.getJSONArray("results")

                            // Process movie data
                            activity?.runOnUiThread {
                                // Update UI with movie data
                                processMovies(results)
                            }


                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun processMovies(results: JSONArray) {
        // Limitar a solo 10 películas
        val moviesToShow = minOf(results.length(), 10)

        // Iterar a través de los primeros 10 resultados de películas
        for (i in 0 until moviesToShow) {
            val movieObject = results.getJSONObject(i)
            val posterPath = movieObject.getString("poster_path")
            val movieId = movieObject.getInt("id")

            /*// Actualizar UI con el póster de la película
            val imageView = ImageView(context)

            // Cargar el póster usando la biblioteca Picasso
            Picasso.get().load("https://image.tmdb.org/t/p/w500$posterPath").into(imageView)
                **/
            filmList.add(Films(posterPath, movieId))

        }

        val homeAdapter:HomeAdapter = HomeAdapter(filmList)

        homeAdapter.setOnClickListener(object : HomeAdapter.OnClickListener {
            override fun onClick(position: Int, model: Films) {
                Toast.makeText(context, "Clic en la película", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, FilmsInformation::class.java)
                intent.putExtra("movieID", model.idPelicula)
                startActivity(intent)
            }
        })

        recyclerView.adapter = homeAdapter
        recyclerView.adapter?.notifyDataSetChanged()

    }

}
