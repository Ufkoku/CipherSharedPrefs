package com.ufkoku.ciphersharedpreferences.demo.ui.main.di;

import com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context.AppContextModule;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder.CipherHolderModule;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.shared_prefs.CipherSharedPrefsModule;
import com.ufkoku.ciphersharedpreferences.demo.ui.main.MainPresenter;

import dagger.Component;

@Component(modules = {AppContextModule.class, CipherHolderModule.class, CipherSharedPrefsModule.class})
public interface MainPresenterComponent {

    void inject(MainPresenter presenter);

}
