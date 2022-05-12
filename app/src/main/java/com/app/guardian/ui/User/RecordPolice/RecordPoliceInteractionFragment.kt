package com.app.guardian.ui.User.RecordPolice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R


class RecordPoliceInteractionFragment : Fragment() {
    private var rootView : View ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_record_police_interaction, container, false)

        return  rootView
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordPoliceInteractionFragment().apply {

            }
    }
}