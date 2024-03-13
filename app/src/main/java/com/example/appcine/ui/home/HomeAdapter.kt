package com.example.appcine.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcine.R
import com.squareup.picasso.Picasso

class HomeAdapter (private val dataList:ArrayList<Films>):RecyclerView.Adapter<HomeAdapter.ViewHolderClass>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.films_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        Picasso.get().load("https://image.tmdb.org/t/p/w500${currentItem.dataImage}").into(holder.rvImage)
        //holder.rvImage.setImageResource(currentItem.dataImage)
        //aquí gestionamos el click
    }

    override fun getItemCount(): Int {
       return dataList.size
    }

    //hay otra clase viewHolder hay que juntarlas
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvImage: ImageView = itemView.findViewById(R.id.imgFilm)
    }
}