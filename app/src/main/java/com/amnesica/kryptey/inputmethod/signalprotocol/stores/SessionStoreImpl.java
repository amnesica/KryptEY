package com.amnesica.kryptey.inputmethod.signalprotocol.stores;

import com.amnesica.kryptey.inputmethod.signalprotocol.Session;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.InvalidMessageException;
import org.signal.libsignal.protocol.NoSessionException;
import org.signal.libsignal.protocol.SignalProtocolAddress;
import org.signal.libsignal.protocol.state.SessionRecord;
import org.signal.libsignal.protocol.state.SessionStore;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SessionStoreImpl implements SessionStore {

  @JsonProperty
  private List<Session> sessions = new ArrayList<>();

  public SessionStoreImpl() {
  }

  @Override
  public synchronized SessionRecord loadSession(SignalProtocolAddress remoteAddress) {
    try {
      if (containsSession(remoteAddress)) {
        return new SessionRecord(sessions.stream()
            .filter(s -> s.getSignalProtocolAddress().equals(remoteAddress))
            .findFirst()
            .get().getSerializedSessionRecord());
      } else {
        return new SessionRecord();
      }
    } catch (InvalidMessageException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public synchronized List<SessionRecord> loadExistingSessions(List<SignalProtocolAddress> addresses) throws NoSessionException {
    List<SessionRecord> resultSessions = new LinkedList<>();
    for (SignalProtocolAddress remoteAddress : addresses) {
      byte[] serialized = sessions.stream()
          .filter(s -> s.getSignalProtocolAddress().equals(remoteAddress))
          .findFirst()
          .get().getSerializedSessionRecord();
      if (serialized == null) {
        throw new NoSessionException("no session for " + remoteAddress);
      }
      try {
        resultSessions.add(new SessionRecord(serialized));
      } catch (InvalidMessageException e) {
        throw new AssertionError(e);
      }
    }
    return resultSessions;
  }

  @Override
  public synchronized List<Integer> getSubDeviceSessions(String name) {
    List<Integer> deviceIds = new LinkedList<>();

    for (Session session : sessions) {
      SignalProtocolAddress address = session.getSignalProtocolAddress();
      if (address.getName().equals(name) &&
          address.getDeviceId() != 1) {
        deviceIds.add(address.getDeviceId());
      }
    }

    return deviceIds;
  }

  @Override
  public synchronized void storeSession(SignalProtocolAddress address, SessionRecord record) {
    deleteSession(address); // before changed implementation from map to list, items got overriden!
    sessions.add(new Session(address, record.serialize()));
  }

  @Override
  public synchronized boolean containsSession(SignalProtocolAddress address) {
    for (Session session : sessions) {
      if (session.getSignalProtocolAddress().getName().equals(address.getName()) &&
          session.getSignalProtocolAddress().getDeviceId() == address.getDeviceId()) return true;
    }
    return false;
  }

  @Override
  public synchronized void deleteSession(SignalProtocolAddress address) {
    List<Session> alteredSessionList = new ArrayList<>(sessions);
    for (Session session : sessions) {
      if (session.getSignalProtocolAddress().getName().equals(address.getName()) &&
          session.getSignalProtocolAddress().getDeviceId() == address.getDeviceId()) {
        alteredSessionList.remove(session);
      }
    }
    sessions = alteredSessionList;
  }

  @Override
  public synchronized void deleteAllSessions(String name) {
    List<Session> alteredSessionList = new ArrayList<>(sessions);
    for (Session session : sessions) {
      if (session.getSignalProtocolAddress().getName().equals(name)) {
        alteredSessionList.remove(session);
      }
    }
    sessions = alteredSessionList;
  }

  public int getSize() {
    return sessions.size();
  }

  public synchronized IdentityKey getPublicKeyFromSession(SignalProtocolAddress remoteAddress) {
    try {
      if (containsSession(remoteAddress)) {
        SessionRecord record = new SessionRecord(sessions.stream()
            .filter(s -> s.getSignalProtocolAddress().equals(remoteAddress))
            .findFirst()
            .get().getSerializedSessionRecord());

        return new IdentityKey(record.getRemoteIdentityKey().getPublicKey());
      } else {
        return null;
      }
    } catch (InvalidMessageException e) {
      throw new AssertionError(e);
    }
  }
}
