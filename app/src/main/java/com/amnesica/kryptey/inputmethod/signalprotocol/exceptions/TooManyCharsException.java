package com.amnesica.kryptey.inputmethod.signalprotocol.exceptions;

import java.io.IOException;

/**
 * Indicates that a message contains too many characters for the given encoding method.
 */
public class TooManyCharsException extends IOException {

  public TooManyCharsException(String message) {
    super(message);
  }

  public TooManyCharsException(String message, IOException e) {
    super(message, e);
  }
}