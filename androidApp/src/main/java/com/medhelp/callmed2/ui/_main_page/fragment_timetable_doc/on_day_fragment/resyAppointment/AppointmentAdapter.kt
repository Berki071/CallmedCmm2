package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.resyAppointment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.ItemAppointmentBinding
import com.medhelp.callmedcmm2.model.VisitResponse.VisitItem

class AppointmentAdapter(var context: Context, var list: List<VisitItem>, val listener: AppointmentHolder.AppointmentHolderListener) :
    RecyclerView.Adapter<AppointmentHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_appointment, parent, false)
        val bindingItem = ItemAppointmentBinding.bind(view)
        return AppointmentHolder(bindingItem, listener)
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

    fun updateConfirmedItem(item: VisitItem){
        for(i in 0 until list.size){
            if(item.id_zap == list[i].id_zap)
                notifyItemChanged(i)
        }
    }
}