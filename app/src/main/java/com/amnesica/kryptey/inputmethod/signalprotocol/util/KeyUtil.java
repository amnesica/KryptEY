package com.amnesica.kryptey.inputmethod.signalprotocol.util;

import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.stores.PreKeyMetadataStore;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.SignalProtocolStoreImpl;

import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.InvalidKeyException;
import org.signal.libsignal.protocol.ecc.Curve;
import org.signal.libsignal.protocol.ecc.ECKeyPair;
import org.signal.libsignal.protocol.ecc.ECPrivateKey;
import org.signal.libsignal.protocol.state.PreKeyRecord;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;
import org.signal.libsignal.protocol.util.KeyHelper;
import org.signal.libsignal.protocol.util.Medium;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class KeyUtil {

  static final String TAG = KeyUtil.class.getSimpleName();

  public static final int BATCH_SIZE = 2; // 100 in Signal app
  private static final long SIGNED_PRE_KEY_MAX_DAYS = TimeUnit.DAYS.toMillis(30); // debug: TimeUnit.MINUTES.toMillis(3)
  private static final long SIGNED_PRE_KEY_ARCHIVE_AGE = TimeUnit.DAYS.toMillis(2); // debug: TimeUnit.SECONDS.toMillis(20)

  public static IdentityKeyPair generateIdentityKeyPair() {
    final ECKeyPair identityKeyPairKeys = Curve.generateKeyPair();

    return new IdentityKeyPair(new IdentityKey(identityKeyPairKeys.getPublicKey()),
        identityKeyPairKeys.getPrivateKey());
  }

  public static int generateRegistrationId() {
    return KeyHelper.generateRegistrationId(false);
  }

  public synchronized static List<PreKeyRecord> generateAndStoreOneTimePreKeys(final SignalProtocolStoreImpl protocolStore, final PreKeyMetadataStore metadataStore) {
    Log.d(TAG, "Generating one-time prekeys...");

    List<PreKeyRecord> records = new LinkedList<>();
    int preKeyIdOffset = metadataStore.getNextOneTimePreKeyId();

    for (int i = 0; i < BATCH_SIZE; i++) {
      int preKeyId = (preKeyIdOffset + i) % Medium.MAX_VALUE;
      PreKeyRecord record = generateAndStoreOneTimePreKey(protocolStore, preKeyId);
      records.add(record);
    }

    return records;
  }

  public synchronized static PreKeyRecord generateAndStoreOneTimePreKey(final SignalProtocolStoreImpl protocolStore, final int preKeyId) {
    Log.d(TAG, "Generating one-time prekey with id: " + preKeyId + "...");
    ECKeyPair keyPair = Curve.generateKeyPair();
    PreKeyRecord record = new PreKeyRecord(preKeyId, keyPair);

    protocolStore.storePreKey(preKeyId, record);
    return record;
  }

  public synchronized static SignedPreKeyRecord generateAndStoreSignedPreKey(final SignalProtocolStoreImpl protocolStore, final PreKeyMetadataStore metadataStore) {
    return generateAndStoreSignedPreKey(protocolStore, metadataStore, protocolStore.getIdentityKeyPair().getPrivateKey());
  }

  public synchronized static SignedPreKeyRecord generateAndStoreSignedPreKey(final SignalProtocolStoreImpl protocolStore,
                                                                             final PreKeyMetadataStore metadataStore,
                                                                             final ECPrivateKey privateKey) {
    Log.d(TAG, "Generating signed prekeys...");

    int signedPreKeyId = metadataStore.getNextSignedPreKeyId();
    SignedPreKeyRecord record = generateSignedPreKey(signedPreKeyId, privateKey, metadataStore);

    protocolStore.storeSignedPreKey(signedPreKeyId, record);
    metadataStore.setNextSignedPreKeyId((signedPreKeyId + 1) % Medium.MAX_VALUE);
    metadataStore.setNextSignedPreKeyRefreshTime(System.currentTimeMillis() + SIGNED_PRE_KEY_MAX_DAYS);
    metadataStore.setOldSignedPreKeyDeletionTime(System.currentTimeMillis() + SIGNED_PRE_KEY_ARCHIVE_AGE);

    return record;
  }

  public synchronized static SignedPreKeyRecord generateSignedPreKey(final int signedPreKeyId, final ECPrivateKey privateKey, final PreKeyMetadataStore metadataStore) {
    try {
      ECKeyPair keyPair = Curve.generateKeyPair();
      byte[] signature = Curve.calculateSignature(privateKey, keyPair.getPublicKey().serialize());

      return new SignedPreKeyRecord(signedPreKeyId, System.currentTimeMillis(), keyPair, signature);
    } catch (InvalidKeyException e) {
      throw new AssertionError(e);
    }
  }

  private static void rotateSignedPreKey(SignalProtocolStoreImpl protocolStore, PreKeyMetadataStore metadataStore) {
    SignedPreKeyRecord signedPreKeyRecord = generateAndStoreSignedPreKey(protocolStore, metadataStore);
    metadataStore.setActiveSignedPreKeyId(signedPreKeyRecord.getId());
    metadataStore.setSignedPreKeyRegistered(true);
    metadataStore.setSignedPreKeyFailureCount(0);
  }

  public static Integer getUnusedOneTimePreKeyId(final SignalProtocolStoreImpl protocolStore) {
    if (protocolStore == null || protocolStore.getPreKeyStore() == null) return null;

    final int preKeyId = 1;
    final Boolean preKeyIsUsed = protocolStore.getPreKeyStore().checkPreKeyAvailable(preKeyId);
    if (preKeyIsUsed == null || preKeyIsUsed) {
      Log.d(TAG, "No unused prekey left. Generating new one time prekey with id " + preKeyId);
      generateAndStoreOneTimePreKey(protocolStore, preKeyId);
    } else {
      Log.d(TAG, "Prekey with id " + preKeyId + " is unused");
    }
    return preKeyId;
  }

  public static boolean refreshSignedPreKeyIfNecessary(final SignalProtocolStoreImpl protocolStore, final PreKeyMetadataStore metadataStore) {
    if (protocolStore == null || metadataStore == null) return false;

    if (System.currentTimeMillis() > metadataStore.getNextSignedPreKeyRefreshTime()) {
      Log.d(TAG, "Rotating signed prekey...");
      rotateSignedPreKey(protocolStore, metadataStore);
      return true;
    } else {
      Log.d(TAG, "Rotation of signed prekey not necessary...");
    }
    deleteOlderSignedPreKeysIfNecessary(protocolStore, metadataStore);
    return false;
  }

  private static void deleteOlderSignedPreKeysIfNecessary(final SignalProtocolStoreImpl protocolStore, final PreKeyMetadataStore metadataStore) {
    if (protocolStore == null || metadataStore == null) return;

    if (System.currentTimeMillis() > SIGNED_PRE_KEY_ARCHIVE_AGE) {
      Log.d(TAG, "Deleting old signed prekeys...");
      protocolStore.getSignedPreKeyStore().removeOldSignedPreKeys(metadataStore.getActiveSignedPreKeyId());
    } else {
      Log.d(TAG, "Deletion of old signed prekeys not necessary...");
    }
  }
}
