package com.amnesica.kryptey.inputmethod.signalprotocol.encoding;

import com.amnesica.kryptey.inputmethod.signalprotocol.util.Base64;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Base64Encoder {

  public static String encode(final String message) {
    final String minifiedJson = EncodeHelper.minifyJSON(message);
    return Base64.encodeBytes(minifiedJson.getBytes(StandardCharsets.UTF_8));
  }

  public static String decode(final String encodedText) throws IOException {
    final String decodedWithSimplifiedKeys = new String(Base64.decode(encodedText));
    return EncodeHelper.deSimplifyJsonKeys(decodedWithSimplifiedKeys);
  }
}
