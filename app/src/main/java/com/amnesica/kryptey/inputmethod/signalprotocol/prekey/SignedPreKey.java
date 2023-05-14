package com.amnesica.kryptey.inputmethod.signalprotocol.prekey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignedPreKey extends PreKey {

  @JsonProperty
  private String signature;

  public SignedPreKey() {
  }

  public SignedPreKey(long keyId, String publicKey, String signature) {
    super(keyId, publicKey);
    this.signature = signature;
  }

  public String getSignature() {
    return signature;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !(object instanceof SignedPreKey)) return false;
    SignedPreKey that = (SignedPreKey) object;

    if (signature == null) {
      return super.equals(object) && that.signature == null;
    } else {
      return super.equals(object) && this.signature.equals(that.signature);
    }
  }

  @Override
  public int hashCode() {
    if (signature == null) {
      return super.hashCode();
    } else {
      return super.hashCode() ^ signature.hashCode();
    }
  }
}

