package com.medhelp.callmed2.ui._main_page.fragment_chat_with_doctor.recycler.active

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.ItemChatWithDoctorBinding
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active.T1ListOfEntriesActiveChildHolder
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active.T1ListOfEntriesActiveTitleHolder
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active.T1ListOfEntriesDataModel
import com.medhelp.callmedcmm2.model.chat.HasPacChatsResponse
import com.medhelp.callmedcmm2.model.chat.HasPacChatsResponse.HasPacChatsItem
import com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class T1ListOfEntriesActiveAdapter(val context: Context, var list: MutableList<T1ListOfEntriesDataModel>, val listener : T1ListOfEntriesActiveChildHolder.ChatWithDoctorActiveChildHolderListener) :
    MultiTypeExpandableRecyclerViewAdapter<T1ListOfEntriesActiveTitleHolder, T1ListOfEntriesActiveChildHolder>(list) {

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): T1ListOfEntriesActiveTitleHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.item_groupe,parent, false)
        return T1ListOfEntriesActiveTitleHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): T1ListOfEntriesActiveChildHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat_with_doctor, parent, false)
        val bindItem= ItemChatWithDoctorBinding.bind(view)
        return T1ListOfEntriesActiveChildHolder(bindItem, listener)
    }

    override fun onBindGroupViewHolder(holder: T1ListOfEntriesActiveTitleHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder?.onBind(group)
    }

    override fun onBindChildViewHolder(
        holder: T1ListOfEntriesActiveChildHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        val itm = (group as T1ListOfEntriesDataModel).items[childIndex]
        holder?.onBindView(itm)
    }

    fun processingHasNewMsg(listId: List<HasPacChatsItem>){
        for(i in 0 until list.size){
            for(j in 0 until list[i].items.size){

                var isEditLatch = false

                for(k in listId){
                    if(k.idRoom!! == (list[i].items[j].idRoom!!)){
                        isEditLatch = true
                        if(!list[i].items[j].isShowNewMsgIco) {
                            list[i].items[j].isShowNewMsgIco = true
                            val flatChildPos: Int = expandableList.getFlattenedChildIndex(i, j)
                            notifyItemChanged(flatChildPos)
                        }
                        break
                    }
                }

                if(!isEditLatch && list[i].items[j].isShowNewMsgIco){
                    list[i].items[j].isShowNewMsgIco = false
                    val flatChildPos: Int = expandableList.getFlattenedChildIndex(i, j)
                    notifyItemChanged(flatChildPos)
                }
            }
        }
    }
}