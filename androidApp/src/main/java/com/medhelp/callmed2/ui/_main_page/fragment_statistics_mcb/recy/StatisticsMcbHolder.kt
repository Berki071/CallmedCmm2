package com.medhelp.callmed2.ui._main_page.fragment_statistics_mcb.recy

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.MkbItem

class StatisticsMcbHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mainBox = itemView.findViewById<LinearLayout>(R.id.mainBox)
    var kod = itemView.findViewById<TextView>(R.id.kod)
    var sum = itemView.findViewById<TextView>(R.id.sum)
//    var bottomBox = itemView.findViewById<LinearLayout>(R.id.bottomBox)
//    var naim = itemView.findViewById<TextView>(R.id.naim)

    var item: MkbItem? = null

//    init {
//        bottomBox.visibility = View.GONE
//
//        mainBox.setOnClickListener {
//            if(bottomBox.visibility == View.GONE)
//                bottomBox.visibility = View.VISIBLE
//            else
//                bottomBox.visibility = View.GONE
//        }
//    }

    fun onBind(item: MkbItem){
        this.item = item
        kod.text = item.kodMkb
        sum.text = item.count.toString()
       // naim.text = item.mkb_naim
    }
}