package com.amnesica.kryptey.inputmethod.signalprotocol.exceptions;

import java.io.IOException;

/**
 * Indicates that a preKeyBundle is invalid.
 */
public class InvalidPreKeyBundleException extends IOException {

  public InvalidPreKeyBundleException(String message) {
    super(message);
  }

  public InvalidPreKeyBundleException(String message, IOException e) {
    super(message, e);
  }
}