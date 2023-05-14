package com.amnesica.kryptey.inputmethod.signalprotocol.prekey;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreKey {

  @JsonProperty
  private long keyId;

  @JsonProperty
  private String publicKey;

  public PreKey() {
  }

  public PreKey(long keyId, String publicKey) {
    this.keyId = keyId;
    this.publicKey = publicKey;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public long getKeyId() {
    return keyId;
  }

  public void setKeyId(long keyId) {
    this.keyId = keyId;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null || !(object instanceof PreKey)) return false;
    PreKey that = (PreKey) object;

    if (publicKey == null) {
      return this.keyId == that.keyId && that.publicKey == null;
    } else {
      return this.keyId == that.keyId && this.publicKey.equals(that.publicKey);
    }
  }

  @Override
  public int hashCode() {
    if (publicKey == null) {
      return (int) this.keyId;
    } else {
      return ((int) this.keyId) ^ publicKey.hashCode();
    }
  }
}
