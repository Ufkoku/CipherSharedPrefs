package com.ufkoku.ciphersharedpreferences.demo.ui.main

import android.os.Bundle

import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable
import com.ufkoku.mvp.viewstate.autosavable.DontSave
import com.ufkoku.mvp_base.viewstate.IViewState

import java.util.ArrayList

@AutoSavable
class MainViewState : IViewState<MainViewState.IViewStateHolder> {

    var key: String? = null

    @DontSave
    var arePrefsLoaded = false

    @DontSave
    val entries: MutableList<PrefsEntry> = ArrayList()

    override fun restore(inState: Bundle) {
        MainViewStateSaver.restore(this, inState)
    }

    override fun save(out: Bundle) {
        MainViewStateSaver.save(this, out)
    }

    override fun apply(view: IViewStateHolder) {
        if (arePrefsLoaded) {
            view.populateEntries(entries)
        }
    }

    interface IViewStateHolder {

        fun populateEntries(entries: List<PrefsEntry>)

    }

}
