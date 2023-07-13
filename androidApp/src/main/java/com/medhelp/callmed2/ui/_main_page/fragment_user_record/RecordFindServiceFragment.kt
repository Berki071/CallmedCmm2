package com.medhelp.callmed2.ui._main_page.fragment_user_record

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medhelp.callmed2.R
import com.medhelp.callmed2.data.model.CategoryResponse
import com.medhelp.callmed2.data.model.ServiceItem
import com.medhelp.callmed2.databinding.RecorsUserSelectServiceBinding
import com.medhelp.callmed2.ui._main_page.MainPageActivity
import com.medhelp.callmed2.ui._main_page.fragment_user_record.df_select_doctor_and_time.SelectDoctorAndTimeDialog
import com.medhelp.callmed2.ui._main_page.fragment_user_record.recy_and_spinner.SearchAdapter
import com.medhelp.callmed2.ui._main_page.fragment_user_record.recy_and_spinner.SearchHolder.SearchHolderListener
import com.medhelp.callmed2.ui._main_page.fragment_user_record.recy_and_spinner.SearchSpinnerAdapter
import com.medhelp.callmed2.ui.base.BaseFragment

class RecordFindServiceFragment : BaseFragment() {


//    @BindView(R.id.rv_search)
//    var recyclerView: RecyclerView? = null
//    @BindView(R.id.spinner_search)
//    var spinner: Spinner? = null
//    @BindView(R.id.toolbar_search)
//    var toolbar: Toolbar? = null
//    @BindView(R.id.err_tv_message)
//    var errMessage: TextView? = null
//    @BindView(R.id.err_load_btn)
//    var errLoadBtn: TextView? = null
//    @BindView(R.id.rootEmpty)
//    var rootEmpty: ConstraintLayout? = null

    lateinit var binding: RecorsUserSelectServiceBinding

    var adapter: SearchAdapter? = null
    private var filterList: MutableList<CategoryResponse>? = null
    var presenter: RecordFindServicePresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.recors_user_select_service, container, false)
        binding = RecorsUserSelectServiceBinding.bind(rootView)

        setHasOptionsMenu(true)
        //((MainPageActivity)getContext()).setVisibleToolbar(false);
        return binding.root
    }

    override fun setUp(view: View) {
        presenter = RecordFindServicePresenter(requireContext(), this)
        binding.spinner.visibility = View.GONE
        setupToolbar()
        presenter!!.data
        binding.recyclerView!!.setOnTouchListener { v: View?, event: MotionEvent? -> closeSearchView() }
        binding.includedEmpty.rootEmpty!!.setOnTouchListener { v: View?, event: MotionEvent? -> closeSearchView() }
    }

    private fun closeSearchView(): Boolean {
        if (!searchViewAndroidActionBar!!.isIconified) {
            searchViewAndroidActionBar!!.isIconified = true
            binding.toolbar!!.collapseActionView()
            return true
        }
        return false
    }

    private fun setupToolbar() {
        (context as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        binding.toolbar!!.setOnClickListener { v: View? -> searchViewItem!!.expandActionView() }
        val actionBar = (context as AppCompatActivity?)!!.supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }
        binding.toolbar!!.post { binding.toolbar!!.title = "Запись" }
        binding.toolbar!!.setNavigationOnClickListener { (context as MainPageActivity?)!!.showNavigationMenu() }
    }

    var searchViewItem: MenuItem? = null
    var searchViewAndroidActionBar: SearchView? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        searchViewItem = menu.findItem(R.id.action_search)
        searchViewAndroidActionBar = MenuItemCompat.getActionView(searchViewItem) as SearchView
        if (searchViewAndroidActionBar == null) return
        searchViewAndroidActionBar!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewAndroidActionBar!!.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                binding.spinner!!.setSelection(0)
                adapter!!.setFilterService(newText)
                return true
            }
        })
    }

    fun showRootEmpty(isShow: Boolean) {
        if (isShow) {
            binding.includedEmpty.rootEmpty!!.visibility = View.VISIBLE
        } else {
            binding.includedEmpty.rootEmpty!!.visibility = View.GONE
        }
    }

    fun showErrorScreen(isShow: Boolean) {
        if (isShow) {
            binding.recyclerView!!.visibility = View.GONE
            binding.spinner!!.visibility = View.GONE
            binding.includedErrorM.errTvMessage.visibility = View.VISIBLE
            binding.includedErrorM.errLoadBtn.visibility = View.VISIBLE
            binding.includedErrorM.errLoadBtn.setOnClickListener { v: View? -> presenter!!.data }
            if (binding.includedEmpty.rootEmpty.visibility == View.VISIBLE) showRootEmpty(false)
        } else {
            binding.spinner!!.visibility = View.VISIBLE
            binding.recyclerView!!.visibility = View.VISIBLE
            binding.includedErrorM.errTvMessage.visibility = View.GONE
            binding.includedErrorM.errLoadBtn.visibility = View.GONE
        }
    }

    fun updateView(categories: List<CategoryResponse>, services: MutableList<ServiceItem>) {
        showErrorScreen(false)
        if (services.size == 0) showRootEmpty(true) else showRootEmpty(false)
        adapter = SearchAdapter(this, requireContext(), services, object : SearchHolderListener {
            override fun clickRecord(repo: ServiceItem?) {
                jumpToNextPage(repo)
            }
        })
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView!!.layoutManager = layoutManager
        binding.recyclerView!!.itemAnimator = DefaultItemAnimator()
        binding.recyclerView!!.adapter = adapter
        filterList = ArrayList()
        filterList!!.add(0, CategoryResponse("Все специальности"))
        filterList!!.addAll(categories!!)
        val spinnerAdapter = SearchSpinnerAdapter(context, filterList)
        binding.spinner!!.adapter = spinnerAdapter
        binding.spinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                adapter!!.setFilterSpinner(filterList!!.get(position).id)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    fun jumpToNextPage(repo: ServiceItem?) {
        val recordUserDialog = SelectDoctorAndTimeDialog()
        recordUserDialog.setData(repo)
        recordUserDialog.show(requireFragmentManager(), "recordUserDialog")
    }

    override fun destroyFragment() {}
    override fun onStartSetStatusFragment(status: Int) {}
}