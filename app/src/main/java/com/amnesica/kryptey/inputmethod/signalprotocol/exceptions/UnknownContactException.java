package com.amnesica.kryptey.inputmethod.signalprotocol.exceptions;

import java.io.IOException;

/**
 * Indicates that there are no unencrypted messages for this contact.
 */
public class UnknownContactException extends IOException {

  public UnknownContactException(String message) {
    super(message);
  }

  public UnknownContactException(String message, IOException e) {
    super(message, e);
  }
}