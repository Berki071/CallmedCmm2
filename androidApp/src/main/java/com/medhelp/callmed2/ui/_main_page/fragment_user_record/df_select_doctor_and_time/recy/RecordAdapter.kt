package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.recy

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.ScheduleItem
import com.medhelp.callmed2.databinding.ItemRecordGroupeHolderBinding
import com.medhelp.callmed2.databinding.ItemRecordItemHolderBinding
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.data_model.DataRecyServiceRecord
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.recy.RecordItemHolder.RecordItemHolderListener
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class RecordAdapter(groups: List<ExpandableGroup<*>?>?, var context: Context, var listener: RecordItemHolderListener) : ExpandableRecyclerViewAdapter<RecordGroupHolder, RecordItemHolder>(groups) {
    var list: List<DataRecyServiceRecord>? = null
    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): RecordGroupHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_record_groupe_holder, parent, false)
        val bindingItem = ItemRecordGroupeHolderBinding.bind(view)
        return RecordGroupHolder(bindingItem)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): RecordItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_record_item_holder, parent, false)
        val bindingItem = ItemRecordItemHolderBinding.bind(view)
        return RecordItemHolder(bindingItem, listener)
    }

    override fun onBindChildViewHolder(
        holder: RecordItemHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>,
        childIndex: Int
    ) {
        holder.onBind(
            group.items[childIndex] as ScheduleItem,
            getClearDateFromTitleGrope(group.title)
        )
    }

    override fun onBindGroupViewHolder(
        holder: RecordGroupHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>
    ) {
        holder.onBind(group.title + " (" + getCountTimes(group) + ")")
    }

    private fun getCountTimes(group: ExpandableGroup<*>): Int {
        var count = 0
        for (tmpItem in group.items as List<ScheduleItem>) {
            if(tmpItem.admTime != null)
                count += tmpItem.admTime.size
        }
        return count
    }

    private fun getClearDateFromTitleGrope(str: String): String {
        val numCh = str.indexOf(" ")
        return if (numCh == -1) str else str.substring(0, numCh)
    }
}