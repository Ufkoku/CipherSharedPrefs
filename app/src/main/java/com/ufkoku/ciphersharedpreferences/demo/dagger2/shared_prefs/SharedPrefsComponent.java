package com.ufkoku.ciphersharedpreferences.demo.dagger2.shared_prefs;

import com.ufkoku.ciphersharedpreferences.demo.dagger2.app_context.AppContextComponent;
import com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder.CipherHolderComponent;
import com.ufkoku.ciphersharedpreferences.demo.ui.MainPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(dependencies = {AppContextComponent.class, CipherHolderComponent.class}, modules = CipherSharedPrefsModule.class)
public interface SharedPrefsComponent {

    void inject(MainPresenter presenter);

}
