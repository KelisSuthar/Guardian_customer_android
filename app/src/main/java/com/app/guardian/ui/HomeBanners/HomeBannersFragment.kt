package com.app.guardian.ui.HomeBanners

import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.common.extentions.visible
import com.app.guardian.databinding.ActivitySignupScreenBinding
import com.app.guardian.databinding.FragmentHomeBannersBinding
import com.app.guardian.model.HomeBanners.BannerCollection
import com.app.guardian.model.specializationList.SpecializationListResp
import com.app.guardian.model.viewModels.AuthenticationViewModel
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.shareddata.base.BaseFragment
import com.app.guardian.ui.HomeBanners.Adapters.HomeBannersAdapter
import com.app.guardian.ui.User.MyVideos.adapter.MyVideoListAdapter
import com.app.guardian.utils.Config
import org.koin.android.viewmodel.ext.android.viewModel

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
    override fun getInflateResource(): Int {
        return R.layout.fragment_home_banners
    }

    override fun initView() {
        mBinding = getBinding()
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
                array,
                object : HomeBannersAdapter.onItemClicklisteners {
                    override fun onClick(position: Int) {

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
        for (i in data.indices) {
            for (j in bannerarray.indices) {
                if (bannerarray[j].user != null) {
                    if (data[i].title.toString() == bannerarray[j].user.specialization.toString()) {
                        array.add(bannerarray[j])
                    }
                }
            }
            homeBannersAdapter?.notifyDataSetChanged()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTryAgain -> {
                onResume()
            }
        }
    }

}