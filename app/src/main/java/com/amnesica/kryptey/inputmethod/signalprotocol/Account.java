package com.amnesica.kryptey.inputmethod.signalprotocol;

import com.amnesica.kryptey.inputmethod.signalprotocol.chat.Contact;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.StorageMessage;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.DuplicateContactException;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.UnknownContactException;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.PreKeyMetadataStore;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.SignalProtocolStoreImpl;

import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.SignalProtocolAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Account {

  private final String mName;
  private final int mDeviceId;
  private IdentityKeyPair mIdentityKeyPair;
  private PreKeyMetadataStore mMetadataStore;
  private SignalProtocolStoreImpl mSignalProtocolStore;
  private SignalProtocolAddress mSignalProtocolAddress;
  private ArrayList<StorageMessage> mUnencryptedMessages;
  private ArrayList<Contact> contactList;

  public Account(String name, int deviceId, IdentityKeyPair identityKeyPair, PreKeyMetadataStore metadataStore, SignalProtocolStoreImpl signalProtocolStore, SignalProtocolAddress signalProtocolAddress) {
    this.mName = name;
    this.mDeviceId = deviceId;
    this.mIdentityKeyPair = identityKeyPair;
    this.mMetadataStore = metadataStore;
    this.mSignalProtocolStore = signalProtocolStore;
    this.mSignalProtocolAddress = signalProtocolAddress;
    this.mUnencryptedMessages = new ArrayList<>();
    this.contactList = new ArrayList<>();
  }

  // testing only
  public Account(String name, int mDeviceId) {
    this.mName = name;
    this.mDeviceId = mDeviceId;
  }

  public String getName() {
    return mName;
  }

  public IdentityKeyPair getIdentityKeyPair() {
    return mIdentityKeyPair;
  }
  
  public void setIdentityKeyPair(IdentityKeyPair identityKeyPair) {
    this.mIdentityKeyPair = identityKeyPair;
  }

  public PreKeyMetadataStore getMetadataStore() {
    return mMetadataStore;
  }

  public SignalProtocolStoreImpl getSignalProtocolStore() {
    return mSignalProtocolStore;
  }

  public SignalProtocolAddress getSignalProtocolAddress() {
    return mSignalProtocolAddress;
  }

  public int getDeviceId() {
    return mDeviceId;
  }

  public void setMetadataStore(PreKeyMetadataStore metadataStore) {
    this.mMetadataStore = metadataStore;
  }

  public void setSignalProtocolStore(SignalProtocolStoreImpl signalProtocolStore) {
    this.mSignalProtocolStore = signalProtocolStore;
  }

  public void setSignalProtocolAddress(SignalProtocolAddress signalProtocolAddress) {
    this.mSignalProtocolAddress = signalProtocolAddress;
  }

  public ArrayList<StorageMessage> getUnencryptedMessages() {
    return mUnencryptedMessages;
  }

  public void setUnencryptedMessages(ArrayList<StorageMessage> unencryptedMessages) {
    this.mUnencryptedMessages = unencryptedMessages;
  }

  public void addUnencryptedMessage(Contact contact, StorageMessage storageMessage) throws RuntimeException {
    if (mUnencryptedMessages == null)
      throw new RuntimeException("Error: UnencryptedMessage could not be saved. mUnencryptedMessages is null");
    mUnencryptedMessages.add(storageMessage);
  }

  public void removeAllUnencryptedMessages(Contact contact) {
    List<StorageMessage> operatedList = new ArrayList<>();
    mUnencryptedMessages.stream()
        .filter(m -> m.getContactUUID().equals(contact.getSignalProtocolAddressName()))
        .forEach(operatedList::add);
    mUnencryptedMessages.removeAll(operatedList);
  }

  public ArrayList<Contact> getContactList() {
    return contactList;
  }

  public void setContactList(ArrayList<Contact> contactList) {
    this.contactList = contactList;
  }

  public void addContactToContactList(Contact contact) throws DuplicateContactException {
    if (this.contactList.contains(contact))
      throw new DuplicateContactException("Error: Contact " + contact.getFirstName() + " " + contact.getLastName() + " already exists in contact list and will not be saved!");
    this.contactList.add(contact);
  }

  public void updateContactInContactList(Contact contact) throws UnknownContactException {
    if (this.contactList.contains(contact)) {
      int indexContact = 0;
      for (int i = 0; i < contactList.size(); i++) {
        if (contactList.get(i).getSignalProtocolAddressName().equals(contact.getSignalProtocolAddressName())) {
          indexContact = i;
        }
      }
      this.contactList.set(indexContact, contact);
    } else {
      throw new UnknownContactException("Contact does not exist in contact list");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Account account = (Account) o;
    return mDeviceId == account.mDeviceId && Objects.equals(mName, account.mName) && Objects.equals(mIdentityKeyPair, account.mIdentityKeyPair) && Objects.equals(mMetadataStore, account.mMetadataStore) && Objects.equals(mSignalProtocolStore, account.mSignalProtocolStore) && Objects.equals(mSignalProtocolAddress, account.mSignalProtocolAddress) && Objects.equals(mUnencryptedMessages, account.mUnencryptedMessages) && Objects.equals(contactList, account.contactList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mName, mDeviceId, mIdentityKeyPair, mMetadataStore, mSignalProtocolStore, mSignalProtocolAddress, mUnencryptedMessages, contactList);
  }

  @Override
  public String toString() {
    return "Account{" +
        "mName='" + mName + '\'' +
        ", mDeviceId=" + mDeviceId +
        ", mIdentityKeyPair=" + mIdentityKeyPair +
        ", mMetadataStore=" + mMetadataStore +
        ", mSignalProtocolStore=" + mSignalProtocolStore +
        ", mSignalProtocolAddress=" + mSignalProtocolAddress +
        ", mUnencryptedMessages=" + mUnencryptedMessages +
        ", contactList=" + contactList +
        '}';
  }
}

