package com.ufkoku.ciphersharedpreferences.demo.ui.main.di

import com.ufkoku.ciphersharedpreferences.demo.ui.main.MainPresenter
import com.ufkoku.ciphersharedpreferences.demo.ui.main.MainViewState

import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    fun mainPresenter(): MainPresenter {
        return MainPresenter()
    }

    @Provides
    fun mainViewState(): MainViewState {
        return MainViewState()
    }

}
