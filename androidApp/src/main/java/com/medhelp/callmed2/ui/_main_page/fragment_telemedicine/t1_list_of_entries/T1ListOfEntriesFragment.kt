package com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.bg.service.MyFirebaseMessagingService
import com.medhelp.callmed2.data.bg.service.PadForMyFirebaseMessagingService
import com.medhelp.callmed2.data.model.AllRecordsTelemedicineItemAndroid
import com.medhelp.callmed2.databinding.FragmentChatWithDoctorBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_chat_with_doctor.recycler.active.T1ListOfEntriesActiveAdapter
import com.medhelp.callmed2.ui._main_page.fragment_chat_with_doctor.recycler.archive.T1ListOfEntriesArchiveAdapter
import com.medhelp.callmed2.ui._main_page.fragment_chat_with_doctor.recycler.archive.T1ListOfEntriesArchiveHolder
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active.T1ListOfEntriesActiveChildHolder
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t1_list_of_entries.recycler.active.T1ListOfEntriesDataModel
import com.medhelp.callmed2.ui._main_page.fragment_telemedicine.t3_room_activity.T3RoomActivity
import com.medhelp.callmed2.ui.base.BaseFragment
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse
import com.medhelp.callmedcmm2.model.chat.AllRecordsTelemedicineResponse.AllRecordsTelemedicineItem
import timber.log.Timber


class T1ListOfEntriesFragment : BaseFragment() {
    lateinit var binding: FragmentChatWithDoctorBinding
    lateinit var presenter: T1ListOfEntriesPresenter

    var whatDataShow: String = WhatDataShow.ACTIVE.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Timber.i("Чат c доктором")
        val rootView = inflater.inflate(R.layout.fragment_chat_with_doctor, container, false)
        binding = FragmentChatWithDoctorBinding.bind(rootView)

        presenter = T1ListOfEntriesPresenter()
        presenter.onAttachView(this)
        setHasOptionsMenu(true)

        val intent = (context as MainPageActivity).intent
        if (intent != null) {
            whatDataShow = intent.getStringExtra("whatDataShow") ?: WhatDataShow.ACTIVE.toString()
        }

