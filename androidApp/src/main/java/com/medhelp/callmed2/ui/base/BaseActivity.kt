package com.medhelp.callmed2.ui.base

import android.Manifest
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.material.snackbar.Snackbar
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.ui.login.LoginActivity
import com.medhelp.callmed2.utils.main.MainUtils
import com.medhelp.callmed2.utils.main.NetworkUtils
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity(), MvpView, BaseFragment.Callback {
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String?>?, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions!!, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String?): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission!!) == PackageManager.PERMISSION_GRANTED
    }

    override fun showLoading() {
        hideLoading()
        dialog = MainUtils.showLoadingDialog(this)
    }

    override fun isLoading(): Boolean {
        return dialog != null && dialog!!.isShowing
    }

    override fun hideLoading() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.cancel()
        }
    }

    private fun showSnackBar(message: String) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
        val sbView = snackbar.view
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        snackbar.setAction(resources.getString(R.string.ok)) { snackbar.dismiss() }
        snackbar.show()
    }

    override fun showError(message: String) {
        if (message != null) {
            showSnackBar(message)
        } else {
            showSnackBar(getString(R.string.some_error))
        }
    }

    override fun showError(@StringRes resId: Int) {
        showError(getString(resId))
    }

    override fun showMessage(message: String) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show()
        }
    }

    override fun showMessage(@StringRes resId: Int) {
        showMessage(getString(resId))
    }

    override fun isNetworkConnected(): Boolean {
        return NetworkUtils.isNetworkConnected(applicationContext)
    }

    override fun onFragmentAttached() {}

    override fun onFragmentDetached(tag: String?) {}

    override fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun openActivityLogin() {
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }


    override fun onDestroy() {
        destroyActivity()
        super.onDestroy()
    }

    protected abstract fun destroyActivity()

    //region permission
    protected fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    protected fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE /*,Manifest.permission.CAMERA*/),
            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.v("Разрешено использование памяти телефона")
                    if (listener != null) {
                        listener!!.permissionGranted()
                    }
                } else {
                    Timber.v("Запрещено использование памяти телефона")
                    if (listener != null) {
                        listener!!.permissionDenied()
                    }
                }
            }
        }
    }

    //endregion
    //region listener
    var listener: ListenerBaseActivity? = null

    interface ListenerBaseActivity {
        fun permissionGranted()
        fun permissionDenied()
    }

//    protected fun setListener(listener: ListenerBaseActivity?) {
//        this.listener = listener
//    }

    protected fun clearListener() {
        listener = null
    } //endregion
}