package com.amnesica.kryptey.inputmethod.signalprotocol.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

public class StorageMessage {
  private String contactUUID; // contact name or address name (uuid)
  private String senderUUID;
  private String recipientUUID;
  private final Instant timestamp;
  private final String unencryptedMessage;

  @JsonCreator
  public StorageMessage(@JsonProperty("contactUUID") String contactUUID,
                        @JsonProperty("senderUUID") String senderUUID,
                        @JsonProperty("recipientUUID") String recipientUUID,
                        @JsonProperty("timestamp") Instant timestamp,
                        @JsonProperty("unencryptedMessage") String unencryptedMessage) {
    this.contactUUID = contactUUID;
    this.senderUUID = senderUUID;
    this.recipientUUID = recipientUUID;
    this.timestamp = timestamp;
    this.unencryptedMessage = unencryptedMessage;
  }

  public String getSenderUUID() {
    return senderUUID;
  }

  public void setSenderUUID(String senderUUID) {
    this.senderUUID = senderUUID;
  }

  public String getRecipientUUID() {
    return recipientUUID;
  }

  public void setRecipientUUID(String recipientUUID) {
    this.recipientUUID = recipientUUID;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getUnencryptedMessage() {
    return unencryptedMessage;
  }

  public String getContactUUID() {
    return contactUUID;
  }

  public void setContactUUID(String contactUUID) {
    this.contactUUID = contactUUID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StorageMessage message = (StorageMessage) o;
    return Objects.equals(contactUUID, message.contactUUID) && Objects.equals(senderUUID, message.senderUUID) && Objects.equals(recipientUUID, message.recipientUUID) && Objects.equals(timestamp, message.timestamp) && Objects.equals(unencryptedMessage, message.unencryptedMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contactUUID, senderUUID, recipientUUID, timestamp, unencryptedMessage);
  }

  @Override
  public String toString() {
    return "StorageMessage{" +
        "contactUUID='" + contactUUID + '\'' +
        ", senderUUID='" + senderUUID + '\'' +
        ", recipientUUID='" + recipientUUID + '\'' +
        ", timestamp=" + timestamp +
        ", unencryptedMessage='" + unencryptedMessage + '\'' +
        '}';
  }
}
