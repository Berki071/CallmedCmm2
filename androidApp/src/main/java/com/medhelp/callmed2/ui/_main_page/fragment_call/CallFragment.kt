package com.medhelp.callmed2.ui._main_page.fragment_call

import android.app.Activity
import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.medhelp.callmed2.R
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_call.call_center_new.CallCenterService
import com.medhelp.callmed2.ui._main_page.fragment_call.call_center_new.CallCenterService.LocalBinder
import com.medhelp.callmed2.ui.base.BaseFragment
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import timber.log.Timber

class CallFragment : BaseFragment(), SensorEventListener {
    var toolbar: Toolbar? = null
    private var callCenterServiceIntent: Intent? = null
    var brError: BroadcastReceiver? = null
    private var context: Context? = null
    private var activity: Activity? = null
    var rootView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = getContext()
        activity = getActivity()
        setHasOptionsMenu(true)

        //region test log to timber
        var isLogTimber = true
        try {
            if (requireArguments().getString("log") == "log") isLogTimber = false
        } catch (e: Exception) {
        }
        if (isLogTimber) Timber.i("Колл центр")
        //endregion
        val rootView = inflater.inflate(R.layout.fragment_call, container, false)
        init(rootView)
        return rootView
    }

    private var mProximity: Sensor? = null
    private var mSensorManager: SensorManager? = null
    private var mPowerManager: PowerManager? = null
    private var mWakeLock: PowerManager.WakeLock? = null

    var isExistTelephonyService = true

    override fun setUp(view: View) {
        //проверка есть ли возможность звонить с телефона
        var tm: TelephonyManager = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        if(tm == null || tm.phoneType==TelephonyManager.PHONE_TYPE_NONE) {
            isExistTelephonyService = false
            Timber.tag("my").w("CallFragment.setUp Нет возможности звонить с устройства")
            showError("Нет возможности звонить с устройства")
            //Different.showAlertInfo(requireActivity(), "Ошибка!", "Нет возможности звонить с устройства")
            return
        }

        try {
            callCenterServiceIntent = Intent(getActivity(), CallCenterService::class.java)
            requireActivity().startService(callCenterServiceIntent)
        }catch (e: Exception){}

        checkDefaultDialer()
        Log.wtf("mmmLog", "setUp")
        if (mBound) {
            // Log.wtf("mmmLog","setUp refreshFioAndPhone");
            mService!!.refreshFioAndPhone()
        }
        mPowerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = mPowerManager!!.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "callmed2:tag"
        )
        mWakeLock?.acquire()
        mSensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mProximity = mSensorManager!!.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar = rootView!!.findViewById(R.id.toolbar)
        (getContext() as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        val actionBar = (getContext() as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        toolbar?.post(Runnable { toolbar!!.setTitle(resources.getString(R.string.callCenter)) })
        toolbar?.setNavigationOnClickListener(View.OnClickListener { (getContext() as MainPageActivity?)!!.showNavigationMenu() })
    }

    //AudioManager audioManager;
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if(isExistTelephonyService)
            mSensorManager!!.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        if(isExistTelephonyService)
            mSensorManager!!.unregisterListener(this)
    }

    private fun init(view: View) {
        rootView = view
        txtFio = view.findViewById(R.id.txtFio)
        txtPhone = view.findViewById(R.id.txtPhone)
        callBtn = view.findViewById(R.id.callBtn)
        endCallBtn = view.findViewById(R.id.endCallBtn)
        textStatus = view.findViewById(R.id.textStatus)
        textTimer = view.findViewById(R.id.textTimer)
        btnMic = view.findViewById(R.id.btnMic)
        txtFio?.setKeyListener(null)
        txtPhone?.setKeyListener(null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // для показа на на экране блокировки
            requireActivity().window.addFlags( /*WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|*/
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        initBroadcastR()
    }

    private fun initBroadcastR() {
        //туду 23api?
        //слушает в MyServiceCall метод sendErrorMessage
        brError = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val s = intent.extras!!
                    .getString(PARAM_ERROR, "")
                if (s.contains("Failed to connect to")) {
                    showError(R.string.connection_error)
                    return
                }
                if (s.contains("connect timed out")) {
                    showError(R.string.error_server)
                } else {
                    showError(R.string.some_error)
                }
            }
        }
        val intentFilter = IntentFilter(BROADCAST_ACTION)
        requireActivity().registerReceiver(brError, intentFilter)
        Log.wtf("mlogUp", "initBroadcastR")
    }

    override fun onStartSetStatusFragment(status: Int) {
        statusFragment = status
        Log.wtf("mmmLog", "statusFragment= " + statusFragment)
    }

    override fun destroyFragment() {
        requireActivity().unregisterReceiver(brError)
        if (callCenterServiceIntent == null) return else {
            Log.wtf("mLogStopService", "3")
            requireActivity().stopService(callCenterServiceIntent)
        }
    }

    // region проверка версии и приема звонков по умолчанию, при необходимости запрос на изменение
    private fun checkDefaultDialer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val telecomManager = requireContext().getSystemService(Context.TELECOM_SERVICE) as TelecomManager ?: return
            val isAlreadyDefaultDialer = if (requireContext().packageName == telecomManager.defaultDialerPackage) true else false
            if (!isAlreadyDefaultDialer) {
//                val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, requireContext().packageName)
//                this.startActivityForResult(intent, REQUEST_CODE_CHANGE_DEFAULT_DIALER)
                launchSetDefaultDialerIntent(requireContext() as MainPageActivity)
            }
        }
    }

    fun launchSetDefaultDialerIntent(activity: AppCompatActivity) {
        Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, activity.packageName).apply {
            if (resolveActivity(activity.packageManager) != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val rm: RoleManager? = activity.getSystemService(RoleManager::class.java)
                    if (rm?.isRoleAvailable(RoleManager.ROLE_DIALER) == true) {
                        @Suppress("DEPRECATION")
                        activity.startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_DIALER), REQUEST_CODE_CHANGE_DEFAULT_DIALER)
                    }
                } else {
                    @Suppress("DEPRECATION")
                    activity.startActivityForResult(this, REQUEST_CODE_CHANGE_DEFAULT_DIALER)
                }
            } else {
                //activity.toastShort(R.string.no_contacts_found)
                Toast.makeText(context, "no_contacts_found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CHANGE_DEFAULT_DIALER) {
            checkSetDefaultDialerResult(resultCode)
        }
    }

    var counterShowAlert = 5
    private fun checkSetDefaultDialerResult(resultCode: Int) {
        when (resultCode) {
            -1 -> {}
            0 -> {
                if(counterShowAlert>0) {
                    counterShowAlert--
                    // message = "User declined request to become default dialer";
                    val message =
                        "Требуется Ваше разрешение для изменения стандартной программы для звонков." +
                                "В некоторых случаях если не появилось окно с предложением сменить необходимо сделать это вручную. " +
                                "Настройки/Все приложения/три точки (вверху справа)/Телефон и выбрать приложение «Медицинский помощник»"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }

                checkDefaultDialer()
            }

            else -> {}
        }
    }

    //endregion
    //region для связи фрагмента с сервисом(можно вызывать в сервисе нужные публичные методы)
    var mService: CallCenterService? = null
    var mBound = false
    override fun onStart() {
        super.onStart()

        if(isExistTelephonyService) {
            // Bind to LocalService
            val intent = Intent(getActivity(), CallCenterService::class.java)
            requireActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if(isExistTelephonyService) {
            // Unbind from the service
            if (mBound) {
                requireActivity().unbindService(mConnection)
                mBound = false
            }
        }
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as LocalBinder
            mService = binder.service
            mBound = true
            Log.wtf("mmmLog", "ServiceConnection refreshFioAndPhone")
            mService?.refreshFioAndPhone()
            mService?.testMicrophone()
            //mService.setVisibleBtnMic(false);
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val distance = event.values[0]
        Log.wtf("mLogEvent", "event $distance")
        if (distance.toDouble() == 0.0) {
            val s = textStatus!!.text.toString()
            if (textStatus != null && textStatus!!.text != "") {
                mWakeLock = mPowerManager!!.newWakeLock(
                    PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK,
                    "callmed2:tag"
                )
                mWakeLock?.acquire()
            }
        } else if (distance.toDouble() == 8.0) {
            mWakeLock = mPowerManager!!.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "callmed2:tag"
            )
            mWakeLock?.acquire()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Log.wtf("mLog","sensor "+sensor.getName());
    } //endregion

    companion object {
        const val REQUEST_CODE_CHANGE_DEFAULT_DIALER = 124
        @JvmField
        var textStatus: TextView? = null
        @JvmField
        var textTimer: TextView? = null
        @JvmField
        var txtFio: EditText? = null
        @JvmField
        var txtPhone: EditText? = null
        @JvmField
        var callBtn: ImageButton? = null
        @JvmField
        var endCallBtn: ImageButton? = null
        @JvmField
        var btnMic: ImageButton? = null
        @JvmField
        var statusFragment = 0
        const val PARAM_ERROR = "PARAM_ERROR"
        const val BROADCAST_ACTION = "com.medhelp.callmed2.ui.call_page.fragment_call.CallFragment"
    }
}