package com.amnesica.kryptey.inputmethod.signalprotocol;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Objects;

public class PreKeyWithStatus {

  final byte[] serializedPreKeyRecord;
  boolean isUsed;

  @JsonCreator
  public PreKeyWithStatus(@JsonProperty("serializedPreKeyRecord") byte[] serializedPreKeyRecord,
                          @JsonProperty("isUsed") boolean isUsed) {
    this.serializedPreKeyRecord = serializedPreKeyRecord;
    this.isUsed = isUsed;
  }

  public byte[] getSerializedPreKeyRecord() {
    return serializedPreKeyRecord;
  }

  public boolean isUsed() {
    return isUsed;
  }

  public void setUsed(boolean used) {
    isUsed = used;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PreKeyWithStatus that = (PreKeyWithStatus) o;
    return isUsed == that.isUsed && Arrays.equals(serializedPreKeyRecord, that.serializedPreKeyRecord);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(isUsed);
    result = 31 * result + Arrays.hashCode(serializedPreKeyRecord);
    return result;
  }

  @Override
  public String toString() {
    return "PreKeyWithStatus{" +
        "serializedPreKeyRecord=" + Arrays.toString(serializedPreKeyRecord) +
        ", isUsed=" + isUsed +
        '}';
  }
}
