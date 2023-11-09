package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.files

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DfShowFilesMediaTmBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.ShowFileTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.ShowMediaTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop.DataForRecyFiles
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.files.recy.ShowFilesMediaTmAdapter
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.files.recy.ShowFilesMediaTmHolder
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem


class ShowFilesMediaTmFragment: Fragment(){
    lateinit var recItem: AllRecordsTelemedicineItem
    lateinit var listener: ShowMediaTelemedicineDf.ShowMediaTelemedicineListener  // при создании диалога

    lateinit var binding: DfShowFilesMediaTmBinding
    lateinit var presenter: ShowFilesMediaTmPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.df_show_files_media_tm, container, false)
        binding = DfShowFilesMediaTmBinding.bind(rootView)
        presenter = ShowFilesMediaTmPresenter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getData()
    }

    fun initRecy(listFile: MutableList<DataForRecyFiles>){

        val llm = LinearLayoutManager(requireContext())
        val adapter = ShowFilesMediaTmAdapter(requireContext(), listFile, object : ShowFilesMediaTmHolder.ShowFilesMediaTmHolderListener{
            override fun onClick(date: DataForRecyFiles) {
                val uriF: Uri = FileProvider.getUriForFile(requireContext(), "com.medhelp.callmed2.fileprovider", date.file!!)

                val dialog = ShowFileTelemedicineDf()
                dialog.uriFile = uriF
                dialog.show(requireFragmentManager(), ShowFileTelemedicineDf::class.java.canonicalName)
            }

            override fun delete(date: DataForRecyFiles) {
                Different.showAlertInfo(requireActivity(), "Удаление","Удалить файл "+ date.file!!.name +"?", object :
                    Different.AlertInfoListener {

                    override fun clickOk() {
                        val uriF: Uri = FileProvider.getUriForFile(requireContext(), "com.medhelp.callmed2.fileprovider", date.file!!)
                        val strUri = uriF.toString()

                        date.file!!.delete()
                        presenter.getData()

                        listener.deleteFile(strUri)
                    }

                    override fun clickNo() {}
                }, true)
            }
        })

        binding.recy.layoutManager = llm
        binding.recy.adapter = adapter
    }
}