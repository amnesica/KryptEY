package com.amnesica.kryptey.inputmethod.signalprotocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.stores.SessionStoreImpl;

import org.junit.Test;
import org.signal.libsignal.protocol.NoSessionException;
import org.signal.libsignal.protocol.SignalProtocolAddress;
import org.signal.libsignal.protocol.state.SessionRecord;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SessionStoreImplTest {

  static final String TAG = SessionStoreImplTest.class.getSimpleName();

  @Test
  public void loadSessionTest() {
    Log.i(TAG, "------------ loadSessionTest: ------------");
    SessionStoreImpl sessionStore = new SessionStoreImpl();

    SessionRecord sessionRecord = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));

    sessionStore.storeSession(signalProtocolAddress, sessionRecord);
    assertEquals(1, sessionStore.getSize());

    SessionRecord loadedSessionRecord = sessionStore.loadSession(signalProtocolAddress);
    assertEquals(sessionRecord.getSessionVersion(), loadedSessionRecord.getSessionVersion());
  }

  @Test
  public void loadExistingSessionsTest() throws NoSessionException {
    Log.i(TAG, "------------ loadExistingSessionsTest: ------------");
    SessionStoreImpl sessionStore = new SessionStoreImpl();

    SessionRecord sessionRecord = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));
    SessionRecord sessionRecord2 = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress2 = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));

    sessionStore.storeSession(signalProtocolAddress, sessionRecord);
    sessionStore.storeSession(signalProtocolAddress2, sessionRecord2);
    assertEquals(2, sessionStore.getSize());

    List<SessionRecord> loadedSessionRecord = sessionStore.loadExistingSessions(Collections.singletonList(signalProtocolAddress));
    assertEquals(1, loadedSessionRecord.size());
  }

  @Test
  public void getSubDeviceSessionsTest() {
    Log.i(TAG, "------------ getSubDeviceSessionsTest: ------------");
    SessionStoreImpl sessionStore = new SessionStoreImpl();

    SessionRecord sessionRecord = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));
    SessionRecord sessionRecord2 = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress2 = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));

    sessionStore.storeSession(signalProtocolAddress, sessionRecord);
    sessionStore.storeSession(signalProtocolAddress2, sessionRecord2);
    assertEquals(2, sessionStore.getSize());

    List<Integer> deviceIds = sessionStore.getSubDeviceSessions(signalProtocolAddress.getName());
    assertEquals(1, deviceIds.size());
  }

  @Test
  public void storeSessionTest() {
    Log.i(TAG, "------------ storeSessionTest: ------------");
    SessionStoreImpl sessionStore = new SessionStoreImpl();

    SessionRecord sessionRecord = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));
    SessionRecord sessionRecord2 = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress2 = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));

    sessionStore.storeSession(signalProtocolAddress, sessionRecord);
    sessionStore.storeSession(signalProtocolAddress2, sessionRecord2);

    // store should delete old one and store new one instead (like map behaviour)
    SessionRecord sessionRecordUsed = sessionStore.loadSession(signalProtocolAddress);
    sessionStore.storeSession(signalProtocolAddress, sessionRecordUsed);

    assertEquals(2, sessionStore.getSize());
  }

  @Test
  public void containsSessionTest() {
    Log.i(TAG, "------------ containsSessionTest: ------------");
    SessionStoreImpl sessionStore = new SessionStoreImpl();

    SessionRecord sessionRecord = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));
    SessionRecord sessionRecord2 = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress2 = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));

    sessionStore.storeSession(signalProtocolAddress, sessionRecord);
    sessionStore.storeSession(signalProtocolAddress2, sessionRecord2);

    boolean sessionOneLoaded = sessionStore.containsSession(signalProtocolAddress);
    boolean sessionTwoLoaded = sessionStore.containsSession(signalProtocolAddress2);
    assertTrue(sessionOneLoaded);
    assertTrue(sessionTwoLoaded);
  }

  @Test
  public void deleteSessionTest() {
    Log.i(TAG, "------------ deleteSessionTest: ------------");
    SessionStoreImpl sessionStore = new SessionStoreImpl();

    SessionRecord sessionRecord = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));
    SessionRecord sessionRecord2 = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress2 = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));

    sessionStore.storeSession(signalProtocolAddress, sessionRecord);
    sessionStore.storeSession(signalProtocolAddress2, sessionRecord2);

    assertTrue(sessionStore.containsSession(signalProtocolAddress));
    sessionStore.deleteSession(signalProtocolAddress);
    assertFalse(sessionStore.containsSession(signalProtocolAddress));
  }

  @Test
  public void deleteAllSessionsTest() {
    Log.i(TAG, "------------ deleteAllSessionsTest: ------------");
    SessionStoreImpl sessionStore = new SessionStoreImpl();

    SessionRecord sessionRecord = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));
    SessionRecord sessionRecord2 = new SessionRecord();
    SignalProtocolAddress signalProtocolAddress2 = new SignalProtocolAddress(UUID.randomUUID().toString(), new Random().nextInt(10000));

    sessionStore.storeSession(signalProtocolAddress, sessionRecord);
    sessionStore.storeSession(signalProtocolAddress2, sessionRecord2);

    assertTrue(sessionStore.containsSession(signalProtocolAddress));
    assertTrue(sessionStore.containsSession(signalProtocolAddress2));
    sessionStore.deleteAllSessions(signalProtocolAddress.getName());
    assertFalse(sessionStore.containsSession(signalProtocolAddress));
    assertTrue(sessionStore.containsSession(signalProtocolAddress2));
  }
}
