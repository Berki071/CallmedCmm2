package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_analyzes

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.AnaliseResponseAndroid
import com.medhelp.callmed2.databinding.DfShowAnalyzsesBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.df.ShowImgPdfHtmlByPathDf
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_analyzes.recy.AnaliseAdapter
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_analyzes.recy.AnaliseHolder
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem

class ShowAnalyzesAlert: DialogFragment() {
    lateinit var recordItem: AllRecordsTelemedicineItem  //при создании диалога!!!

    lateinit var binding: DfShowAnalyzsesBinding
    var presenter: ShowAnalyzesPresenter? = null
    private var adapter: AnaliseAdapter? = null
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
        val v = inflater.inflate(R.layout.df_show_analyzses, null)
        binding = DfShowAnalyzsesBinding.bind(v)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = ShowAnalyzesPresenter(this)

        binding.errorDownload.errTvMessage.setVisibility(View.GONE)
        binding.errorDownload.errLoadBtn.setVisibility(View.GONE)

        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        presenter!!.updateAnaliseList()
    }

    private fun setupToolbar() {
        binding.toolbar!!.setNavigationOnClickListener { dialog?.dismiss() }


    }

    fun showErrorScreen() {
        binding.recyclerView?.setVisibility(View.GONE)
        binding.errorDownload.errTvMessage?.setVisibility(View.VISIBLE)
        binding.errorDownload.errLoadBtn?.setVisibility(View.VISIBLE)
        binding.errorDownload.errLoadBtn?.setOnClickListener(View.OnClickListener { v: View? -> presenter!!.updateAnaliseList() })
    }


    //endregion
    fun updateAnaliseData(response: MutableList<AnaliseResponseAndroid>) {
        try {
            if (response.size == 0 || response[0].linkToPDF == null) {
                binding.rootEmpty?.setVisibility(ViewGroup.VISIBLE)
                binding.recyclerView?.setVisibility(View.GONE)
                return
            }
            binding.rootEmpty?.setVisibility(ViewGroup.GONE)
            binding.recyclerView?.setVisibility(View.VISIBLE)
            binding.errorDownload.errTvMessage?.setVisibility(View.GONE)
            binding.errorDownload.errLoadBtn?.setVisibility(View.GONE)
            if (adapter == null) {
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
                binding.recyclerView?.setLayoutManager(layoutManager)
                adapter = AnaliseAdapter(requireContext(), response, object : AnaliseHolder.AnaliseRecyItemListener {
                    override fun downloadPDF(position: Int) {
                        adapter?.list?.get(position)?.isHideDownload = false
                        adapter?.checkList()
                        adapter?.notifyItemChanged(position)
                        presenter!!.loadFile(position, adapter!!.list)
                    }

                    override fun openPDF(position: Int) {
                        val dialog = ShowImgPdfHtmlByPathDf()
                        dialog.pathToFile = adapter!!.list.get(position).pathToFile
                        dialog.show(parentFragmentManager, ShowImgPdfHtmlByPathDf::class.java.canonicalName)
                    }

                    override fun deletePDFDialog(position: Int) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Вы действительно хотите удалить файл?")
                            .setNegativeButton(
                                "нет",
                                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
                            .setPositiveButton(
                                "да",
                                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                                    dialog.dismiss()
                                    presenter!!.deleteFile(position, adapter!!.list)
                                })
                        val alertDialog = builder.create()
                        alertDialog.show()
                    }

                })
                binding.recyclerView?.setAdapter(adapter)
            } else {
                adapter?.setListAnal(response)
            }
        } catch (e: Exception) {
            binding.rootEmpty?.setVisibility(ViewGroup.VISIBLE)
            binding.recyclerView?.setVisibility(View.GONE)
            Log.wtf("fat", e.message)
        }
    }

    fun showErrorDownload() {
        if(context!=null  && !isDetached){
            val snackbar: Snackbar = Snackbar.make(binding.recyclerView, resources.getString(R.string.empty_error_message), Snackbar.LENGTH_SHORT)
            val sbView = snackbar.view
            val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            textView.maxLines = 3
            snackbar.show()
        }
    }
}