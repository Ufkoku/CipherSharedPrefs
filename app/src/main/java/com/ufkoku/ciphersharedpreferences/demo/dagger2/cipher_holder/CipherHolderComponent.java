package com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder;

import com.ufkoku.cipher_sharedprefs.api.ICipherHolder;

import dagger.Component;

@Component(modules = CipherHolderModule.class)
public interface CipherHolderComponent {
    ICipherHolder cipherHolder();
}
