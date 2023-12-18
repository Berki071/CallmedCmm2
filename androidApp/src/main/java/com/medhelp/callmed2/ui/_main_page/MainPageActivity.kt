package com.medhelp.callmed2.ui._main_page

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.Constants
import com.medhelp.callmed2.data.model.timetable.Doctor
import com.medhelp.callmed2.ui._main_page.fragment_call.CallFragment
import com.medhelp.callmed2.ui._main_page.fragment_call.call_center_new.CallCenterService
import com.medhelp.callmed2.ui._main_page.fragment_doc_recognition.DocRecognitionFragment
import com.medhelp.callmed2.ui._main_page.fragment_scan_passport.ScanPassportFragment
import com.medhelp.callmed2.ui._main_page.fragment_scanner_doc.ScannerDocFragment
import com.medhelp.callmed2.ui._main_page.fragment_settings.SettingsFragment
import com.medhelp.callmed2.ui._main_page.fragment_statistics_mcb.StatisticsMcbFragment
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.T1ListOfEntriesFragment
import com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.TimetableDocFragment
import com.medhelp.callmed2.ui._main_page.fragment_user_record.RecordFindServiceFragment
import com.medhelp.callmed2.ui.base.BaseActivity
import com.medhelp.callmed2.ui.login.LoginActivity
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmed2.utils.Different.AlertInfoListener
import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2
import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2.BuilderImage
import com.medhelp.callmed2.utils.WorkTofFile.show_file.ShowFile2.ShowListener
import com.medhelp.callmedcmm2.model.CenterResponse
import timber.log.Timber
import java.io.File

class MainPageActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var navView: NavigationView
    lateinit var drawer: DrawerLayout
    lateinit var rootFragment: LinearLayout

    lateinit var headerTitle: TextView
    lateinit var headerLogo: ImageView
    lateinit var nameDoc: TextView

    lateinit var presenter: MainPagePresenter

    var selectFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_page)
        setUp()
        //presenter.startCheckShowNotyRasp()
    }

    fun setUp() {
        navView = findViewById(R.id.nav_view_analise)
        drawer = findViewById(R.id.drawer_analise)
        rootFragment = findViewById(R.id.rootFragment)

        presenter = MainPagePresenter(this)

        updateHeader(presenter.preferencesManager.centerInfo)
        setupDrawer()
        testWhichFragmentToShow()

        if (presenter.preferencesManager.isShowPartMessenger && !checkPermissions()) requestPermissions()
        if (presenter.preferencesManager.isShowPartCallCenter) testPermission()

        presenter.getCurrentDocInfo()
    }

    override fun onResume() {
        super.onResume()
        CheckForUpdateApp(this) //запуск проверки более свежей версии
        askNotificationPermission()
    }

    //region 13 android request permission for notification
    private val requestPermissionLauncher =
        registerForActivityResult<String, Boolean>(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
            } else {
            }
        }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    //endregion

    override fun onDestroy() {
        wakeLockScrin(false)
        super.onDestroy()
    }

    override fun destroyActivity() {}
    private fun wakeLockScrin(boo: Boolean) {
        val isAction = presenter!!.preferencesManager.lockScreenCallCenter
        if (isAction && boo) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    var dialog: AlertDialog? = null

    private fun testWhichFragmentToShow() {
        var pointer: Int? = null
        try {
            pointer = intent.extras!!.getInt(Constants.KEY_FOR_INTENT_POINTER_TO_PAGE, -1)
            if (pointer == -1) pointer = null
        } catch (e: Exception) {
            Log.wtf(
                "mLog",
                "CallPageActivity information for the pointer is not present, everything of fine"
            )
        }
        if (pointer == null) {
            val cs = presenter!!.preferencesManager.isShowPartCallCenter
            val m = presenter!!.preferencesManager.isShowPartMessenger
            val tt = presenter!!.preferencesManager.isShowPartTimetable
            val raspDoc = presenter!!.preferencesManager.isShowPartRaspDoc
            val scanDoc = presenter!!.preferencesManager.isShowPartScanDoc
            if (!cs) {
                val callServiceIntent = Intent(this, CallCenterService::class.java)
                stopService(callServiceIntent)
            }
            pointer = if (!cs && !m && !tt && !raspDoc && !scanDoc) MENU_SETTINGS else {
                if (cs) MENU_CALL_SENTER else if (tt) MENU_RECORD_USER else if (raspDoc) MENU_TIMETABLE_DOC else if (scanDoc) MENU_SCANNER_DOC else if (m) MENU_CHAT_WITH_DOC else MENU_SETTINGS
            }
        }
        when (pointer) {
            MENU_CALL_SENTER -> showCallFragment()
            MENU_CHAT_WITH_DOC -> showChatFragment()
            MENU_SETTINGS -> showSettingsFragments()
            //MENU_ONLINE_CONSULTATION -> showOnlineConsultation()
            MENU_TIMETABLE_DOC -> showTimetableDocFragment()
            MENU_SCANNER_DOC -> showScanerDoc()
            MENU_RECORD_USER -> showRecordUserFragment()
        }
    }

    private fun setupDrawer() {
        val drawerToggle: ActionBarDrawerToggle = object :
            ActionBarDrawerToggle(this, drawer, null, R.string.open_drawer, R.string.close_drawer) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                hideKeyboard()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        }
        drawer!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        setupNavMenu()
    }

    // region navigation menu
    private fun setupNavMenu() {
        val headerLayout = navView!!.getHeaderView(0)
        headerLogo = headerLayout.findViewById(R.id.header_logo)
        headerTitle = headerLayout.findViewById(R.id.header_tv_title)
        navView!!.setNavigationItemSelectedListener(this)
        setVisibleItemMenu()
    }

    private fun setVisibleItemMenu() {
        val cs = presenter!!.preferencesManager.isShowPartCallCenter
        val m = presenter!!.preferencesManager.isShowPartMessenger
        val rec = presenter!!.preferencesManager.isShowPartTimetable
        val scan = presenter!!.preferencesManager.isShowPartScanDoc
        val docRasp = presenter!!.preferencesManager.isShowPartRaspDoc
        val passRecognize = presenter!!.preferencesManager.isShowPassportRecognize

        val menu = navView!!.menu
        if (cs)
            menu.add(0, MENU_CALL_SENTER, Menu.NONE, resources.getString(R.string.callCenter)).setIcon(R.drawable.ic_call_light_green_a200_24dp).isChecked = true
        if (rec) // перенос флага с расписания
            menu.add(0, MENU_RECORD_USER, Menu.NONE, "Записать").setIcon(R.drawable.ic_assignment_turned_in_white_24dp).isChecked = true
        if (docRasp)
            menu.add(0, MENU_TIMETABLE_DOC, Menu.NONE, resources.getString(R.string.timetable)).setIcon(R.drawable.ic_assignment_turned_in_white_24dp).isChecked = true
        if (m)
            menu.add(0, MENU_CHAT_WITH_DOC, Menu.NONE, "Телемедицина").setIcon(R.drawable.ic_chat_pink_a200_24dp).isChecked = true
        if (docRasp)
            menu.add(0, MENU_STATISTICS_MCB, Menu.NONE, resources.getString(R.string.statistic_mcb)).setIcon(R.drawable.baseline_featured_play_list_white_24).isChecked = true

        if (false)
            menu.add(0, MENU_ONLINE_CONSULTATION, Menu.NONE, resources.getString(R.string.onlineConsultation)).setIcon(R.drawable.ic_videocam_black_24dp).isChecked = true
        if (scan)
            menu.add(0, MENU_SCANNER_DOC, Menu.NONE, "Сканнер документов").setIcon(R.drawable.baseline_scanner_white_24).isChecked = true

        if(passRecognize)
            menu.add(0, MENU_SCAN_PASSPORT, Menu.NONE, resources.getString(R.string.scanPassport)).setIcon(R.drawable.baseline_contact_emergency_24).isChecked = true

        if (false)
            menu.add(0, MENU_DOC_RECOGNITION, Menu.NONE, "Распознание текста").setIcon(R.drawable.baseline_emoji_symbols_white_24).isChecked = true
        menu.add(0, MENU_SETTINGS, Menu.NONE, resources.getString(R.string.Settings)).setIcon(R.drawable.ic_settings_light_green_a200_24dp).isChecked = true
        menu.add(0, MENU_EXIT, Menu.NONE, resources.getString(R.string.exit)).setIcon(R.drawable.ic_exit_to_app_light_green_a200_24dp).isChecked = true

        navView!!.invalidate()
    }

    fun updateHeader(response: CenterResponse.CenterItem?) {
        if(response==null)
            return

        val headerLayout = navView.getHeaderView(0)
        headerLogo = headerLayout.findViewById(R.id.header_logo)
        headerTitle = headerLayout.findViewById(R.id.header_tv_title)
        nameDoc = headerLayout.findViewById(R.id.nameDoc)
        headerTitle?.setText(response.title)
        if (response.logo != null && response.logo != "") {
            BuilderImage(this@MainPageActivity)
                .setType(ShowFile2.TYPE_ICO)
                .load(response.logo)
                .token(presenter.preferencesManager.accessToken)
                .imgError(R.drawable.sh_center)
                .into(headerLogo)
                .setListener(object : ShowListener {
                    override fun complete(file: File?) {
                        Log.wtf("", "")
                    }

                    override fun error(error: String?) {
                        Log.wtf("", "")
                    }
                })
                .build()
        } else {
            headerLogo?.setImageResource(R.drawable.sh_center)
        }
    }

    fun updateHiaderDocName(doc: Doctor) {
        nameDoc!!.text = doc.fullName
    }

    //region navigation menu jump to fragment
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer!!.closeDrawer(GravityCompat.START)
        return when (item.itemId) {
            MENU_CALL_SENTER -> {
                showCallFragment()
                true
            }
            MENU_RECORD_USER -> {
                showRecordUserFragment()
                true
            }
            MENU_STATISTICS_MCB -> {
                showStatisticsMcbFragment()
                true
            }
            MENU_TIMETABLE_DOC -> {
                showTimetableDocFragment()
                true
            }
//            MENU_ONLINE_CONSULTATION -> {
//                showOnlineConsultation()
//                true
//            }
            MENU_SCANNER_DOC -> {
                showScanerDoc()
                true
            }
            MENU_CHAT_WITH_DOC -> {
                showChatFragment()
                true
            }
            MENU_DOC_RECOGNITION -> {
                showDocRecognitionFragment()
                true
            }
            MENU_SCAN_PASSPORT -> {
                showScanPassport()
                true
            }
            MENU_SETTINGS -> {
                showSettingsFragments()
                true
            }
            MENU_EXIT -> {
                showLoginActivity()
                true
            }
            else -> false
        }
    }

    private fun showDocRecognitionFragment() {
        if (selectFragment is DocRecognitionFragment) return
        if (selectFragment is CallFragment) {
            wakeLockScrin(false)
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = DocRecognitionFragment()
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_TIMETABLE_DOC.toString())
        fragmentTransaction.commit()
    }
    private fun showScanPassport(){
        if (selectFragment is ScanPassportFragment) return
        if (selectFragment is CallFragment) {
            wakeLockScrin(false)
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = ScanPassportFragment()
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_TIMETABLE_DOC.toString())
        fragmentTransaction.commit()
    }

    private fun showTimetableDocFragment() {
        if (selectFragment is TimetableDocFragment) return
        if (selectFragment is CallFragment) {
            wakeLockScrin(false)
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = TimetableDocFragment()
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_TIMETABLE_DOC.toString())
        fragmentTransaction.commit()
        clearTimetableNotification()
    }

    private fun showStatisticsMcbFragment() {
        if (selectFragment is StatisticsMcbFragment) return
        if (selectFragment is CallFragment) {
            wakeLockScrin(false)
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = StatisticsMcbFragment()
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_STATISTICS_MCB.toString())
        fragmentTransaction.commit()
    }

    private fun showRecordUserFragment() {
        if (selectFragment is RecordFindServiceFragment) return
        if (selectFragment is CallFragment) {
            wakeLockScrin(false)
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = RecordFindServiceFragment()
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_RECORD_USER.toString())
        fragmentTransaction.commit()
        clearTimetableNotification()
    }

    private fun clearTimetableNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.NOTIFICATION_ID_TIMETABLE_DOC)
    }

