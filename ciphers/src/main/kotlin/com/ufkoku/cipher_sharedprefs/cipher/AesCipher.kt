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

import com.ufkoku.cipher_sharedprefs.api.ICipherHolder
import com.ufkoku.cipher_sharedprefs.cipher.AesCipherMode.*
import java.nio.charset.Charset
import java.security.Key
import java.util.*

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Uses AES in provided mode to encrypt data.
 *
 * @param keyBytes array is used as key, for encryption. Supported size 128/192/256 bits -> 16/24/32 bytes
 * @param mode mode of block algorithm https://en.wikipedia.org/wiki/Block_cipher_mode_of_operation. Modes in IV_DEPENDENT_MODES array requires ivBytes, same length as keyBytes.
 * @param ivBytes array of initialization vector
 * */
class AesCipher(keyBytes: ByteArray, @AesCipherMode mode: String, ivBytes: ByteArray?) : ICipherHolder {

    companion object {
        val IV_DEPENDENT_MODES = arrayOf(CBC, CFB, OFB, CTR)

        val BLOCK_SIZE = 16

        private val CHARSET = Charset.forName("latin-1")
    }

    private val cipher: Cipher
    private val mode: String
    private val key: Key
    private val initVector: IvParameterSpec?

    init {
        if (mode in IV_DEPENDENT_MODES) {
            if (ivBytes == null) {
                throw IllegalArgumentException("Mode $mode requires initialization vector (IV)")
            }
            if (ivBytes.size != keyBytes.size) {
                throw IllegalArgumentException("Initialization vector length must be equal to key length")
            }
        }

        cipher = Cipher.getInstance("AES/$mode/NoPadding")
        this.mode = mode
        key = SecretKeySpec(keyBytes, "AES")

        if (ivBytes != null) {
            initVector = IvParameterSpec(ivBytes)
        } else {
            initVector = null
        }

    }

    constructor(keyBytes: ByteArray) : this(keyBytes, ECB, null)

    override fun encrypt(string: String): String {
        if (string.isEmpty()) {
            return ""
        }

        synchronized(cipher) {

            //init cipher for encryption
            if (initVector != null) {
                cipher.init(Cipher.ENCRYPT_MODE, key, initVector)
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key)
            }

            var sourceBytes = string.toByteArray()
            val originalSourceSize: Int = sourceBytes.size
            val completedBlocksSize: Int
            val newSourceSize: Int
            if (mode == AesCipherMode.CTR) {
                //CTR not need full block
                completedBlocksSize = originalSourceSize
                newSourceSize = originalSourceSize
            } else {
                completedBlocksSize = if (originalSourceSize % BLOCK_SIZE != 0) (BLOCK_SIZE * Math.ceil(sourceBytes.size.toDouble() / BLOCK_SIZE.toDouble())).toInt() else originalSourceSize
                newSourceSize = 16 + completedBlocksSize
                sourceBytes = Arrays.copyOf(sourceBytes, newSourceSize)

                val salt = ByteArray(newSourceSize - 1 - originalSourceSize)
                Random().nextBytes(salt) //avoid filling with 0 values
                for ((i: Int, b: Byte) in salt.withIndex()) {
                    sourceBytes[originalSourceSize + i] = b
                }
                sourceBytes[newSourceSize - 1] = (newSourceSize - originalSourceSize).toByte()
            }

            val encryptedBytes: ByteArray = ByteArray(newSourceSize)
            for (i in 0 until newSourceSize step BLOCK_SIZE) {
                val toIndex = Math.min(newSourceSize, i + BLOCK_SIZE)
                val encryptedBlockBytes = cipher.update(sourceBytes.copyOfRange(i, toIndex))
                for (j in 0 until encryptedBlockBytes.size) {
                    encryptedBytes[i + j] = encryptedBlockBytes[j]
                }
            }
            cipher.doFinal() //reset cipher

            //save as latin-1 sting, it not modifies byte array
            return String(encryptedBytes, CHARSET)
        }
    }

    override fun decrypt(string: String): String {
        if (string.isEmpty()) {
            return ""
        }

        synchronized(cipher) {

            //init cipher for decryption
            if (initVector != null) {
                cipher.init(Cipher.DECRYPT_MODE, key, initVector)
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key)
            }

            val sourceBytes = string.toByteArray(CHARSET)

            val decryptedBytes = ByteArray(sourceBytes.size)
            for (i in 0 until sourceBytes.size step BLOCK_SIZE) {
                val toIndex = Math.min(sourceBytes.size, i + BLOCK_SIZE)
                val decryptedBlockBytes = cipher.update(sourceBytes.copyOfRange(i, toIndex))
                for (j in 0 until decryptedBlockBytes.size) {
                    decryptedBytes[i + j] = decryptedBlockBytes[j]
                }
            }

            //reset cipher
            cipher.doFinal()

            //return start string
            if (mode == AesCipherMode.CTR) {
                return String(decryptedBytes)
            } else {
                return String(decryptedBytes.copyOfRange(0, decryptedBytes.size - decryptedBytes[decryptedBytes.size - 1].toInt()))
            }
        }
    }

}
