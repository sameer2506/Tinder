package com.example.tinder.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.tinder.R
import com.example.tinder.model.MessageData

class MessageListAdapter(
    private val list: ArrayList<MessageData>
):  RecyclerView.Adapter<MessageListAdapter.MyView>() {

    inner class MyView(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view
            .findViewById(R.id.text_view_1)

        val textView2: TextView = view
            .findViewById(R.id.text_view_2)

        val card1: CardView = view
            .findViewById(R.id.card_1)

        val card2: CardView = view
            .findViewById(R.id.card_2)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyView {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.message_list_layout,
                parent,
                false
            )
        return MyView(itemView)
    }

    override fun onBindViewHolder(
        holder: MyView,
        position: Int
    ) {
        val data = list[position]

        if (data.status == "receive") {
            holder.textView.text = data.message
            holder.card2.visibility = View.GONE
        } else {
            holder.card1.visibility = View.GONE
            holder.textView2.text = data.message
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }





}