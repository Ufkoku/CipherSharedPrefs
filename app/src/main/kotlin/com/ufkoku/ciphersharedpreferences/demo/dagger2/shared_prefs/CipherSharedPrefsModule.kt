package com.ufkoku.ciphersharedpreferences.demo.dagger2.shared_prefs

import android.content.Context
import android.content.SharedPreferences

import com.ufkoku.cipher_sharedprefs.CipherSharedPreferences
import com.ufkoku.cipher_sharedprefs.api.ICipherHolder
import com.ufkoku.cipher_sharedprefs.cache.SharedPrefsLruCache

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

@Module
class CipherSharedPrefsModule {

    @Provides
    fun provideSharedPrefs(context: Context): SharedPreferences {
        val name = "EncryptedPrefs"
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideCipherSharedPrefs(
            sharedPreferences: SharedPreferences,
            cipher: ICipherHolder): CipherSharedPreferences {
        return CipherSharedPreferences(sharedPreferences, cipher, SharedPrefsLruCache(10))
    }

}