        return rootView
    }

    override fun setUp(view: View) {
        binding.rootEmpty.visibility = View.GONE

        if (!presenter!!.isCheckLoginAndPassword) return

        setupRefresh()
        setupToolbar()

        presenter.getData(if(whatDataShow == WhatDataShow.ACTIVE.toString()) "new" else "old")
        presenter.areThereAnyNewTelemedicineMsg()

        PadForMyFirebaseMessagingService.listener = object: MyFirebaseMessagingService.MyFirebaseMessagingServiceListener{
            override fun updateRecordInfo() {
                presenter.getData(if(whatDataShow == WhatDataShow.ACTIVE.toString()) "new" else "old")
            }
        }
    }

    override fun onDestroy() {
        PadForMyFirebaseMessagingService.listener = null
        presenter.onDetachView()
        super.onDestroy()
    }

    private fun setupRefresh() {
        binding.swipeProfile!!.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                presenter.getData(if(whatDataShow == WhatDataShow.ACTIVE.toString()) "new" else "old")
            }
        })
        binding.swipeProfile!!.setColorSchemeColors(
            getResources().getColor(android.R.color.holo_blue_bright),
            getResources().getColor(android.R.color.holo_green_light),
            getResources().getColor(android.R.color.holo_orange_light),
            getResources().getColor(android.R.color.holo_red_light)
        )
    }

    private fun setupToolbar() {
        (context as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        val actionBar = (context as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar!!.setNavigationOnClickListener { (context as MainPageActivity?)!!.showNavigationMenu() }
    }
    var searchViewItem: MenuItem? = null
    var searchViewAndroidActionBar: SearchView? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chat_with_doctor, menu)

        val btnArchive = menu.findItem(R.id.archive)
        val btnUnarchive = menu.findItem(R.id.unarchive)

        searchViewItem = menu.findItem(R.id.searchBtn)
        searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as SearchView


        if(whatDataShow == WhatDataShow.ACTIVE.toString()) {
            binding.toolbar!!.title = "Рабочие чаты"
            btnUnarchive.isVisible=false
            searchViewItem?.isVisible=false
        }else {
            binding.toolbar!!.title = "Архивные чаты"
            btnArchive.isVisible=false
            searchViewItem?.isVisible=true
        }

        if (searchViewAndroidActionBar != null) {
            searchViewAndroidActionBar!!.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchViewAndroidActionBar?.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean { presenter?.setFilterService(newText)
                    return true
                }
            })
        }

        btnArchive.setOnMenuItemClickListener {
            btnArchive.isVisible=false
            btnUnarchive.isVisible=true
            searchViewItem?.isVisible=true
            presenter.query = ""
            binding.toolbar!!.title = "Архивные чаты"
            whatDataShow = WhatDataShow.ARCHIVE.toString()
            presenter.getData(if(whatDataShow == WhatDataShow.ACTIVE.toString()) "new" else "old")
            true
        }

        btnUnarchive.setOnMenuItemClickListener {
            btnArchive.isVisible=true
            btnUnarchive.isVisible=false

            presenter.query = ""
            binding.toolbar!!.title = "Рабочие чаты"
            whatDataShow = WhatDataShow.ACTIVE.toString()
            presenter.getData(if(whatDataShow == WhatDataShow.ACTIVE.toString()) "new" else "old")

            searchViewAndroidActionBar?.let{
                if(!it.isIconified) {
                    it.isIconified = true
                    it.onActionViewCollapsed()
                    binding.toolbar.collapseActionView();
                }
            }
            searchViewItem?.isVisible=false

            true
        }
    }

    var adapterActive: T1ListOfEntriesActiveAdapter? = null
    fun updateRecyActive(list: MutableList<T1ListOfEntriesDataModel>) {
        if(list.size == 0){
            binding.recy.visibility = View.GONE
            binding.rootEmpty.visibility = View.VISIBLE
            return
        }

        binding.recy.visibility = View.VISIBLE
        binding.rootEmpty.visibility = View.GONE

        adapterActive = T1ListOfEntriesActiveAdapter(requireContext(), list, object:
            T1ListOfEntriesActiveChildHolder.ChatWithDoctorActiveChildHolderListener{
            override fun enterTheRoom(item: AllRecordsTelemedicineItemAndroid) {
                showRoomActivity(item)
            }

            override fun closeTm(item: AllRecordsTelemedicineItemAndroid) {
                presenter.closeRecordTelemedicine(item)
            }

            override fun sendNotyReminder(item: AllRecordsTelemedicineItemAndroid, msg: String, type: String) {
                presenter.sendMsgNotificationTimeReminder(item,msg)
                presenter.updateTelemedicineReminderDocAboutRecord(item,type)
            }
        })
        binding.recy.layoutManager = LinearLayoutManager(context)
        binding.recy.adapter = adapterActive

        adapterActive?.onGroupClick(0)
    }

    fun updateRecyArchive(list: List<AllRecordsTelemedicineItem>) {
        if(list.size == 0 || list[0].idRoom == null){
            binding.recy.visibility = View.GONE
            binding.rootEmpty.visibility = View.VISIBLE
            return
        }

        binding.recy.visibility = View.VISIBLE
        binding.rootEmpty.visibility = View.GONE

        val adapter = T1ListOfEntriesArchiveAdapter(requireContext(), list, object:
            T1ListOfEntriesArchiveHolder.T1ListOfEntriesArchiveHolderListener {
            override fun enterTheRoom(inf: AllRecordsTelemedicineItem) {
                showRoomActivity(inf)
            }
        })
        binding.recy!!.layoutManager = LinearLayoutManager(context)
        binding.recy!!.adapter = adapter
    }

    private fun showRoomActivity(item: AllRecordsTelemedicineItem) {
        val gson = Gson()
        val strItem = gson.toJson(item)

        val newIntent = Intent(context, T3RoomActivity::class.java)
        newIntent.putExtra("recItem", strItem)
        newIntent.putExtra("whatDataShow", whatDataShow)
        (requireContext() as MainPageActivity).startActivity(newIntent)
        (requireContext() as MainPageActivity).finish()
    }
    override fun onDestroyB() {}
    override fun onStartSetStatusFragment(status: Int) {}
    fun showError() {}

    enum class WhatDataShow{
        ARCHIVE, ACTIVE
    }

}