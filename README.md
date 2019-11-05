@Deprecated
Use [androidx.EncryptedSharedPreferences](https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences) instead

# CipherSharedPrefs

![alt tag](https://img.shields.io/badge/version-1.0.3-brightgreen.svg)

CipherSharedPrefs implements SharedPreferences with encryption layer and some new features.

```gradle
repositories {
    maven { url 'https://dl.bintray.com/ufkoku/maven/' }
}

dependencies {
    compile 'com.ufkoku.cipher_sharedprefs:sharedprefs:version'
    //or
    compile 'com.ufkoku.cipher_sharedprefs:sharedprefs:version@aar'{
        transitive = true
    }
}
```

## How it works
When you put value to CipherSharedPreferences it is transformed to String and encrypted by provided ICipherHolder object, encryped string is saved to SharedPreferences delegate.

```java
new CipherSharedPreferences(
                context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE),
                iCipherHolder,
                iCacheHolder, //optional
                iTransformer //optional
        )
```

### Cipher
You must provide implementation of ICipherHolder to CipherSharedPreferences constructor. It is used for data encryption and decryption.

Library contains AES implementation AesCipher.
* **Default mode**: ECB;
* **Supported modes**: CBC, CFB, CTR, ECB, OFB;
* **IV requires modes**: CBC, CFB, OFB, CTR;
* **Supported keys**: Supported size 128/192/256 bits -> 16/24/32 bytes;
* **IV lenght**: equals to key lenght;

IV - initialization vector

```gradle
dependencies {
    compile 'com.ufkoku.cipher_sharedprefs:ciphers:version'
    //or
    compile 'com.ufkoku.cipher_sharedprefs:ciphers:version@aar'{
        transitive = true
    }
}
```

### Cache (Optional)
You can provide ICacheHolder to CipherSharedPreferences.

If ICacheHolder exists, values WITHOUT encryption will be saved there, after get/put methods for key.  
When you call get methods, CipherSharedPreferences checks cache, and if value exists - it will be returned, otherwise it will be decrypted from delegate, and saved to cache.  
When you call put methods, CipherSharedPreferences saves value to cache WITHOUT encryption, and encrypted string to delegate.  

**PROS**
* When you getting value, that contains in cache, you are not spending time on decryption.

**LIMITATIONS**
* If you use cache, you should use CipherSharedPreferences or ICacheHolder as singleton, cache won't be refreshed if you modify prefs from another instance.

There are already implemented cache holders, that you can use in module caches

```gradle
dependencies {
    compile 'com.ufkoku.cipher_sharedprefs:caches:version'
    //or
    compile 'com.ufkoku.cipher_sharedprefs:caches:version@aar'{
        transitive = true
    }
}
```

Implementations:
* SharedPrefsLruCache wrap over [SizeBasedEnhancedLruCache](https://github.com/Ufkoku/SizeBasedEnhancedLruCache), it limits cache by size in bytes.
* SharedPrefsMapCache wrap over HashMap

### Object saving (Optional)
CipherSharedPreferences can save objects and retrieve them back via methods

```kotlin
CipherSharedPreferences {
    
    //get object
    <T> getObject(key: String, defValue: T?, clazz: Class<T>): T?

    CipherEditor{
        //save object
        putObject(key: String, value: Any?): SharedPreferences.Editor
    }

}
```

To use this feature you need to provide ITransformer implementation to CipherSharedPreferences constructor.
```java
//Example of ITransformer
//This transformer uses GSON to transform input objects to string and back.
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
```

Example of usage
```java
        Set<String> value = new HashSet<>();
        value.add("str1");
        value.add("str2");
        value.add("str3");
        
        preferences.edit().putObject(KEY_1, value).commit();
        preferences.getObject(KEY_1, null, value.getClass())        
```
