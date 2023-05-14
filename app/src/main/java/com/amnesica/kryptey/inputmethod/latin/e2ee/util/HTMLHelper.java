package com.amnesica.kryptey.inputmethod.latin.e2ee.util;

public class HTMLHelper {
  public static String replaceHtmlCharacters(String item) {
    return replaceHtmlNBSPCharacters(item);
  }

  private static String replaceHtmlNBSPCharacters(String item) {
    return item.replaceAll("\u00a0", " ").trim();
  }
}
