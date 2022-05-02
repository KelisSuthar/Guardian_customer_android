package com.app.guardian.ui.SelectRole

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod
import com.app.guardian.common.extentions.gone
import com.app.guardian.databinding.ActivitySelectRoleScreenBinding
import com.app.guardian.shareddata.base.BaseActivity
import com.app.guardian.ui.Login.LoginActivity
import com.app.guardian.ui.forgot.ForgotPasswordActivity

class SelectRoleScreen : BaseActivity(),View.OnClickListener {
    lateinit var mBinding:ActivitySelectRoleScreenBinding
    var selectedRole :String = ""
    override fun getResource(): Int {
        ReusedMethod.updateStatusBarColor(this, R.color.colorPrimaryDark, 4)
        return  R.layout.activity_select_role_screen
    }

    override fun initView() {
        mBinding = getBinding()
        mBinding.headder.tvHeaderText.text = resources.getString(R.string.select_user_role)
        mBinding.headder.ivBack.gone()
    }

    override fun initObserver() {

    }

    override fun handleListener() {
        mBinding.rl1.setOnClickListener(this)
        mBinding.rl2.setOnClickListener(this)
        mBinding.rl3.setOnClickListener(this)
        mBinding.rb1.setOnClickListener(this)
        mBinding.rb2.setOnClickListener(this)
        mBinding.rb3.setOnClickListener(this)
        mBinding.btnSubmit.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when(p0?.id)
        {
            R.id.rl1->{
                changeLayout(1)
            }
            R.id.rl2->{
                changeLayout(2)
            }
            R.id.rl3->{
                changeLayout(3)
            }
            R.id.rb1->{
                changeLayout(1)
            }
            R.id.rb2->{
                changeLayout(2)
            }
            R.id.rb3->{
                changeLayout(3)
            }
            R.id.btnSubmit->{
                when {
                    mBinding.rb1.isChecked -> {
                        startActivity(
                            Intent(
                                this@SelectRoleScreen,
                                LoginActivity::class.java
                            )
                        )
                        overridePendingTransition(R.anim.rightto, R.anim.left)
                    }
                    mBinding.rb2.isChecked -> {

                    }
                    mBinding.rb3.isChecked -> {

                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun changeLayout(i: Int) {
        when (i) {
            1 -> {
                mBinding.rl1.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mBinding.rl2.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlue_2))
                mBinding.rl3.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlue_2))
                mBinding.rb1.isChecked = true
                mBinding.rb2.isChecked = false
                mBinding.rb3.isChecked = false
                mBinding.rb1.setTextColor( ContextCompat.getColor(this,R.color.white))
                mBinding.rb2.setTextColor( ContextCompat.getColor(this,R.color.black))
                mBinding.rb3.setTextColor( ContextCompat.getColor(this,R.color.black))
                mBinding.rb1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user_selected, 0, 0, 0);
                mBinding.rb2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                mBinding.rb3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                selectedRole = ""

            }
            2 -> {
                mBinding.rl1.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlue_2))
                mBinding.rl2.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mBinding.rl3.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlue_2))
                mBinding.rb1.isChecked = false
                mBinding.rb2.isChecked = true
                mBinding.rb3.isChecked = false
                mBinding.rb1.setTextColor( ContextCompat.getColor(this,R.color.black))
                mBinding.rb2.setTextColor( ContextCompat.getColor(this,R.color.white))
                mBinding.rb3.setTextColor( ContextCompat.getColor(this,R.color.black))
                mBinding.rb1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                mBinding.rb2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user_selected, 0, 0, 0);
                mBinding.rb3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
            }
            3 -> {
                mBinding.rl1.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlue_2))
                mBinding.rl2.setBackgroundColor(ContextCompat.getColor(this, R.color.lightBlue_2))
                mBinding.rl3.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                mBinding.rb1.isChecked = false
                mBinding.rb2.isChecked = false
                mBinding.rb3.isChecked = true
                mBinding.rb1.setTextColor( ContextCompat.getColor(this,R.color.black))
                mBinding.rb2.setTextColor( ContextCompat.getColor(this,R.color.black))
                mBinding.rb3.setTextColor( ContextCompat.getColor(this,R.color.white))
                mBinding.rb1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                mBinding.rb2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user, 0, 0, 0);
                mBinding.rb3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_user_selected, 0, 0, 0);

            }
        }
    }

}