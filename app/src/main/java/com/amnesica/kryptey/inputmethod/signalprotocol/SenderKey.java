package com.amnesica.kryptey.inputmethod.signalprotocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SenderKey {
  String signalProtocolAddressName;
  int deviceId;
  String distributionId;

  @JsonCreator
  public SenderKey(@JsonProperty("signalProtocolAddress") String signalProtocolAddressName,
                   @JsonProperty("deviceId") int deviceId,
                   @JsonProperty("distributionId") String distributionId) {
    this.signalProtocolAddressName = signalProtocolAddressName;
    this.deviceId = deviceId;
    this.distributionId = distributionId;
  }

  public SenderKey() {
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

  public String getDistributionId() {
    return distributionId;
  }

  public void setDistributionId(String distributionId) {
    this.distributionId = distributionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SenderKey senderKey = (SenderKey) o;
    return deviceId == senderKey.deviceId && Objects.equals(signalProtocolAddressName, senderKey.signalProtocolAddressName) && Objects.equals(distributionId, senderKey.distributionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(signalProtocolAddressName, deviceId, distributionId);
  }

  @Override
  public String toString() {
    return signalProtocolAddressName + "." + deviceId + "." + distributionId;
  }
}
