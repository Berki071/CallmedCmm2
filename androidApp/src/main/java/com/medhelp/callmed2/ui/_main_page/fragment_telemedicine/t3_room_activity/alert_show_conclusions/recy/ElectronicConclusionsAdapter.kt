package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_conclusions.recy

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmedcmm2.model.chat.DataClassForElectronicRecy

class ElectronicConclusionsAdapter(
    var context: Context,
    var list: List<DataClassForElectronicRecy>,
    var listener: ElectronicConclusionsHolder.ElectronicConclusionsHolderListener
) : RecyclerView.Adapter<ElectronicConclusionsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectronicConclusionsHolder {
        val v = LayoutInflater.from(context)
            .inflate(R.layout.item_electronic_conclusions, parent, false)
        return ElectronicConclusionsHolder(v, listener)
    }

    override fun onBindViewHolder(holder: ElectronicConclusionsHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun checkList() {
        //подсказки, определение элемента от которого будет показываться
        var load = true
        var delete = true
        for (i in list.indices) {
            if (!load && !delete) {
                break
            }
            val ar = list[i]
            if (ar.isDownloadIn) {
                ar.isShowTooltip = load
                if (load) load = false
            }
            if (!ar.isDownloadIn) {
                ar.isShowTooltip = delete
                if (delete) delete = false
            }
        }
    }

    fun updateItemInRecy(item: DataClassForElectronicRecy) {
        for (i in list.indices) {
            if (list[i].datePer.equals(item.datePer)) {
                notifyItemChanged(i)
            }
        }
    }

    fun processingShowHideBox(item: DataClassForElectronicRecy) {
        for (i in list.indices) {
            if (!list[i].datePer.equals(item.datePer)) {
                list[i].isShowHideBox = false
            }
        }
        notifyDataSetChanged()
    }
}