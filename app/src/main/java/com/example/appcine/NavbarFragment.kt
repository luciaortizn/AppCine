package com.example.appcine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NavbarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NavbarFragment : Fragment(), NavigationBarView.OnItemSelectedListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    // Implementa la función onNavigationItemSelected
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_1 -> {
                //logica home

                return true
            }
            R.id.item_2 -> {
                //lógica favoritos

                return true
            }

            R.id.item_3 -> {
                //necesito cambiar de fragment a la vista
                val perfilFragment = PerfilFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.newPopularFilmFragment, perfilFragment)
                    .addToBackStack(null).commit()
                return true
            }
            else -> return false
        }
    }
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Código para cambiar de navegación
        val view = inflater.inflate(R.layout.fragment_navbar, container, false)

        // Obtén una referencia a tu BottomNavigationView
        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)

        // Establece el listener para la selección de elementos
        bottomNavigationView.setOnItemSelectedListener(this)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navbar, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_navbar.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NavbarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}