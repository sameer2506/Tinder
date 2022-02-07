package com.example.tinder.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.tinder.R
import com.squareup.picasso.Picasso

class ImageAdapter(
    private val list: ArrayList<String>
) :
    RecyclerView.Adapter<ImageAdapter.MyView>() {

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view
            .findViewById(R.id.img)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyView {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.image_adapter,
                parent,
                false
            )
        return MyView(itemView)
    }

    override fun onBindViewHolder(
        holder: MyView,
        position: Int
    ) {
        Picasso.get().load(list[position]).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}