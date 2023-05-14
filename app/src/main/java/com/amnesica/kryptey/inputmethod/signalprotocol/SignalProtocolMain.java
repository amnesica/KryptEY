package com.amnesica.kryptey.inputmethod.signalprotocol;

import android.content.Context;
import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.chat.Contact;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.StorageMessage;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.DuplicateContactException;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.InvalidContactException;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.UnknownContactException;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.UnknownMessageException;
import com.amnesica.kryptey.inputmethod.signalprotocol.helper.StorageHelper;
import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.PreKeyEntity;
import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.PreKeyResponse;
import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.PreKeyResponseItem;
import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.SignedPreKeyEntity;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.PreKeyMetadataStore;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.PreKeyMetadataStoreImpl;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.SignalProtocolStoreImpl;
import com.amnesica.kryptey.inputmethod.signalprotocol.util.KeyUtil;

import org.signal.libsignal.protocol.DuplicateMessageException;
import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.InvalidKeyException;
import org.signal.libsignal.protocol.InvalidKeyIdException;
import org.signal.libsignal.protocol.InvalidMessageException;
import org.signal.libsignal.protocol.InvalidVersionException;
import org.signal.libsignal.protocol.LegacyMessageException;
import org.signal.libsignal.protocol.NoSessionException;
import org.signal.libsignal.protocol.SessionBuilder;
import org.signal.libsignal.protocol.SessionCipher;
import org.signal.libsignal.protocol.SignalProtocolAddress;
import org.signal.libsignal.protocol.UntrustedIdentityException;
import org.signal.libsignal.protocol.ecc.Curve;
import org.signal.libsignal.protocol.ecc.ECPublicKey;
import org.signal.libsignal.protocol.fingerprint.Fingerprint;
import org.signal.libsignal.protocol.fingerprint.NumericFingerprintGenerator;
import org.signal.libsignal.protocol.message.CiphertextMessage;
import org.signal.libsignal.protocol.message.PreKeySignalMessage;
import org.signal.libsignal.protocol.message.SignalMessage;
import org.signal.libsignal.protocol.state.PreKeyBundle;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Main class for signal protocol
 */
public class SignalProtocolMain {
  static final String TAG = SignalProtocolMain.class.getSimpleName();

  private StorageHelper mStorageHelper;
  private Account mAccount;

  private static final SignalProtocolMain sInstance = new SignalProtocolMain();

  // know when tests are running so mock values can be used
  public static boolean testIsRunning = false;

  public static SignalProtocolMain getInstance() {
    return sInstance;
  }

  private SignalProtocolMain() {
    // Intentional empty constructor for singleton.
  }

  public static void initialize(final Context context) {
    Log.d(TAG, "Initializing signal protocol...");
    sInstance.initializeStorageHelper(context);
    sInstance.initializeProtocol();
  }

  public static void reloadAccount(final Context context) {
    Log.d(TAG, "Reloading local account for signal protocol (not first app run)...");
    sInstance.initializeStorageHelper(context);
    sInstance.reloadAccountFromSharedPreferences();
    sInstance.storeAllAccountInformationInSharedPreferences();
  }

  public static MessageEnvelope encryptMessage(final String unencryptedMessage, final SignalProtocolAddress signalProtocolAddress) {
    Log.d(TAG, "Encrypting signal message...");
    return sInstance.encrypt(unencryptedMessage, signalProtocolAddress);
  }

  public static String decryptMessage(final MessageEnvelope messageEnvelope, final SignalProtocolAddress signalProtocolAddress) throws InvalidMessageException, InvalidContactException, UnknownMessageException, UntrustedIdentityException, DuplicateMessageException, InvalidVersionException, InvalidKeyIdException, LegacyMessageException, InvalidKeyException, NoSessionException {
    Log.d(TAG, "Decrypting signal message...");
    return sInstance.decrypt(messageEnvelope, signalProtocolAddress);
  }

  public static boolean processPreKeyResponseMessage(final MessageEnvelope messageEnvelope, final SignalProtocolAddress signalProtocolAddress) {
    Log.d(TAG, "Processing pre key response signal message...");
    return sInstance.processPreKeyResponse(messageEnvelope, signalProtocolAddress);
  }

  public static MessageEnvelope getPreKeyResponseMessage() {
    Log.d(TAG, "Creating pre key response message...");
    return sInstance.createPreKeyResponseMessage();
  }

