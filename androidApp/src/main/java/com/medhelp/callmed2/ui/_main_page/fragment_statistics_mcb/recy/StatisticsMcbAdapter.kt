package com.medhelp.callmed2.ui._main_page.fragment_statistics_mcb.recy

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.MkbItem
import java.util.*

class StatisticsMcbAdapter(val context: Context, var list: MutableList<MkbItem>): RecyclerView.Adapter<StatisticsMcbHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsMcbHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.statistics_mcb_item,parent,false)
        return StatisticsMcbHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: StatisticsMcbHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun sortByMcb(){
        list.sortBy{it.kodMkb}
        notifyDataSetChanged()
    }
    fun sortByCount(){
        list.sortByDescending{it.count}
        notifyDataSetChanged()
    }

    fun listToString(): String {
        var str = ""
        str += "КОД МКБ10"+": " + "КОЛИЧЕСТВО"+ "\n"
        for(i in list){
            str += i.kodMkb + ": " + i.count.toString() + "\n"
        }
        
        return str
    }

}