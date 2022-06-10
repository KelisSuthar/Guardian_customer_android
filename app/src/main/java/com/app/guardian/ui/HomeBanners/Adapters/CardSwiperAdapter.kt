package com.app.guardian.ui.HomeBanners.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.model.HomeBanners.BannerCollection
import com.github.islamkhsh.CardSliderAdapter


class CardSwiperAdapter(private val array: ArrayList<BannerCollection>) :
    CardSliderAdapter<CardSwiperAdapter.viewHolder>() {

    override fun getItemCount() = array.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.banner_row_layout_3, parent, false)
        return viewHolder(view)
    }

    override fun bindVH(holder: viewHolder, position: Int) {
        holder.bind(position)
    }

    class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {

        }
    }
}