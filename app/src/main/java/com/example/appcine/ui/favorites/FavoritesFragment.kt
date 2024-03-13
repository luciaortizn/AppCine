package com.example.appcine.ui.favorites

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
        getData()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getData() {
        for (i in imageList.indices){}
        recyclerView.adapter = HomeAdapter(favoritesList)
    }
}