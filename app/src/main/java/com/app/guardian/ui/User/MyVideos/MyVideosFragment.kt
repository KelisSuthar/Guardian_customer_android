package com.app.guardian.ui.User.MyVideos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.databinding.FragmentMyVideosBinding
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.User.MyVideos.adapter.MyVideoListAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [MyVideosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyVideosFragment : BaseFragment(), View.OnClickListener {
    var myVideoListAdapter: MyVideoListAdapter? = null
    lateinit var mBinding: FragmentMyVideosBinding
    var arrayList = ArrayList<String>()
    override fun getInflateResource(): Int {
        return R.layout.fragment_my_videos

    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.contacted_history),
            false,
            false
        )

    }

    override fun onResume() {
        super.onResume()
        if (mBinding.rb1.isChecked) {

        } else {

        }
        setAdapter()
    }

    private fun setAdapter() {
        myVideoListAdapter =
            MyVideoListAdapter(requireContext(), object : MyVideoListAdapter.onItemClicklisteners {
                override fun onItemClick(position: Int?) {

                }

            })
        mBinding.rv.adapter = myVideoListAdapter
        arrayList.add("1")
        arrayList.add("2")
        arrayList.add("3")
        myVideoListAdapter!!.updateAll(arrayList)
    }

    override fun postInit() {

    }

    override fun handleListener() {

    }

    override fun initObserver() {

    }

    override fun onClick(v: View?) {

    }


}