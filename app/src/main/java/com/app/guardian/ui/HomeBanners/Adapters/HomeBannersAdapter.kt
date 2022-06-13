package com.app.guardian.ui.HomeBanners.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.model.HomeBanners.BannerCollection
import com.github.islamkhsh.CardSliderViewPager

class HomeBannersAdapter(
    val context: Context,
    val map: HashMap<String, ArrayList<BannerCollection>>,
    val listener: onItemClicklisteners
) : RecyclerView.Adapter<HomeBannersAdapter.myViewHolder>() {

    var cardSwiperAdapter: CardSwiperAdapter? = null


    open inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txt = view.findViewById<AppCompatTextView>(R.id.txtSpecialization)
        var cardSwiper = view.findViewById<CardSliderViewPager>(R.id.cardSwiper)
        open fun bindItem(position: Int) {
            cardSwiperAdapter = CardSwiperAdapter(context, map[map.keys.toList()[position]]!!,object :CardSwiperAdapter.OnItemClickListener{
                override fun onClick(position: Int, url: String) {
                    listener.onClick(position,url)
                }

            })
            cardSwiper.adapter = cardSwiperAdapter
            cardSwiperAdapter!!.notifyDataSetChanged()
            txt.text = map.keys.toList()[position]
        }
    }


    interface onItemClicklisteners {
        fun onClick(position: Int, url: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.banner_row_layout_2, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.bindItem(position)
    }

    override fun getItemCount(): Int {
        return map.size
    }


}