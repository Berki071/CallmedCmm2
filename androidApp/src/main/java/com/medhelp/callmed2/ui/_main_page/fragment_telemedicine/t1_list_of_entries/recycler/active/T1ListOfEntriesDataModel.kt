package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active

import com.medhelp.callmed2.data.model.AllRecordsTelemedicineItemAndroid
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

data class T1ListOfEntriesDataModel(val titleM: String, val list: List<AllRecordsTelemedicineItemAndroid>): ExpandableGroup<AllRecordsTelemedicineItemAndroid>(titleM,list)