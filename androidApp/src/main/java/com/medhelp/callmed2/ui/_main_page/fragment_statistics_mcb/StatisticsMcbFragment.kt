package com.medhelp.callmed2.ui._main_page.fragment_statistics_mcb

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.MkbItem
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_statistics_mcb.recy.StatisticsMcbAdapter
import com.medhelp.callmed2.ui._main_page.fragment_statistics_mcb.views.SelectDatesForStatisticMkbView
import com.medhelp.callmed2.ui.base.BaseFragment
import com.medhelp.callmed2.utils.main.MDate
import timber.log.Timber
import java.util.*

class StatisticsMcbFragment : BaseFragment() {

    lateinit var toolbar : Toolbar
    lateinit var topSelectDates : SelectDatesForStatisticMkbView
    lateinit var mainBoxTitles : LinearLayout
    lateinit var titleKod: TextView
    lateinit var titleSum: TextView
    lateinit var recy: RecyclerView
    lateinit var presenter: StatisticsMcbPresenter
    lateinit var rootEmpty: LinearLayout
    lateinit var sendData: Button

    var adapter: StatisticsMcbAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Timber.i("Статистика МКБ")
        val view = inflater.inflate(R.layout.fragment_statistecs_mcb, container, false)
        return view
    }

    override fun setUp(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        topSelectDates = view.findViewById(R.id.topSelectDates)
        mainBoxTitles = view.findViewById(R.id.mainBoxTitles)
        titleKod = view.findViewById(R.id.titleKod)
        titleSum = view.findViewById(R.id.titleSum)
        recy = view.findViewById(R.id.recy)
        rootEmpty = view.findViewById(R.id.rootEmpty)
        sendData = view.findViewById(R.id.sendData)
        presenter = StatisticsMcbPresenter(this)

        mainBoxTitles.visibility = View.GONE
        rootEmpty.visibility = View.GONE
        sendData.visibility = View.GONE

        setupToolbar()

        requireContext()

        topSelectDates.listener = object : SelectDatesForStatisticMkbView.SelectDatesForStatisticMkbListener{
            override fun requestMcbList(dFrom: String, dTo: String) {
                presenter.loadStatMkb(dFrom,dTo)
            }
        }

        titleKod.setOnClickListener {
            adapter?.sortByMcb()
            titleKod.text = Html.fromHtml("<u>Код МКБ10</u>")
            titleSum.text = Html.fromHtml("Количество")
        }
        titleSum.setOnClickListener {
            adapter?.sortByCount()
            titleSum.text = Html.fromHtml("<u>Количество</u>")
            titleKod.text = Html.fromHtml("Код МКБ10")
        }
        sendData.setOnClickListener{
            val set =  adapter?.listToString()

            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, set)
            sendIntent.setType("text/plain")
            startActivity(Intent.createChooser(sendIntent, null))
        }
    }

    private fun setupToolbar(){
        (context as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        val actionBar = (context as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }

        toolbar.setNavigationOnClickListener { (context as MainPageActivity?)!!.showNavigationMenu() }
    }


    fun initRecy(list: MutableList<MkbItem>){
        if(list.size == 0){
            mainBoxTitles.visibility = View.GONE
            rootEmpty.visibility = View.VISIBLE
            sendData.visibility = View.GONE
        }else{
            mainBoxTitles.visibility = View.VISIBLE
            rootEmpty.visibility = View.GONE
            sendData.visibility = View.VISIBLE
        }

        val lm = LinearLayoutManager(requireContext())
        adapter = StatisticsMcbAdapter(requireContext(), list)

        recy.layoutManager = lm
        recy.adapter = adapter

        titleKod.callOnClick()
    }

    override fun destroyFragment() {}
    override fun onStartSetStatusFragment(status: Int) {}
}