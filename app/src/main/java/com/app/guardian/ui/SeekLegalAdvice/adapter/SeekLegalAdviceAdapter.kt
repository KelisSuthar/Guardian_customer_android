package com.app.guardian.ui.SeekLegalAdvice.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.guardian.R
import com.app.guardian.model.SeekLegalAdviceResp.SeekLegalAdviceResp

class SeekLegalAdviceAdapter(
    var context: Activity,
    var isIconShow: Boolean,
    var arrayList: ArrayList<SeekLegalAdviceResp>,
    var listener: onClickListeners
) :
    RecyclerView.Adapter<SeekLegalAdviceAdapter.myViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): myViewHolder {
        return myViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_seek_legal_advice, parent, false)
        )
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class myViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var rbRowKnowRight = view.findViewById<TextView>(R.id.rbRowKnowRight)
        var liEdit = view.findViewById<LinearLayout>(R.id.liEdit)
        var lldelete = view.findViewById<LinearLayout>(R.id.lldelete)
        var conIcons = view.findViewById<ConstraintLayout>(R.id.conIcons)
        fun bind(position: Int) {
            val seekLegalAdviceData = arrayList[position]

            if (isIconShow) {
                conIcons.visibility = View.VISIBLE
            } else {
                conIcons.visibility = View.GONE
            }
            rbRowKnowRight.text = seekLegalAdviceData.title
            liEdit.setOnClickListener {listener. onEditClick(position) }
            lldelete.setOnClickListener {listener. onDeleteClick(position) }
            itemView.setOnClickListener {listener. onItemClick(position) }

        }
    }

    interface onClickListeners {
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onItemClick(position: Int)
    }
}