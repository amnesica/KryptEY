package com.amnesica.kryptey.inputmethod.signalprotocol.exceptions;

import java.io.IOException;

/**
 * Indicates that a message is unknown (is not of type PRE_KEY or WHISPER_TYPE).
 */
public class UnknownMessageException extends IOException {

  public UnknownMessageException(String message) {
    super(message);
  }

  public UnknownMessageException(String message, IOException e) {
    super(message, e);
  }
}