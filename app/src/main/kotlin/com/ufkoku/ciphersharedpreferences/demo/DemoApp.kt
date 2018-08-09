package com.ufkoku.ciphersharedpreferences.demo

import android.app.Application

import com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context.AppContextModule

class DemoApp : Application() {

    companion object {

        var instance: DemoApp? = null
            private set

        fun getAppContextModule(): AppContextModule? {
            return instance!!.appContextModule
        }

    }

    private var appContextModule: AppContextModule? = null

    override fun onCreate() {
        appContextModule = AppContextModule(this)

        instance = this

        super.onCreate()
    }

}
