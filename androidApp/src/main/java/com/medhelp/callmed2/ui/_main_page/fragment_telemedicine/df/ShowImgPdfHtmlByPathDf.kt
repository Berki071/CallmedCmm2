package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.Html
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.davemorrissey.labs.subscaleview.ImageSource
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DfShowImgPdfHtmlByPathBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import timber.log.Timber
import java.io.File
import java.util.Locale


class ShowImgPdfHtmlByPathDf: DialogFragment() {
    lateinit var pathToFile: String    // инициализировать при создании алерта


    lateinit var binding: DfShowImgPdfHtmlByPathBinding
    override fun onStart() {
        super.onStart()
        //костыль, по умолчанию окно показывается не во весь размер
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawableResource(R.color.white)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.df_show_img_pdf_html_by_path, null)
        binding = DfShowImgPdfHtmlByPathBinding.bind(v)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        val webSetting: WebSettings = binding.htmlView.settings
        webSetting.builtInZoomControls = true
        webSetting.allowFileAccess = true
        webSetting.useWideViewPort = true
        webSetting.loadWithOverviewMode = true
        binding.htmlView.webViewClient = WebViewClient()

        val expansion = getExspansion(pathToFile)
        when (expansion) {
            "pdf" -> openFilePdf(pathToFile)
            "jpg", "jpeg" -> openImage(pathToFile)
            "html" -> opentHtml(pathToFile)
            else -> showAlertUnknown(expansion)
        }
    }

    private fun openImage(path: String?) {
        binding.pdfView?.visibility = View.GONE
        binding.htmlView?.visibility = View.GONE
        binding.image?.visibility = View.VISIBLE
        binding.image?.setImage(ImageSource.uri(path!!))
    }

    private fun openFilePdf(path: String?) {
        binding.image.visibility = View.GONE
        binding.htmlView.visibility = View.GONE
        binding.pdfView.visibility = View.VISIBLE
        val file = File(path)
        binding.pdfView.fromFile(file)
            .defaultPage(0)
            .onLoad { nbPages: Int -> }
            .onError { t: Throwable? ->
                Timber.tag("my").w(LoggingTree.getMessageForError(t, "ShowImgPdfHtmlByPathDf\$openFile "))
            }
            .load()
    }

    private fun opentHtml(path: String?) {
        binding.image.visibility = View.GONE
        binding.pdfView.visibility = View.GONE
        binding.htmlView.visibility = View.VISIBLE
        val file = File(path)
        binding.htmlView.loadUrl(file.absolutePath)
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }

//        binding.toolbar.inflateMenu(R.menu.menu_share)
//        binding.toolbar.setOnMenuItemClickListener { menuItem ->
//            if (menuItem.itemId == R.id.btnShare) {
//                clickShare()
//            }
//            true
//        }
    }

    private fun clickShare() {
        val sharingFile = File(pathToFile)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val toSendMessage = Intent(Intent.ACTION_SEND)

        val expansion = getExspansion(pathToFile)
        if(expansion == "html"){
            toSendMessage.type = "text/html"
        }else{
            toSendMessage.type = "application/pdf"
        }

        val uri = FileProvider.getUriForFile(requireContext(), "com.medhelp.callmed2.fileprovider", sharingFile)
        toSendMessage.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(toSendMessage, "MedHelper"))
    }

    private fun getExspansion(path: String?): String {
        val index = path!!.lastIndexOf(".")
        return path.substring(index + 1).lowercase(Locale.getDefault())
    }

    var dialogMsg: AlertDialog? = null
    private fun showAlertUnknown(expansion: String) {
        val str = "Неизвестное расширение файла \"$expansion\""
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_2textview_btn, null)
        val title = view.findViewById<TextView>(R.id.title)
        val text = view.findViewById<TextView>(R.id.text)
        val btnYes = view.findViewById<Button>(R.id.btnYes)
        val btnNo = view.findViewById<Button>(R.id.btnNo)
        btnNo.visibility = View.GONE
        title.text = Html.fromHtml("<u>Ошибка!</u>")
        text.text = str
        btnYes.setOnClickListener { v: View? -> dialogMsg!!.cancel() }
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        dialogMsg = builder.create()
        dialogMsg!!.show()
    }
}