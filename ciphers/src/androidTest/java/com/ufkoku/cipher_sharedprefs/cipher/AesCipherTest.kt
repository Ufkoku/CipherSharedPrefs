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

package com.ufkoku.cipher_sharedprefs.cipher

import android.annotation.SuppressLint
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import android.util.Log

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

import java.nio.charset.Charset

@RunWith(AndroidJUnit4::class)
@SmallTest
@SuppressLint("ApplySharedPref")
class AesCipherTest {

    companion object {

        @JvmStatic
        private val TAG = "AesCipherTest"

        @JvmStatic
        private val KEY = "1234567890123456".toByteArray(Charset.forName("latin-1"))

        @JvmStatic
        private val IV = "6543210987654321".toByteArray(Charset.forName("latin-1"))

        @JvmStatic
        private val FISH = "Test string for encryption" //%16 != 0

        @JvmStatic
        private val FISH_2 = "Test string for encryption value" //%16 == 0
    }

    @Test
    fun ecbTest() {
        val cipher = AesCipher(KEY)
        test(cipher, AesCipherMode.ECB)
    }

    @Test
    fun cbcTest() {
        val cipher = AesCipher(KEY, AesCipherMode.CBC, IV)
        test(cipher, AesCipherMode.CBC)
    }

    @Test
    fun cfbTest() {
        val cipher = AesCipher(KEY, AesCipherMode.CFB, IV)
        test(cipher, AesCipherMode.CFB)
    }

    @Test
    fun ofbTest() {
        val cipher = AesCipher(KEY, AesCipherMode.OFB, IV)
        test(cipher, AesCipherMode.OFB)
    }

    @Test
    fun ctrTest() {
        val cipher = AesCipher(KEY, AesCipherMode.CTR, IV)
        test(cipher, AesCipherMode.CTR)
    }

    private fun test(cipher: AesCipher, @AesCipherMode mode: String) {
        test(cipher, mode, FISH)
        test(cipher, mode, FISH_2)
    }

    private fun test(cipher: AesCipher, @AesCipherMode mode: String, text: String) {
        val encrypted = cipher.encrypt(text)
        Assert.assertNotEquals(text, encrypted)
        Log.d("$TAG/$mode", "Original text$text;Encrypted text $encrypted")
        Assert.assertEquals(text, cipher.decrypt(encrypted))
    }

}