package com.amnesica.kryptey.inputmethod.signalprotocol.exceptions;

import java.io.IOException;

/**
 * Indicates that a contact already exists in contact list.
 */
public class DuplicateContactException extends IOException {

  public DuplicateContactException(String message) {
    super(message);
  }

  public DuplicateContactException(String message, IOException e) {
    super(message, e);
  }
}