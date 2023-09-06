package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_conclusions

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.DfShowAnalyzsesBinding
import com.medhelp.callmed2.databinding.DfShowConclusionsBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.ShowImgPdfHtmlByPathDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_analyzes.ShowAnalyzesPresenter
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_conclusions.recy.ElectronicConclusionsAdapter
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_conclusions.recy.ElectronicConclusionsHolder
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineItem
import com.medhelp.callmedcmm2.model.chat.AnaliseResponse
import com.medhelp.callmedcmm2.model.chat.DataClassForElectronicRecy
import com.medhelp.callmedcmm2.model.chat.ResultZakl2Item
import timber.log.Timber

class ShowConclusionsAlert: DialogFragment() {
    lateinit var recordItem: AllRecordsTelemedicineItem  //при создании диалога!!!


    lateinit var binding: DfShowConclusionsBinding
    var presenter: ShowConclusionsPresenter? = null

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
        val v = inflater.inflate(R.layout.df_show_conclusions, null)
        binding = DfShowConclusionsBinding.bind(v)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ShowConclusionsPresenter(this)

        binding.toolbar.setNavigationIcon(R.drawable.ic_back_white_24dp)
        binding.toolbar!!.setNavigationOnClickListener {
            dialog?.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter!!.getData()
    }

    fun showErrorScreen() {
        binding.recy.setVisibility(View.GONE)
    }

    @JvmField
    var electronicConclusionsAdapter: ElectronicConclusionsAdapter? = null
    fun initRecy(list: List<DataClassForElectronicRecy>?) {
        if (list == null) {
            showEmpty(true)
            return
        } else showEmpty(false)
        val linearLayoutManager = LinearLayoutManager(context)
        electronicConclusionsAdapter = ElectronicConclusionsAdapter(requireContext(), list,
            object : ElectronicConclusionsHolder.ElectronicConclusionsHolderListener {
                override fun openHideBox(data: DataClassForElectronicRecy) {
                    electronicConclusionsAdapter?.processingShowHideBox(data!!)
                }

                override fun showDoc(data: DataClassForElectronicRecy) {
                    if (data!!.isDownloadIn) openPDF(data) else downloadPDF(data)
                }

                override fun deleteDoc(data: DataClassForElectronicRecy) {
                    deletePDFDialog(data)
                }
            })
        binding.recy.setLayoutManager(linearLayoutManager)
        binding.recy.setAdapter(electronicConclusionsAdapter)
    }

    private fun showEmpty(boo: Boolean) {
        if (boo) {
            binding.rootEmpty.setVisibility(View.VISIBLE)
            binding.recy.setVisibility(View.GONE)
        } else {
            binding.rootEmpty.setVisibility(View.GONE)
            binding.recy.setVisibility(View.VISIBLE)
        }
    }

    fun deletePDFDialog(data: DataClassForElectronicRecy) {
        val builder = AlertDialog.Builder(
            requireContext()
        )
        builder.setMessage("Вы действительно хотите удалить файл?")
            .setNegativeButton(
                "нет",
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
            .setPositiveButton(
                "да",
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                    presenter!!.deleteFile(data)
                })
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun openPDF(data: DataClassForElectronicRecy?) {
        val dialog = ShowImgPdfHtmlByPathDf()
        dialog.pathToFile = data!!.pathToFile
        dialog.show(parentFragmentManager, ShowImgPdfHtmlByPathDf::class.java.canonicalName)
    }

    fun downloadPDF(data: DataClassForElectronicRecy?) {
        data?.isHideDownload = false
        electronicConclusionsAdapter?.updateItemInRecy(data!!)
        if (data is AnaliseResponse)
            presenter!!.loadFile(data as AnaliseResponse)
        else
            presenter!!.loadFile2(data as ResultZakl2Item)
    }

    fun showErrorDownload() {
        if(context!=null  && !isDetached){
            val snackbar: Snackbar = Snackbar.make(binding.recy, resources.getString(R.string.empty_error_message), Snackbar.LENGTH_SHORT)
            val sbView = snackbar.view
            val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            textView.maxLines = 3
            snackbar.show()
        }
    }

}