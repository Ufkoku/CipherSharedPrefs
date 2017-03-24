package com.ufkoku.ciphersharedpreferences.demo;

import android.app.Application;

import com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context.AppContextComponent;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context.AppContextModule;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context.DaggerAppContextComponent;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder.CipherHolderComponent;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder.CipherHolderModule;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder.DaggerCipherHolderComponent;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.shared_prefs.CipherSharedPrefsModule;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.shared_prefs.DaggerSharedPrefsComponent;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.shared_prefs.SharedPrefsComponent;

public class DemoApp extends Application {

    private static DemoApp instance;

    private AppContextComponent appContextComponent;
    private CipherHolderComponent cipherHolderComponent;
    private SharedPrefsComponent sharedPrefsComponent;

    @Override
    public void onCreate() {
        appContextComponent = DaggerAppContextComponent
                .builder()
                .appContextModule(new AppContextModule(this))
                .build();

        instance = this;

        super.onCreate();
    }

    public static DemoApp getInstance() {
        return instance;
    }

    public static void initializeWithKey(byte[] key) {
        instance.cipherHolderComponent = DaggerCipherHolderComponent.builder()
                .cipherHolderModule(new CipherHolderModule(key))
                .build();

        instance.sharedPrefsComponent = DaggerSharedPrefsComponent.builder()
                .appContextComponent(instance.appContextComponent)
                .cipherHolderComponent(instance.cipherHolderComponent)
                .cipherSharedPrefsModule(new CipherSharedPrefsModule())
                .build();
    }

    public static AppContextComponent getAppContextComponent() {
        return instance.appContextComponent;
    }

    public static CipherHolderComponent getCipherHolderComponent() {
        return instance.cipherHolderComponent;
    }

    public static SharedPrefsComponent getSharedPrefsComponent() {
        return instance.sharedPrefsComponent;
    }

}
