package com.amnesica.kryptey.inputmethod.signalprotocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.SignalProtocolAddress;

import java.util.Objects;

public class TrustedKey {
  SignalProtocolAddress signalProtocolAddress;
  IdentityKey identityKey;

  @JsonCreator
  public TrustedKey(@JsonProperty("signalProtocolAddress") SignalProtocolAddress signalProtocolAddress, @JsonProperty("identityKey") IdentityKey identityKey) {
    this.signalProtocolAddress = signalProtocolAddress;
    this.identityKey = identityKey;
  }

  public TrustedKey() {
  }

  public SignalProtocolAddress getSignalProtocolAddress() {
    return signalProtocolAddress;
  }

  public void setSignalProtocolAddress(SignalProtocolAddress signalProtocolAddress) {
    this.signalProtocolAddress = signalProtocolAddress;
  }

  public IdentityKey getIdentityKey() {
    return identityKey;
  }

  public void setIdentityKey(IdentityKey identityKey) {
    this.identityKey = identityKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TrustedKey that = (TrustedKey) o;
    return Objects.equals(signalProtocolAddress, that.signalProtocolAddress) && Objects.equals(identityKey, that.identityKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(signalProtocolAddress, identityKey);
  }

  @Override
  public String toString() {
    return "TrustedKey{" +
        "signalProtocolAddress=" + signalProtocolAddress +
        ", identityKey=" + identityKey +
        '}';
  }
}
