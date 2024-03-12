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
        holder.rvImage.setImageResource(currentItem.dataImage)
        holder.rvTitle.text = currentItem.dataTitle
        holder.rvFavoritesIcon.setImageResource(currentItem.favoritesImage)
        //aquí gestionamos el click
    }
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvImage: ImageView = itemView.findViewById(R.id.imgFilm)
        val rvTitle: TextView = itemView.findViewById(R.id.titleFilm)
        val rvFavoritesIcon : ImageView = itemView.findViewById(R.id.favoritesFilm)

    }
}


