package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.medhelp.callmed2.R
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder

class T1ListOfEntriesActiveTitleHolder(itemView: View) : GroupViewHolder(itemView) {
    var title : TextView = itemView.findViewById(R.id.title)
    var imgArrow : ImageView = itemView.findViewById(R.id.imgArrow)

    override fun expand() {
        imgArrow.setImageResource(R.drawable.ic_arrow_up)
    }

    override fun collapse() {
        imgArrow.setImageResource(R.drawable.ic_arrow_down)
    }

    fun onBind(group: ExpandableGroup<*>?){
        group?.let {
            title.text = it.title
        }
    }
}