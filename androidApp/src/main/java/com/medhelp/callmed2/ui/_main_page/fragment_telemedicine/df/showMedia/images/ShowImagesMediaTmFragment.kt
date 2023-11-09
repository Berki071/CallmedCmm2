package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.images

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DfShowImagesMediaTmBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.ShowImageTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.ShowMediaTelemedicineDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.dop.DataForRecyFiles
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.images.recy.ShowImagesMediaTmAdapter
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.showMedia.images.recy.ShowImagesMediaTmHolder
import com.medhelp.callmed2.utils.Different
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse
import com.medhelp.callmedcmm2.model.chat.MessageRoomResponse.MessageRoomItem
import java.io.File

class ShowImagesMediaTmFragment : Fragment()  {
    lateinit var recItem: AllRecordsTelemedicineItem
    lateinit var listener: ShowMediaTelemedicineDf.ShowMediaTelemedicineListener  // при создании диалога

    lateinit var binding: DfShowImagesMediaTmBinding
    lateinit var presenter: ShowImagesMediaTmPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.df_show_images_media_tm, container, false)
        binding  =  DfShowImagesMediaTmBinding.bind(rootView)
        presenter = ShowImagesMediaTmPresenter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getData()
    }

    fun initRecy(listFile: MutableList<DataForRecyFiles>){

        val llm = LinearLayoutManager(requireContext())
        val adapter = ShowImagesMediaTmAdapter(requireContext(), listFile, object : ShowImagesMediaTmHolder.ShowImagesMediaTmListener{
            override fun onClickShow(data: DataForRecyFiles) {
                val pair = fileToMessage(data.file!!, presenter.listImage)

                val dialog = ShowImageTelemedicineDf()
                dialog.setData(pair.first, pair.second)
                dialog.show(requireFragmentManager(), ShowImageTelemedicineDf::class.java.canonicalName)
            }

            override fun delete(data: DataForRecyFiles) {
                Different.showAlertInfo(requireActivity(), "Удаление","Удалить файл "+ data.file!!.name +"?", object :
                    Different.AlertInfoListener {
                    override fun clickOk() {
                        val uriF: Uri = FileProvider.getUriForFile(requireContext(), "com.medhelp.callmed2.fileprovider", data.file!!)
                        val strUri = uriF.toString()

                        data.file!!.delete()
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

    fun fileToMessage(file: File, listImage: MutableList<File>): Pair<MessageRoomItem, MutableList<MessageRoomItem>>  {
        var mesSelect: MessageRoomItem = MessageRoomItem()
        var listMes: MutableList<MessageRoomItem> = mutableListOf()

        for(i in 0 until listImage.size){
            val uriF: Uri = FileProvider.getUriForFile(requireContext(), "com.medhelp.callmed2.fileprovider", listImage[i])

            val message = MessageRoomItem()
            message.idMessage = i
            message.text = uriF.toString()

            listMes.add(message)

            if(listImage[i] == file){
                mesSelect = message
            }
        }

        return Pair(mesSelect,listMes)
    }
}