package com.example.appcine.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcine.R
import com.example.appcine.databinding.FragmentFavoritesBinding
import com.example.appcine.ui.home.Films
import com.example.appcine.ui.home.HomeAdapter

class FavoritesFragment : Fragment() {

    private lateinit var  recyclerView: RecyclerView
    private lateinit var favoritesList:ArrayList<Films>
    lateinit var imageList:Array<Int>
    lateinit var titleList:Array<String>
    lateinit var favoriteIconList:Array<Int>
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
        imageList = arrayOf(
            R.drawable.ic_add_a_photo_24,
        )
        titleList = arrayOf(
            "Wonka"

        )
        favoriteIconList = arrayOf(
            R.drawable.baseline_favorite_24,
        )

        recyclerView = view.findViewById(R.id.recycler_view_favorites)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        favoritesList = arrayListOf<Films>()
        getData()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getData() {
        for (i in imageList.indices){
            val film= Films(imageList[i], titleList[i], favoriteIconList[i] )
            //filtra por icono pero debería filtrar también por usuario
            if(film.favoritesImage == R.drawable.baseline_favorite_24) {
                favoritesList.add(film)
            }
        }
        recyclerView.adapter = HomeAdapter(favoritesList)
    }
}