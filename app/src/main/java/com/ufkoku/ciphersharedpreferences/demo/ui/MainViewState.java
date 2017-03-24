package com.ufkoku.ciphersharedpreferences.demo.ui;

import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry;
import com.ufkoku.mvp_base.viewstate.IViewState;

import java.util.ArrayList;
import java.util.List;

public class MainViewState implements IViewState<IMainActivity> {

    private List<PrefsEntry> entries = new ArrayList<>();

    private boolean prefsLoaded = false;

    public List<PrefsEntry> getEntries() {
        return entries;
    }

    public boolean isPrefsLoaded() {
        return prefsLoaded;
    }

    public void setPrefsLoaded(boolean prefsLoaded) {
        this.prefsLoaded = prefsLoaded;
    }

    @Override
    public void apply(IMainActivity activity) {
        if (prefsLoaded) {
            activity.populateEntries(entries);
        }
    }

}
