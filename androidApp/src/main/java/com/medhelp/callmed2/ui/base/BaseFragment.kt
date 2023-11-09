package com.medhelp.callmed2.ui.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.medhelp.callmed2.utils.main.MainUtils

abstract class BaseFragment : Fragment(), MvpView {
    var baseActivity: BaseActivity? = null
        private set
    var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            val activity = context
            baseActivity = activity
            activity.onFragmentAttached()
        }
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun showLoading() {
        hideLoading()
        dialog = MainUtils.showLoadingDialog(this.context)
    }

    override fun isLoading(): Boolean {
        return dialog != null && dialog!!.isShowing
    }

    override fun hideLoading() {
        if (dialog != null && dialog!!.isShowing) {
            try {
                dialog!!.cancel()
                dialog  = null
            } catch (e: Exception) {
            }
        }
    }

    override fun showError(message: String) {
        if (baseActivity != null) {
            baseActivity!!.showError(message)
        }
    }

    override fun showError(@StringRes resId: Int) {
        if (baseActivity != null) {
            baseActivity!!.showError(resId)
        }
    }

    override fun showMessage(message: String) {
        if (baseActivity != null) {
            baseActivity!!.showMessage(message)
        }
    }

    override fun showMessage(@StringRes resId: Int) {
        if (baseActivity != null) {
            baseActivity!!.showMessage(resId)
        }
    }

    override fun isNetworkConnected(): Boolean {
        return baseActivity != null && baseActivity!!.isNetworkConnected
    }

    override fun hideKeyboard() {
        if (baseActivity != null) {
            baseActivity!!.hideKeyboard()
        }
    }

    override fun openActivityLogin() {
        if (baseActivity != null) {
            baseActivity!!.openActivityLogin()
        }
    }

    protected abstract fun setUp(view: View)
    override fun onDestroy() {
        onDestroyB()
        super.onDestroy()
    }

    protected abstract fun onDestroyB()
    interface Callback {
        fun onFragmentAttached()
        fun onFragmentDetached(tag: String?)
    }

    protected abstract fun onStartSetStatusFragment(status: Int)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onStartSetStatusFragment(CREATE_VIEW)
        setUp(view)
    }

    override fun onResume() {
        onStartSetStatusFragment(RESUMED)
        super.onResume()
    }

    //    @Override
    //    public void onPause() {
    //        super.onPause();
    //    }
    //    @Override
    //    public void onDestroyView() {
    //       // onStartSetStatusFragment(STOP);
    //        super.onDestroyView();
    //    }
    override fun onStop() {
        onStartSetStatusFragment(STOP)
        super.onStop()
    }

    companion object {
        const val CREATE_VIEW = 0
        const val RESUMED = 1
        const val STOP = 2
    }
}