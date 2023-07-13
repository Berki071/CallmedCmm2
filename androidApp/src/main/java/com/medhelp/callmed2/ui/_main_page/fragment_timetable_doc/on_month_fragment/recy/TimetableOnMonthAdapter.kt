package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_month_fragment.recy

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.AllRaspSotrItem
import com.medhelp.callmed2.databinding.TimetableOnMonthItemBinding

class TimetableOnMonthAdapter(val context: Context, val list : List<AllRaspSotrItem>): RecyclerView.Adapter<TimetableOnMonthHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableOnMonthHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.timetable_on_month_item, parent, false)
        val binding = TimetableOnMonthItemBinding.bind(view)
        return TimetableOnMonthHolder(binding)
    }

    override fun getItemCount(): Int  = list.size

    override fun onBindViewHolder(holder: TimetableOnMonthHolder, position: Int) {
        holder.onBind(list[position])
    }
}