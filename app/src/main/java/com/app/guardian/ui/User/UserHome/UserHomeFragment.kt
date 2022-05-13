package com.app.guardian.ui.User.UserHome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.ui.Home.HomeActivity


class UserHomeFragment : Fragment() {

    private var rootView : View ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_user_home, container, false)

        (activity as HomeActivity).headerTextVisible("",false,false)
        return  rootView
    }

    companion object {

    }
}