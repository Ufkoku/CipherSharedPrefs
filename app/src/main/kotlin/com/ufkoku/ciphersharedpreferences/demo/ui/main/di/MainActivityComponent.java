package com.ufkoku.ciphersharedpreferences.demo.ui.main.di;

import com.ufkoku.ciphersharedpreferences.demo.ui.main.MainPresenter;
import com.ufkoku.ciphersharedpreferences.demo.ui.main.MainViewState;

import dagger.Component;

@Component(modules = {MainActivityModule.class})
public interface MainActivityComponent {

    MainPresenter getMainPresenter();

    MainViewState getMainViewState();

}
