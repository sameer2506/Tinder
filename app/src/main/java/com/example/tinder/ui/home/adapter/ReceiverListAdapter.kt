package com.example.tinder.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.tinder.R
import com.example.tinder.model.ReceiverListData

class ReceiverListAdapter(
    private val list: ArrayList<ReceiverListData>,
    private val listener: OnItemClickSelect
) : RecyclerView.Adapter<ReceiverListAdapter.MyView>() {

    inner class MyView(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textView: TextView = view
            .findViewById(R.id.tv_data)

        val card: CardView = view
            .findViewById(R.id.receiver_list_card)

        init {
            card.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            val data = list[position]
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(data)
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyView {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.receiver_list,
                parent,
                false
            )
        return MyView(itemView)
    }

    override fun onBindViewHolder(
        holder: MyView,
        position: Int
    ) {
        holder.textView.text = list[position].name

        if (position % 2 == 0) {
            holder.card.setBackgroundColor(Color.GRAY)
        } else {
            holder.card.setBackgroundColor(Color.MAGENTA)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickSelect {
        fun onItemClick(list: ReceiverListData)
    }




}