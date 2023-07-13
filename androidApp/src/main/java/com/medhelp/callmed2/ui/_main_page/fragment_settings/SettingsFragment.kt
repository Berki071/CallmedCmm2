package com.medhelp.callmed2.ui._main_page.fragment_settings

import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.IPDataResponse
import com.medhelp.callmed2.databinding.FragmentSettingsBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui.base.BaseFragment
import com.medhelp.callmed2.utils.IpUtils
import timber.log.Timber

class SettingsFragment : BaseFragment() {
    lateinit var binding: FragmentSettingsBinding
    var presenter: SettingsPresenter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.i("Настройки")
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        binding = FragmentSettingsBinding.bind(rootView)

        presenter = SettingsPresenter(this)
        return binding.root
    }

    override fun setUp(view: View) {
        val ip = IpUtils.getIPAddress(true)
        if (ip != null) {
            binding.myIP.text = "Ваш IP: $ip"
        }
        try {
            val pInfo = requireActivity().packageManager.getPackageInfo(
                requireContext().packageName, 0
            )
            val version = pInfo.versionCode
            binding.versionCode.text = "v. $version"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        //проверка авторизации
        if (!presenter!!.isCheckLoginAndPassword) return
        checkShowView()
        testStartStateSwitch()
        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                testCapabilityShowLockScreen()
                Timber.v("Авторизация по отпечатку пальца включена")
            } else {
                presenter!!.needShowLockScreen = false
                Timber.v("Авторизация по отпечатку пальца отключена")
            }
        }
        binding.switch2!!.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter!!.lockScreenCallCenter = isChecked
            Timber.v("Блокировка экрана на странице Call центра" + if (isChecked) "включена" else "отключена")
        }
        binding.switchNoty!!.setOnCheckedChangeListener { buttonView, isChecked ->
            presenter!!.preferencesManager.isShowNotifications = isChecked
            Timber.v("Отправка уведомлений " + if (isChecked) "включена" else "отключена")
            showLoading()
            if (!isChecked) presenter!!.sendFcmToken("null") else {
                presenter!!.fBToken
            }
        }
        presenter!!.allIp
        setupToolbar()
    }

    private fun checkShowView() {
        if (presenter!!.isShowCallCenter) {
            binding.titleSpinner!!.visibility = View.VISIBLE
            binding.card1!!.visibility = View.VISIBLE
            binding.cardLockScreen!!.visibility = View.VISIBLE
        } else {
            binding.titleSpinner!!.visibility = View.GONE
            binding.card1!!.visibility = View.GONE
            binding.cardLockScreen!!.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        (context as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        val actionBar = (context as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar!!.post { binding.toolbar!!.title = resources.getString(R.string.Settings) }
        binding.toolbar!!.setNavigationOnClickListener { (context as MainPageActivity?)!!.showNavigationMenu() }
    }

    private fun testCapabilityShowLockScreen() {
        val mKeyguardManager =
            requireActivity().getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!mKeyguardManager.isKeyguardSecure) {
            // Show a message that the user hasn't set up a lock screen.
            Toast.makeText(
                context, """
     Secure lock screen hasn't set up.
     Go to 'Settings -> Security -> Screenlock' to set up a lock screen
     """.trimIndent(), Toast.LENGTH_LONG
            ).show()
            binding.switch1.isChecked = false
            return
        } else {
            presenter!!.needShowLockScreen = true
        }
    }

    private fun testStartStateSwitch() {
        binding.switch1.isChecked = presenter!!.needShowLockScreen
        if (presenter!!.isShowCallCenter) {
            binding.switch2.isChecked = presenter!!.lockScreenCallCenter
        }
        binding.switchNoty!!.isChecked = presenter!!.preferencesManager.isShowNotifications
    }

    override fun destroyFragment() {
        // presenter.unSubscribe();
    }

    override fun onStartSetStatusFragment(status: Int) {}
    private var latchDefValue = false
    fun refreshSpinner(data: List<IPDataResponse>) {
        val dataForSpinner = ArrayList<String>()
        dataForSpinner.add("")
        for (item in data) {
            dataForSpinner.add(item.ip)
        }

        //ArrayAdapter<String>adapter=new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, dataForSpinner);
        val adapter = getAdapterSpinner(dataForSpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner!!.adapter = adapter
        val oldSelectedItem = searchSelectedItemInList(presenter!!.ip, dataForSpinner)
        latchDefValue = true
        binding.spinner!!.setSelection(oldSelectedItem)
        binding.spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0 && !latchDefValue) {
                    val ip = binding.spinner!!.selectedItem.toString()
                    presenter!!.ip = ip
                }
                if (latchDefValue) {
                    latchDefValue = !latchDefValue
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun searchSelectedItemInList(selected: String, list: List<String>): Int {
        for (i in list.indices) {
            if (list[i] == selected) {
                return i
            }
        }
        return 0
    }

    private fun getAdapterSpinner(list: ArrayList<String>): ArrayAdapter<String> {
        // Initializing an ArrayAdapter
        return object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, list) {
            override fun isEnabled(position: Int): Boolean {
                return if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    false
                } else {
                    true
                }
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getDropDownView(position, convertView, parent)
            }
        }
    }

    fun showErrorM(errMsg: String?) {
        if (errMsg == null) return
        if (errMsg.contains("Failed to connect to")) {
            showError(R.string.connection_error)
            return
        }
        if (errMsg.contains("connect timed out")) {
            showError(R.string.error_server)
        }
        if (errMsg.contains("Ошибка!")) {
            showError("Ошибка!")
        } else {
            showError(R.string.some_error)
        }
    }
}