package com.amnesica.kryptey.inputmethod.signalprotocol.stores;

public class PreKeyMetadataStoreImpl extends PreKeyMetadataStore {

  public int getNextSignedPreKeyId() {
    return nextSignedPreKeyId;
  }

  public void setNextSignedPreKeyId(int nextSignedPreKeyId) {
    this.nextSignedPreKeyId = nextSignedPreKeyId;
  }

  public int getActiveSignedPreKeyId() {
    return activeSignedPreKeyId;
  }

  public void setActiveSignedPreKeyId(int activeSignedPreKeyId) {
    this.activeSignedPreKeyId = activeSignedPreKeyId;
  }

  public boolean isSignedPreKeyRegistered() {
    return isSignedPreKeyRegistered;
  }

  public void setSignedPreKeyRegistered(boolean signedPreKeyRegistered) {
    isSignedPreKeyRegistered = signedPreKeyRegistered;
  }

  public int getSignedPreKeyFailureCount() {
    return signedPreKeyFailureCount;
  }

  public void setSignedPreKeyFailureCount(int signedPreKeyFailureCount) {
    this.signedPreKeyFailureCount = signedPreKeyFailureCount;
  }

  public int getNextOneTimePreKeyId() {
    return nextOneTimePreKeyId;
  }

  public void setNextOneTimePreKeyId(int nextOneTimePreKeyId) {
    this.nextOneTimePreKeyId = nextOneTimePreKeyId;
  }

  public long getNextSignedPreKeyRefreshTime() {
    return nextSignedPreKeyRefreshTime;
  }

  public void setNextSignedPreKeyRefreshTime(long nextSignedPreKeyRefreshTime) {
    this.nextSignedPreKeyRefreshTime = nextSignedPreKeyRefreshTime;
  }

  public long getOldSignedPreKeyDeletionTime() {
    return oldSignedPreKeyDeletionTime;
  }

  public void setOldSignedPreKeyDeletionTime(long oldSignedPreKeyDeletionTime) {
    this.oldSignedPreKeyDeletionTime = oldSignedPreKeyDeletionTime;
  }
}

