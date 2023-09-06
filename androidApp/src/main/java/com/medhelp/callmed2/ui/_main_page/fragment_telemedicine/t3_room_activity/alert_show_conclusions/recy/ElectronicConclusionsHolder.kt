package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.alert_show_conclusions.recy

import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmedcmm2.model.chat.DataClassForElectronicRecy

class ElectronicConclusionsHolder(itemView: View, var listener: ElectronicConclusionsHolderListener?) : RecyclerView.ViewHolder(itemView) {
    var data: DataClassForElectronicRecy? = null
    var dataBox: ConstraintLayout
    var date: TextView
    var title: TextView
    var hideBox: LinearLayout
    var btnShow: ImageView
    var btnDelete: ImageView
    var dividedLine: View
    var shade: View?
    var progressBar: ProgressBar?
    fun processingHideBox() {
        if (!data!!.isShowHideBox) {
            hideBox.visibility = View.GONE
        } else {
            hideBox.visibility = View.VISIBLE
        }
    }

    fun onBind(data: DataClassForElectronicRecy) {
        this.data = data
        hideDownload(data.isHideDownload)
        date.text = Html.fromHtml("<b>" + data.datePer + "</b>")
        title.text = data.textTitle

        processingHideBox()
        if (data.isDownloadIn) {
            btnShow.setImageResource(R.drawable.ic_visibility_light_blue_400_24dp)
            dividedLine.visibility = View.VISIBLE
            btnDelete.visibility = View.VISIBLE
        } else {
            btnShow.setImageResource(R.drawable.ic_file_download_blue_600_24dp)
            dividedLine.visibility = View.GONE
            btnDelete.visibility = View.GONE
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
        date = itemView.findViewById(R.id.date)
        title = itemView.findViewById(R.id.title)
        hideBox = itemView.findViewById(R.id.hideBox)
        btnShow = itemView.findViewById(R.id.btnShow)
        btnDelete = itemView.findViewById(R.id.btnDelete)
        dataBox = itemView.findViewById(R.id.dataBox)
        dividedLine = itemView.findViewById(R.id.dividedLine)
        shade = itemView.findViewById(R.id.shade)
        progressBar = itemView.findViewById(R.id.progressBar)
        dataBox.setOnClickListener { v: View? ->
            data!!.isShowHideBox = !data!!.isShowHideBox
            if (listener != null) listener!!.openHideBox(data!!)
        }
        btnShow.setOnClickListener { v: View? -> if (listener != null) listener!!.showDoc(data!!) }
        btnDelete.setOnClickListener { v: View? -> if (listener != null) listener!!.deleteDoc(data!!) }
    }

    interface ElectronicConclusionsHolderListener {
        fun openHideBox(data: DataClassForElectronicRecy)
        fun showDoc(data: DataClassForElectronicRecy)
        fun deleteDoc(data: DataClassForElectronicRecy)
    }
}