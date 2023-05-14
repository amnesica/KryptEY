package com.amnesica.kryptey.inputmethod.signalprotocol.encoding;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class EncodeHelper {

  private static final String TAG = EncodeHelper.class.getSimpleName();
  static HashMap<String, String> simplifiedJSONMap = initSimplifiedJsonMap();

  public static String convertByteArrayToBinary(byte[] input) {
    final StringBuilder result = new StringBuilder();
    for (byte b : input) {
      int val = b;
      for (int i = 0; i < 8; i++) {
        result.append((val & 128) == 0 ? 0 : 1); // 128 = 1000 0000
        val <<= 1;
      }
    }
    return result.toString();
  }

  public static byte[] convertBinaryToByteArray(String binary) {
    return new BigInteger(binary, 2).toByteArray();
  }

  public static String convertInvisibleStringToBinary(String encodedMessage) {
    StringBuilder result = new StringBuilder();
    StringBuilder resultUnicode = new StringBuilder();
    final String regex = "\\p{C}";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(encodedMessage);

    while (matcher.find()) {
      final String s = encodedMessage.substring(matcher.start(), matcher.end());
      switch (s) {
        case "\u200C":
          result.append("0000");
          resultUnicode.append("\\u200C");
          break;
        case "\u200D":
          result.append("0001");
          resultUnicode.append("\\u200D");
          break;
        case "\u2060":
          result.append("0010");
          resultUnicode.append("\\u2060");
          break;
        case "\u2062":
          result.append("0011");
          resultUnicode.append("\\u2062");
          break;

        case "\u200B":
          result.append("0100");
          resultUnicode.append("\\u200B");
          break;
        case "\u200E":
          result.append("0101");
          resultUnicode.append("\\u200E");
          break;
        case "\u200F":
          result.append("0110");
          resultUnicode.append("\\u200F");
          break;
        case "\u2064":
          result.append("0111");
          resultUnicode.append("\\u2064");
          break;

        case "\u206A":
          result.append("1000");
          resultUnicode.append("\\u206A");
          break;
        case "\u206B":
          result.append("1001");
          resultUnicode.append("\\u206B");
          break;
        case "\u206C":
          result.append("1010");
          resultUnicode.append("\\u206C");
          break;
        case "\u206D":
          result.append("1011");
          resultUnicode.append("\\u206D");
          break;

        case "\u206E":
          result.append("1100");
          resultUnicode.append("\\u206E");
          break;
        case "\u206F":
          result.append("1101");
          resultUnicode.append("\\u206F");
          break;
        case "\uFEFF":
          result.append("1110");
          resultUnicode.append("\\uFEFF");
          break;
        case "\u061C":
          result.append("1111");
          resultUnicode.append("\\u061C");
          break;
      }
    }
    Log.d(TAG, String.valueOf(resultUnicode));
    return result.toString();
  }

  public static String convertBinaryToInvisibleString(String binaryString) {
    StringBuilder result = new StringBuilder();
    StringBuilder resultUnicode = new StringBuilder();

    for (int i = 0; i < binaryString.length(); i += 4) {
      final int startInclusive = i;
      final int endExclusive = i + 4;

      if (endExclusive >= binaryString.length() + 1) continue;
      String binaryDigits = binaryString.substring(startInclusive, endExclusive);

      switch (binaryDigits) {
        case "0000":
          result.append("\u200C");
          resultUnicode.append("\\u200C");
          break;
        case "0001":
          result.append("\u200D");
          resultUnicode.append("\\u200D");
          break;
        case "0010":
          result.append("\u2060");
          resultUnicode.append("\\u2060");
          break;
        case "0011":
          result.append("\u2062");
          resultUnicode.append("\\u2062");
          break;

        case "0100":
          result.append("\u200B");
          resultUnicode.append("\\u200B");
          break;
        case "0101":
          result.append("\u200E");
          resultUnicode.append("\\u200E");
          break;
        case "0110":
          result.append("\u200F");
          resultUnicode.append("\\u200F");
          break;
        case "0111":
          result.append("\u2064");
          resultUnicode.append("\\u2064");
          break;

        case "1000":
          result.append("\u206A");
          resultUnicode.append("\\u206A");
          break;
        case "1001":
          result.append("\u206B");
          resultUnicode.append("\\u206B");
          break;
        case "1010":
          result.append("\u206C");
          resultUnicode.append("\\u206C");
          break;
        case "1011":
          result.append("\u206D");
          resultUnicode.append("\\u206D");
          break;

        case "1100":
          result.append("\u206E");
          resultUnicode.append("\\u206E");
          break;
        case "1101":
          result.append("\u206F");
          resultUnicode.append("\\u206F");
          break;
        case "1110":
          result.append("\uFEFF");
          resultUnicode.append("\\uFEFF");
          break;
        case "1111":
          result.append("\u061C");
          resultUnicode.append("\\u061C");
          break;
      }
    }

    Log.d(TAG, String.valueOf(resultUnicode));
    return result.toString();
  }

  public static boolean encodedTextContainsInvisibleCharacters(final String encodedText) throws IOException {
    if (encodedText == null || encodedText.isEmpty())
      throw new IOException("There is no message to check");
    final String regex = "\\p{C}";
    final Pattern pattern = Pattern.compile(regex);
    final Matcher matcher = pattern.matcher(encodedText);
    return matcher.find();
  }

  public static String minifyJSON(String json) {
    String minifiedJSON = json.replaceAll(" ", "")
        .replaceAll("\n", "");
    return simplifyJsonKeys(minifiedJSON);
  }

  public static byte[] compressString(final String message) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION, true);
    DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(stream, compresser);
    deflaterOutputStream.write(message.getBytes(StandardCharsets.UTF_8));
    deflaterOutputStream.close();
    return stream.toByteArray();
  }

  public static String decompressString(byte[] compressedMessage) throws IOException {
    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
    Inflater decompresser = new Inflater(true);
    InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(stream2, decompresser);
    inflaterOutputStream.write(compressedMessage);
    inflaterOutputStream.close();
    return stream2.toString();
  }

  private static String simplifyJsonKeys(String json) {
    return json
        .replaceAll("\"preKeyResponse\"", "\"" + simplifiedJSONMap.get("preKeyResponse") + "\"")
        .replaceAll("\"identityKey\"", "\"" + simplifiedJSONMap.get("identityKey") + "\"")
        .replaceAll("\"publicKey\"", "\"" + simplifiedJSONMap.get("publicKey") + "\"")
        .replaceAll("\"devices\"", "\"" + simplifiedJSONMap.get("devices") + "\"")
        .replaceAll("\"deviceId\"", "\"" + simplifiedJSONMap.get("deviceId") + "\"")
        .replaceAll("\"registrationId\"", "\"" + simplifiedJSONMap.get("registrationId") + "\"")
        .replaceAll("\"signedPreKey\"", "\"" + simplifiedJSONMap.get("signedPreKey") + "\"")
        .replaceAll("\"keyId\"", "\"" + simplifiedJSONMap.get("keyId") + "\"")
        .replaceAll("\"signature\"", "\"" + simplifiedJSONMap.get("signature") + "\"")
        .replaceAll("\"preKey\"", "\"" + simplifiedJSONMap.get("preKey") + "\"")
        .replaceAll("\"ciphertextMessage\"", "\"" + simplifiedJSONMap.get("ciphertextMessage") + "\"")
        .replaceAll("\"ciphertextType\"", "\"" + simplifiedJSONMap.get("ciphertextType") + "\"")
        .replaceAll("\"timestamp\"", "\"" + simplifiedJSONMap.get("timestamp") + "\"")
        .replaceAll("\"signalProtocolAddressName\"", "\"" + simplifiedJSONMap.get("signalProtocolAddressName") + "\"");
  }

  private static HashMap<String, String> initSimplifiedJsonMap() {
    HashMap<String, String> simplifiedJSONMap = new HashMap<>();
    simplifiedJSONMap.put("preKeyResponse", "pR");
    simplifiedJSONMap.put("identityKey", "i");
    simplifiedJSONMap.put("publicKey", "pK");
    simplifiedJSONMap.put("devices", "d");
    simplifiedJSONMap.put("deviceId", "dI");
    simplifiedJSONMap.put("registrationId", "rI");
    simplifiedJSONMap.put("signedPreKey", "sK");
    simplifiedJSONMap.put("keyId", "k");
    simplifiedJSONMap.put("signature", "s");
    simplifiedJSONMap.put("preKey", "prK");
    simplifiedJSONMap.put("ciphertextMessage", "c");
    simplifiedJSONMap.put("ciphertextType", "cT");
    simplifiedJSONMap.put("timestamp", "t");
    simplifiedJSONMap.put("signalProtocolAddressName", "a");
    return simplifiedJSONMap;
  }

  public static String deSimplifyJsonKeys(final String simplifiedJSON) {
    return simplifiedJSON
        .replaceAll("\"pR\"", "\"" + getMapKeyFromValue("pR") + "\"")
        .replaceAll("\"i\"", "\"" + getMapKeyFromValue("i") + "\"")
        .replaceAll("\"pK\"", "\"" + getMapKeyFromValue("pK") + "\"")
        .replaceAll("\"d\"", "\"" + getMapKeyFromValue("d") + "\"")
        .replaceAll("\"dI\"", "\"" + getMapKeyFromValue("dI") + "\"")
        .replaceAll("\"iK\"", "\"" + getMapKeyFromValue("iK") + "\"")
        .replaceAll("\"rI\"", "\"" + getMapKeyFromValue("rI") + "\"")
        .replaceAll("\"k\"", "\"" + getMapKeyFromValue("k") + "\"")
        .replaceAll("\"s\"", "\"" + getMapKeyFromValue("s") + "\"")
        .replaceAll("\"sK\"", "\"" + getMapKeyFromValue("sK") + "\"")
        .replaceAll("\"c\"", "\"" + getMapKeyFromValue("c") + "\"")
        .replaceAll("\"cT\"", "\"" + getMapKeyFromValue("cT") + "\"")
        .replaceAll("\"t\"", "\"" + getMapKeyFromValue("t") + "\"")
        .replaceAll("\"prK\"", "\"" + getMapKeyFromValue("prK") + "\"")
        .replaceAll("\"a\"", "\"" + getMapKeyFromValue("a") + "\"");
  }

  private static String getMapKeyFromValue(String value) {
    String key = null;
    for (Map.Entry<String, String> entry : simplifiedJSONMap.entrySet()) {
      if (Objects.equals(value, entry.getValue())) {
        key = entry.getKey();
      }
    }
    return key;
  }
}
