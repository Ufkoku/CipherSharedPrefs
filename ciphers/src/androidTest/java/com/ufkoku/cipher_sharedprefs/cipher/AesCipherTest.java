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

package com.ufkoku.cipher_sharedprefs.cipher;

import android.annotation.SuppressLint;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;

@RunWith(AndroidJUnit4.class)
@SmallTest
@SuppressLint("ApplySharedPref")
public class AesCipherTest {

    private static final String TAG = "AesCipherTest";

    private static final byte[] KEY = "1234567890123456".getBytes(Charset.forName("latin-1"));
    private static final byte[] IV = "6543210987654321".getBytes(Charset.forName("latin-1"));

    private static final String FISH = "Test string for encryption"; //%16 != 0
    private static final String FISH_2 = "Test string for encryption value"; //%16 == 0

    @Test
    public void ecbTest() {
        AesCipher cipher = new AesCipher(KEY);
        test(cipher, AesCipherMode.ECB);
    }

    @Test
    public void cbcTest() {
        AesCipher cipher = new AesCipher(KEY, AesCipherMode.CBC, IV);
        test(cipher, AesCipherMode.CBC);
    }

    @Test
    public void cfbTest() {
        AesCipher cipher = new AesCipher(KEY, AesCipherMode.CFB, IV);
        test(cipher, AesCipherMode.CFB);
    }

    @Test
    public void ofbTest() {
        AesCipher cipher = new AesCipher(KEY, AesCipherMode.OFB, IV);
        test(cipher, AesCipherMode.OFB);
    }

    @Test
    public void ctrTest() {
        AesCipher cipher = new AesCipher(KEY, AesCipherMode.CTR, IV);
        test(cipher, AesCipherMode.CTR);
    }

    private void test(AesCipher cipher, @AesCipherMode String mode) {
        test(cipher, mode, FISH);
        test(cipher, mode, FISH_2);
    }

    private void test(AesCipher cipher, @AesCipherMode String mode, String text) {
        String encrypted = cipher.encrypt(text);
        Assert.assertNotEquals(text, encrypted);
        Log.d(TAG + "/" + mode, "Original text" + text + ";Encrypted text " + encrypted);
        Assert.assertEquals(text, cipher.decrypt(encrypted));
    }

}