  public static MessageType getMessageType(final MessageEnvelope messageEnvelope) {
    Log.d(TAG, "Getting message type...");
    if (messageEnvelope == null) return null;

    if (messageEnvelope.getPreKeyResponse() != null && messageEnvelope.getCiphertextMessage() != null) {
      Log.d(TAG, "UPDATED_PRE_KEY_MESSAGE_WITH_CONTENT detected...");
      return MessageType.UPDATED_PRE_KEY_RESPONSE_MESSAGE_AND_SIGNAL_MESSAGE;
    } else if (messageEnvelope.getPreKeyResponse() != null) {
      Log.d(TAG, "PRE_KEY_RESPONSE_MESSAGE detected...");
      return MessageType.PRE_KEY_RESPONSE_MESSAGE;
    } else if (messageEnvelope.getCiphertextMessage() != null) {
      Log.d(TAG, "SIGNAL_MESSAGE detected...");
      // hint: PreKeySignalMessage or SignalMessage (ciphertextType is only set here!)
      sInstance.logMessageType(messageEnvelope.getCiphertextType());
      return MessageType.SIGNAL_MESSAGE;
    }
    return null;
  }

  public static Object extractContactFromMessageEnvelope(final MessageEnvelope messageEnvelope) {
    Log.d(TAG, "Extracting contact from message envelope...");
    return sInstance.extractContactFromEnvelope(messageEnvelope);
  }

  public static Contact addContact(final CharSequence firstName, final CharSequence lastName, final String signalProtocolAddressName, final int deviceId) throws DuplicateContactException, InvalidContactException {
    Log.d(TAG, "Creating and adding contact to contact list...");
    return sInstance.createAndAddContactToList(firstName, lastName, signalProtocolAddressName, deviceId);
  }

  public static ArrayList<Contact> getContactList() {
    Log.d(TAG, "Getting contact list...");
    return sInstance.getContactListFromAccount();
  }

  public static void removeContactFromContactListAndProtocol(final Contact contact) {
    Log.d(TAG, "Removing contact from contact list and protocol...");
    sInstance.removeContact(contact);
  }

  public static Fingerprint getFingerprint(Contact contact) {
    Log.d(TAG, "Generating fingerprint...");
    return sInstance.createFingerprint(contact);
  }

  public static void verifyContact(Contact contact) throws UnknownContactException {
    Log.d(TAG, "Verifying contact...");
    sInstance.verifyContactInContactList(contact);
  }

  private void verifyContactInContactList(Contact contact) throws UnknownContactException {
    if (contact == null) return;
    contact.setVerified(true);
    mAccount.updateContactInContactList(contact);
    storeAllAccountInformationInSharedPreferences();
  }

  private Fingerprint createFingerprint(Contact contact) {
    if (contact == null) return null;

    final IdentityKey localIdentity = getAccount().getIdentityKeyPair().getPublicKey();
    // find session for contact to extract public key
    final IdentityKey remoteIdentity = getAccount().getSignalProtocolStore().getSessionStore().getPublicKeyFromSession(contact.getSignalProtocolAddress());

    if (localIdentity == null && remoteIdentity == null) return null;

    final int version = 2; // use UUID
    final byte[] localId = getAccount().getSignalProtocolAddress().getName().getBytes();
    final byte[] remoteId = contact.getSignalProtocolAddress().getName().getBytes();

    NumericFingerprintGenerator numericFingerprintGenerator = new NumericFingerprintGenerator(5200);

    return numericFingerprintGenerator.createFor(version,
        localId, localIdentity,
        remoteId, remoteIdentity);
  }

  private ArrayList<Contact> getContactListFromAccount() {
    if (mAccount != null) {
      return mAccount.getContactList();
    }
    return null;
  }

  private Contact extractContactFromEnvelope(MessageEnvelope messageEnvelope) {
    final SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(messageEnvelope.signalProtocolAddressName, messageEnvelope.getDeviceId());
    return getContactFromAddressInContactList(signalProtocolAddress);
  }

  private Contact getContactFromAddressInContactList(SignalProtocolAddress signalProtocolAddress) {
    ArrayList<Contact> contacts = getContactListFromAccount();
    if (contacts == null) return null;
    return contacts.stream().filter(c -> c.getSignalProtocolAddress().equals(signalProtocolAddress)).findFirst().orElse(null);
  }

