package com.amnesica.kryptey.inputmethod.signalprotocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.signal.libsignal.protocol.SignalProtocolAddress;

import java.util.Objects;

public class Session {
  private SignalProtocolAddress signalProtocolAddress;
  private byte[] serializedSessionRecord;

  @JsonCreator
  public Session(@JsonProperty("signalProtocolAddress") SignalProtocolAddress signalProtocolAddress,
                 @JsonProperty("serializedSessionRecord") byte[] serializedSessionRecord) {
    this.signalProtocolAddress = signalProtocolAddress;
    this.serializedSessionRecord = serializedSessionRecord;
  }

  public Session() {
  }

  public SignalProtocolAddress getSignalProtocolAddress() {
    return signalProtocolAddress;
  }

  public void setSignalProtocolAddress(SignalProtocolAddress signalProtocolAddress) {
    this.signalProtocolAddress = signalProtocolAddress;
  }

  public byte[] getSerializedSessionRecord() {
    return serializedSessionRecord;
  }

  public void setSerializedSessionRecord(byte[] serializedSessionRecord) {
    this.serializedSessionRecord = serializedSessionRecord;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Session session = (Session) o;
    return Objects.equals(signalProtocolAddress, session.signalProtocolAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(signalProtocolAddress);
  }
}
