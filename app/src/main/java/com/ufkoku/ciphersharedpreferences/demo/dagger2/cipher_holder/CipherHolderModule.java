package com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder;

import com.ufkoku.cipher_sharedprefs.api.ICipherHolder;
import com.ufkoku.cipher_sharedprefs.cipher.AesCipher;

import java.util.Arrays;

import dagger.Module;
import dagger.Provides;

@Module
public class CipherHolderModule {

    private final byte[] key;

    public CipherHolderModule(byte[] key) {
        this.key = Arrays.copyOf(key, key.length);
    }

    @Provides
    public ICipherHolder provideCipher() {
        return new AesCipher(key);
    }

}
