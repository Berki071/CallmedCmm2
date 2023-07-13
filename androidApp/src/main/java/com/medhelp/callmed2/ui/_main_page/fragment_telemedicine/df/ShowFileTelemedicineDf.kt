package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.barteksc.pdfviewer.listener.OnErrorListener
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DfShowFileTelemedicineBinding
import com.medhelp.callmed2.utils.timber_log.LoggingTree.Companion.getMessageForError
import timber.log.Timber
import java.util.*

class ShowFileTelemedicineDf: DialogFragment() {
    lateinit var uriFile: Uri // инициализируется при создании диалога

    lateinit var binding: DfShowFileTelemedicineBinding


    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)

        //костыль, по умолчанию окно показывается не во весь размер
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            Objects.requireNonNull(dialog.window)?.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.df_show_file_telemedicine, null)
        binding = DfShowFileTelemedicineBinding.bind(view)
        initValue()
        return binding.root
    }

    private fun initValue() {
        binding.toolbar.inflateMenu(R.menu.menu_share)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.btnShare -> {
                    clickShare()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            dialog?.dismiss()
        }

        binding.pdfView.fromUri(uriFile)
            .defaultPage(0)
            .onLoad(OnLoadCompleteListener { nbPages: Int -> })
            .onError(OnErrorListener { t: Throwable? ->
                Timber.tag("my").e(getMessageForError(t, "ShowPDFActivity\$openFile "))
            })
            .load()
    }


    private fun clickShare() {
        val toSendMessage = Intent(Intent.ACTION_SEND)
        toSendMessage.type = "image/*"
        toSendMessage.putExtra(Intent.EXTRA_STREAM, uriFile)
        startActivity(Intent.createChooser(toSendMessage, "MedHelper"))
    }
}