package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.resyAppointment

import android.graphics.Typeface
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.databinding.ItemAppointmentBinding
import com.medhelp.callmed2.utils.main.MainUtils
import com.medhelp.callmedcmm2.model.VisitResponse.VisitItem


private const val INDIVIDUAL_DB_NAME = "msoli"
class AppointmentHolder(val bindingItem: ItemAppointmentBinding, val listener: AppointmentHolderListener) : RecyclerView.ViewHolder(bindingItem.root) {
    var data: VisitItem? = null
    val dbName: String?


    init {
        dbName = PreferencesManager(context = itemView.context).centerInfo?.db_name

        bindingItem.topConstr.setOnClickListener {
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

        if (dbName == INDIVIDUAL_DB_NAME) {
            bindingItem.topConstr.setOnLongClickListener {
                if (data != null && data!!.statPriem != "p") {
                    showConfirmationMenu()
                    true
                } else
                    false
            }
        }
    }


    fun onBind(data: VisitItem) {
        bindingItem.dropDawnPanel!!.visibility = View.GONE
        this.data = data
        if (data.naim == null || data.naim == "") {
            bindingItem.name!!.setTextColor(itemView.context.resources.getColor(R.color.dark_gray))
            bindingItem.name!!.setTypeface(bindingItem.name!!.typeface, Typeface.NORMAL)
            bindingItem.time!!.text = data.time
            bindingItem.name!!.text = "Нет записи"
            bindingItem.topConstr!!.isClickable = false
        } else {
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

            if(dbName == INDIVIDUAL_DB_NAME){
                if(data.statPriem == "p"){
                    bindingItem.name!!.setTextColor(itemView.context.resources.getColor(R.color.greenText2))
                }else{
                    bindingItem.name!!.setTextColor(itemView.context.resources.getColor(R.color.red))
                }

            }else{
                bindingItem.name!!.setTextColor(itemView.context.resources.getColor(R.color.greenText2))
            }
        }
    }



    private fun showConfirmationMenu() {
        val popupMenu = PopupMenu(itemView.context, itemView)
        popupMenu.inflate(R.menu.confirmation_menu)

        popupMenu
            .setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuСonfirmation -> {
                        listener.appointmentConfirmed(data!!)
                        true
                    }

                    else -> false
                }
            }

        popupMenu.show()
    }

    interface AppointmentHolderListener{
        fun appointmentConfirmed(item: VisitItem)
    }
}