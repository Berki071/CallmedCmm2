package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_analyzes.recy

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.AnaliseResponseAndroid
import com.medhelp.callmedcmm2.model.chat.AnaliseResponse
import java.lang.Exception

class AnaliseAdapter(val context: Context, var list: MutableList<AnaliseResponseAndroid>, val recyItemListener: AnaliseHolder.AnaliseRecyItemListener) : RecyclerView.Adapter<AnaliseHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnaliseHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_analise, parent, false)
        return AnaliseHolder(view, recyItemListener, context)
    }

    override fun onBindViewHolder(holder: AnaliseHolder, position: Int) {
        try {
            holder.onBindViewHolder(
                position,
                getItem(position).date,
                getItem(position).isDownloadIn!!,
                getItem(position).isHideDownload
            )
        } catch (e: Exception) {
            Log.wtf("fat", e.message)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun getItem(num: Int): AnaliseResponse {
        return list[num]
    }

    fun setListAnal(list: MutableList<AnaliseResponseAndroid>) {
        this.list = list
        checkList()
        notifyDataSetChanged()
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
            if (ar.isDownloadIn!!) {
                ar.isShowTooltip = load
                if (load) load = false
            }
            if (!ar.isDownloadIn!!) {
                ar.isShowTooltip = delete
                if (delete) delete = false
            }
        }
    }

    init {
        checkList()
    }
}