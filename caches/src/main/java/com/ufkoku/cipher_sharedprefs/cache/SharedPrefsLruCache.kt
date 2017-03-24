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

package com.ufkoku.cipher_sharedprefs.cache

import com.ufkoku.cache.SizeBasedEnhancedLruCache
import com.ufkoku.cipher_sharedprefs.api.ICacheHolder

open class SharedPrefsLruCache(sizeInBytes: Int) : ICacheHolder {

    protected val cache: SizeBasedEnhancedLruCache<String, Any?> = SizeBasedEnhancedLruCache(sizeInBytes)

    override fun getAsMap(): Map<String, *> {
        return cache.snapshot()
    }

    override fun put(key: String, value: Any?) {
        cache.put(key, value)
    }

    override fun get(key: String): Any? {
        return cache.get(key)
    }

    override fun containsKey(key: String): Boolean {
        return cache.containsKey(key)
    }

    override fun remove(key: String) {
        cache.remove(key)
    }

    override fun clear() {
        cache.evictAll()
    }

}
