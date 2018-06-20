package com.ufkoku.ciphersharedpreferences.demo;

import android.app.Application;

import com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context.AppContextModule;

public class DemoApp extends Application {

    private static DemoApp instance;

    private AppContextModule appContextModule;

    @Override
    public void onCreate() {
        appContextModule = new AppContextModule(this);

        instance = this;

        super.onCreate();
    }

    public static DemoApp getInstance() {
        return instance;
    }

    public static AppContextModule getAppContextModule() {
        return instance.appContextModule;
    }

}
