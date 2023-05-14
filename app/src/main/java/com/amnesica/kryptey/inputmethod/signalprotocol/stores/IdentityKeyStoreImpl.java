package com.amnesica.kryptey.inputmethod.signalprotocol.stores;

import com.amnesica.kryptey.inputmethod.signalprotocol.TrustedKey;
import com.amnesica.kryptey.inputmethod.signalprotocol.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.SignalProtocolAddress;
import org.signal.libsignal.protocol.state.IdentityKeyStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IdentityKeyStoreImpl implements IdentityKeyStore {

  @JsonProperty
  private List<TrustedKey> trustedKeys = new ArrayList<>();

  @JsonProperty
  @JsonSerialize(using = JsonUtil.IdentityKeyPairSerializer.class)
  @JsonDeserialize(using = JsonUtil.IdentityKeyPairDeserializer.class)
  private IdentityKeyPair identityKeyPair;

  @JsonProperty
  private int localRegistrationId;

  public IdentityKeyStoreImpl(IdentityKeyPair identityKeyPair, int localRegistrationId) {
    this.identityKeyPair = identityKeyPair;
    this.localRegistrationId = localRegistrationId;
  }

  public IdentityKeyStoreImpl() {
  }

  @Override
  public IdentityKeyPair getIdentityKeyPair() {
    return identityKeyPair;
  }

  @Override
  public int getLocalRegistrationId() {
    return localRegistrationId;
  }

  @Override
  public boolean saveIdentity(SignalProtocolAddress address, IdentityKey identityKey) {
    IdentityKey existing = getIdentityKeyFromEntryInList(address);

    if (!identityKey.equals(existing)) {
      trustedKeys.add(new TrustedKey(address, identityKey));
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean isTrustedIdentity(SignalProtocolAddress address, IdentityKey identityKey, Direction direction) {
    IdentityKey trusted = getIdentityKeyFromEntryInList(address);
    return (trusted == null || trusted.equals(identityKey));
  }

  @Override
  public IdentityKey getIdentity(SignalProtocolAddress address) {
    return getIdentityKeyFromEntryInList(address);
  }

  private IdentityKey getIdentityKeyFromEntryInList(SignalProtocolAddress address) {
    for (TrustedKey trustedKey : trustedKeys) {
      if (trustedKey != null && trustedKey.getSignalProtocolAddress().equals(address))
        return trustedKey.getIdentityKey();
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IdentityKeyStoreImpl that = (IdentityKeyStoreImpl) o;
    return localRegistrationId == that.localRegistrationId && Objects.equals(trustedKeys, that.trustedKeys) && Objects.equals(identityKeyPair, that.identityKeyPair);
  }

  @Override
  public int hashCode() {
    return Objects.hash(trustedKeys, identityKeyPair, localRegistrationId);
  }

  public List<TrustedKey> getTrustedKeys() {
    return trustedKeys;
  }
}
