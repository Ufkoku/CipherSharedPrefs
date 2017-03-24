package com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class AppContextModule {

    private Context app;

    public AppContextModule(Context app) {
        this.app = app;
    }

    @Provides
    @NonNull
    public Context provideAppContext() {
        return app;
    }

}
