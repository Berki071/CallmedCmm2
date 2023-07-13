package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.resyAppointment

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.VisitResponse
import com.medhelp.callmed2.databinding.ItemAppointmentBinding
import com.medhelp.callmed2.utils.main.MainUtils

class AppointmentHolder(val bindingItem: ItemAppointmentBinding) : RecyclerView.ViewHolder(bindingItem.root) {
    var data: VisitResponse? = null

    init {
        bindingItem.topConstr!!.setOnClickListener {
            if (bindingItem.dropDawnPanel!!.visibility == View.VISIBLE) {
                bindingItem.dropDawnPanel!!.visibility = View.GONE
                bindingItem.topConstr!!.layoutParams.height = MainUtils.dpToPx(itemView.context, 40)
            } else {
                bindingItem.topConstr!!.layoutParams.height = MainUtils.dpToPx(itemView.context, 30)
                bindingItem.dropDawnPanel!!.visibility = View.VISIBLE
            }
        }
        bindingItem.dropDawnPanel!!.setOnClickListener {
            bindingItem.dropDawnPanel!!.visibility = View.GONE
            bindingItem.topConstr!!.layoutParams.height = MainUtils.dpToPx(itemView.context, 40)
        }
    }

    fun onBind(data: VisitResponse) {
        bindingItem.dropDawnPanel!!.visibility = View.GONE
        this.data = data
        if (data.naim == null || data.naim == "") {
            bindingItem.name!!.setTextColor(itemView.context.resources.getColor(R.color.dark_gray))
            bindingItem.name!!.setTypeface(bindingItem.name!!.typeface, Typeface.NORMAL)
            bindingItem.time!!.text = data.time
            bindingItem.name!!.text = "Нет записи"
            bindingItem.topConstr!!.isClickable = false
        } else {
            bindingItem.name!!.setTextColor(itemView.context.resources.getColor(R.color.greenText2))
            bindingItem.name!!.setTypeface(bindingItem.name!!.typeface, Typeface.BOLD)
            bindingItem.topConstr!!.isClickable = true
            bindingItem.service!!.text = data.naim
            bindingItem.name!!.text = data.fullName
            bindingItem.time!!.text = data.time
            bindingItem.topConstr!!.layoutParams.height = MainUtils.dpToPx(itemView.context, 40)
            if (data.komment == null || data.komment == "") {
                bindingItem.comment!!.visibility = View.GONE
                bindingItem.titleComment!!.visibility = View.GONE
            } else {
                bindingItem.comment!!.visibility = View.VISIBLE
                bindingItem.titleComment!!.visibility = View.VISIBLE
                bindingItem.comment!!.text = data.komment
            }
        }
    }
}