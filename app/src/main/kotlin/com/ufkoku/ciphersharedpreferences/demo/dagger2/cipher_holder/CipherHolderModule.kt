package com.ufkoku.ciphersharedpreferences.demo.dagger2.cipher_holder

import com.ufkoku.cipher_sharedprefs.api.ICipherHolder
import com.ufkoku.cipher_sharedprefs.cipher.AesCipher

import java.util.Arrays

import dagger.Module
import dagger.Provides

@Module
class CipherHolderModule(val key: ByteArray) {

    @Provides
    fun provideCipher(): ICipherHolder {
        return AesCipher(key)
    }

}
