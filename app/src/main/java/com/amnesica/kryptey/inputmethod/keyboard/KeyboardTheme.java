/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amnesica.kryptey.inputmethod.keyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amnesica.kryptey.inputmethod.R;
import com.amnesica.kryptey.inputmethod.compat.PreferenceManagerCompat;
import com.amnesica.kryptey.inputmethod.latin.settings.Settings;

public final class KeyboardTheme {
  private static final String TAG = KeyboardTheme.class.getSimpleName();

  static final String KEYBOARD_THEME_KEY = "pref_keyboard_theme_20140509";

  // These should be aligned with Keyboard.themeId and Keyboard.Case.keyboardTheme
  //
  public static final int THEME_ID_PURE_DAY = 6;
  public static final int THEME_ID_PURE_NIGHT = 7;
  public static final int DEFAULT_THEME_ID = THEME_ID_PURE_NIGHT;

  /* package private for testing */
  static final KeyboardTheme[] KEYBOARD_THEMES = {
      new KeyboardTheme(THEME_ID_PURE_DAY, "LXXPureDay", R.style.KeyboardTheme_LXX_Pure_Day),
      new KeyboardTheme(THEME_ID_PURE_NIGHT, "LXXPureNight", R.style.KeyboardTheme_LXX_Pure_Night),
  };

  public final int mThemeId;
  public final int mStyleId;
  public final String mThemeName;

  // Note: The themeId should be aligned with "themeId" attribute of Keyboard style
  // in values/themes-<style>.xml.
  private KeyboardTheme(final int themeId, final String themeName, final int styleId) {
    mThemeId = themeId;
    mThemeName = themeName;
    mStyleId = styleId;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    return (o instanceof KeyboardTheme) && ((KeyboardTheme) o).mThemeId == mThemeId;
  }

  @Override
  public int hashCode() {
    return mThemeId;
  }

  /* package private for testing */
  static KeyboardTheme searchKeyboardThemeById(final int themeId) {
    // TODO: This search algorithm isn't optimal if there are many themes.
    for (final KeyboardTheme theme : KEYBOARD_THEMES) {
      if (theme.mThemeId == themeId) {
        return theme;
      }
    }
    return null;
  }

  /* package private for testing */
  static KeyboardTheme getDefaultKeyboardTheme() {
    return searchKeyboardThemeById(DEFAULT_THEME_ID);
  }

  public static String getKeyboardThemeName(final int themeId) {
    final KeyboardTheme theme = searchKeyboardThemeById(themeId);
    Log.i("Getting theme ID", Integer.toString(themeId));
    return theme.mThemeName;
  }

  public static void saveKeyboardThemeId(final int themeId, final SharedPreferences prefs) {
    prefs.edit().putString(KEYBOARD_THEME_KEY, Integer.toString(themeId)).apply();
  }

  public static KeyboardTheme getKeyboardTheme(final Context context) {
    final SharedPreferences prefs = PreferenceManagerCompat.getDeviceSharedPreferences(context);
    return getKeyboardTheme(prefs);
  }

  public static KeyboardTheme getKeyboardTheme(final SharedPreferences prefs) {
    final String themeIdString = prefs.getString(KEYBOARD_THEME_KEY, null);
    if (themeIdString == null) {
      return searchKeyboardThemeById(THEME_ID_PURE_NIGHT);
    }
    try {
      final int themeId = Integer.parseInt(themeIdString);
      final KeyboardTheme theme = searchKeyboardThemeById(themeId);
      if (theme != null) {
        return theme;
      }
      Log.w(TAG, "Unknown keyboard theme in preference: " + themeIdString);
    } catch (final NumberFormatException e) {
      Log.w(TAG, "Illegal keyboard theme in preference: " + themeIdString, e);
    }
    // Remove preference that contains unknown or illegal theme id.
    prefs.edit().remove(KEYBOARD_THEME_KEY).remove(Settings.PREF_KEYBOARD_COLOR).apply();
    return getDefaultKeyboardTheme();
  }
}
