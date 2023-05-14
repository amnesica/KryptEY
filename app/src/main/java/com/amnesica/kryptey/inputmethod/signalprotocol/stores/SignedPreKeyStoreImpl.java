package com.amnesica.kryptey.inputmethod.signalprotocol.stores;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.signal.libsignal.protocol.InvalidKeyIdException;
import org.signal.libsignal.protocol.InvalidMessageException;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;
import org.signal.libsignal.protocol.state.SignedPreKeyStore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SignedPreKeyStoreImpl implements SignedPreKeyStore {
  static final String TAG = SignedPreKeyStoreImpl.class.getSimpleName();

  @JsonProperty
  private final Map<Integer, byte[]> store = new HashMap<>();

  public SignedPreKeyStoreImpl() {
  }

  @Override
  public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) throws InvalidKeyIdException {
    Log.d(TAG, "Loading SignedPreKeyRecord with id: " + signedPreKeyId);
    try {
      if (!store.containsKey(signedPreKeyId)) {
        throw new InvalidKeyIdException("No such signed prekeyrecord! " + signedPreKeyId);
      }

      return new SignedPreKeyRecord(store.get(signedPreKeyId));
    } catch (InvalidMessageException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public List<SignedPreKeyRecord> loadSignedPreKeys() {
    Log.d(TAG, "Loading all SignedPreKeyRecords");
    try {
      List<SignedPreKeyRecord> results = new LinkedList<>();

      for (byte[] serialized : store.values()) {
        results.add(new SignedPreKeyRecord(serialized));
      }

      return results;
    } catch (InvalidMessageException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
    Log.d(TAG, "Storing SignedPreKeyRecord with id: " + signedPreKeyId);
    store.put(signedPreKeyId, record.serialize());
  }

  @Override
  public boolean containsSignedPreKey(int signedPreKeyId) {
    return store.containsKey(signedPreKeyId);
  }

  @Override
  public void removeSignedPreKey(int signedPreKeyId) {
    Log.d(TAG, "Removing SignedPreKeyRecord with id: " + signedPreKeyId);
    store.remove(signedPreKeyId);
  }

  public void removeOldSignedPreKeys(int activeSignedPreKeyId) {
    Log.d(TAG, "Removing old SignedPreKeyRecord smaller than active signed pre key id: " + activeSignedPreKeyId);
    for (int i = 0; i < activeSignedPreKeyId; i++) {
      if (containsSignedPreKey(i)) {
        removeSignedPreKey(i);
      }
    }
  }
}

