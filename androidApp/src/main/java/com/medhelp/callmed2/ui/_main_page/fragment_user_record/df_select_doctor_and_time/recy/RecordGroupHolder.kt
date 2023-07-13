package com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.recy

import android.view.View
import android.widget.TextView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.databinding.ItemRecordGroupeHolderBinding
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder

class RecordGroupHolder(val bindingItem: ItemRecordGroupeHolderBinding) : GroupViewHolder(bindingItem.root) {
    var date: String? = null
    var preferencesManager: PreferencesManager

    init {
        preferencesManager = PreferencesManager(itemView.context)
    }

    fun onBind(date: String?) {
        this.date = date
        bindingItem.title!!.text = date
        testHint()
    }

    private fun testHint() {
//        if (!preferencesManager.getTooltipRecordDateItem()) {
//            preferencesManager.setTooltipRecordDateItem();
//            title.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    showTooltipPlus(title);
//                }
//            }, 500);
//        }
    } //    private void showTooltipPlus(View view)
    //    {
    //        if(view==null)
    //            return;
    //        Tooltip builder = new Tooltip.Builder(view.getContext())
    //                .anchor(view, 0, 0,false)
    //                .closePolicy(
    //                        new ClosePolicy(100))
    //                .activateDelay(0)
    //                .text(view.getContext().getString(R.string.tooltipRecordDateItem))
    //                .maxWidth(620)
    //                .showDuration(20000)
    //                .arrow(true)
    //                .styleId(R.style.ToolTipLayoutCustomStyle)
    //                .overlay(true) //мигающий круглешок
    //                .create();
    //
    //        builder.show(view, Tooltip.Gravity.BOTTOM,false);
    //    }
}