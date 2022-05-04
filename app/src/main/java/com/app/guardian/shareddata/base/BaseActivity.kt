package com.app.guardian.shareddata.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.guardian.R
import com.app.guardian.common.CustomProgressDialog
import com.app.guardian.common.LocaleUtils
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.extentions.isVisible
import com.app.guardian.shareddata.BaseView


import org.jetbrains.annotations.NotNull
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity(), BaseView {
    var dialog: Dialog? = null

    @LayoutRes
    abstract fun getResource(): Int

    private lateinit var binding: ViewDataBinding
    private var progress: CustomProgressDialog? = null
    abstract fun initView()
    abstract fun initObserver()

    //    abstract fun initProgressBar()
//    abstract fun showLoadingIndicator(isShow: Boolean)
//    abstract fun displayMessage(message: String)
    abstract fun handleListener()
    var isInternetConnected: Boolean = true


    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        /* when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
             Configuration.UI_MODE_NIGHT_YES ->{
                 AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
             }
             Configuration.UI_MODE_NIGHT_NO ->{
                 AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
             }

         }*/
        super.onCreate(savedInstanceState)
        setView(getResource(), savedInstanceState)
    }

    private fun setView(@LayoutRes layoutId: Int, savedInstanceState: Bundle?) {
        try {
            binding = DataBindingUtil.setContentView(this, layoutId)
            initProgressBar()
            initObserver()
            initView()
            handleListener()

        } catch (e: Exception) {
            Timber.e(this.localClassName, e.printStackTrace())
            // resToast(e.message!!)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.leftto, R.anim.right)
    }

    fun initProgressBar() {
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCancelable(false)
    }

    fun showLoadingIndicator(isShow: Boolean) {

        isVisible(isShow, dialog)
    }


    protected fun <T : ViewDataBinding> getBinding(): T {
        @Suppress("UNCHECKED_CAST")
        return binding as T
    }

    private lateinit var mToolbar: Toolbar

    @SuppressLint("RestrictedApi")
    protected fun setToolbar(
        @NotNull toolbar: Toolbar, @NotNull title: String, isBackEnabled: Boolean = false,
        backgroundColor: Int = R.color.colorPrimaryDark
    ) {
        this.mToolbar = toolbar
        super.setSupportActionBar(toolbar)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, backgroundColor))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title = title

        toolbar.title = title

        if (isBackEnabled) {
            supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
            //if (backgroundColor == R.color.white)
            // toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    fun setTitle(@NotNull title: String) {
        this.mToolbar.title = title
    }


    fun setNavigationIcon(navigationIconResId: Int) {
        if (::mToolbar.isInitialized) {
            mToolbar.setNavigationIcon(navigationIconResId)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    fun changeFragment(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        layout: Int,
        addToBackStack: Boolean = false
    ) {
        fragmentManager.beginTransaction().replace(layout, fragment, fragment::class.java.name)
            .commit()
    }

    fun checkFragmentVisible(fragmentManager: FragmentManager, fragmentName: String): Boolean {
        return fragmentManager.findFragmentByTag(fragmentName)?.isVisible ?: false
    }

    override fun onUnknownError(error: String?) {
        error?.let {
            Timber.d("Base Activity Unknown error $error")
            displayMessage(this, error)
        }
    }

    override fun internalServer() {
        Timber.d("Base Activity API Internal server")
        displayMessage(this, getString(R.string.text_error_internal_server))
    }

    override fun onTimeout() {
        Timber.d("Base Activity API Timeout")
        displayMessage(this, getString(R.string.text_error_timeout))
    }

    override fun onNetworkError() {
        Timber.d("Base Activity network error")
        displayMessage(this, getString(R.string.text_error_network))
    }

    override fun onConnectionError() {
        Timber.d("Base Activity internet issue")
        displayMessage(this, getString(R.string.text_error_connection))
    }

    override fun attachBaseContext(newBase: Context?) {
        //super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
        super.attachBaseContext(LocaleUtils.setLocale(newBase!!, "en"))
    }

/* override fun autoLogout() {
     mUserHolder?.clearData()
     val intent = Intent(this, SignInActivity::class.java)
     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
             or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
     startActivity(intent)
 }*/

}