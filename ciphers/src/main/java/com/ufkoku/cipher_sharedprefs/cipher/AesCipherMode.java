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

import android.support.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

import static com.ufkoku.cipher_sharedprefs.cipher.AesCipherMode.CBC;
import static com.ufkoku.cipher_sharedprefs.cipher.AesCipherMode.CFB;
import static com.ufkoku.cipher_sharedprefs.cipher.AesCipherMode.CTR;
import static com.ufkoku.cipher_sharedprefs.cipher.AesCipherMode.ECB;
import static com.ufkoku.cipher_sharedprefs.cipher.AesCipherMode.OFB;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@StringDef({ECB, CBC, CFB, OFB, CTR})
public @interface AesCipherMode {

    String ECB = "ECB";
    String CBC = "CBC";
    String CFB = "CFB";
    String OFB = "OFB";
    String CTR = "CTR";

}
