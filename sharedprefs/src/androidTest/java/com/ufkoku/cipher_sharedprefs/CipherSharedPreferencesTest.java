/*
 * Copyright 2017 Ufkoku (https://github.com/Ufkoku/CipherSharedPrefs)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ufkoku.cipher_sharedprefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.ufkoku.cipher_sharedprefs.api.ITransformer;
import com.ufkoku.cipher_sharedprefs.cache.SharedPrefsMapCache;
import com.ufkoku.cipher_sharedprefs.cipher.AesCipher;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
@SmallTest
@SuppressLint("ApplySharedPref")
public class CipherSharedPreferencesTest {

    private static final String SHARED_PREFS = "PrefsName";
    private static final String KEY_1 = "Key1";
    private static final String KEY_2 = "Key2";

    private final AesCipher cipher;

    private final ITransformer transformer;

    public CipherSharedPreferencesTest() {
        byte[] key = new byte[16];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) (-80 + i * 10);
        }

        cipher = new AesCipher(key);

        transformer = new ITransformer() {
            private Gson gson = new Gson();

            @NotNull
            @Override
            public String toString(@NotNull Object o) {
                return gson.toJson(o);
            }

            @Override
            public <T> T fromString(@NotNull String string, @NonNull Class<T> clazz) {
                return gson.fromJson(string, clazz);
            }
        };
    }

    private CipherSharedPreferences createCipherPreferences(boolean withCache) {
        return new CipherSharedPreferences(
                InstrumentationRegistry.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE),
                cipher,
                withCache ? new SharedPrefsMapCache() : null,
                transformer
        );
    }

    private SharedPreferences createDefaultPreferences() {
        return InstrumentationRegistry.getContext()
                .getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Before
    public void before() {
        createDefaultPreferences().edit().clear().commit();
    }

    //------------------------------Boolean tests--------------------------------------//

    @Test
    public void putBooleanNoCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putBoolean(KEY_1, true).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals("true", cipher.decrypt(encrypted));
    }

    @Test
    public void getBooleanNoCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putBoolean(KEY_1, true).commit();
        Assert.assertTrue(preferences.getBoolean(KEY_1, false));
    }

    @Test
    public void getBooleanDefaultValueNoCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putBoolean(KEY_1, true).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertFalse(preferences.getBoolean(KEY_1, false));
        Assert.assertTrue(preferences.getBoolean(KEY_1, true));
    }

    @Test
    public void putBooleanCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putBoolean(KEY_1, true).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals("true", cipher.decrypt(encrypted));
    }

    @Test
    public void getBooleanCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putBoolean(KEY_1, true).commit();
        Assert.assertTrue(preferences.getBoolean(KEY_1, false));

        Assert.assertTrue((Boolean) preferences.getCacheAsMap().get(KEY_1));
    }

    @Test
    public void getBooleanDefaultValueCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putBoolean(KEY_1, true).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertFalse(preferences.getBoolean(KEY_1, false));
        Assert.assertTrue(preferences.getBoolean(KEY_1, true));

        Assert.assertNull(preferences.getCacheAsMap().get(KEY_1));
    }

    //------------------------------Float tests----------------------------------------//

    @Test
    public void putFloatNoCacheTest() {
        float value = 675F;

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putFloat(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(value, Float.parseFloat(cipher.decrypt(encrypted)), 0.1);
    }

    @Test
    public void getFloatNoCacheTest() {
        float value = 675F;

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putFloat(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getFloat(KEY_1, 0), 0.1);
    }

    @Test
    public void getFloatDefaultValueNoCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putFloat(KEY_1, 675F).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(-75.0, preferences.getFloat(KEY_1, -75F), 0.1);
    }

    @Test
    public void putFloatCacheTest() {
        float value = 675F;

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putFloat(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(value, Float.parseFloat(cipher.decrypt(encrypted)), 0.1);
    }

    @Test
    public void getFloatCacheTest() {
        float value = 675F;

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putFloat(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getFloat(KEY_1, 0), 0.1);

        Assert.assertEquals(value, (Float) preferences.getCacheAsMap().get(KEY_1), 0.1);
    }

    @Test
    public void getFloatDefaultValueCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putFloat(KEY_1, 675F).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(-75.0, preferences.getFloat(KEY_1, -75F), 0.1);
    }

    //------------------------------Int tests------------------------------------------//

    @Test
    public void putIntNoCacheTest() {
        int value = 675;

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putInt(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(value, Integer.parseInt(cipher.decrypt(encrypted)));
    }

    @Test
    public void getIntNoCacheTest() {
        int value = 675;

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putInt(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getInt(KEY_1, 0));
    }

    @Test
    public void getIntDefaultValueNoCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putInt(KEY_1, 675).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(-75, preferences.getInt(KEY_1, -75));
    }

    @Test
    public void putIntCacheTest() {
        int value = 675;

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putInt(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(value, Integer.parseInt(cipher.decrypt(encrypted)));
    }

    @Test
    public void getIntCacheTest() {
        int value = 675;

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putInt(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getInt(KEY_1, 0));

        Assert.assertEquals(value, ((Integer) preferences.getCacheAsMap().get(KEY_1)).longValue());
    }

    @Test
    public void getIntDefaultValueCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putInt(KEY_1, 675).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(-75, preferences.getInt(KEY_1, -75));
    }

    //------------------------------Long tests-----------------------------------------//

    @Test
    public void putLongNoCacheTest() {
        long value = 675L;

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putLong(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(value, Long.parseLong(cipher.decrypt(encrypted)));
    }

    @Test
    public void getLongNoCacheTest() {
        long value = 675L;

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putLong(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getLong(KEY_1, 0));
    }

    @Test
    public void getLongDefaultValueNoCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putLong(KEY_1, 675).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(-75, preferences.getLong(KEY_1, -75));
    }

    @Test
    public void putLongCacheTest() {
        long value = 675L;

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putLong(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(value, Long.parseLong(cipher.decrypt(encrypted)));
    }

    @Test
    public void getLongCacheTest() {
        long value = 675L;

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putLong(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getLong(KEY_1, 0));

        Assert.assertEquals(value, ((Long) preferences.getCacheAsMap().get(KEY_1)).longValue());
    }

    @Test
    public void getLongDefaultValueCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putLong(KEY_1, 675).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(-75, preferences.getLong(KEY_1, -75));
    }

    //------------------------------String tests---------------------------------------//

    @Test
    public void putStringNoCacheTest() {
        String value = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putString(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(value, cipher.decrypt(encrypted));
    }

    @Test
    public void getStringNoCacheTest() {
        String value = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putString(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getString(KEY_1, null));
    }

    @Test
    public void getStringDefaultValueNoCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putString(KEY_1, "str").commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals("defVal", preferences.getString(KEY_1, "defVal"));
    }

    @Test
    public void putStringCacheTest() {
        String value = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putString(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(value, cipher.decrypt(encrypted));
    }

    @Test
    public void getStringCacheTest() {
        String value = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putString(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getString(KEY_1, null));

        Assert.assertEquals(value, preferences.getCacheAsMap().get(KEY_1));
    }

    @Test
    public void getStringDefaultValueCacheTest() {
        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putString(KEY_1, "str").commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals("defVal", preferences.getString(KEY_1, "defVal"));
    }

    //------------------------------String Set tests-----------------------------------//

    @Test
    public void putStringSetNoCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putStringSet(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(new JSONArray(value).toString(), cipher.decrypt(encrypted));
    }

    @Test
    public void getStringSetNoCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putStringSet(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getStringSet(KEY_1, null));
    }

    @Test
    public void getStringSetDefaultValueNoCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putStringSet(KEY_1, new HashSet<String>()).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(value, preferences.getStringSet(KEY_1, value));
    }

    @Test
    public void putStringSetCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putStringSet(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(new JSONArray(value).toString(), cipher.decrypt(encrypted));
    }

    @Test
    public void getStringSetCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putStringSet(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getStringSet(KEY_1, null));

        Assert.assertEquals(value, preferences.getCacheAsMap().get(KEY_1));
    }

    @Test
    public void getStringSetDefaultValueCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putStringSet(KEY_1, new HashSet<String>()).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(value, preferences.getStringSet(KEY_1, value));
    }

    //------------------------------Object  tests--------------------------------------//

    @Test
    public void putObjectNoCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putObject(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(transformer.toString(value), cipher.decrypt(encrypted));
    }

    @Test
    public void getObjectSetNoCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putObject(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getObject(KEY_1, null, value.getClass()));
    }

    @Test
    public void getObjectSetDefaultValueNoCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit().putObject(KEY_1, new HashSet<String>()).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(value, preferences.getObject(KEY_1, value, Set.class));
    }

    @Test
    public void putObjectSetCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putObject(KEY_1, value).commit();

        String encrypted = createDefaultPreferences().getString(KEY_1, null);
        Assert.assertNotNull(encrypted);
        Assert.assertEquals(transformer.toString(value), cipher.decrypt(encrypted));
    }

    @Test
    public void getObjectSetCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putObject(KEY_1, value).commit();
        Assert.assertEquals(value, preferences.getObject(KEY_1, null, value.getClass()));

        Assert.assertEquals(value, preferences.getCacheAsMap().get(KEY_1));
    }

    @Test
    public void getObjectSetDefaultValueCacheTest() {
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit().putObject(KEY_1, new HashSet<String>()).commit();
        preferences.edit().remove(KEY_1).commit();

        Assert.assertEquals(value, preferences.getObject(KEY_1, value, Set.class));
    }

    //------------------------------All tests------------------------------------------//

    @Test
    public void getAllTest() {
        long longValue = 675L;
        String stringValue = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit();

        Map<String, String> all = preferences.getAll();
        Assert.assertEquals("" + longValue, all.get(KEY_1));
        Assert.assertEquals(stringValue, all.get(KEY_2));
    }

    @Test
    public void getCacheAsMapNoCacheTest() {
        long longValue = 675L;
        String stringValue = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit();

        Map<String, ?> cacheMap = preferences.getCacheAsMap();
        Assert.assertNull(cacheMap);
    }

    @Test
    public void getCacheAsMapTest() {
        long longValue = 675L;
        String stringValue = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit();

        Map<String, ?> cacheMap = preferences.getCacheAsMap();
        Assert.assertNotNull(cacheMap);
        Assert.assertEquals(longValue, cacheMap.get(KEY_1));
        Assert.assertEquals(stringValue, cacheMap.get(KEY_2));
    }

    @Test
    public void clearCacheTest() {
        long longValue = 675L;
        String stringValue = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit();

        Assert.assertEquals(2, preferences.getCacheAsMap().size());
        preferences.clearCache();
        Assert.assertEquals(0, preferences.getCacheAsMap().size());
    }

    @Test
    public void removeKeyNoCacheTest() {
        long longValue = 675L;
        String stringValue = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit();

        preferences.edit().remove(KEY_1).commit();

        Assert.assertFalse(preferences.contains(KEY_1));
        Assert.assertTrue(preferences.contains(KEY_2));
    }

    @Test
    public void removeKeyCacheTest() {
        long longValue = 675L;
        String stringValue = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit();

        preferences.edit().remove(KEY_1).commit();

        Assert.assertFalse(preferences.contains(KEY_1));
        Assert.assertTrue(preferences.contains(KEY_2));
    }

    @Test
    public void clearPrefsNoCacheTest() {
        long longValue = 675L;
        String stringValue = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(false);

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit();

        preferences.edit().clear().commit();

        Assert.assertFalse(preferences.contains(KEY_1));
        Assert.assertFalse(preferences.contains(KEY_2));
    }

    @Test
    public void clearPrefsCacheTest() {
        long longValue = 675L;
        String stringValue = "SomeString";

        CipherSharedPreferences preferences = createCipherPreferences(true);

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit();

        preferences.edit().clear().commit();

        Assert.assertFalse(preferences.contains(KEY_1));
        Assert.assertFalse(preferences.contains(KEY_2));
    }

}
