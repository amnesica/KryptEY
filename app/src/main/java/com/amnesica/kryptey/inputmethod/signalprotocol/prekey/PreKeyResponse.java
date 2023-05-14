package com.amnesica.kryptey.inputmethod.signalprotocol.prekey;

import com.amnesica.kryptey.inputmethod.signalprotocol.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.signal.libsignal.protocol.IdentityKey;

import java.util.List;
import java.util.Objects;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class PreKeyResponse {

  @JsonProperty
  @JsonSerialize(using = JsonUtil.IdentityKeySerializer.class)
  @JsonDeserialize(using = JsonUtil.IdentityKeyDeserializer.class)
  public IdentityKey identityKey;

  @JsonProperty
  private List<PreKeyResponseItem> devices;

  public PreKeyResponse() {
  }

  public PreKeyResponse(IdentityKey identityKey, List<PreKeyResponseItem> devices) {
    this.identityKey = identityKey;
    this.devices = devices;
  }

  public IdentityKey getIdentityKey() {
    return identityKey;
  }

  public List<PreKeyResponseItem> getDevices() {
    return devices;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PreKeyResponse that = (PreKeyResponse) o;
    return Objects.equals(identityKey, that.identityKey) && Objects.equals(devices, that.devices);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identityKey, devices);
  }
}
