package com.amnesica.kryptey.inputmethod.signalprotocol;


import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.PreKeyResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class MessageEnvelope implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty
  PreKeyResponse preKeyResponse;

  @JsonProperty
  byte[] ciphertextMessage;

  @JsonProperty
  int ciphertextType;

  @JsonProperty
  long timestamp;

  @JsonProperty
  String signalProtocolAddressName;

  @JsonProperty
  int deviceId;

  public MessageEnvelope(final byte[] ciphertextMessage, final int ciphertextType, final String signalProtocolAddressName, final int deviceId) {
    this.ciphertextMessage = ciphertextMessage;
    this.ciphertextType = ciphertextType;
    this.signalProtocolAddressName = signalProtocolAddressName;
    this.deviceId = deviceId;
    this.timestamp = System.currentTimeMillis();
  }

  public MessageEnvelope(final PreKeyResponse preKeyResponse, final String signalProtocolAddressName, final int deviceId) {
    this.preKeyResponse = preKeyResponse;
    this.signalProtocolAddressName = signalProtocolAddressName;
    this.deviceId = deviceId;
    this.timestamp = System.currentTimeMillis();
  }

  public MessageEnvelope() {
    // default constructor for serialization
  }

  public PreKeyResponse getPreKeyResponse() {
    return preKeyResponse;
  }

  public void setPreKeyResponse(PreKeyResponse preKeyResponse) {
    this.preKeyResponse = preKeyResponse;
  }

  public byte[] getCiphertextMessage() {
    return ciphertextMessage;
  }

  public void setCiphertextMessage(byte[] ciphertextMessage) {
    this.ciphertextMessage = ciphertextMessage;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getSignalProtocolAddressName() {
    return signalProtocolAddressName;
  }

  public void setSignalProtocolAddressName(String signalProtocolAddressName) {
    this.signalProtocolAddressName = signalProtocolAddressName;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  public int getCiphertextType() {
    return ciphertextType;
  }

  public void setCiphertextType(int ciphertextType) {
    this.ciphertextType = ciphertextType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MessageEnvelope that = (MessageEnvelope) o;
    return ciphertextType == that.ciphertextType && timestamp == that.timestamp && deviceId == that.deviceId && Objects.equals(preKeyResponse, that.preKeyResponse) && Arrays.equals(ciphertextMessage, that.ciphertextMessage) && Objects.equals(signalProtocolAddressName, that.signalProtocolAddressName);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(preKeyResponse, ciphertextType, timestamp, signalProtocolAddressName, deviceId);
    result = 31 * result + Arrays.hashCode(ciphertextMessage);
    return result;
  }

  @Override
  public String toString() {
    return "MessageEnvelope{" +
        "preKeyResponse=" + preKeyResponse +
        ", ciphertextMessage=" + Arrays.toString(ciphertextMessage) +
        ", ciphertextType=" + ciphertextType +
        ", timestamp=" + timestamp +
        ", signalProtocolAddressName='" + signalProtocolAddressName + '\'' +
        ", deviceId=" + deviceId +
        '}';
  }
}
