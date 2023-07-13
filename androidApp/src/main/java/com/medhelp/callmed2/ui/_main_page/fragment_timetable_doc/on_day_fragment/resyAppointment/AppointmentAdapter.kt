package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.resyAppointment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.VisitResponse
import com.medhelp.callmed2.databinding.ItemAppointmentBinding

class AppointmentAdapter(var context: Context, var list: List<VisitResponse>) :
    RecyclerView.Adapter<AppointmentHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false)
        val bindingItem = ItemAppointmentBinding.bind(view)
        return AppointmentHolder(bindingItem)
    }

    override fun onBindViewHolder(holder: AppointmentHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun clear() {
        list = ArrayList()
        notifyDataSetChanged()
    }
}