package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_analyzes.recy

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.pref.PreferencesManager
import it.sephiroth.android.library.xtooltip.Tooltip
import java.lang.Exception

class AnaliseHolder(itemView: View, recyItemListener: AnaliseRecyItemListener, context: Context) : RecyclerView.ViewHolder(itemView) {

    var title: TextView
    var imgLoadDelete: ImageView
    var shade: View?
    var progressBar: ProgressBar?
    var line: View?
    var imgEye: ImageView?

    var positionM = 0
    var downloadIn = false
    val pref: PreferencesManager
    val context: Context
    val LOAD_TYPE = "LOAD_TYPE"
    val DELETE_TYPE = "DELETE_TYPE"

    fun onBindViewHolder(position: Int, date: String?, downloadIn: Boolean, hideD: Boolean) {
        try {
            this.positionM = position
            this.downloadIn = downloadIn
            title.text = date
            hideDownload(hideD)
            if (downloadIn) {
                imgLoadDelete.isClickable = true
                imgLoadDelete.setImageResource(R.drawable.ic_delete_red_500_24dp)
                if (imgEye != null) imgEye!!.visibility = View.VISIBLE
                if (line != null) line!!.visibility = View.VISIBLE

            } else {
                imgLoadDelete.isClickable = false
                imgLoadDelete.setImageResource(R.drawable.ic_file_download_blue_600_24dp)
                if (imgEye != null) imgEye!!.visibility = View.GONE
                if (line != null) line!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.wtf("fat", e.message)
        }
    }

    fun hideDownload(boo: Boolean) {
        if (boo) {
            if (shade != null) shade!!.visibility = View.GONE
            if (progressBar != null) progressBar!!.visibility = View.GONE
        } else {
            if (shade != null) shade!!.visibility = View.VISIBLE
            if (progressBar != null) progressBar!!.visibility = View.VISIBLE
        }
    }

    init {
        title = itemView.findViewById(R.id.title)
        imgLoadDelete = itemView.findViewById(R.id.imgLoad)
        shade = itemView.findViewById(R.id.shade)
        progressBar = itemView.findViewById(R.id.progressBar)
        line = itemView.findViewById(R.id.line)
        imgEye = itemView.findViewById(R.id.eye)
        this.context = context
        pref = PreferencesManager(context)
        itemView.setOnClickListener { v: View? ->
            if (downloadIn) {
                recyItemListener.openPDF(positionM)
            } else {
                recyItemListener.downloadPDF(positionM)
            }
        }
        imgLoadDelete.setOnClickListener { v: View? -> recyItemListener.deletePDFDialog(positionM) }
    }

    interface AnaliseRecyItemListener{
        fun downloadPDF(position: Int)
        fun openPDF(position: Int)
        fun deletePDFDialog(position: Int)
    }
}