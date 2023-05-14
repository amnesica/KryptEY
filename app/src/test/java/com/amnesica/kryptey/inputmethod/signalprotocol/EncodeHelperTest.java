package com.amnesica.kryptey.inputmethod.signalprotocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.encoding.EncodeHelper;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EncodeHelperTest {

  static final String TAG = EncodeHelperTest.class.getSimpleName();

  private static final String loremMessage = "\n" +
      "What is Lorem Ipsum?\n" +
      "\n" +
      "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n" +
      "Why do we use it?\n" +
      "\n" +
      "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).\n" +
      "\n" +
      "Where does it come from?\n" +
      "\n" +
      "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
      "\n" +
      "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.\n";

  // hint: dummy message, not real PreKeyMessage!
  private final String message = "{\n" +
      "  \"preKeyResponse\": {\n" +
      "    \"identityKey\": {\n" +
      "      \"publicKey\": \"BfcNEz1BI250USabljseyzT/I7EBiKz0nm6iaIdi4FdI\"\n" +
      "    },\n" +
      "    \"devices\": [\n" +
      "      {\n" +
      "        \"deviceId\": 455,\n" +
      "        \"registrationId\": 8949,\n" +
      "        \"signedPreKey\": {\n" +
      "          \"keyId\": 1,\n" +
      "          \"publicKey\": \"Bb6ZZwryabtRqjbHE7gUpYRnYK8sU4MOxpiibzSRD0kQ\",\n" +
      "          \"signature\": \"vgDaCgk3P5zVdNyVS6Xr0xaYgBpVAcYKh+i77J5f9a1re+CUKLGOUTL7SzJ6a3NLPKOV137VQm2/0c1tBR1thg\"\n" +
      "        },\n" +
      "        \"preKey\": {\n" +
      "          \"keyId\": 1,\n" +
      "          \"publicKey\": \"BTxCuP6gAmwlXabjsxxRF+IgbgySH7Xro7LPrkiEtrlU\"\n" +
      "        }\n" +
      "      }\n" +
      "    ]\n" +
      "  },\n" +
      "  \"ciphertextMessage\": \"MwgBEiEFf5m9kjo0NyaaamgGE4zEc2rb8ZGpjGShXn9FFVOFrk0aIQX3DRM9QSNudFEqsZY7Hss0/yOxAYis9J5uomiHYuBXSCJCMwohBZ52lRNyWg3mknliHk2v+byIs1AYNBQdQ4kSMwqELNhvEAAYACIQX3sYXKogcyfhNRd/htsfZp5KpBknS+HKKPVFMAE=\",\n" +
      "  \"ciphertextType\": 3,\n" +
      "  \"timestamp\": 1674375657.791099,\n" +
      "  \"signalProtocolAddressName\": \"ba68f08f-57ea-4b4a-8b24-82ea156da53f\",\n" +
      "  \"deviceId\": 455\n" +
      "}";

  private static final String decoyText = "Some decoy message";

  @Test
  public void convertByteArrayToBinaryTest() {
    Log.i(TAG, "------------ convertByteArrayToBinaryTest: ------------");
    // simple test
    String a = "abc";
    String binaryA = EncodeHelper.convertByteArrayToBinary(a.getBytes(StandardCharsets.UTF_8));
    String aDecoded = new String(EncodeHelper.convertBinaryToByteArray(binaryA));
    assertEquals(a, aDecoded);

    // actual test
    String messageBinaryString = EncodeHelper.convertByteArrayToBinary(loremMessage.getBytes(StandardCharsets.UTF_8));
    assertNotNull(messageBinaryString);
    assertEquals(loremMessage, new String(EncodeHelper.convertBinaryToByteArray(messageBinaryString)));
  }

  @Test
  public void convertBinaryToByteArrayTest() {
    Log.i(TAG, "------------ convertBinaryToByteArrayTest: ------------");
    String binaryMessage = EncodeHelper.convertByteArrayToBinary(loremMessage.getBytes(StandardCharsets.UTF_8));
    String decoded = new String(EncodeHelper.convertBinaryToByteArray(binaryMessage));
    assertNotNull(decoded);
    assertEquals(loremMessage, decoded);
  }

  @Test
  public void convertBinaryToInvisibleStringTest() {
    Log.i(TAG, "------------ convertBinaryToInvisibleStringTest: ------------");
    String binaryMessage = EncodeHelper.convertByteArrayToBinary(loremMessage.getBytes(StandardCharsets.UTF_8));
    String invisibleResult = EncodeHelper.convertBinaryToInvisibleString(binaryMessage);
    assertNotNull(invisibleResult);
  }

  @Test
  public void encodedTextContainsInvisibleCharactersTest() throws IOException {
    Log.i(TAG, "------------ encodedTextContainsInvisibleCharactersTest: ------------");
    String normalText = "Hello World";

    String binaryMessage = EncodeHelper.convertByteArrayToBinary(normalText.getBytes(StandardCharsets.UTF_8));
    String textWithInvisibleText = EncodeHelper.convertBinaryToInvisibleString(binaryMessage) + "Hello World";
    assertNotNull(textWithInvisibleText);

    assertFalse(EncodeHelper.encodedTextContainsInvisibleCharacters(normalText));
    assertTrue(EncodeHelper.encodedTextContainsInvisibleCharacters(textWithInvisibleText));
  }

  @Test
  public void convertBinaryStringTest() {
    Log.i(TAG, "------------ convertBinaryStringTest: ------------");
    final String binary = "01100001011000100110001100100000011000010110001001100011001000000110000101100010011000110010000001100001011000100110001100100000011000010110001001100011";
    final String invString = EncodeHelper.convertBinaryToInvisibleString(binary);
    assertNotNull(invString);

    final String binaryDecoded = EncodeHelper.convertInvisibleStringToBinary(invString);
    assertEquals(binary, binaryDecoded);
  }

  @Test
  public void minifyJsonTest() {
    Log.i(TAG, "------------ minifyJsonTest: ------------");
    Log.i(TAG, "message (bytes): " + message.getBytes().length);
    String minifiedJson = EncodeHelper.minifyJSON(message); // hint: minify includes simplify method
    assertNotNull(minifiedJson);
    assertFalse(minifiedJson.contains(" "));
    assertFalse(minifiedJson.contains("preKeyResponse"));
    Log.i(TAG, "minifiedJson (bytes): " + minifiedJson.getBytes().length);

    String deSimplifiedJson = EncodeHelper.deSimplifyJsonKeys(minifiedJson);
    assertNotNull(deSimplifiedJson);
    assertTrue(deSimplifiedJson.contains("preKeyResponse"));
    Log.i(TAG, "deSimplifiedJson (bytes): " + deSimplifiedJson.getBytes().length);

    String minifiedMessageWithoutChangingKeys = message.replaceAll(" ", "")
        .replaceAll("\n", "");
    assertEquals(minifiedMessageWithoutChangingKeys, deSimplifiedJson);
  }
}
