package com.app.guardian.ui.SupportGroups

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.ui.Home.HomeActivity


class SupportGroupList : Fragment() {

    private var rootView : View ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_support_group_list, container, false)
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.support_services),
            true,
            true
        )
        return  rootView
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SupportGroupList().apply {

            }
    }
}