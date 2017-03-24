package com.ufkoku.ciphersharedpreferences.demo.ui;

import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry;
import com.ufkoku.mvp_base.view.IMvpView;

import java.util.List;

interface IMainActivity extends IMvpView {

    void onEntryLoaded(PrefsEntry entry);

    void populateEntries(List<PrefsEntry> entries);

}
