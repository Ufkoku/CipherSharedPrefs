package com.ufkoku.ciphersharedpreferences.demo.ui.main.di;

import com.ufkoku.ciphersharedpreferences.demo.ui.main.MainPresenter;
import com.ufkoku.ciphersharedpreferences.demo.ui.main.MainViewState;

import dagger.Module;
import dagger.Provides;

@Module
public class MainAcivityModule {

    @Provides
    public MainPresenter mainPresenter() {
        return new MainPresenter();
    }

    @Provides
    public MainViewState mainViewState() {
        return new MainViewState();
    }

}
