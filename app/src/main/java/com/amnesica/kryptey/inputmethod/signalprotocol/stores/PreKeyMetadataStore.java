package com.amnesica.kryptey.inputmethod.signalprotocol.stores;

/**
 * Allows storing various metadata around prekey state.
 */
public abstract class PreKeyMetadataStore {

  int nextSignedPreKeyId = 0;
  int activeSignedPreKeyId = 0;
  boolean isSignedPreKeyRegistered = false;
  int signedPreKeyFailureCount = 0;
  int nextOneTimePreKeyId = 0;

  long nextSignedPreKeyRefreshTime = 0L;
  long oldSignedPreKeyDeletionTime = 0L; // lastSignedPreKeyRotationTime + 48h

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

  // get bound
  public int getNextOneTimePreKeyId() {
    return nextOneTimePreKeyId;
  }

  // set bound
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
