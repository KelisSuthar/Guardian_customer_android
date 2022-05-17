package com.app.guardian.ui.BannerAds

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.app.guardian.R
import com.app.guardian.model.UserModels.HomeFrag.UserHomeBannerResp
import com.app.guardian.ui.signup.adapter.ImageAdapter
import com.bumptech.glide.Glide

class BannerAdsPager(var activity: Activity, var arrayList: ArrayList<UserHomeBannerResp>,var listener: BannerAdsPager.onItemClicklisteners) : PagerAdapter() {


    override fun getCount(): Int {
        return arrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view == `object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imgview: View? = LayoutInflater.from(activity)
            ?.inflate(R.layout.banner_row_layout, container, false)
        val myImage: ImageView? = imgview?.findViewById(R.id.ivPropertyImg) as ImageView?
        Glide.with(activity).load(arrayList[position].banner_avatar).placeholder(R.drawable.sample_banner)
            .into(
                myImage!!
            )
        container.addView(imgview)
        myImage.setOnClickListener { listener.onItemClick(position) }
        return imgview!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
    interface onItemClicklisteners {
        fun onItemClick(position: Int)
    }
}