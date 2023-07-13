package com.medhelp.callmed2.ui.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.ActivityLoginBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui.base.BaseActivity

class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding

    var presenter: LoginPresenter? = null

    private var username: String? = null
    private var password: String? = null

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (phone != "" ||
            binding.etPassword!!.text.toString().trim { it <= ' ' } != ""
        ) {
            outState.putString(LOGIN_KEY, username)
            outState.putString(PASSWORD_KEY, password)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = LoginPresenter(this)
        presenter!!.username
        if (savedInstanceState != null) {
            username = savedInstanceState.getString(LOGIN_KEY)
            binding.etUsername!!.setText(username)
            password = savedInstanceState.getString(PASSWORD_KEY)
            binding.etPassword!!.setText(password)
        }
        setUp()
    }

    fun updateUsernameAndPassHint(username: String?, pass: String?) {
        if (username != null && username.length > 0) {
            binding.etUsername!!.setText(username)
        }
        if (pass != null && pass.length > 0) {
            binding.etPassword!!.setText(pass)
        }
    }

    private fun cleanUserData() {
        binding.etPassword!!.setText("")
    }

    //    public boolean isNeedSave() {
    //        return chbSave.isChecked();
    //    }
    protected fun setUp() {
        binding.etPassword!!.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                userLogin()
            }
            false
        }
        binding.etUsername!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.etPassword!!.isFocusable = true
            }
            false
        }

        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            userLogin()
        }
    }

    fun openProfileActivity() {
        val intent = Intent(this@LoginActivity, MainPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun closeActivity() {
        finish()
    }


    private fun userLogin() {
        username = phone
        password = binding.etPassword!!.text.toString()
        presenter!!.onLoginClick(username, password)
        cleanUserData()
    }

    override fun onPostResume() {
        super.onPostResume()
        presenter!!.testConnections()
    }

    fun setEnable(boo: Boolean?) {
        binding.btnLogin.isEnabled = boo!!
    }

    override fun destroyActivity() {}
    fun showAlert(msg: Int, index: Int, log: String?, pass: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
            .setPositiveButton("Обновить") { dialog, which ->
                if (index == 0) {
                    presenter!!.testConnections()
                } else {
                    presenter!!.onLoginClick(log, pass)
                }
            }
            .setNegativeButton("Отмена") { dialog, which -> dialog.dismiss() }
            .show()
    }

    //    public void startBGService() {
    //        if(presenter.getShowPartMessenger()) {
    //            Intent serviceIntent = new Intent(this, ServiceStartOnBoot.class);
    //            this.startService(serviceIntent);
    //        }
    //    }
    val context: Context
        get() = this
    private val phone: String
        private get() {
            var ph = binding.etUsername!!.text.toString()
            ph = ph.substring(3)
            ph = ph.replace("\\)".toRegex(), "")
            ph = ph.replace("-".toRegex(), "")
            ph = ph.replace("_".toRegex(), "")
            return ph
        }

    companion object {
        private const val LOGIN_KEY = "LOGIN_KEY"
        private const val PASSWORD_KEY = "PASSWORD_KEY"
        fun getStartIntent(context: Context?): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}