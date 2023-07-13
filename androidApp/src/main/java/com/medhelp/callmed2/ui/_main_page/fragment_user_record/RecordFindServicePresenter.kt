package com.medhelp.callmed2.ui._main_page.fragment_user_record

import android.content.Context
import com.medhelp.callmed2.data.model.CategoryResponse
import com.medhelp.callmed2.data.model.ServiceList
import com.medhelp.callmed2.data.model.SpecialtyList
import com.medhelp.callmed2.data.network.NetworkManager
import com.medhelp.callmed2.data.pref.PreferencesManager
import com.medhelp.callmed2.utils.timber_log.LoggingTree
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RecordFindServicePresenter(var context: Context, var mainView: RecordFindServiceFragment?) {
    var networkManager: NetworkManager

    init {
        val prefManager = PreferencesManager(context)
        networkManager = NetworkManager(prefManager)
    }

    val data: Unit
        get() {
            mainView!!.showLoading()
            val cd = CompositeDisposable()
            cd.add(networkManager
                .categoryApiCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: SpecialtyList? ->
                    if (response != null) getPrice(response.spec) else mainView!!.hideLoading()
                    cd.dispose()
                }) { throwable: Throwable? ->
                    Timber.e(
                        LoggingTree.getMessageForError(
                            throwable,
                            "SearchPresenter\$getData "
                        )
                    )
                    if (mainView == null) {
                        return@subscribe
                    }
                    mainView!!.hideLoading()
                    mainView!!.showErrorScreen(true)
                    cd.dispose()
                })
        }

    private fun getPrice(categoryResponse: List<CategoryResponse>) {
        val cd = CompositeDisposable()
        cd.add(networkManager
            .priceApiCall
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response: ServiceList ->
                if (response.services != null) {
                    mainView!!.updateView(categoryResponse, response.services)
                }
                mainView!!.hideLoading()
                cd.dispose()
            }) { throwable: Throwable? ->
                Timber.e(LoggingTree.getMessageForError(throwable, "SearchPresenter\$getPrice"))
                if (mainView == null) {
                    return@subscribe
                }
                mainView!!.hideLoading()
                mainView!!.showErrorScreen(true)
                cd.dispose()
            })
    }
}