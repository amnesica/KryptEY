package com.amnesica.kryptey.inputmethod.crypto;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Deprecated
// Basic AES encryption class for testing
public class AESCrypt {
  private static final String ALGORITHM = "AES";

  public static String encrypt(CharSequence value, CharSequence password) throws Exception {
    Key key = generateKey(password);
    Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encryptedByteValue = cipher.doFinal(value.toString().getBytes(StandardCharsets.UTF_8));
    return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
  }

  public static String decrypt(CharSequence value, CharSequence password) throws Exception {
    Key key = generateKey(password);
    Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decryptedValue64 = Base64.decode(value.toString(), Base64.DEFAULT);
    byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
    return new String(decryptedByteValue, StandardCharsets.UTF_8);
  }

  private static Key generateKey(final CharSequence password) {
    Key key = new SecretKeySpec(password.toString().getBytes(), AESCrypt.ALGORITHM);
    return key;
  }
}