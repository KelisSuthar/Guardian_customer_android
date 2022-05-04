package com.app.guardian.ui.signup

import android.view.View
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.databinding.ActivitySignupScreenBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.signup.adapter.ImageAdapter

class SignupScreen : BaseActivity(), View.OnClickListener {
    lateinit var  mBinding:ActivitySignupScreenBinding
    var imageAdapter: ImageAdapter? = null
    var images = ArrayList<String>()
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return R.layout.activity_signup_screen
    }

    override fun initView() {
    mBinding = getBinding()
        setAdaper()
    }

    private fun setAdaper() {
        mBinding.rvImage.adapter = null
        imageAdapter = ImageAdapter(this,images,object :ImageAdapter.onItemClicklisteners{
            override fun onCancelCick(position: Int) {
                images.removeAt(position)
            }

        })
        mBinding.rvImage.adapter = imageAdapter
    }


    override fun initObserver() {

    }

    override fun handleListener() {

    }

    override fun onClick(p0: View?) {
        when(p0?.id){

        }
    }

}