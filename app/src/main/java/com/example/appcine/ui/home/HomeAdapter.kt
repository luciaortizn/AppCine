package com.example.appcine.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.appcine.R
import com.squareup.picasso.Picasso

class HomeAdapter (private val dataList:ArrayList<Films>):RecyclerView.Adapter<HomeAdapter.ViewHolderClass>(){
    private var onClickListener: OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.films_list_item, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        Picasso.get().load("https://image.tmdb.org/t/p/w500${currentItem.dataImage}").into(holder.rvImage)


        holder.itemView.setOnClickListener{

            if (onClickListener != null){
                onClickListener!!.onClick(position, currentItem)
            }
          //  onItemClick?.invoke(currentItem)

        }

    }
    //enlaza el onclicklistener
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener


    }
    override fun getItemCount(): Int {
       return dataList.size
    }

    //hay otra clase viewHolder hay que juntarlas
    class ViewHolderClass(itemView: View):RecyclerView.ViewHolder(itemView){
        val rvImage: ImageView = itemView.findViewById(R.id.imgFilm)
    }

    interface OnClickListener {
        fun onClick(position: Int, model: Films)
    }
}
