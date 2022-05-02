package com.app.guardian.shareddata.base
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.app.guardian.R
import com.app.guardian.common.ReusedMethod.Companion.displayMessage
import com.app.guardian.common.extentions.isVisible
import com.app.guardian.shareddata.BaseView

import org.jetbrains.annotations.NotNull


abstract class BaseFragment : Fragment(),

    BaseView {
    var dialog: Dialog? = null
    /**
     * to get Fragment resource file
     */
    @LayoutRes
    abstract fun getInflateResource(): Int
    /**
     * to set fragment option menu
     */
    protected open fun hasOptionMenu(): Boolean = false
    /**
     * to display error message
     */
//    abstract fun displayMessage(message: String)
    /**
     * to initialize variables
     */
    abstract fun initView()
    /**
     * to call API or bind adapter
     */
    abstract fun postInit()
    /**
     * to define all listener
     */
    abstract fun handleListener()
    //    abstract fun initProgressBar()
    abstract fun initObserver()
    //    abstract fun showLoadingIndicator(isShow: Boolean)
    var isInternetConnected: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initProgressBar()
        initObserver()
    }
    private lateinit var binding: ViewDataBinding
    fun initProgressBar() {

        dialog = Dialog(requireContext())
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent);
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCancelable(false)
    }
    fun showLoadingIndicator(isShow: Boolean) {

        isVisible(isShow, dialog)
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getInflateResource(), container, false)
        setHasOptionsMenu(hasOptionMenu())
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        handleListener()
        postInit()
    }
    fun setTitle(@NotNull title: String) {
        (context as BaseActivity).title = title
    }
    @Suppress("UNCHECKED_CAST")
    @NonNull
    protected fun <T : ViewDataBinding> getBinding(): T {
        return binding as T
    }
    override fun onUnknownError(error: String?) {
        error?.let {
            displayMessage(requireActivity(),error)
        }
    }
    override fun internalServer() {
        displayMessage(requireActivity(),getString(R.string.text_error_internal_server))
    }
    override fun onTimeout() {
        displayMessage(requireActivity(),getString(R.string.text_error_timeout))
    }
    override fun onNetworkError() {
        displayMessage(requireActivity(),getString(R.string.text_error_network))
    }
    override fun onConnectionError() {
        displayMessage(requireActivity(),getString(R.string.text_error_connection))
    }
}



