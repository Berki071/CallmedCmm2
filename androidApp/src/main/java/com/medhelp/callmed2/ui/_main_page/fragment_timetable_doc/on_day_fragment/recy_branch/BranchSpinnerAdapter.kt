package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc.on_day_fragment.recy_branch

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SpinnerAdapter
import android.widget.TextView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.timetable.SettingsAllBranchHospitalResponse
import com.medhelp.callmed2.utils.Different

class BranchSpinnerAdapter(private val context: Context, private val list: List<SettingsAllBranchHospitalResponse>) : BaseAdapter(), SpinnerAdapter {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(i: Int): Any {
        return list[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getDropDownView(position: Int, view: View?, parent: ViewGroup?): View {
        val txt = TextView(context)
        txt.setPadding(16, 16, 16, 16)
        txt.textSize = 18f
        txt.gravity = Gravity.CENTER_VERTICAL
        txt.text = list[position].nameBranch
        txt.setTextColor(Color.parseColor("#FF424242"))
        return txt
    }

    override fun getView(i: Int, view: View?, viewgroup: ViewGroup?): View {
        val txt = TextView(context)
        txt.gravity = Gravity.CENTER
        txt.setPadding(16, 16, 16, 16)
        txt.textSize = 16f
        txt.text = list[i].nameBranch
        txt.setTextColor(Color.parseColor("#212121"))
        txt.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_account_balance_blue_24dp,
            0,
            0,
            0
        )
        txt.compoundDrawablePadding = Different.convertDpToPixel(8f, context).toInt()
        return txt
    }

    fun getListItem(i: Int): SettingsAllBranchHospitalResponse {
        return list[i]
    }
}