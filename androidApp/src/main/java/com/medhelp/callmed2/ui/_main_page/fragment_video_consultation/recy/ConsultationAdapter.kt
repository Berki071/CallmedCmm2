//package com.medhelp.callmed2.ui._main_page.fragment_video_consultation.recy
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.medhelp.callmed2.R
//import com.medhelp.callmed2.data.model.VisitItem2
//import com.medhelp.callmed2.databinding.ItemOnlineConsultationBinding
//
//class ConsultationAdapter(
//    private val context: Context,
//    private var list: List<VisitItem2>,
//    private val token: String,
//    var listener: ConsultationListener
//) : RecyclerView.Adapter<ConsultationHolder>() {
//    fun setList(list: List<VisitItem2>) {
//        this.list = list
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationHolder {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_online_consultation, parent, false)
//        val bindingItem = ItemOnlineConsultationBinding.bind(view)
//
//        return ConsultationHolder(bindingItem, token, listener)
//    }
//
//    override fun onBindViewHolder(holder: ConsultationHolder, position: Int) {
//        holder.onBindView(list[position])
//    }
//
//    override fun getItemCount(): Int {
//        return list.size
//    }
//
//    override fun getItemId(position: Int): Long {
//        return position.toLong()
//    }
//
//    interface ConsultationListener {
//        fun onClickItemRecy(itm: VisitItem2)
//    }
//}