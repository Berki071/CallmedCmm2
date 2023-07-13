package com.medhelp.callmed2.ui.splash

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.databinding.ActivitySplashBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.T1ListOfEntriesFragment
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.ui.base.BaseActivity
import com.medhelp.callmed2.ui.login.LoginActivity

class SplashActivity : BaseActivity() {
    var presenter: SplashPresenter? = null

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent?.getStringExtra("type_message") ?: "null"
        //Log.wtf("NotyLogMy","0,5 сплэш " + type)

        presenter = SplashPresenter(this)
        presenter!!.openNextActivity()
    }

    fun openLoginActivity() {
        val intent = LoginActivity.getStartIntent(this)
        startActivity(intent)
        finish()
    }

    fun openProfileActivity(showLock: Boolean) {
        if (!showLock) {
          //  Log.wtf("notttty", "openProfileActivity 1")

            val dataForIntent: List<String>? = getIntentData()
            if(dataForIntent == null) {
                //Log.wtf("notttty", "openProfileActivity 2")
                val intent = Intent(this@SplashActivity, MainPageActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                //Log.wtf("NotyLogMy","6 openProfileActivity " + (dataForIntent[2] == Constants.TelemedicineNotificationType.MESSAGE.fullName))
                if(dataForIntent[2] == Constants.TelemedicineNotificationType.MESSAGE.fullName) {
                    val intent = Intent(this@SplashActivity, T3RoomActivity::class.java)
                    intent.putExtra("idRoom", dataForIntent[0])
                    intent.putExtra("idTm", dataForIntent[1])
                    intent.putExtra("whatDataShow", T1ListOfEntriesFragment.WhatDataShow.ACTIVE.toString())
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this@SplashActivity, MainPageActivity::class.java)
                    intent.putExtra(Constants.KEY_FOR_INTENT_POINTER_TO_PAGE, MainPageActivity.MENU_CHAT_WITH_DOC)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val mKeyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                val intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null)
                if (intent != null) {
                    startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS)
                } else {
                    openProfileActivity(false)
                }
            } else {
                openProfileActivity(false)
            }
        }
    }

    fun getIntentData(): List<String>?{
        val curIntent = intent
        if (curIntent != null) {
            val idRoom = curIntent.getStringExtra("idRoom")
            val idTm = curIntent.getStringExtra("idTm")
            val type = curIntent.getStringExtra("type_message")
            if (idRoom != null && idTm != null && type!=null) {
                //Log.wtf("NotyLogMy","5 сплэш " + type)
                val list : List<String> = listOf(idRoom,idTm,type)
                return list
            }
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            // Challenge completed, proceed with using cipher
            if (resultCode == RESULT_OK) {
                openProfileActivity(false)
            } else {
                // The user canceled or didn’t complete the lock screen
                // operation. Go to error/cancellation flow.
                // Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                finishAffinity()
            }
        }
    }

    override fun destroyActivity() {}
    fun startBGService() {
        // Intent serviceIntent = new Intent(this, ServiceStartOnBoot.class);
        //this.startService(serviceIntent);
    }

    val context: Context
        get() = this

    companion object {
        private const val REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1
        fun getStartIntent(context: Context?): Intent {
            return Intent(context, SplashActivity::class.java)
        }
    }
}