package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_month_fragment.recy

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.AllRaspSotrItem
import com.medhelp.callmed2.databinding.TimetableOnMonthItemBinding

class TimetableOnMonthHolder(val binding: TimetableOnMonthItemBinding): RecyclerView.ViewHolder(binding.root) {
    var data: AllRaspSotrItem? = null

    fun onBind(data: AllRaspSotrItem){
        this.data = data

        binding.date.text = data.data
        binding.timeFrom.text = data.start
        binding.dateTo.text = data.end
        binding.filial.text = data.naim_filial
    }
}