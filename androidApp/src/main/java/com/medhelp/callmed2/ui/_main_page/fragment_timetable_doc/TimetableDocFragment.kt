package com.medhelp.callmed2.ui._main_page.fragment_timetable_doc

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager

import com.google.android.material.tabs.TabLayout
import com.medhelp.callmed2.R
import com.medhelp.callmed2.databinding.FragmentTimetableDocBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui.base.BaseFragment
import timber.log.Timber

class TimetableDocFragment : BaseFragment() {
    lateinit var binding : FragmentTimetableDocBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.i("Расписание доктора yна день")
        val rootView = inflater.inflate(R.layout.fragment_timetable_doc, container, false)
        binding = FragmentTimetableDocBinding.bind(rootView)

        return binding.root
    }

    override fun setUp(view: View) {
        setupToolbar()

        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
               if(position == 0)
                   Timber.i("Расписание доктора yна день")
                else
                   Timber.i("Расписание доктора на месяц")

            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
        binding.viewpager.adapter = SampleFragmentPagerAdapter(childFragmentManager, requireContext())
        binding.tabLayoutM.setupWithViewPager(binding.viewpager)
    }

    private fun setupToolbar() {
        (context as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        val actionBar = (context as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar!!.post {
            try {
                if (isAdded && requireContext() != null)
                    binding.toolbar.title = resources.getString(R.string.timetable)
            } catch (e: Exception) {
            }
        }
        binding.toolbar!!.setNavigationOnClickListener { (context as MainPageActivity?)!!.showNavigationMenu() }
    }

    override fun destroyFragment() {}
    override fun onStartSetStatusFragment(status: Int) {}
    fun showErrorScreen() {}
}