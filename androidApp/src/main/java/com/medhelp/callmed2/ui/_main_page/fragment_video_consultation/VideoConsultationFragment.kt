package com.medhelp.callmed2.ui._main_page.fragment_video_consultation

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.VisitItem2
import com.medhelp.callmed2.databinding.FragmentOnlineConsultatinBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_video_consultation.recy.ConsultationAdapter
import com.medhelp.callmed2.ui._main_page.fragment_video_consultation.recy.ConsultationAdapter.ConsultationListener
import com.medhelp.callmed2.ui.base.BaseFragment
import com.medhelp.callmed2.ui.video_chat.VideoChatActivity
import timber.log.Timber

class VideoConsultationFragment : BaseFragment(), ConsultationListener {

//    @BindView(R.id.toolbar)
//    var toolbar: Toolbar? = null
//    @BindView(R.id.recy)
//    var recy: RecyclerView? = null
//    @BindView(R.id.rootEmpty)
//    var rootEmpty: ConstraintLayout? = null

    lateinit var binding: FragmentOnlineConsultatinBinding

    var presenter: VideoConsultationPresenter? = null

    var adapter: ConsultationAdapter? = null
    var isPermissionGranted = false
    private var alertPermission: AlertDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Timber.i("Онлайн консультация(список клиентов)")
        val rootView = inflater.inflate(R.layout.fragment_online_consultatin, container, false)
        binding = FragmentOnlineConsultatinBinding.bind(rootView)

        presenter = VideoConsultationPresenter(this)
        return binding.root
    }

    override fun setUp(view: View) {
        presenter!!.consultationList
        if (!testPermission()) {
            rquestPermission()
        } else {
            isPermissionGranted = true
        }
        setupToolbar()
    }

    private fun setupToolbar() {
        (context as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        val actionBar = (context as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar.post { binding.toolbar.title = resources.getString(R.string.onlineConsultation) }
        binding.toolbar.setNavigationOnClickListener { (context as MainPageActivity?)!!.showNavigationMenu() }
    }

    override fun destroyFragment() {}
    override fun onStartSetStatusFragment(status: Int) {}
    fun showErrorScreen() {
        binding.rootEmpty.visibility = View.VISIBLE
    }

    fun updateRecy(data: List<VisitItem2>) {
        if ((data == null || data.size == 0 || data.size == 1) && data[0].date == null) {
            binding.recy.visibility = View.GONE
            binding.rootEmpty.visibility = View.VISIBLE
        } else {
            binding.recy!!.visibility = View.VISIBLE
            binding.rootEmpty!!.visibility = View.GONE
            adapter = ConsultationAdapter(requireContext(), data, presenter!!.userToken, this)
            adapter!!.setHasStableIds(true)
            binding.recy!!.layoutManager = LinearLayoutManager(context)
            binding.recy!!.adapter = adapter
        }
    }

    private fun testPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            false
        } else {
            true
        }
    }

    private fun rquestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
            ), PERMISSIONS_REQUEST_ONLINE_CONSULTATION
        )
    }

    override fun onClickItemRecy(itm: VisitItem2) {
        showAlertForNextPage(itm)
    }

    private fun showAlertForNextPage(itm: VisitItem2) {
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_video_call_select, null)
        val titleTool = view.findViewById<TextView>(R.id.titleToolbar)
        val textPatientFio = view.findViewById<TextView>(R.id.name)
        val textService = view.findViewById<TextView>(R.id.service)
        val textDateStart = view.findViewById<TextView>(R.id.dateStart)
        val textTimeStartAndEnd = view.findViewById<TextView>(R.id.timeStartAndEnd)
        val btnYes = view.findViewById<Button>(R.id.btnYes)
        val btnNo = view.findViewById<Button>(R.id.btnNo)
        titleTool.text = "Позвонить?"
        textPatientFio.text = itm.allNameUser
        textService.text = itm.serviceName
        textDateStart.text = itm.date
        textTimeStartAndEnd.text = itm.timeStartAndEnd
        btnYes.setOnClickListener { v: View? ->
            nextPage(itm)
            alertPermission!!.cancel()
        }
        btnNo.visibility = View.VISIBLE
        btnNo.setOnClickListener { alertPermission!!.cancel() }
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        alertPermission = builder.create()
        alertPermission?.setCanceledOnTouchOutside(false)
        alertPermission?.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
        alertPermission?.show()
    }

    private fun nextPage(itm: VisitItem2) {
        if (isPermissionGranted) {
            val intent = Intent(context, VideoChatActivity::class.java)
            intent.putExtra(VisitItem2::class.java.canonicalName, itm)
            startActivity(intent)
            (context as MainPageActivity?)!!.finish()
        } else {
            alertAboutPermission()
        }
    }

    private fun alertAboutPermission() {
        val str = "Для продолжения работы необходим доступ к камере и микрофону телефона"
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_2textview_btn, null)
        val title = view.findViewById<TextView>(R.id.title)
        val text = view.findViewById<TextView>(R.id.text)
        val btnYes = view.findViewById<Button>(R.id.btnYes)
        val btnNo = view.findViewById<Button>(R.id.btnNo)
        title.text = Html.fromHtml("<u>Необходимо разрешение от пользователя</u>")
        text.text = str
        btnYes.setOnClickListener { v: View? ->
            rquestPermission()
            alertPermission!!.cancel()
        }
        btnNo.visibility = View.VISIBLE
        btnNo.setOnClickListener { alertPermission!!.cancel() }
        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        alertPermission = builder.create()
        alertPermission?.setCanceledOnTouchOutside(false)
        alertPermission?.show()
    }

    companion object {
        const val PERMISSIONS_REQUEST_ONLINE_CONSULTATION = 122
    }
}