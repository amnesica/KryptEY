package com.amnesica.kryptey.inputmethod.signalprotocol.encoding;

import android.content.Context;
import android.util.Log;

import com.amnesica.kryptey.inputmethod.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FairyTaleEncoder {

  static final String TAG = FairyTaleEncoder.class.getSimpleName();

  public static final Map<Integer, String> mSentencesMap = new HashMap<>();

  private static void init(final Context context) {
    extractSentencesAndPutInMap(mSentencesMap, context.getResources().getString(R.string.e2ee_rapunzel));
    extractSentencesAndPutInMap(mSentencesMap, context.getResources().getString(R.string.e2ee_cinderella));
  }

  // for test only
  public static void initForTest(final String rapunzel, final String cinderella) {
    extractSentencesAndPutInMap(mSentencesMap, rapunzel);
    extractSentencesAndPutInMap(mSentencesMap, cinderella);
  }

  private static void extractSentencesAndPutInMap(final Map<Integer, String> sentencesMap, String text) {
    final String regex = "([^.]\\w*[,\\s]*[^.]*)";
    final Pattern pattern = Pattern.compile(regex);
    final Matcher matcher = pattern.matcher(text);

    int i = 0;
    while (matcher.find()) {
      final String sentence = text.substring(matcher.start(), matcher.end())
          .replaceAll("\n", " ")
          .replaceAll("\\s+", " ")
          .trim();
      sentencesMap.put(i, sentence);
      i++;
    }
  }

  public static String encode(final String message, final Context context) throws IOException {
    if (message == null) return null;

    // hint: for testing use initForTest method before calling this method
    if (mSentencesMap.size() == 0 && context != null) init(context);

    Log.d(TAG, "message: " + message);
    Log.d(TAG, "length message (bytes): " + message.getBytes().length);

    final String minifiedJson = EncodeHelper.minifyJSON(message);
    Log.d(TAG, "minifiedJson message: " + minifiedJson);

    final byte[] compressedMessage = EncodeHelper.compressString(minifiedJson);
    final String decoySentence = mSentencesMap.get(new Random().nextInt(mSentencesMap.size()));
    final String binaryMessage = EncodeHelper.convertByteArrayToBinary(compressedMessage);

    Log.d(TAG, "binary message: " + binaryMessage);
    Log.d(TAG, "binary message (bytes): " + binaryMessage.getBytes().length);

    final String invisibleMessage = EncodeHelper.convertBinaryToInvisibleString(binaryMessage);
    Log.d(TAG, "length invisible message: " + invisibleMessage.length());

    return decoySentence + invisibleMessage;
  }

  public static String decode(final String encodedText) throws IOException {
    if (encodedText == null) return null;
    final String binary = EncodeHelper.convertInvisibleStringToBinary(encodedText);
    Log.d(TAG, "binary message: " + binary);
    Log.d(TAG, "length invisible message: " + binary.length());
    final byte[] compressedResult = EncodeHelper.convertBinaryToByteArray(binary);
    final String decompressedResult = EncodeHelper.decompressString(compressedResult);
    return EncodeHelper.deSimplifyJsonKeys(decompressedResult);
  }
}