//    private fun showOnlineConsultation() {
//        if (selectFragment is VideoConsultationFragment) return
//        if (selectFragment is CallFragment) {
//            wakeLockScrin(false)
//        }
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//        deleteShowFragment(fragmentTransaction)
//        selectFragment = VideoConsultationFragment()
//        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_ONLINE_CONSULTATION.toString())
//        fragmentTransaction.commit()
//    }

    private fun showCallFragment() {
        if (selectFragment is CallFragment) return
        wakeLockScrin(true)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = CallFragment()
        try {
            var s: String? = ""
            s = intent.extras!!.getString("log")
            val bundle = Bundle()
            bundle.putString("log", s)
            selectFragment!!.arguments = bundle
            intent.putExtra("log", "not")
        } catch (e: Exception) {
            val s = e.message
        }
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_CALL_SENTER.toString())
        fragmentTransaction.commit()
    }

    private fun showScanerDoc() {
        if (selectFragment is ScannerDocFragment) return
        if (selectFragment is CallFragment) wakeLockScrin(false)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = ScannerDocFragment()
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_SCANNER_DOC.toString())
        fragmentTransaction.commit()
    }

    private fun showChatFragment() {
        if (selectFragment is T1ListOfEntriesFragment) return
        if (selectFragment is CallFragment) {
            wakeLockScrin(false)
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = T1ListOfEntriesFragment()
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_CHAT_WITH_DOC.toString())
        fragmentTransaction.commit()
    }

    private fun showSettingsFragments() {
        if (selectFragment is SettingsFragment) return
        if (selectFragment is CallFragment) {
            wakeLockScrin(false)
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        deleteShowFragment(fragmentTransaction)
        selectFragment = SettingsFragment()
        fragmentTransaction.replace(rootFragment!!.id, selectFragment!!, MENU_SETTINGS.toString())
        fragmentTransaction.commit()
    }

    private fun deleteShowFragment(fragmentTransaction: FragmentTransaction) {
        selectFragment?.let{ fragmentTransaction.remove(it) }
        selectFragment = null
    }

    fun showLoginActivity() {
        Different.showAlertInfo(
            this,
            "",
            "Вы действительно хотите выйти из учетной записи?",
            object : AlertInfoListener {
                override fun clickOk() {
                    Timber.v("Выход из учетной записи")
                    ShowFile2.listBilderImage = ArrayList()
                    ShowFile2.loadFile = ArrayList()

//                Intent serviceIntent = new Intent(MainPageActivity.this, ServiceStartOnBoot.class);
//                stopService(serviceIntent);
                    val callServiceIntent =
                        Intent(this@MainPageActivity, CallCenterService::class.java)
                    // Log.wtf("mLogStopService","2");
                    stopService(callServiceIntent)
                    val intent = LoginActivity.getStartIntent(this@MainPageActivity)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    presenter!!.removePassword()
                    startActivity(intent)
                    finish()
                }

                override fun clickNo() {}
            },
            true,
            "Нет",
            "Да"
        )
    }

    //endregion
    //endregion
    //region permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.PERMISSION_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.v("Разрешено считывать состояние телефона")
                } else {
                    Timber.v("Запрещено считывать состояние телефона")
                    showAlert()
                }
                return
            }
