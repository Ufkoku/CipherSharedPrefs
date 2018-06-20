package com.ufkoku.ciphersharedpreferences.demo.ui.main

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.ufkoku.cipher_sharedprefs.CipherSharedPreferences
import com.ufkoku.ciphersharedpreferences.demo.DemoApp
import com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder.CipherHolderModule
import com.ufkoku.ciphersharedpreferences.demo.dagger2.shared_prefs.CipherSharedPrefsModule
import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry
import com.ufkoku.ciphersharedpreferences.demo.ui.main.di.DaggerMainPresenterComponent
import com.ufkoku.mvp.presenter.rx2.BaseAsyncRxSchedulerPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import javax.inject.Inject

@SuppressLint("ApplySharedPref")
class MainPresenter : BaseAsyncRxSchedulerPresenter<MainPresenter.IPresenterListener>() {

    companion object {

        @JvmStatic
        val TASK_GET_RUNNING = 0

        @JvmStatic
        val TASK_ADD_RUNNING = 1

    }

    @Inject
    protected lateinit var sharedPreferences: SharedPreferences

    @Inject
    protected lateinit var cipherSharedPreferences: CipherSharedPreferences

    val isInitialized: Boolean
        get() = ::sharedPreferences.isInitialized && ::cipherSharedPreferences.isInitialized

    override fun createExecutor(): ThreadPoolExecutor {
        return ScheduledThreadPoolExecutor(1)
    }

    fun initializeWithKey(key: String) {
        DaggerMainPresenterComponent.builder()
                .appContextModule(DemoApp.getAppContextModule())
                .cipherHolderModule(CipherHolderModule(key.toByteArray()))
                .cipherSharedPrefsModule(CipherSharedPrefsModule())
                .build()
                .inject(this)
    }

    fun getValues() {
        Observable.defer { Observable.fromIterable(cipherSharedPreferences.all.entries) }
                .map { entry -> PrefsEntry(entry.key, entry.value, sharedPreferences.getString(entry.key, null)) }
                .collectInto(ArrayList<PrefsEntry>()) { list, entry -> list.add(entry) }
                .withId(TASK_GET_RUNNING)
                .subscribeOn(scheduler)
                .subscribe { prefs -> postResult { it.onEntriesFetched(prefs) } }
    }

    fun addValue(key: String, value: String) {
        Observable.fromCallable {
            cipherSharedPreferences.edit().putString(key, value).commit()
            return@fromCallable PrefsEntry(key, value, sharedPreferences.getString(key, null))
        }
                .withId(TASK_ADD_RUNNING)
                .subscribeOn(scheduler)
                .subscribe { prefsEntry -> postResult { it.onEntryCreated(prefsEntry) } }
    }

    fun removeValue(entry: PrefsEntry) {
        Observable.defer { Observable.just(cipherSharedPreferences.edit().remove(entry.key).commit()) }
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun isKeyUnique(key: String): Boolean {
        return !cipherSharedPreferences.contains(key)
    }

    interface IPresenterListener : ITaskListener {

        fun onEntriesFetched(entry: List<PrefsEntry>)

        fun onEntryCreated(entry: PrefsEntry)

    }

}
