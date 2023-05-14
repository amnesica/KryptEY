package com.amnesica.kryptey.inputmethod.signalprotocol.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.Account;
import com.amnesica.kryptey.inputmethod.signalprotocol.ProtocolIdentifier;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.Contact;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.StorageMessage;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.PreKeyMetadataStore;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.SignalProtocolStoreImpl;
import com.amnesica.kryptey.inputmethod.signalprotocol.util.JsonUtil;

import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.SignalProtocolAddress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageHelper {
  static final String TAG = StorageHelper.class.getSimpleName();

  private final Context mContext;
  private final String mSharedPreferenceName = "protocol";

  public StorageHelper(Context context) {
    this.mContext = context;
  }

  public Account getAccountFromSharedPreferences() {
    final String name = (String) getClassFromSharedPreferences(ProtocolIdentifier.UNIQUE_USER_ID);
    final SignalProtocolStoreImpl signalProtocolStore = (SignalProtocolStoreImpl) getClassFromSharedPreferences(ProtocolIdentifier.PROTOCOL_STORE);
    final IdentityKeyPair identityKeyPair = signalProtocolStore.getIdentityKeyPair();

    final PreKeyMetadataStore metadataStore = (PreKeyMetadataStore) getClassFromSharedPreferences(ProtocolIdentifier.METADATA_STORE);
    final SignalProtocolAddress signalProtocolAddress = (SignalProtocolAddress) getClassFromSharedPreferences(ProtocolIdentifier.PROTOCOL_ADDRESS);

    final ArrayList<StorageMessage> unencryptedMessages = JsonUtil.convertUnencryptedMessagesList((ArrayList<StorageMessage>) getClassFromSharedPreferences(ProtocolIdentifier.UNENCRYPTED_MESSAGES));
    final ArrayList<Contact> contactList = JsonUtil.convertContactsList((ArrayList<Contact>) getClassFromSharedPreferences(ProtocolIdentifier.CONTACTS));

    Account account = new Account(name, signalProtocolAddress.getDeviceId(), identityKeyPair, metadataStore, signalProtocolStore, signalProtocolAddress); // deviceId is static
    account.setUnencryptedMessages(unencryptedMessages);
    account.setContactList(contactList);

    return account;
  }

  public void storeAllInformationInSharedPreferences(final Account account) {
    storeMetaDataStoreInSharedPreferences(account.getMetadataStore());
    storeUniqueUserIdInSharedPreferences(account.getName());
    storeSignalProtocolInSharedPreferences(account.getSignalProtocolStore()); // incl. registrationId + identityKeyPair
    storeSignalProtocolAddressInSharedPreferences(account.getSignalProtocolAddress());
    storeDeviceIdInSharedPreferences(account.getDeviceId());
    storeUnencryptedMessagesMapInSharedPreferences(account.getUnencryptedMessages());
    storeContactListInSharedPreferences(account.getContactList());
  }

  private void storeUnencryptedMessagesMapInSharedPreferences(List<StorageMessage> unencryptedMessages) {
    storeInSharedPreferences(ProtocolIdentifier.UNENCRYPTED_MESSAGES, unencryptedMessages);
  }

  public void storeMetaDataStoreInSharedPreferences(final PreKeyMetadataStore metadataStore) {
    storeInSharedPreferences(ProtocolIdentifier.METADATA_STORE, metadataStore);
  }

  public void storeUniqueUserIdInSharedPreferences(final String uniqueUserId) {
    storeInSharedPreferences(ProtocolIdentifier.UNIQUE_USER_ID, uniqueUserId);
  }

  public void storeSignalProtocolInSharedPreferences(final SignalProtocolStoreImpl signalProtocolStore) {
    storeInSharedPreferences(ProtocolIdentifier.PROTOCOL_STORE, signalProtocolStore);
  }

  public void storeSignalProtocolAddressInSharedPreferences(final SignalProtocolAddress signalProtocolAddress) {
    storeInSharedPreferences(ProtocolIdentifier.PROTOCOL_ADDRESS, signalProtocolAddress);
  }

  public void storeDeviceIdInSharedPreferences(final Integer deviceId) {
    storeInSharedPreferences(ProtocolIdentifier.DEVICE_ID, deviceId);
  }

  private void storeContactListInSharedPreferences(List<Contact> contactList) {
    storeInSharedPreferences(ProtocolIdentifier.CONTACTS, contactList);
  }

  public void storeInSharedPreferences(final ProtocolIdentifier protocolIdentifier, final Object objectToStore) {
    if (mContext == null) {
      logError("mContext");
      return;
    }
    SharedPreferences sharedPreferences = mContext.getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
    if (sharedPreferences == null) {
      logError("sharedPreferences");
      return;
    }
    sharedPreferences.edit().putString(String.valueOf(protocolIdentifier), JsonUtil.toJson(objectToStore)).apply();
  }

  public Object getClassFromSharedPreferences(final ProtocolIdentifier protocolIdentifier) {
    if (mContext == null) {
      logError("mContext");
      return null;
    }
    SharedPreferences sharedPreferences = mContext.getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
    if (sharedPreferences == null) {
      logError("sharedPreferences");
      return null;
    }
    final String json = sharedPreferences.getString(String.valueOf(protocolIdentifier), null);
    try {
      if (json == null) throw new IOException("Required content not found! Was is stored before?");
      return JsonUtil.fromJson(json, protocolIdentifier.className);
    } catch (IOException e) {
      Log.e(TAG, "Error: Could not process " + protocolIdentifier + " from sharedPreferences");
      e.printStackTrace();
    }
    return null;
  }

  private void logError(final String nameObject) {
    Log.e(TAG, "Error: Possible null value for " + nameObject);
  }
}
