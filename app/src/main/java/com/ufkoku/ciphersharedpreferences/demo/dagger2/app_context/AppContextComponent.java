package com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context;

import android.content.Context;

import dagger.Component;

@Component(modules = AppContextModule.class)
public interface AppContextComponent {

    Context context();

}