//            VideoConsultationFragment.PERMISSIONS_REQUEST_ONLINE_CONSULTATION -> {
//                if (grantResults.size > 0) {
//                    var isGranted = true
//                    for (res in grantResults) {
//                        if (res != PackageManager.PERMISSION_GRANTED) {
//                            isGranted = false
//                        }
//                    }
//                    if (isGranted && selectFragment != null) {
//                        (selectFragment!! as VideoConsultationFragment).isPermissionGranted = true
//                    }
//                }
//                return
//            }
        }
    }

    private fun showAlert() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if(tm == null || tm.phoneType== TelephonyManager.PHONE_TYPE_NONE)
            return

        if ((this@MainPageActivity as AppCompatActivity?)!!.isFinishing) return
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Необходимо дать приложению разрешение на работу со звонками и телефонной книгой")
            .setPositiveButton("Ok") { dialog, which ->
                testPermission()
                dialog.cancel()
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun testPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_CONTACTS
                )
                requestPermissions(permissions, Constants.PERMISSION_REQUEST_READ_PHONE_STATE)
            }
        }
    }

    //endregion
    //region android override
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer!!.closeDrawer(GravityCompat.START)
        } else {
            testOnBackPresses()
        }
    }

    private fun testOnBackPresses() {
        //super.onBackPressed();
        val menu = navView!!.menu
        val firstItem = menu.getItem(0)
        val itemid = firstItem.itemId
        when (itemid) {
            MENU_CALL_SENTER -> if (selectFragment is CallFragment) super.onBackPressed() else showCallFragment()
            MENU_TIMETABLE_DOC -> if (selectFragment is TimetableDocFragment) super.onBackPressed() else showTimetableDocFragment()
            //MENU_ONLINE_CONSULTATION -> if (selectFragment is VideoConsultationFragment) super.onBackPressed() else showOnlineConsultation()
            MENU_CHAT_WITH_DOC -> if (selectFragment is T1ListOfEntriesFragment) super.onBackPressed() else showChatFragment()
            MENU_SETTINGS -> if (selectFragment is SettingsFragment) super.onBackPressed() else showSettingsFragments()
            MENU_SCANNER_DOC -> if (selectFragment is ScannerDocFragment) super.onBackPressed() else showScanerDoc()
        }
    }

    //endregion
    fun showNavigationMenu() {
        drawer!!.openDrawer(Gravity.LEFT)
    }

    companion object {
        const val MENU_CALL_SENTER = 0
        const val MENU_RECORD_USER = 1
        const val MENU_TIMETABLE_DOC = 2
        const val MENU_STATISTICS_MCB = 3
        const val MENU_ONLINE_CONSULTATION = 4
        const val MENU_SCANNER_DOC = 5
        const val MENU_CHAT_WITH_DOC = 6
        const val MENU_DOC_RECOGNITION = 7
        const val MENU_SETTINGS = 8
        const val MENU_EXIT = 9
        const val MENU_SCAN_PASSPORT = 10

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}