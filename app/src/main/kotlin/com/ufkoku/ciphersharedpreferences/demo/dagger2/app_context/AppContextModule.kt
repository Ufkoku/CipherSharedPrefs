package com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context

import android.content.Context

import dagger.Module
import dagger.Provides

@Module
class AppContextModule(private val app: Context) {

    @Provides
    fun provideAppContext(): Context {
        return app
    }

}
