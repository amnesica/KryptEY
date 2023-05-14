package com.amnesica.kryptey.inputmethod.signalprotocol.stores;

import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.SenderKey;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.signal.libsignal.protocol.InvalidMessageException;
import org.signal.libsignal.protocol.SignalProtocolAddress;
import org.signal.libsignal.protocol.groups.state.SenderKeyRecord;
import org.signal.libsignal.protocol.groups.state.SenderKeyStore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SenderKeyStoreImpl implements SenderKeyStore {
  static final String TAG = SenderKeyStoreImpl.class.getSimpleName();

  @JsonProperty
  private final Map<SenderKey, SenderKeyRecord> store = new HashMap<>(); //private final Map<Pair<SignalProtocolAddress, UUID>, SenderKeyRecord> store = new HashMap<>();

  public SenderKeyStoreImpl() {
  }

  @Override
  public void storeSenderKey(SignalProtocolAddress sender, UUID distributionId, SenderKeyRecord record) {
    Log.d(TAG, "Storing SenderKeyRecord with address: " + sender + " and distributionId: " + distributionId + " and record: " + record);
    store.put(new SenderKey(sender.getName(), sender.getDeviceId(), distributionId.toString()), record);
  }

  @Override
  public SenderKeyRecord loadSenderKey(SignalProtocolAddress sender, UUID distributionId) {
    Log.d(TAG, "Loading SenderKeyRecord with address: " + sender + " and distributionId: " + distributionId);

    try {
      SenderKeyRecord record = store.get(new SenderKey(sender.getName(), sender.getDeviceId(), distributionId.toString()));

      if (record == null) {
        return null;
      } else {
        return new SenderKeyRecord(record.serialize());
      }
    } catch (InvalidMessageException e) {
      throw new AssertionError(e);
    }
  }
}
