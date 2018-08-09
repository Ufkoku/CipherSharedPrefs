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

package com.ufkoku.cipher_sharedprefs

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import com.google.gson.Gson
import com.ufkoku.cipher_sharedprefs.api.ITransformer
import com.ufkoku.cipher_sharedprefs.cache.SharedPrefsMapCache
import com.ufkoku.cipher_sharedprefs.cipher.AesCipher
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@SmallTest
@SuppressLint("ApplySharedPref")
class CipherSharedPreferencesTest {

    private val cipher: AesCipher

    private val transformer: ITransformer

    init {
        val key = ByteArray(16)
        for (i in key.indices) {
            key[i] = (-80 + i * 10).toByte()
        }

        cipher = AesCipher(key)

        transformer = object : ITransformer {
            private val gson = Gson()

            override fun toString(o: Any): String {
                return gson.toJson(o)
            }

            override fun <T> fromString(string: String, clazz: Class<T>): T {
                return gson.fromJson(string, clazz)
            }
        }
    }

    private fun createCipherPreferences(withCache: Boolean): CipherSharedPreferences {
        return CipherSharedPreferences(
                InstrumentationRegistry.getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE),
                cipher,
                if (withCache) SharedPrefsMapCache() else null,
                transformer
        )
    }

    private fun createDefaultPreferences(): SharedPreferences {
        return InstrumentationRegistry.getContext()
                .getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }

    @Before
    fun before() {
        createDefaultPreferences().edit().clear().commit()
    }

    //------------------------------Boolean tests--------------------------------------//

    @Test
    fun putBooleanNoCacheTest() {
        val preferences = createCipherPreferences(false)

        preferences.edit().putBoolean(KEY_1, true).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals("true", cipher.decrypt(encrypted!!))
    }

    @Test
    fun getBooleanNoCacheTest() {
        val preferences = createCipherPreferences(false)

        preferences.edit().putBoolean(KEY_1, true).commit()
        Assert.assertTrue(preferences.getBoolean(KEY_1, false))
    }

    @Test
    fun getBooleanDefaultValueNoCacheTest() {
        val preferences = createCipherPreferences(false)

        preferences.edit().putBoolean(KEY_1, true).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertFalse(preferences.getBoolean(KEY_1, false))
        Assert.assertTrue(preferences.getBoolean(KEY_1, true))
    }

    @Test
    fun putBooleanCacheTest() {
        val preferences = createCipherPreferences(true)

        preferences.edit().putBoolean(KEY_1, true).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals("true", cipher.decrypt(encrypted!!))
    }

    @Test
    fun getBooleanCacheTest() {
        val preferences = createCipherPreferences(true)

        preferences.edit().putBoolean(KEY_1, true).commit()
        Assert.assertTrue(preferences.getBoolean(KEY_1, false))

        Assert.assertTrue(preferences.getCacheAsMap()!![KEY_1] as Boolean)
    }

    @Test
    fun getBooleanDefaultValueCacheTest() {
        val preferences = createCipherPreferences(true)

        preferences.edit().putBoolean(KEY_1, true).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertFalse(preferences.getBoolean(KEY_1, false))
        Assert.assertTrue(preferences.getBoolean(KEY_1, true))

        Assert.assertNull(preferences.getCacheAsMap()!![KEY_1])
    }

    //------------------------------Float tests----------------------------------------//

    @Test
    fun putFloatNoCacheTest() {
        val value = 675f

        val preferences = createCipherPreferences(false)

        preferences.edit().putFloat(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(value.toDouble(), java.lang.Float.parseFloat(cipher.decrypt(encrypted!!)).toDouble(), 0.1)
    }

    @Test
    fun getFloatNoCacheTest() {
        val value = 675f

        val preferences = createCipherPreferences(false)

        preferences.edit().putFloat(KEY_1, value).commit()
        Assert.assertEquals(value.toDouble(), preferences.getFloat(KEY_1, 0f).toDouble(), 0.1)
    }

    @Test
    fun getFloatDefaultValueNoCacheTest() {
        val preferences = createCipherPreferences(false)

        preferences.edit().putFloat(KEY_1, 675f).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(-75.0, preferences.getFloat(KEY_1, -75f).toDouble(), 0.1)
    }

    @Test
    fun putFloatCacheTest() {
        val value = 675f

        val preferences = createCipherPreferences(true)

        preferences.edit().putFloat(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(value.toDouble(), java.lang.Float.parseFloat(cipher.decrypt(encrypted!!)).toDouble(), 0.1)
    }

    @Test
    fun getFloatCacheTest() {
        val value = 675f

        val preferences = createCipherPreferences(true)

        preferences.edit().putFloat(KEY_1, value).commit()
        Assert.assertEquals(value.toDouble(), preferences.getFloat(KEY_1, 0f).toDouble(), 0.1)

        Assert.assertEquals(value.toDouble(), (preferences.getCacheAsMap()!![KEY_1] as Float).toDouble(), 0.1)
    }

    @Test
    fun getFloatDefaultValueCacheTest() {
        val preferences = createCipherPreferences(true)

        preferences.edit().putFloat(KEY_1, 675f).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(-75.0, preferences.getFloat(KEY_1, -75f).toDouble(), 0.1)
    }

    //------------------------------Int tests------------------------------------------//

    @Test
    fun putIntNoCacheTest() {
        val value = 675

        val preferences = createCipherPreferences(false)

        preferences.edit().putInt(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(value.toLong(), Integer.parseInt(cipher.decrypt(encrypted!!)).toLong())
    }

    @Test
    fun getIntNoCacheTest() {
        val value = 675

        val preferences = createCipherPreferences(false)

        preferences.edit().putInt(KEY_1, value).commit()
        Assert.assertEquals(value.toLong(), preferences.getInt(KEY_1, 0).toLong())
    }

    @Test
    fun getIntDefaultValueNoCacheTest() {
        val preferences = createCipherPreferences(false)

        preferences.edit().putInt(KEY_1, 675).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(-75, preferences.getInt(KEY_1, -75).toLong())
    }

    @Test
    fun putIntCacheTest() {
        val value = 675

        val preferences = createCipherPreferences(false)

        preferences.edit().putInt(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(value.toLong(), Integer.parseInt(cipher.decrypt(encrypted!!)).toLong())
    }

    @Test
    fun getIntCacheTest() {
        val value = 675

        val preferences = createCipherPreferences(true)

        preferences.edit().putInt(KEY_1, value).commit()
        Assert.assertEquals(value.toLong(), preferences.getInt(KEY_1, 0).toLong())

        Assert.assertEquals(value.toLong(), (preferences.getCacheAsMap()!![KEY_1] as Int).toLong())
    }

    @Test
    fun getIntDefaultValueCacheTest() {
        val preferences = createCipherPreferences(true)

        preferences.edit().putInt(KEY_1, 675).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(-75, preferences.getInt(KEY_1, -75).toLong())
    }

    //------------------------------Long tests-----------------------------------------//

    @Test
    fun putLongNoCacheTest() {
        val value = 675L

        val preferences = createCipherPreferences(false)

        preferences.edit().putLong(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(value, java.lang.Long.parseLong(cipher.decrypt(encrypted!!)))
    }

    @Test
    fun getLongNoCacheTest() {
        val value = 675L

        val preferences = createCipherPreferences(false)

        preferences.edit().putLong(KEY_1, value).commit()
        Assert.assertEquals(value, preferences.getLong(KEY_1, 0))
    }

    @Test
    fun getLongDefaultValueNoCacheTest() {
        val preferences = createCipherPreferences(false)

        preferences.edit().putLong(KEY_1, 675).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(-75, preferences.getLong(KEY_1, -75))
    }

    @Test
    fun putLongCacheTest() {
        val value = 675L

        val preferences = createCipherPreferences(true)

        preferences.edit().putLong(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(value, java.lang.Long.parseLong(cipher.decrypt(encrypted!!)))
    }

    @Test
    fun getLongCacheTest() {
        val value = 675L

        val preferences = createCipherPreferences(true)

        preferences.edit().putLong(KEY_1, value).commit()
        Assert.assertEquals(value, preferences.getLong(KEY_1, 0))

        Assert.assertEquals(value, (preferences.getCacheAsMap()!![KEY_1] as Long).toLong())
    }

    @Test
    fun getLongDefaultValueCacheTest() {
        val preferences = createCipherPreferences(true)

        preferences.edit().putLong(KEY_1, 675).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(-75, preferences.getLong(KEY_1, -75))
    }

    //------------------------------String tests---------------------------------------//

    @Test
    fun putStringNoCacheTest() {
        val value = "SomeString"

        val preferences = createCipherPreferences(false)

        preferences.edit().putString(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(value, cipher.decrypt(encrypted!!))
    }

    @Test
    fun getStringNoCacheTest() {
        val value = "SomeString"

        val preferences = createCipherPreferences(false)

        preferences.edit().putString(KEY_1, value).commit()
        Assert.assertEquals(value, preferences.getString(KEY_1, null))
    }

    @Test
    fun getStringDefaultValueNoCacheTest() {
        val preferences = createCipherPreferences(false)

        preferences.edit().putString(KEY_1, "str").commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals("defVal", preferences.getString(KEY_1, "defVal"))
    }

    @Test
    fun putStringCacheTest() {
        val value = "SomeString"

        val preferences = createCipherPreferences(true)

        preferences.edit().putString(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(value, cipher.decrypt(encrypted!!))
    }

    @Test
    fun getStringCacheTest() {
        val value = "SomeString"

        val preferences = createCipherPreferences(true)

        preferences.edit().putString(KEY_1, value).commit()
        Assert.assertEquals(value, preferences.getString(KEY_1, null))

        Assert.assertEquals(value, preferences.getCacheAsMap()!![KEY_1])
    }

    @Test
    fun getStringDefaultValueCacheTest() {
        val preferences = createCipherPreferences(true)

        preferences.edit().putString(KEY_1, "str").commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals("defVal", preferences.getString(KEY_1, "defVal"))
    }

    //------------------------------String Set tests-----------------------------------//

    @Test
    fun putStringSetNoCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(false)

        preferences.edit().putStringSet(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getStringSet(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertNotEquals(value, encrypted)

        val decrypted = HashSet<String>(3)
        for (str in encrypted!!) {
            decrypted.add(cipher.decrypt(str))
        }

        Assert.assertEquals(value, decrypted)
    }

    @Test
    fun getStringSetNoCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(false)

        preferences.edit().putStringSet(KEY_1, value).commit()
        Assert.assertEquals(value, preferences.getStringSet(KEY_1, null))
    }

    @Test
    fun getStringSetDefaultValueNoCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(false)

        preferences.edit().putStringSet(KEY_1, HashSet()).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(value, preferences.getStringSet(KEY_1, value))
    }

    @Test
    fun putStringSetCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(true)

        preferences.edit().putStringSet(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getStringSet(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertNotEquals(value, encrypted)

        val decrypted = HashSet<String>(3)
        for (str in encrypted!!) {
            decrypted.add(cipher.decrypt(str))
        }

        Assert.assertEquals(value, decrypted)
    }

    @Test
    fun getStringSetCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(true)

        preferences.edit().putStringSet(KEY_1, value).commit()
        Assert.assertEquals(value, preferences.getStringSet(KEY_1, null))

        Assert.assertEquals(value, preferences.getCacheAsMap()!![KEY_1])
    }

    @Test
    fun getStringSetDefaultValueCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(false)

        preferences.edit().putStringSet(KEY_1, HashSet()).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(value, preferences.getStringSet(KEY_1, value))
    }

    //------------------------------Object  tests--------------------------------------//

    @Test
    fun putObjectNoCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(false)

        preferences.edit().putObject(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)
        Assert.assertNotNull(encrypted)
        Assert.assertEquals(transformer.toString(value), cipher.decrypt(encrypted!!))
    }

    @Test
    fun getObjectSetNoCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(false)

        preferences.edit().putObject(KEY_1, value).commit()

        Assert.assertEquals(value, preferences.getObject(KEY_1, value, Set::class.java))
    }

    @Test
    fun getObjectSetDefaultValueNoCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(false)

        preferences.edit().putObject(KEY_1, HashSet<String>()).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(value, preferences.getObject(KEY_1, value, Set::class.java))
    }

    @Test
    fun putObjectSetCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(true)

        preferences.edit().putObject(KEY_1, value).commit()

        val encrypted = createDefaultPreferences().getString(KEY_1, null)

        Assert.assertNotNull(encrypted)
        Assert.assertEquals(transformer.toString(value), cipher.decrypt(encrypted!!))
    }

    @Test
    fun getObjectSetCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(true)

        preferences.edit().putObject(KEY_1, value).commit()

        Assert.assertEquals(value,  preferences.getObject(KEY_1, null, Set::class.java))
        Assert.assertEquals(value, preferences.getCacheAsMap()!![KEY_1])
    }

    @Test
    fun getObjectSetDefaultValueCacheTest() {
        val value = HashSet<String>()
        value.add("str1")
        value.add("str2")
        value.add("str3")

        val preferences = createCipherPreferences(true)

        preferences.edit().putObject(KEY_1, HashSet<String>()).commit()
        preferences.edit().remove(KEY_1).commit()

        Assert.assertEquals(value, preferences.getObject(KEY_1, value, Set::class.java))
    }

    //------------------------------All tests------------------------------------------//

    @Test
    fun getAllTest() {
        val longValue = 675L
        val stringValue = "SomeString"

        val preferences = createCipherPreferences(false)

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit()

        val all = preferences.all
        Assert.assertEquals("" + longValue, all[KEY_1])
        Assert.assertEquals(stringValue, all[KEY_2])
    }

    @Test
    fun getCacheAsMapNoCacheTest() {
        val longValue = 675L
        val stringValue = "SomeString"

        val preferences = createCipherPreferences(false)

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit()

        val cacheMap = preferences.getCacheAsMap()
        Assert.assertNull(cacheMap)
    }

    @Test
    fun getCacheAsMapTest() {
        val longValue = 675L
        val stringValue = "SomeString"

        val preferences = createCipherPreferences(true)

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit()

        val cacheMap = preferences.getCacheAsMap()
        Assert.assertNotNull(cacheMap)
        Assert.assertEquals(longValue, cacheMap!![KEY_1])
        Assert.assertEquals(stringValue, cacheMap[KEY_2])
    }

    @Test
    fun clearCacheTest() {
        val longValue = 675L
        val stringValue = "SomeString"

        val preferences = createCipherPreferences(true)

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit()

        Assert.assertEquals(2, preferences.getCacheAsMap()!!.size.toLong())
        preferences.clearCache()
        Assert.assertEquals(0, preferences.getCacheAsMap()!!.size.toLong())
    }

    @Test
    fun removeKeyNoCacheTest() {
        val longValue = 675L
        val stringValue = "SomeString"

        val preferences = createCipherPreferences(false)

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit()

        preferences.edit().remove(KEY_1).commit()

        Assert.assertFalse(preferences.contains(KEY_1))
        Assert.assertTrue(preferences.contains(KEY_2))
    }

    @Test
    fun removeKeyCacheTest() {
        val longValue = 675L
        val stringValue = "SomeString"

        val preferences = createCipherPreferences(true)

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit()

        preferences.edit().remove(KEY_1).commit()

        Assert.assertFalse(preferences.contains(KEY_1))
        Assert.assertTrue(preferences.contains(KEY_2))
    }

    @Test
    fun clearPrefsNoCacheTest() {
        val longValue = 675L
        val stringValue = "SomeString"

        val preferences = createCipherPreferences(false)

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit()

        preferences.edit().clear().commit()

        Assert.assertFalse(preferences.contains(KEY_1))
        Assert.assertFalse(preferences.contains(KEY_2))
    }

    @Test
    fun clearPrefsCacheTest() {
        val longValue = 675L
        val stringValue = "SomeString"

        val preferences = createCipherPreferences(true)

        preferences.edit()
                .putLong(KEY_1, longValue)
                .putString(KEY_2, stringValue)
                .commit()

        preferences.edit().clear().commit()

        Assert.assertFalse(preferences.contains(KEY_1))
        Assert.assertFalse(preferences.contains(KEY_2))
    }

    companion object {

        private val SHARED_PREFS = "PrefsName"
        private val KEY_1 = "Key1"
        private val KEY_2 = "Key2"
    }

}