  private Contact createAndAddContactToList(final CharSequence firstName, final CharSequence lastName, final String signalProtocolAddressName, final int deviceId) throws DuplicateContactException, InvalidContactException {
    if (firstName == null || firstName.length() == 0 || signalProtocolAddressName == null || deviceId == 0)
      throw new InvalidContactException("Error: Contact is invalid. Some information is missing!");

    final Contact recipient = new Contact(String.valueOf(firstName), String.valueOf(lastName), signalProtocolAddressName, deviceId, false);
    mAccount.addContactToContactList(recipient);
    storeAllAccountInformationInSharedPreferences();
    return recipient;
  }

  private void removeContact(final Contact contactToRemove) {
    ArrayList<Contact> contacts = getContactListFromAccount();
    if (contacts == null) return;

    Log.d(TAG, "Deleting contact from contact list: " + contactToRemove.getFirstName() + " " + contactToRemove.getLastName());
    ArrayList<Contact> newContacts = new ArrayList<>();
    for (Contact contact : contacts) {
      if (!contact.equals(contactToRemove)) {
        newContacts.add(contact);
      }
    }
    mAccount.setContactList(newContacts);

    Log.d(TAG, "Deleting session for contact: " + contactToRemove.getFirstName() + " " + contactToRemove.getLastName());
    if (mAccount.getSignalProtocolStore().getSessionStore().containsSession(contactToRemove.getSignalProtocolAddress())) {
      mAccount.getSignalProtocolStore().getSessionStore().deleteSession(contactToRemove.getSignalProtocolAddress());
    }

    Log.d(TAG, "Deleting unencrypted messages from contact: " + contactToRemove.getFirstName() + " " + contactToRemove.getLastName());
    mAccount.removeAllUnencryptedMessages(contactToRemove);

    storeAllAccountInformationInSharedPreferences();
  }

  public static List<StorageMessage> getUnencryptedMessagesList(Contact contact) throws UnknownContactException {
    Log.d(TAG, "Getting unencrypted messages list...");
    return sInstance.getUnencryptedMessagesListFromAccount(contact);
  }

