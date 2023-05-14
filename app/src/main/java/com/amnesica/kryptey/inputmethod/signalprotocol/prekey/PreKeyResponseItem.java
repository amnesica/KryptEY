package com.amnesica.kryptey.inputmethod.signalprotocol.prekey;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class PreKeyResponseItem {

  @JsonProperty
  public int deviceId;

  @JsonProperty
  public int registrationId;

  @JsonProperty
  public SignedPreKeyEntity signedPreKey;

  @JsonProperty
  public PreKeyEntity preKey;

  public PreKeyResponseItem(int deviceId, int registrationId, SignedPreKeyEntity signedPreKey, PreKeyEntity preKey) {
    this.deviceId = deviceId;
    this.registrationId = registrationId;
    this.signedPreKey = signedPreKey;
    this.preKey = preKey;
  }

  public PreKeyResponseItem() {
    // default constructor for serialization
  }

  public int getDeviceId() {
    return deviceId;
  }

  public int getRegistrationId() {
    return registrationId;
  }

  public SignedPreKeyEntity getSignedPreKey() {
    return signedPreKey;
  }

  public PreKeyEntity getPreKey() {
    return preKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PreKeyResponseItem that = (PreKeyResponseItem) o;
    return deviceId == that.deviceId && registrationId == that.registrationId && Objects.equals(signedPreKey, that.signedPreKey) && Objects.equals(preKey, that.preKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(deviceId, registrationId, signedPreKey, preKey);
  }
}
