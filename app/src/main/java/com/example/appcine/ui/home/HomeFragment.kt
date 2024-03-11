package com.example.appcine.ui.home

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
import com.example.appcine.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var  recyclerView: RecyclerView
    private lateinit var filmList:ArrayList<Films>
    lateinit var imageList:Array<Int>
    lateinit var titleList:Array<String>

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

        imageList = arrayOf(
            R.drawable.ic_add_a_photo_24,
            R.drawable.ic_add_a_photo_24,
            R.drawable.ic_add_a_photo_24,
            R.drawable.ic_add_a_photo_24,
            R.drawable.ic_add_a_photo_24

        )
        titleList = arrayOf(
            "Wonka",
            "Titanic",
            "Poor Things",
            "Oppenheimer",
            "A través de mi ventana"
        )
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        filmList = arrayListOf<Films>()
        getData()


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getData() {
        for (i in imageList.indices){
            val folder= Films(imageList[i], titleList[i])
            filmList.add(folder)
        }
        recyclerView.adapter = HomeAdapter(filmList)
    }
}