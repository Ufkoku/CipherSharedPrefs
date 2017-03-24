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

import android.content.SharedPreferences
import com.ufkoku.cipher_sharedprefs.api.ICacheHolder
import com.ufkoku.cipher_sharedprefs.api.ICipherHolder
import com.ufkoku.cipher_sharedprefs.api.ITransformer
import org.json.JSONArray

open class CipherSharedPreferences(
        delegate: SharedPreferences,
        cipher: ICipherHolder,
        cache: ICacheHolder?,
        transformer: ITransformer?) : SharedPreferences {

    protected val delegate: SharedPreferences
    protected val cipher: ICipherHolder
    protected val cache: ICacheHolder?
    protected val transformer: ITransformer?

    init {
        this.delegate = delegate
        this.cipher = cipher
        this.cache = cache
        this.transformer = transformer
    }

    constructor(delegate: SharedPreferences, cipher: ICipherHolder) : this(delegate, cipher, null, null)

    constructor(delegate: SharedPreferences, cipher: ICipherHolder, cache: ICacheHolder?) : this(delegate, cipher, cache, null)

    constructor(delegate: SharedPreferences, cipher: ICipherHolder, transformer: ITransformer) : this(delegate, cipher, null, transformer)

    override fun edit(): CipherEditor {
        return CipherEditor(delegate.edit())
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        val value: Boolean
        if (cache != null && cache.containsKey(key)) {
            value = cache.get(key) as Boolean
        } else {
            val fetchedVal = delegate.getString(key, null)
            value = if (fetchedVal != null) java.lang.Boolean.parseBoolean(cipher.decrypt(fetchedVal)) else defValue
            if (fetchedVal != null) {
                cache?.put(key, value)
            }
        }
        return value
    }

    override fun getFloat(key: String, defValue: Float): Float {
        val value: Float
        if (cache != null && cache.containsKey(key)) {
            value = cache.get(key) as Float
        } else {
            val fetchedVal = delegate.getString(key, null)
            value = if (fetchedVal != null) java.lang.Float.parseFloat(cipher.decrypt(fetchedVal)) else defValue
            if (fetchedVal != null) {
                cache?.put(key, value)
            }
        }
        return value
    }

    override fun getInt(key: String, defValue: Int): Int {
        val value: Int
        if (cache != null && cache.containsKey(key)) {
            value = cache.get(key) as Int
        } else {
            val fetchedVal = delegate.getString(key, null)
            value = if (fetchedVal != null) java.lang.Integer.parseInt(cipher.decrypt(fetchedVal)) else defValue
            if (fetchedVal != null) {
                cache?.put(key, value)
            }
        }
        return value
    }

    override fun getLong(key: String, defValue: Long): Long {
        val value: Long
        if (cache != null && cache.containsKey(key)) {
            value = cache.get(key) as Long
        } else {
            val fetchedVal = delegate.getString(key, null)
            value = if (fetchedVal != null) java.lang.Long.parseLong(cipher.decrypt(fetchedVal)) else defValue
            if (fetchedVal != null) {
                cache?.put(key, value)
            }
        }
        return value
    }

    override fun getString(key: String, defValue: String?): String? {
        val value: String?
        if (cache != null && cache.containsKey(key)) {
            value = cache.get(key) as String?
        } else {
            if (delegate.contains(key)) {
                val fetchedValue = delegate.getString(key, null)
                value = if (fetchedValue != null) cipher.decrypt(fetchedValue) else null
                cache?.put(key, value)
            } else {
                return defValue
            }
        }
        return value
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        val value: Set<String>?
        if (cache != null && cache.containsKey(key)) {
            value = cache.get(key) as Set<String>?
        } else {
            if (delegate.contains(key)) {
                val fetchedValue = delegate.getString(key, null)
                if (fetchedValue != null) {
                    val decryptedJsonArray = JSONArray(cipher.decrypt(fetchedValue))
                    if (decryptedJsonArray.length() == 0) {
                        value = HashSet()
                    } else {
                        value = HashSet(decryptedJsonArray.length())
                        for (i in 0 until decryptedJsonArray.length()) {
                            (value as MutableSet<String>).add(decryptedJsonArray.getString(i))
                        }
                    }
                } else {
                    value = null
                }
                cache?.put(key, value)
            } else {
                value = defValues
            }
        }
        return value
    }

    /**
     * Get an object from string in SharedPrefs using set ITransformer
     * Throws exceptions if ITransformer not set
     * */
    open fun <T> getObject(key: String, defValue: T?, clazz: Class<T>): T? {
        val value: T?
        if (cache != null && cache.containsKey(key)) {
            value = cache.get(key) as T?
        } else {
            if (delegate.contains(key)) {
                val fetchedValue = delegate.getString(key, null)
                value = if (fetchedValue != null) transformer!!.fromString(cipher.decrypt(fetchedValue), clazz) else null
                cache?.put(key, value)
            } else {
                value = defValue
            }
        }
        return value
    }

    /**
     * Note 1: this method ignores cache
     * Note 2: this method returns map of <String, String> type
     *
     * @return map of decrypted strings
     * */
    override fun getAll(): Map<String, String> {
        val map = HashMap<String, String>()
        delegate.all.forEach { map[it.key] = cipher.decrypt(it.value as String) }
        return map
    }

    /**
     * @return cache as map
     * */
    open fun getCacheAsMap(): Map<String, *>? {
        return cache?.getAsMap()
    }

    /**
     * Removes all records from Cache
     * */
    open fun clearCache() {
        cache?.clear()
    }

    override fun contains(key: String): Boolean {
        return (cache != null && cache.containsKey(key)) || delegate.contains(key)
    }

    override fun registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        delegate.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener) {
        delegate.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    open inner class CipherEditor(val delegate: SharedPreferences.Editor) : SharedPreferences.Editor {

        protected var clearAllOldRecords = false
        protected val removeValuesTempCache: MutableSet<String>?
        protected val putValuesTempCache: MutableMap<String, Any?>?

        init {
            if (cache != null) {
                removeValuesTempCache = LinkedHashSet()
                putValuesTempCache = LinkedHashMap()
            } else {
                removeValuesTempCache = null
                putValuesTempCache = null
            }
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            putValuesTempCache?.put(key, value)
            delegate.putString(key, cipher.encrypt(value.toString()))
            return this
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            putValuesTempCache?.put(key, value)
            delegate.putString(key, cipher.encrypt(value.toString()))
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            putValuesTempCache?.put(key, value)
            delegate.putString(key, cipher.encrypt(value.toString()))
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            putValuesTempCache?.put(key, value)
            delegate.putString(key, cipher.encrypt(value.toString()))
            return this
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            putValuesTempCache?.put(key, value)
            delegate.putString(key, if (value != null) cipher.encrypt(value) else null)
            return this
        }

        override fun putStringSet(key: String, value: Set<String>?): SharedPreferences.Editor {
            putValuesTempCache?.put(key, value)
            delegate.putString(key, if (value != null) cipher.encrypt(JSONArray(value).toString()) else null)
            return this
        }

        fun putObject(key: String, value: Any?): SharedPreferences.Editor {
            putValuesTempCache?.put(key, value)
            delegate.putString(key, if (value != null) cipher.encrypt(transformer!!.toString(value)) else null)
            return this
        }

        override fun apply() {
            applyChangesToCache()
            delegate.apply()
        }

        override fun commit(): Boolean {
            if (delegate.commit()) {
                applyChangesToCache()
                return true
            } else {
                return false
            }
        }

        override fun remove(s: String): SharedPreferences.Editor {
            if (!clearAllOldRecords) {
                removeValuesTempCache?.add(s)
                delegate.remove(s)
            }
            return this
        }

        override fun clear(): SharedPreferences.Editor {
            clearAllOldRecords = true
            delegate.clear()
            return this
        }

        protected open fun applyChangesToCache() {
            if (cache != null) {
                if (clearAllOldRecords) {
                    cache.clear()
                } else {
                    removeValuesTempCache?.forEach { cache.remove(it) }
                }
                putValuesTempCache?.forEach { cache.put(it.key, it.value) }
            }
        }

    }

}
