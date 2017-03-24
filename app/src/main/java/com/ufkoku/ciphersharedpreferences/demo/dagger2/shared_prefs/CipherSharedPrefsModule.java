package com.ufkoku.ciphersharedpreferences.demo.dagger2.shared_prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.ufkoku.cipher_sharedprefs.CipherSharedPreferences;
import com.ufkoku.cipher_sharedprefs.api.ICipherHolder;
import com.ufkoku.cipher_sharedprefs.cache.SharedPrefsLruCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CipherSharedPrefsModule {

    @Provides
    @Singleton
    @NonNull
    public SharedPreferences provideSharedPrefs(Context context) {
        String name = "EncryptedPrefs";
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    @NonNull
    public CipherSharedPreferences provideCipherSharedPrefs(
            @NonNull SharedPreferences sharedPreferences,
            @NonNull ICipherHolder cipher) {
        return new CipherSharedPreferences(sharedPreferences, cipher, new SharedPrefsLruCache(10));
    }

}
