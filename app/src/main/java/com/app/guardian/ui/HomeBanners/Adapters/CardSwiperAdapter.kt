package com.app.guardian.ui.HomeBanners.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.common.extentions.loadImage
import com.app.guardian.model.HomeBanners.BannerCollection
import com.bumptech.glide.Glide
import com.github.islamkhsh.CardSliderAdapter


class CardSwiperAdapter(val context: Context, private val array: ArrayList<BannerCollection>) :
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

    inner class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivBannerImg = view.findViewById<AppCompatImageView>(R.id.ivBannerImg)
        fun bind(position: Int) {
            Glide.with(context)
                .load(array[position].banner_avatar)
                .placeholder(R.drawable.sample_banner)
                .into(ivBannerImg)


        }
    }
}