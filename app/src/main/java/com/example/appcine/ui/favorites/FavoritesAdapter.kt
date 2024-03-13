package com.example.appcine.ui.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcine.R
import com.example.appcine.ui.home.Films
import com.example.appcine.ui.home.HomeAdapter
import com.squareup.picasso.Picasso

class FavoritesAdapter(private val dataList:ArrayList<Films>): RecyclerView.Adapter<FavoritesAdapter.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        //aquí se selecciona el layout de items que es igual al de las cards de home fragment
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.films_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        Picasso.get().load("https://image.tmdb.org/t/p/w500${currentItem.dataImage}").into(holder.rvImage)
        //holder.rvImage.setImageResource(currentItem.dataImage)
        //aquí gestionamos el click
    }
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvImage: ImageView = itemView.findViewById(R.id.imgFilm)
    }
}


