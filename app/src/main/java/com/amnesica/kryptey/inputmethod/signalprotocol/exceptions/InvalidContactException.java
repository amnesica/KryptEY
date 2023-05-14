package com.amnesica.kryptey.inputmethod.signalprotocol.exceptions;

import java.io.IOException;

/**
 * Indicates that a contact is invalid. Some information for generating contact is missing.
 */
public class InvalidContactException extends IOException {

  public InvalidContactException(String message) {
    super(message);
  }

  public InvalidContactException(String message, IOException e) {
    super(message, e);
  }
}