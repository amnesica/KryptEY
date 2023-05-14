package com.amnesica.kryptey.inputmethod.signalprotocol;

import com.amnesica.kryptey.inputmethod.signalprotocol.stores.PreKeyMetadataStoreImpl;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.SignalProtocolStoreImpl;

import org.signal.libsignal.protocol.SignalProtocolAddress;

import java.util.ArrayList;

public enum ProtocolIdentifier {
  UNIQUE_USER_ID(String.class),
  METADATA_STORE(PreKeyMetadataStoreImpl.class),
  PROTOCOL_STORE(SignalProtocolStoreImpl.class),
  PROTOCOL_ADDRESS(SignalProtocolAddress.class),
  DEVICE_ID(Integer.class),
  UNENCRYPTED_MESSAGES(ArrayList.class),
  CONTACTS(ArrayList.class);

  public final Class className;

  ProtocolIdentifier(Class className) {
    this.className = className;
  }
}