  private List<StorageMessage> getUnencryptedMessagesListFromAccount(Contact contact) throws UnknownContactException {
    if (mAccount != null && contact != null) {
      List<StorageMessage> messagesWithContact = mAccount.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contact.getSignalProtocolAddressName())).collect(Collectors.toList());
      if (messagesWithContact.size() == 0) {
        throw new UnknownContactException("No messages were found for contact: " + contact.getFirstName() + " " + contact.getLastName());
      }
      return messagesWithContact;
    }
    return null;
  }

  public static String getNameOfAccount() {
    Log.d(TAG, "Getting account name (uuid)...");
    return sInstance.getAccountName();
  }

  private String getAccountName() {
    return String.valueOf(getAccount().getName());
  }

  private MessageEnvelope encrypt(final String unencryptedMessage, final SignalProtocolAddress signalProtocolAddress) {
    if (unencryptedMessage == null || signalProtocolAddress == null) return null;
    try {
      MessageEnvelope messageEnvelope = null;
      // check age of signedPreKey and generate new one if necessary (and delete old ones after archive age)
      if (KeyUtil.refreshSignedPreKeyIfNecessary(mAccount.getSignalProtocolStore(), mAccount.getMetadataStore())) {
        // signed pre key was refreshed -> send new preKeyBundle together with message
        messageEnvelope = getPreKeyResponseMessage();
      }

      final SessionCipher sessionCipher = new SessionCipher(mAccount.getSignalProtocolStore(), signalProtocolAddress);
      CiphertextMessage ciphertextMessage = sessionCipher.encrypt(unencryptedMessage.getBytes());
      logMessageType(ciphertextMessage.getType());

      if (messageEnvelope == null) {
        messageEnvelope = new MessageEnvelope(ciphertextMessage.serialize(), ciphertextMessage.getType(), mAccount.getName(), mAccount.getDeviceId());
      } else {
        // add ciphertextMessage with type to preKeyResponse message
        messageEnvelope.setCiphertextMessage(ciphertextMessage.serialize());
        messageEnvelope.setCiphertextType(ciphertextMessage.getType());
        Log.d(TAG, "Signed pre key rotated. Adding ciphertextMessage...");
      }

      // store unencrypted message somewhere with recipient in map
      Log.d(TAG, "Attempting to save unencrypted message...");
      storeUnencryptedMessageInMap(mAccount, signalProtocolAddress, unencryptedMessage, Instant.ofEpochMilli(messageEnvelope.getTimestamp()), true);

      storeAllAccountInformationInSharedPreferences();

      return messageEnvelope;
    } catch (UntrustedIdentityException | InvalidContactException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void logMessageType(int type) {
    if (type == 3) {
      Log.d(TAG, "CiphertextMessage = PRE_KEY");
    } else if (type == 2) {
      Log.d(TAG, "CiphertextMessage = WHISPER_TYPE");
    }
  }

  private boolean processPreKeyResponse(final MessageEnvelope messageEnvelope, final SignalProtocolAddress signalProtocolAddress) {
    if (messageEnvelope == null) return false;
    try {
      // build session with recipients protocol address when preKeyResponse was send
      if (messageEnvelope.getPreKeyResponse() != null) {
        final PreKeyBundle preKeyBundle = createPreKeyBundle(messageEnvelope.getPreKeyResponse());

        buildSession(preKeyBundle, signalProtocolAddress);
        Log.d(TAG, "Session with PreKeyBundle created: " + sessionExists(signalProtocolAddress));
        Log.d(TAG, "Amount of pre key ids: " + mAccount.getSignalProtocolStore().getPreKeyStore().getSize());
        storeAllAccountInformationInSharedPreferences();
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private String decrypt(final MessageEnvelope messageEnvelope, final SignalProtocolAddress signalProtocolAddress) throws InvalidContactException, UnknownMessageException, InvalidMessageException, InvalidVersionException, LegacyMessageException, InvalidKeyException, UntrustedIdentityException, DuplicateMessageException, InvalidKeyIdException, NoSessionException {
    if (messageEnvelope == null) return null;
    String decryptedMessage;

    final SessionCipher sessionCipher = new SessionCipher(mAccount.getSignalProtocolStore(), signalProtocolAddress);

    // update session with new signed pre key from recipient
    if (messageEnvelope.getCiphertextMessage() != null && messageEnvelope.getPreKeyResponse() != null) {
      Log.d(TAG, "Message with cipherText and updated preKeyResponse received...");
      processPreKeyResponseMessage(messageEnvelope, signalProtocolAddress);
    }

    logMessageType(messageEnvelope.getCiphertextType());

    byte[] plaintext;
    if (messageEnvelope.getCiphertextType() == CiphertextMessage.PREKEY_TYPE) {
      // decrypting message and storing session with preKeySignalMessage
      final PreKeySignalMessage preKeySignalMessage = new PreKeySignalMessage(messageEnvelope.getCiphertextMessage());

      Log.d(TAG, "PreKeySignalMessage: Used signed prekey id: " + preKeySignalMessage.getSignedPreKeyId());

      plaintext = sessionCipher.decrypt(preKeySignalMessage);
      decryptedMessage = new String(plaintext);

      if (preKeySignalMessage.getPreKeyId().isPresent())
        KeyUtil.generateAndStoreOneTimePreKey(mAccount.getSignalProtocolStore(), preKeySignalMessage.getPreKeyId().get());

      Log.d(TAG, "Session with PreKeySignalMessage created (after decryption): " + sessionExists(signalProtocolAddress));
      Log.d(TAG, "Amount of pre key ids: " + mAccount.getSignalProtocolStore().getPreKeyStore().getSize());
    } else if (messageEnvelope.getCiphertextType() == CiphertextMessage.WHISPER_TYPE) {
      // only decrypting message (session already exists)
      plaintext = sessionCipher.decrypt(new SignalMessage(messageEnvelope.getCiphertextMessage()));
      decryptedMessage = new String(plaintext);
      Log.d(TAG, "Amount of pre key ids: " + mAccount.getSignalProtocolStore().getPreKeyStore().getSize());
    } else {
      throw new UnknownMessageException("Received message is not of type PRE_KEY or WHISPER_TYPE");
    }

    if (plaintext != null) {
      // store unencrypted message somewhere with recipient in map
      Log.d(TAG, "Attempting to save unencrypted message...");
      storeUnencryptedMessageInMap(mAccount, signalProtocolAddress, decryptedMessage, Instant.ofEpochMilli(messageEnvelope.getTimestamp()), false);
    }
    storeAllAccountInformationInSharedPreferences();

    return decryptedMessage;
  }

  public PreKeyBundle createPreKeyBundle(PreKeyResponse preKeyResponse) throws IOException {
    if (preKeyResponse.getDevices() == null || preKeyResponse.getDevices().size() < 1)
      throw new IOException("Empty prekey list");

    PreKeyResponseItem device = preKeyResponse.getDevices().get(0);
    ECPublicKey preKey = null;
    ECPublicKey signedPreKey = null;
    byte[] signedPreKeySignature = null;
    int preKeyId = -1;
    int signedPreKeyId = -1;

    if (device.getPreKey() != null) {
      preKeyId = device.getPreKey().getKeyId();
      preKey = device.getPreKey().getPublicKey();
    }

    if (device.getSignedPreKey() != null) {
      signedPreKeyId = device.getSignedPreKey().getKeyId();
      signedPreKey = device.getSignedPreKey().getPublicKey();
      signedPreKeySignature = device.getSignedPreKey().getSignature();
    }

    return new PreKeyBundle(device.getRegistrationId(), device.getDeviceId(), preKeyId, preKey,
        signedPreKeyId, signedPreKey, signedPreKeySignature, preKeyResponse.getIdentityKey());
  }

  private void storeUnencryptedMessageInMap(Account account, SignalProtocolAddress signalProtocolAddress, final String decryptedMessage, final Instant timestamp, final boolean isFromOwnAccount) throws InvalidContactException {
    final Optional<Contact> recipient;
    if (testIsRunning) {
      // for running tests only!
      recipient = Optional.of(new Contact("test", "test", signalProtocolAddress.getName(), signalProtocolAddress.getDeviceId(), false));
    } else {
      recipient = getContactList().stream().filter(c -> c.getSignalProtocolAddress().equals(signalProtocolAddress)).findFirst();
    }

    if (!recipient.isPresent())
      throw new InvalidContactException("No contact found with signalProtocolAddress: " + signalProtocolAddress);

    StorageMessage storageMessage;
    if (isFromOwnAccount) {
      storageMessage = new StorageMessage(signalProtocolAddress.getName(), account.getSignalProtocolAddress().getName(), signalProtocolAddress.getName(), timestamp, decryptedMessage);
    } else {
      storageMessage = new StorageMessage(signalProtocolAddress.getName(), signalProtocolAddress.getName(), account.getSignalProtocolAddress().getName(), timestamp, decryptedMessage);
    }

    recipient.ifPresent(contact -> account.addUnencryptedMessage(contact, storageMessage));
  }

  private boolean sessionExists(SignalProtocolAddress signalProtocolAddress) {
    return mAccount.getSignalProtocolStore().containsSession(signalProtocolAddress);
  }

  private MessageEnvelope createPreKeyResponseMessage() {
    try {
      final PreKeyResponse preKeyResponse = createPreKeyResponse();
      return new MessageEnvelope(preKeyResponse, mAccount.getSignalProtocolAddress().getName(), mAccount.getSignalProtocolAddress().getDeviceId());
    } catch (InvalidKeyIdException | InvalidKeyException e) {
      Log.e(TAG, "Error: Creating pre key response message failed");
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Create signature from identity private key and create pre key bundle
   *
   * @return PreKeyBundle
   * @throws InvalidKeyIdException InvalidKeyIdException
   * @throws InvalidKeyException   InvalidKeyException
   */
  private PreKeyBundle getPreKeyBundle() throws InvalidKeyIdException, InvalidKeyException {
    // check age of signedPreKey and generate new one if necessary (and delete old ones after archive age)
    KeyUtil.refreshSignedPreKeyIfNecessary(mAccount.getSignalProtocolStore(), mAccount.getMetadataStore());

    final byte[] signedPreKeySignature = Curve.calculateSignature(
        mAccount.getSignalProtocolStore().getIdentityKeyPair().getPrivateKey(),
        mAccount.getSignalProtocolStore().loadSignedPreKey(mAccount.getMetadataStore().getActiveSignedPreKeyId()).getKeyPair().getPublicKey().serialize());

    final int preKeyId = KeyUtil.getUnusedOneTimePreKeyId(mAccount.getSignalProtocolStore());

    Log.d(TAG, "Generating PreKeyBundle with pre key id: " + preKeyId);
    final PreKeyBundle preKeyBundle = new PreKeyBundle(
        mAccount.getSignalProtocolStore().getLocalRegistrationId(),
        mAccount.getDeviceId(),
        preKeyId,
        mAccount.getSignalProtocolStore().loadPreKey(preKeyId).getKeyPair().getPublicKey(),
        mAccount.getMetadataStore().getActiveSignedPreKeyId(),
        mAccount.getSignalProtocolStore().loadSignedPreKey(mAccount.getMetadataStore().getActiveSignedPreKeyId()).getKeyPair().getPublicKey(),
        signedPreKeySignature,
        mAccount.getSignalProtocolStore().getIdentityKeyPair().getPublicKey());

    return preKeyBundle;
  }

  private PreKeyResponse createPreKeyResponse() throws InvalidKeyIdException, InvalidKeyException {
    final PreKeyBundle preKeyBundle = getPreKeyBundle();

    List<PreKeyResponseItem> responseItems = new LinkedList<>();
    responseItems.add(new PreKeyResponseItem(
        preKeyBundle.getDeviceId(),
        preKeyBundle.getRegistrationId(),
        new SignedPreKeyEntity(preKeyBundle.getSignedPreKeyId(), preKeyBundle.getSignedPreKey(), preKeyBundle.getSignedPreKeySignature()),
        new PreKeyEntity(preKeyBundle.getPreKeyId(), preKeyBundle.getPreKey())));

    return new PreKeyResponse(preKeyBundle.getIdentityKey(), responseItems);
  }

  /**
   * Instantiate a SessionBuilder for a remote recipientId + deviceId tuple
   *
   * @param preKeyBundle                   PreKeyBundle
   * @param recipientSignalProtocolAddress SignalProtocolAddress
   */
  private void buildSession(final PreKeyBundle preKeyBundle, final SignalProtocolAddress recipientSignalProtocolAddress) {
    try {
      SessionBuilder sessionBuilder = new SessionBuilder(mAccount.getSignalProtocolStore(), recipientSignalProtocolAddress);
      sessionBuilder.process(preKeyBundle);
      storeAllAccountInformationInSharedPreferences();
    } catch (InvalidKeyException | UntrustedIdentityException e) {
      Log.e(TAG, "Error: Building session with recipient id " + recipientSignalProtocolAddress.getName() + " failed");
      e.printStackTrace();
    }
  }

  /**
   * Initializes the protocol by generating and storing all necessary keys and stores
   */
  private void initializeProtocol() {
    final String uniqueUserId = UUID.randomUUID().toString();
    final int deviceId = new Random().nextInt(10000);
    final SignalProtocolAddress signalProtocolAddress = new SignalProtocolAddress(uniqueUserId, deviceId);
    final PreKeyMetadataStore metadataStore = new PreKeyMetadataStoreImpl();

    // generate IdentityKeyPair, registrationId
    final IdentityKeyPair identityKeyPair = KeyUtil.generateIdentityKeyPair();
    final int registrationId = KeyUtil.generateRegistrationId();

    // generate new signalProtocolStore
    final SignalProtocolStoreImpl signalProtocolStore = new SignalProtocolStoreImpl(identityKeyPair, registrationId);

    // generate and store preKeys in PreKeyStore
    KeyUtil.generateAndStoreOneTimePreKeys(signalProtocolStore, metadataStore);

    // generate and store signed prekey in SignedPreKeyStore
    final SignedPreKeyRecord signedPreKey = KeyUtil.generateAndStoreSignedPreKey(signalProtocolStore, metadataStore);

    metadataStore.setActiveSignedPreKeyId(signedPreKey.getId());
    metadataStore.setSignedPreKeyRegistered(true);

    // create account for device
    mAccount = new Account(uniqueUserId, deviceId, identityKeyPair, metadataStore, signalProtocolStore, signalProtocolAddress);

    storeAllAccountInformationInSharedPreferences();
  }

  private void reloadAccountFromSharedPreferences() {
    mAccount = mStorageHelper.getAccountFromSharedPreferences();
  }

  private void storeAllAccountInformationInSharedPreferences() {
    if (mStorageHelper != null) {
      mStorageHelper.storeAllInformationInSharedPreferences(mAccount);
    } else {
      Log.e(TAG, "Error: No protocol resources were stored (mStorageHelper is null)");
    }
  }

  private void initializeStorageHelper(Context context) {
    if (context == null) {
      Log.e(TAG, "Error: mStorageHelper cannot get initialized because context is null");
      return;
    }
    mStorageHelper = new StorageHelper(context);
  }

  // needed for testing only
  public Account getAccount() {
    return mAccount;
  }

  // needed for testing only
  public void setAccount(final Account account) {
    this.mAccount = account;
  }
}
