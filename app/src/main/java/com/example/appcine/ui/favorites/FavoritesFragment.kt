package com.example.appcine.ui.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcine.R
import com.example.appcine.databinding.FragmentFavoritesBinding
import com.example.appcine.ui.home.Films
import com.example.appcine.ui.home.HomeAdapter
import org.json.JSONArray

class FavoritesFragment : Fragment() {

    private lateinit var  recyclerView: RecyclerView
    private lateinit var favoritesList:ArrayList<Films>
    lateinit var imageList:Array<String>
    private var _binding: FragmentFavoritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val favoritesViewModel =
            ViewModelProvider(this).get(FavoritesViewModel::class.java)

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        favoritesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageList = arrayOf()
        val numColumnas = 2
        // GridLayoutManager con 2 columnas
        val layoutManager = GridLayoutManager(context, numColumnas)
        // Establezco el layoutManager en el RecyclerView
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        favoritesList = arrayListOf<Films>()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun processMovies(results: JSONArray) {
        // Limitar a solo 10 películas
        val moviesToShow = minOf(results.length(), 10)

        // Iterar a través de los primeros 10 resultados de películas
        for (i in 0 until moviesToShow) {
            val movieObject = results.getJSONObject(i)
            var posterPath = movieObject.getString("poster_path")
            val movieId = movieObject.getInt("id")

            /*// Actualizar UI con el póster de la película
            val imageView = ImageView(context)

            // Cargar el póster usando la biblioteca Picasso
            Picasso.get().load("https://image.tmdb.org/t/p/w500$posterPath").into(imageView)
                **/
            if(posterPath.isEmpty()|| posterPath.length<5){
                posterPath = "https://lightwidget.com/wp-content/uploads/localhost-file-not-found.jpg";
            }

            favoritesList.add(Films(posterPath, movieId))

        }
        recyclerView.adapter?.notifyDataSetChanged()

    }
}