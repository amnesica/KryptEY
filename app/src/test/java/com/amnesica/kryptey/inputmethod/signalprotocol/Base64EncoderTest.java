package com.amnesica.kryptey.inputmethod.signalprotocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.encoding.Base64Encoder;

import org.junit.Test;

import java.io.IOException;

public class Base64EncoderTest {

  static final String TAG = Base64EncoderTest.class.getSimpleName();

  // hint: dummy message, not real PreKeyMessage!
  private final String message = "{\n" +
      "  \"ciphertextMessage\" : null,\n" +
      "  \"ciphertextType\" : 0,\n" +
      "  \"deviceId\" : 2860,\n" +
      "  \"preKeyResponse\" : {\n" +
      "    \"devices\" : [ {\n" +
      "      \"deviceId\" : 2860,\n" +
      "      \"preKey\" : {\n" +
      "        \"keyId\" : 187,\n" +
      "        \"publicKey\" : \"BWYx2LP/2DSzm/zJtDazzqcamaEVo0kyU+rEzu73jFvJM\"\n" +
      "      },\n" +
      "      \"registrationId\" : 16268,\n" +
      "      \"signedPreKey\" : {\n" +
      "        \"keyId\" : 60,\n" +
      "        \"publicKey\" : \"BUiB07r7Qr0YnnESeaNWhdQq1SshdjdnVohxHfyWA4JkNw4d\",\n" +
      "        \"signature\" : \"K6GButcueEdIYifKyOhsrXxiNUsHsEOPTVFV96efMfAkO0uE0zH5CrpZU25nhy3lUgGEdYFgQIT29JyuXg3IFGhQ\"\n" +
      "      }\n" +
      "    } ],\n" +
      "    \"identityKey\" : {\n" +
      "      \"publicKey\" : \"BaUr2CmXaHgL0DUvmyHfQPYUdhs3lwW5+5ieuSskDMazh9\"\n" +
      "    }\n" +
      "  },\n" +
      "  \"signalProtocolAddressName\" : \"f50d2f3f-ca11-4985-ac31-904f80cf4a07\",\n" +
      "  \"timestamp\" : 1674142143915\n" +
      "} ";

  private final String messageDeSimplified = "{\"ciphertextMessage\":null,\"ciphertextType\":0,\"deviceId\":2860,\"preKeyResponse\":{\"devices\":[{\"deviceId\":2860,\"preKey\":{\"keyId\":187,\"publicKey\":\"BWYx2LP/2DSzm/zJtDazzqcamaEVo0kyU+rEzu73jFvJM\"},\"registrationId\":16268,\"signedPreKey\":{\"keyId\":60,\"publicKey\":\"BUiB07r7Qr0YnnESeaNWhdQq1SshdjdnVohxHfyWA4JkNw4d\",\"signature\":\"K6GButcueEdIYifKyOhsrXxiNUsHsEOPTVFV96efMfAkO0uE0zH5CrpZU25nhy3lUgGEdYFgQIT29JyuXg3IFGhQ\"}}],\"identityKey\":{\"publicKey\":\"BaUr2CmXaHgL0DUvmyHfQPYUdhs3lwW5+5ieuSskDMazh9\"}},\"signalProtocolAddressName\":\"f50d2f3f-ca11-4985-ac31-904f80cf4a07\",\"timestamp\":1674142143915}";

  @Test
  public void encodeDecodeTest() throws IOException {
    Log.d(TAG, "------------ encodeDecodeTest: ------------");
    // hint: simplified values would be shown because objectMapper is not implemented in the method calls
    final String encodedMessage = Base64Encoder.encode(message);
    assertNotNull(encodedMessage);
    Log.d(TAG, encodedMessage);

    final String decodedMessage = Base64Encoder.decode(encodedMessage);
    assertNotNull(decodedMessage);
    assertEquals(messageDeSimplified, decodedMessage);
  }
}
