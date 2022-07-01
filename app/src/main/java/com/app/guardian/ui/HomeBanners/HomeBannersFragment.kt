package com.app.guardian.ui.HomeBanners

import android.icu.text.Bidi.invertMap
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.FragmentHomeBannersBinding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.Home.HomeActivity
import com.app.guardian.ui.HomeBanners.Adapters.HomeBannersAdapter
import com.app.guardian.utils.Config
import com.google.android.gms.common.util.MapUtils
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [HomeBannersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeBannersFragment(var bannerarray: ArrayList<BannerCollection>) : BaseFragment(),
    View.OnClickListener {
    private val mViewModel: AuthenticationViewModel by viewModel()
    lateinit var mBinding: FragmentHomeBannersBinding
    var homeBannersAdapter: HomeBannersAdapter? = null
    var array = ArrayList<BannerCollection>()
    val hashMapBannerColloection = HashMap<String, ArrayList<BannerCollection>>()
    var reverse_map = HashMap<String, ArrayList<BannerCollection>>()
    override fun getInflateResource(): Int {
        return R.layout.fragment_home_banners
    }

    override fun initView() {
        mBinding = getBinding()
        (activity as HomeActivity).bottomTabVisibility(false)
        (activity as HomeActivity).headerTextVisible(
            requireActivity().resources.getString(R.string.home_banners),
            isBackButtonVisible = true,
            isHeaderVisible = true
        )
    }

    override fun onResume() {
        super.onResume()
        mBinding.nodataBanner.gone()
        mBinding.noInternetBanner.llNointernet.gone()
        mBinding.rvBanners.visible()
        callApi()
        setAdapter()
    }

    private fun setAdapter() {
        homeBannersAdapter =
            HomeBannersAdapter(
                requireContext(),
                reverse_map,
                object : HomeBannersAdapter.onItemClicklisteners {
                    override fun onClick(position: Int, url: String) {
                        ReusedMethod.redirectUrl(requireActivity(), url)
                    }

                })
        mBinding.rvBanners.adapter = homeBannersAdapter
    }

    private fun callApi() {
        if (ReusedMethod.isNetworkConnected(requireContext())) {
            mViewModel.getSpecializationList(true, context as BaseActivity)
        } else {
            mBinding.nodataBanner.gone()
            mBinding.noInternetBanner.llNointernet.visible()
            mBinding.rvBanners.gone()
        }
    }

    override fun postInit() {

    }

    override fun handleListener() {
        mBinding.noInternetBanner.btnTryAgain.setOnClickListener(this)
    }

    override fun initObserver() {
        //SPECIALIZATION LIST RESP
        mViewModel.getSpecializationListResp().observe(this) { response ->
            response?.let { requestState ->
                showLoadingIndicator(requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->

                        if (it.status) {
                            setData(data)
                        } else {

                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Config.NETWORK_ERROR ->
                            ReusedMethod.displayMessage(
                                requireActivity(),
                                getString(R.string.text_error_network)
                            )

                        Config.CUSTOM_ERROR ->
                            errorObj.customMessage
                                ?.let {}
                    }
                }
            }
        }
    }

    private fun setData(data: MutableList<SpecializationListResp>) {

//        for (i in data.indices) {
//            for (j in bannerarray.indices) {
//                if (bannerarray[j].user != null) {
//                    if (data[i].title.toString() == bannerarray[j].user.specialization.toString()) {
//                        array.add(bannerarray[j])
//                        hashMapBannerColloection[data[i].title] = bannerarray[j]
//                    }
//                }
//
//            }

        hashMapBannerColloection.clear()

        for (apiData in bannerarray.indices) {
            if (bannerarray[apiData].user != null) {

                if (hashMapBannerColloection.containsKey(bannerarray[apiData].user.specialization)) {
                    hashMapBannerColloection[bannerarray[apiData].user.specialization]?.add(
                        bannerarray[apiData]
                    )
                } else {
                    val listBannerData: ArrayList<BannerCollection> = arrayListOf()

                    val getHasMapKey = hashMapBannerColloection.keys
                    if (getHasMapKey.isNotEmpty()) {
                        listBannerData.clear()
                        Log.e("HashBanner_collection", "${apiData.toString()}")

                    }
                    listBannerData.add(bannerarray[apiData])
                    hashMapBannerColloection[bannerarray[apiData].user.specialization] =
                        listBannerData

                }
            }
        }

        Log.e("HashBanner_collection", "${hashMapBannerColloection.size}")
        Log.e("HashBanner_collection", "${hashMapBannerColloection.keys.toString()}")
//        println("HashBanner_collection $hashMapBannerColloection")
        val alKeys: List<String> = ArrayList<String>(hashMapBannerColloection.keys)

        Collections.reverse(alKeys)

        for (strKey: String in alKeys) {
            println(
                "Key : " + strKey + "\t\t"
                        + "Value : "
                        + hashMapBannerColloection.get(strKey)
            )
            reverse_map.put(strKey, hashMapBannerColloection.get(strKey)!!)
        }

        Log.i("THIS_APP", reverse_map.toString())
        Log.e("HashBanner_collection", "${hashMapBannerColloection.size}")
        homeBannersAdapter?.notifyDataSetChanged()

//        println("THIS_APP${hashMapBannerColloection["Constitutional Law"]}")

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

}