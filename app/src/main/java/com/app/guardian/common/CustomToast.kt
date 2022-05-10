package com.app.guardian.common

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import com.app.guardian.R
import com.google.android.material.textview.MaterialTextView


object CustomToast {
    fun showToast(context: Context?, toast_title: String?, duration: Boolean, txtColor: Int, bgColor: Int, postion: Boolean) {
        /*val toast: Toast = if (duration) {
            Toast.makeText(context, toast_title, Toast.LENGTH_LONG)
        } else {
            Toast.makeText(context, toast_title, Toast.LENGTH_SHORT)
        }*/
        val toast: Toast = Toast.makeText(context, toast_title, Toast.LENGTH_SHORT)
        val li = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val toastView = li.inflate(R.layout.toast_hint_layout, null)
        val text: MaterialTextView = toastView.findViewById(R.id.txtToast)
        val ll = toastView.findViewById<LinearLayout>(R.id.ll_toast)
        // ImageView ivStatus = toastView.findViewById(R.id.ivStatus);
//        ll.setBackgroundColor(bgColor);
        text.text = toast_title
//        text.setTextColor(txtColor)
        /* if (drawable != 0) {
            ivStatus.setVisibility(View.VISIBLE);
            ivStatus.setImageResource(drawable);

        }*/
// text.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
/*if (postion) {
            toast.setGravity(Gravity.TOP, 0, 50);
        } else
            toast.setGravity(Gravity.BOTTOM, 0, 50);*/toast.setGravity(Gravity.BOTTOM, 0, 50)
        toast.view = toastView
        toast.show()
    }
}