package com.medhelp.callmed2.ui._main_page.fragment_user_record.recy_and_spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.ServiceItem
import com.medhelp.callmed2.databinding.ItemSearchBinding
import com.medhelp.callmed2.ui._main_page.fragment_user_record.RecordFindServiceFragment
import com.medhelp.callmed2.ui._main_page.fragment_user_record.recy_and_spinner.SearchHolder.SearchHolderListener
import java.util.*

class SearchAdapter(var mainView: RecordFindServiceFragment, private val context: Context, response: MutableList<ServiceItem>, listener: SearchHolderListener) : RecyclerView.Adapter<SearchHolder>() {

    private val mainList: List<ServiceItem>
    private var searchList: MutableList<ServiceItem>
    private val listener: SearchHolderListener
    var query = ""
    var idSpec = 0

    init {
        mainList = response
        searchList = response
        this.listener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SearchHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_search, viewGroup, false)
        val binding = ItemSearchBinding.bind(view)

        return SearchHolder(binding, listener)
    }

    override fun onBindViewHolder(searchHolder: SearchHolder, i: Int) {
        searchHolder.onBind(searchList[i])
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    fun setFilterService(query: String) {
        var query = query
        query = query.lowercase(Locale.getDefault())
        if (this.query == query) return
        this.query = query
        filtrationList()
    }

    fun setFilterSpinner(idSpec: Int) {
        if (idSpec == this.idSpec) return
        this.idSpec = idSpec
        filtrationList()
    }

    private fun filtrationList() {
        searchList = ArrayList()
        val filteredModelList: MutableList<ServiceItem> = ArrayList()
        for (model in mainList) {
            val text = model.title.lowercase(Locale.getDefault())
            if (text.contains(query)) {
                filteredModelList.add(model)
            }
        }
        if (idSpec == 0) {
            searchList.addAll(sortByWight(filteredModelList))
        } else {
            val serviceList: MutableList<ServiceItem> = ArrayList()
            for (serviceResponse in filteredModelList) {
                if (serviceResponse.idSpec == idSpec) {
                    serviceList.add(serviceResponse)
                }
            }
            searchList.addAll(sortByWight(serviceList))
        }
        if (searchList.size == 0) mainView.showRootEmpty(true) else mainView.showRootEmpty(false)
        notifyDataSetChanged()
    }

    private fun sortByWight(list: MutableList<ServiceItem>): List<ServiceItem> {
        var tmp: ServiceItem
        for (j in 0..list.size - 2) {
            var i = list.size - 1
            while (j < i) {
                val wight1 = if (list[i].poryadok == 0) 0 else 11 - list[i].poryadok
                val wight2 = if (list[i - 1].poryadok == 0) 0 else 11 - list[i - 1].poryadok
                if (wight1 > wight2) {
                    tmp = list[i - 1]
                    list[i - 1] = list[i]
                    list[i] = tmp
                }
                i--
            }
        }
        return list
    }
}