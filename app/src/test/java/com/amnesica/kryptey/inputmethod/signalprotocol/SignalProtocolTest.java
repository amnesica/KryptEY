package com.amnesica.kryptey.inputmethod.signalprotocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.chat.Contact;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.StorageMessage;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.DuplicateContactException;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.InvalidContactException;
import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.PreKeyEntity;
import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.PreKeyResponse;
import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.PreKeyResponseItem;
import com.amnesica.kryptey.inputmethod.signalprotocol.prekey.SignedPreKeyEntity;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.IdentityKeyStoreImpl;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.PreKeyMetadataStore;
import com.amnesica.kryptey.inputmethod.signalprotocol.stores.SignalProtocolStoreImpl;
import com.amnesica.kryptey.inputmethod.signalprotocol.util.JsonUtil;
import com.amnesica.kryptey.inputmethod.signalprotocol.util.KeyUtil;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
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
import org.signal.libsignal.protocol.SignalProtocolAddress;
import org.signal.libsignal.protocol.UntrustedIdentityException;
import org.signal.libsignal.protocol.ecc.Curve;
import org.signal.libsignal.protocol.ecc.ECPublicKey;
import org.signal.libsignal.protocol.fingerprint.Fingerprint;
import org.signal.libsignal.protocol.fingerprint.NumericFingerprintGenerator;
import org.signal.libsignal.protocol.state.PreKeyBundle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SignalProtocolTest {
  static final String TAG = SignalProtocolTest.class.getSimpleName();

  private static Account alice;
  private static Account bob;

  private static Map<ProtocolIdentifier, String> sharedPreferencesStoreAlice;
  private static Map<ProtocolIdentifier, String> sharedPreferencesStoreBob;

  private static final String ALICE_USERNAME = "alice";
  private static final String BOB_USERNAME = "bob";

  @BeforeClass
  public static void initializeProtocol() {
    Log.i(TAG, "------------ initializeProtocol: ------------");
    alice = initializeAccount(ALICE_USERNAME);
    bob = initializeAccount(BOB_USERNAME);
    assertNotNull(alice);
    assertNotNull(bob);
    assertNotEquals(alice, bob);
  }

  @Before
  public void reloadAccount() throws IOException {
    Log.i(TAG, "------------ reloadAccount: ------------");
    alice = loadAccount(ALICE_USERNAME);
    bob = loadAccount(BOB_USERNAME);
    assertNotNull(alice);
    assertNotNull(bob);
    assertNotEquals(alice, bob);
  }

  @Test
  public void buildSessionWithPreKeyBundleOnlySenderTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException {
    Log.i(TAG, "------------ buildSessionWithPreKeyBundleOnlySenderTest: ------------");
    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);
  }

  @Test
  public void buildSessionWithPreKeyBundleOnBothSidesTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException {
    Log.i(TAG, "------------ buildSessionWithPreKeyBundleOnBothSidesTest: ------------");
    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);
    buildSessionWithPreKeyResponseMessage(bob, BOB_USERNAME, alice, ALICE_USERNAME);
  }

  @Test
  public void buildSessionWithPreKeyBundleOnBothSidesAndSendMessageTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException {
    Log.i(TAG, "------------ buildSessionWithPreKeyBundleOnBothSidesTest: ------------");
    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);
    buildSessionWithPreKeyResponseMessage(bob, BOB_USERNAME, alice, ALICE_USERNAME);
    // hint: this fails! sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, "test");
  }

  @Test
  public void buildMultipleSessionsWithPreKeyBundleOnBothSidesTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException {
    Log.i(TAG, "------------ buildMultipleSessionsWithPreKeyBundleOnBothSidesTest: ------------");
    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);
    buildSessionWithPreKeyResponseMessage(bob, BOB_USERNAME, alice, ALICE_USERNAME);
    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);
    buildSessionWithPreKeyResponseMessage(bob, BOB_USERNAME, alice, ALICE_USERNAME);
  }

  @Test
  public void serializeDeserializeSignalProtocolAddressTest() {
    Log.i(TAG, "------------ serializeDeserializeSignalProtocolAddressTest: ------------");
    SignalProtocolAddress address = alice.getSignalProtocolAddress();
    String addressSerialized = JsonUtil.toJson(address);

    SignalProtocolAddress address2 = new SignalProtocolAddress(alice.getName(), alice.getDeviceId());
    String addressSerialized2 = JsonUtil.toJson(address2);

    assertEquals(address, address2);
    assertEquals(addressSerialized, addressSerialized2);

    SignalProtocolAddress address3 = new SignalProtocolAddress(alice.getSignalProtocolAddress().getName(), alice.getSignalProtocolAddress().getDeviceId());
    String addressSerialized3 = JsonUtil.toJson(address3);

    assertEquals(address, address3);
    assertEquals(addressSerialized, addressSerialized3);

    compareSignalProtocolAddressWithUnsafeHandle(address, address2);
    compareSignalProtocolAddressWithUnsafeHandle(address, address3);
    compareSignalProtocolAddressWithUnsafeHandle(address2, address3);
  }

  public boolean compareSignalProtocolAddressWithUnsafeHandle(Object first, Object second) {
    if (first == null) return false;
    if (!(first instanceof SignalProtocolAddress)) return false;
    if (second == null) return false;
    if (!(second instanceof SignalProtocolAddress)) return false;

    SignalProtocolAddress one = (SignalProtocolAddress) first;
    SignalProtocolAddress two = (SignalProtocolAddress) second;
    return one.getName().equals(two.getName()) && one.getDeviceId() == two.getDeviceId() && one.unsafeNativeHandleWithoutGuard() == two.unsafeNativeHandleWithoutGuard();
  }

  private void buildSessionWithPreKeyResponseMessage(final Account sender, final String senderUsername, final Account recipient, final String recipientUsername) throws IOException {
    // recipient side
    setActiveProtocolAccount(recipient, recipientUsername);
    MessageEnvelope preKeyResponseMessageGeneratedByRecipient = SignalProtocolMain.getPreKeyResponseMessage();
    assertNotNull(preKeyResponseMessageGeneratedByRecipient);

    // test serializing/deserializing
    String clipboardPreKeyResponse = JsonUtil.toJson(preKeyResponseMessageGeneratedByRecipient);
    MessageEnvelope outgoingPreKeyResponse = JsonUtil.fromJson(clipboardPreKeyResponse, MessageEnvelope.class);
    assertEquals(preKeyResponseMessageGeneratedByRecipient, outgoingPreKeyResponse);

    Log.i(TAG, clipboardPreKeyResponse);
    assertNotNull(outgoingPreKeyResponse.getPreKeyResponse());
    assertNull(outgoingPreKeyResponse.getCiphertextMessage());
    assertNotNull(outgoingPreKeyResponse.getSignalProtocolAddressName());

    SignalProtocolAddress addressRecipient = new SignalProtocolAddress(outgoingPreKeyResponse.getSignalProtocolAddressName(), outgoingPreKeyResponse.getDeviceId());
    assertNotNull(addressRecipient);

    setActiveProtocolAccount(sender, senderUsername);
    SignalProtocolMain.processPreKeyResponseMessage(outgoingPreKeyResponse, addressRecipient);

    assertTrue(sender.getSignalProtocolStore().containsSession(addressRecipient));
    assertEquals(3, sender.getSignalProtocolStore().loadSession(addressRecipient).getSessionVersion());
    assertEquals(recipient.getSignalProtocolStore().getIdentityKeyStore().getIdentityKeyPair().getPublicKey(), sender.getSignalProtocolStore().getIdentityKeyStore().getIdentity(addressRecipient));

    putAllInformationInMapSharedPreferences(sender, senderUsername);

    logSizePreKeyStore();
    logSizeSessionStore();
  }

  @Test
  public void buildSessionAndSendMessageTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException, NoSessionException, InvalidMessageException, DuplicateMessageException, InvalidVersionException, LegacyMessageException {
    Log.i(TAG, "------------ buildSessionAndSendMessageTest: ------------");
    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);

    final String unencryptedMessage = "Looks like I picked the wrong week to quit drinking.";
    sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, unencryptedMessage);
  }

  @Test
  public void buildSessionAndSendMessageWithPreKeyResponseTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException, NoSessionException, InvalidMessageException, DuplicateMessageException, InvalidVersionException, LegacyMessageException {
    Log.i(TAG, "------------ buildSessionAndSendMessageWithPreKeyResponseTest: ------------");
    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);

    final String unencryptedMessage = "Looks like I picked the wrong week to quit drinking.";
    sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, unencryptedMessage);

    final String unencryptedMessage2 = "Looks like I picked the wrong week to quit smoking.";
    sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, unencryptedMessage2);

    printHashMapForAccountName(ALICE_USERNAME, sharedPreferencesStoreAlice);
  }

  @Test
  public void buildSessionAndRunInteractionTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException, NoSessionException, InvalidMessageException, DuplicateMessageException, InvalidVersionException, LegacyMessageException {
    Log.i(TAG, "------------ buildSessionAndRunInteractionTest: ------------");

    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);

    final String unencryptedMessage = "Can you fly this plane, and land it?";
    sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, unencryptedMessage);

    final String unencryptedMessage2 = "Surely you can’t be serious";
    sendMessageAfterSessionBuild(bob, BOB_USERNAME, alice, ALICE_USERNAME, unencryptedMessage2);

    final String unencryptedMessage3 = "I am serious ... and don’t call me Shirley.";
    sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, unencryptedMessage3);

    final String unencryptedMessage4 = "I won’t deceive you, Mr. Striker. We’re running out of time.";
    sendMessageAfterSessionBuild(bob, BOB_USERNAME, alice, ALICE_USERNAME, unencryptedMessage4);

    final String unencryptedMessage5 = "Surely there must be something you can do.";
    sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, unencryptedMessage5);

    final String unencryptedMessage6 = "I’m doing everything I can ... and stop calling me Shirley!";
    sendMessageAfterSessionBuild(bob, BOB_USERNAME, alice, ALICE_USERNAME, unencryptedMessage6);

    printHashMapForAccountName(ALICE_USERNAME, sharedPreferencesStoreAlice);
    printHashMapForAccountName(BOB_USERNAME, sharedPreferencesStoreBob);
  }

  private void sendMessageAfterSessionBuild(final Account sender, final String senderUsername, final Account recipient, final String recipientUsername, final String unencryptedMessage) throws IOException, NoSessionException, InvalidMessageException, UntrustedIdentityException, DuplicateMessageException, InvalidVersionException, InvalidKeyIdException, LegacyMessageException, InvalidKeyException {
    Log.i(TAG, "--- start of method ---");
    Log.i(TAG, "sender: " + senderUsername + ", message to send: " + unencryptedMessage);

    setActiveProtocolAccount(sender, senderUsername);
    final MessageEnvelope sendingMessageEnvelope = SignalProtocolMain.encryptMessage(unencryptedMessage, recipient.getSignalProtocolAddress());
    assertNotNull(sendingMessageEnvelope.getCiphertextMessage());

    setActiveProtocolAccount(recipient, recipientUsername);
    final CharSequence encryptedMessageFromClipboard = JsonUtil.toJson(sendingMessageEnvelope);
    assertNotNull(encryptedMessageFromClipboard);

    // TODO fairytale debug test
    /*
    FairyTaleEncoder.initForTest(FairyTaleEncoderTest.rapunzelText, FairyTaleEncoderTest.cinderellaText);

    // encode
    final String encodedMessage = FairyTaleEncoder.encode(encryptedMessageFromClipboard.toString(), null);
    assertNotNull(encodedMessage);
    Log.d(TAG, "encodedMessage: " + encodedMessage);
    Log.d(TAG, "encodedMessage (length): " + encodedMessage.length());
    Log.d(TAG, "encodedMessage (bytes): " + encodedMessage.getBytes(StandardCharsets.UTF_8).length);

    // decode
    String decodedMessage = FairyTaleEncoder.decode(encodedMessage);
    assertNotNull(decodedMessage);
    assertEquals(encryptedMessageFromClipboard, decodedMessage); */

    Log.d(TAG, "encodedMessage: " + encryptedMessageFromClipboard);
    Log.d(TAG, "encodedMessage (length): " + encryptedMessageFromClipboard.length());
    Log.d(TAG, "encodedMessage (bytes): " + encryptedMessageFromClipboard.toString().getBytes(StandardCharsets.UTF_8).length);

    final MessageEnvelope receivedMessageEnvelope = JsonUtil.fromJson(encryptedMessageFromClipboard.toString(), MessageEnvelope.class);
    assertEquals(sendingMessageEnvelope, receivedMessageEnvelope);

    final CharSequence decryptedMessage = SignalProtocolMain.decryptMessage(receivedMessageEnvelope, sender.getSignalProtocolAddress());
    assertEquals(unencryptedMessage, decryptedMessage);
    assertEquals(1, sender.getSignalProtocolStore().getSessionStore().loadExistingSessions(Collections.singletonList(recipient.getSignalProtocolAddress())).size());

    Log.i(TAG, "UNENCRYPTED MESSAGE: " + unencryptedMessage);
    Log.i(TAG, "DECRYPTED MESSAGE: " + decryptedMessage);
    Log.i(TAG, "recipient: " + recipientUsername + ", unencryptedMessage: " + decryptedMessage);
    Log.i(TAG, "--- end of method ---");

    logSizePreKeyStore();
    logSizeSessionStore();

    putAllInformationInMapSharedPreferences(sender, senderUsername);
    putAllInformationInMapSharedPreferences(recipient, recipientUsername);
  }

  @Test
  public void verifyUnencryptedMessagesAreStoredInMapTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException, NoSessionException, InvalidMessageException, DuplicateMessageException, InvalidVersionException, LegacyMessageException {
    Log.i(TAG, "------------ verifyUnencryptedMessagesAreStoredInMapTest: ------------");
    final Contact contactAlice = new Contact("test", "test", alice.getName(), alice.getDeviceId(), false);
    final Contact contactBob = new Contact("test", "test", bob.getName(), bob.getDeviceId(), false);

    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);

    // reset unencryptedMessages lists when all tests are running (then 2 messages are already stored)
    alice.setUnencryptedMessages(new ArrayList<>());
    bob.setUnencryptedMessages(new ArrayList<>());

    assertEquals(0, alice.getUnencryptedMessages().size());
    assertEquals(0, bob.getUnencryptedMessages().size());

    final String unencryptedMessage = "First time?";
    sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, unencryptedMessage);

    assertEquals(1, alice.getUnencryptedMessages().size());
    assertEquals(1, bob.getUnencryptedMessages().size());

    assertNotNull(alice.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contactBob.getSignalProtocolAddressName())).collect(Collectors.toList()));
    assertNotNull(bob.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contactAlice.getSignalProtocolAddressName())).collect(Collectors.toList()));

    assertEquals(unencryptedMessage, Objects.requireNonNull(alice.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contactBob.getSignalProtocolAddressName())).collect(Collectors.toList())).get(0).getUnencryptedMessage());
    assertEquals(unencryptedMessage, Objects.requireNonNull(bob.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contactAlice.getSignalProtocolAddressName())).collect(Collectors.toList())).get(0).getUnencryptedMessage());

    final String unencryptedMessage2 = "No, I’ve been nervous lots of times.";
    sendMessageAfterSessionBuild(bob, BOB_USERNAME, alice, ALICE_USERNAME, unencryptedMessage2);

    assertEquals(2, alice.getUnencryptedMessages().size());
    assertEquals(2, bob.getUnencryptedMessages().size());

    assertNotNull(alice.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contactBob.getSignalProtocolAddressName())).collect(Collectors.toList()));
    assertNotNull(bob.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contactAlice.getSignalProtocolAddressName())).collect(Collectors.toList()));

    assertEquals(unencryptedMessage2, Objects.requireNonNull(alice.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contactBob.getSignalProtocolAddressName())).collect(Collectors.toList())).get(1).getUnencryptedMessage());
    assertEquals(unencryptedMessage2, Objects.requireNonNull(bob.getUnencryptedMessages().stream().filter(m -> m.getContactUUID().equals(contactAlice.getSignalProtocolAddressName())).collect(Collectors.toList())).get(1).getUnencryptedMessage());
  }

  @Test
  public void addContactToContactListTest() throws DuplicateContactException, InvalidContactException {
    Log.i(TAG, "------------ addContactToContactListTest: ------------");
    setActiveProtocolAccount(alice, ALICE_USERNAME);

    SignalProtocolMain.addContact("bob", "lastName", bob.getSignalProtocolAddress().getName(), bob.getDeviceId());
    assertTrue(alice.getContactList().contains(new Contact("bob", "lastName", bob.getSignalProtocolAddress().getName(), bob.getDeviceId(), false)));
    assertEquals(1, alice.getContactList().size());

    // test duplicate exception
    DuplicateContactException thrown = assertThrows(DuplicateContactException.class, () -> SignalProtocolMain.addContact("bob", "lastName", bob.getSignalProtocolAddress().getName(), bob.getDeviceId()));
    assertEquals("Error: Contact bob lastName already exists in contact list and will not be saved!", thrown.getMessage());

    putAllInformationInMapSharedPreferences(alice, ALICE_USERNAME);
  }

  @Test
  public void objectMapperSignalProtocolAddressSerializeTest() throws IOException {
    Log.i(TAG, "------------ objectMapperSignalProtocolAddressSerializeTest: ------------");
    SignalProtocolAddress address = new SignalProtocolAddress("1234-1234-1234-1234", 1);
    String addressSerialized = JsonUtil.toJson(address);
    assertNotNull(addressSerialized);
    Log.i(TAG, addressSerialized);

    SignalProtocolAddress addressDeserialized = JsonUtil.fromJson(addressSerialized, SignalProtocolAddress.class);
    assertNotNull(addressDeserialized);

    assertEquals(address, addressDeserialized);
    compareSignalProtocolAddressWithUnsafeHandle(address, addressDeserialized); // real equals test
  }

  @Test
  public void verifySignatureTest() {
    Log.i(TAG, "------------ verifySignatureTest: ------------");
    setActiveProtocolAccount(alice, ALICE_USERNAME);

    final Fingerprint fingerprintAlice = generateFingerprint(alice, bob);
    assertNotNull(fingerprintAlice);
    Log.i(TAG, "Fingerprint alice-bob: " + fingerprintAlice.getDisplayableFingerprint().getDisplayText());

    setActiveProtocolAccount(bob, BOB_USERNAME);
    final Fingerprint fingerprintBob = generateFingerprint(bob, alice);
    assertNotNull(fingerprintBob);
    Log.i(TAG, "Fingerprint bob-alice: " + fingerprintBob.getDisplayableFingerprint().getDisplayText());

    assertEquals(fingerprintAlice.getDisplayableFingerprint().getDisplayText(), fingerprintBob.getDisplayableFingerprint().getDisplayText());
  }

  @Test
  public void objectMapperIdentityKeyStoreSerializeTest() throws IOException {
    Log.i(TAG, "------------ objectMapperIdentityKeyStoreSerializeTest: ------------");
    IdentityKeyStoreImpl identityKeyStore = new IdentityKeyStoreImpl(KeyUtil.generateIdentityKeyPair(), 1);
    assertNotNull(identityKeyStore);
    String identityKeyStoreSerialized = JsonUtil.toJson(identityKeyStore);
    assertNotNull(identityKeyStoreSerialized);

    IdentityKeyStoreImpl identityKeyStoreDeserialized = JsonUtil.fromJson(identityKeyStoreSerialized, IdentityKeyStoreImpl.class);
    assertNotNull(identityKeyStoreDeserialized);

    SignalProtocolAddress address = new SignalProtocolAddress("1234-1234-1234-1234", 1);
    assertNotNull(address);
    IdentityKeyPair identityKeyChatPartner = KeyUtil.generateIdentityKeyPair();
    assertNotNull(identityKeyChatPartner);
    identityKeyStoreDeserialized.saveIdentity(address, identityKeyChatPartner.getPublicKey());
    assertEquals(identityKeyChatPartner.getPublicKey(), identityKeyStoreDeserialized.getIdentity(address));

    String identityKeyStoreWithTrustedKeySerialized = JsonUtil.toJson(identityKeyStoreDeserialized);
    assertNotNull(identityKeyStoreWithTrustedKeySerialized);

    IdentityKeyStoreImpl identityKeyStoreWithTrustedKeyDeserialized = JsonUtil.fromJson(identityKeyStoreWithTrustedKeySerialized, IdentityKeyStoreImpl.class);
    assertNotNull(identityKeyStoreWithTrustedKeyDeserialized);

    // Log.i(TAG, identityKeyStoreWithTrustedKeySerialized);
    String identityKeyStoreWithTrustedKeySerialized2 = JsonUtil.toJson(identityKeyStoreWithTrustedKeyDeserialized);
    assertNotNull(identityKeyStoreWithTrustedKeyDeserialized);
    // Log.i(TAG, identityKeyStoreWithTrustedKeySerialized2);
    // hint: equals test of identityStores is non trivial! (uncomment lines to compare strings)
  }

  @Test
  public void maxMessageLengthTest() throws IOException, UntrustedIdentityException, InvalidKeyIdException, InvalidKeyException, NoSessionException, InvalidMessageException, DuplicateMessageException, InvalidVersionException, LegacyMessageException {
    Log.i(TAG, "------------ maxMessageLengthTest: ------------");

    buildSessionWithPreKeyResponseMessage(alice, ALICE_USERNAME, bob, BOB_USERNAME);

    /* found thresholds for character limitation (3500 bytes) in threema:
       INVITE: irrelevant (no message)
       PREKEY: 250 chars (to be safe) (with array: 300 chars)
       SIGNAL/WHISPER: 350 chars (to be safe) (with array: 300 chars)
       UPDATED_SIGNED_PRE_KEY: 50 chars (to be safe) (with array: 100 chars) */

    // PRE KEY MESSAGE
    final String unencryptedMessage = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. \n" + "\n" + "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. \n" + "\n" + "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. \n" + "\n" + "Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim v";
    sendMessageAfterSessionBuild(alice, ALICE_USERNAME, bob, BOB_USERNAME, unencryptedMessage);

    // WHISPER/SIGNAL MESSAGE
    final String unencryptedMessage2 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. \n" + "\n" + "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. \n" + "\n" + "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. \n" + "\n" + "Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim v";
    sendMessageAfterSessionBuild(bob, BOB_USERNAME, alice, ALICE_USERNAME, unencryptedMessage2);

    printHashMapForAccountName(ALICE_USERNAME, sharedPreferencesStoreAlice);
    printHashMapForAccountName(BOB_USERNAME, sharedPreferencesStoreBob);
  }

  private Fingerprint generateFingerprint(Account localAccount, Account remoteAccount) {
    final IdentityKey localIdentity = localAccount.getIdentityKeyPair().getPublicKey();
    final IdentityKey remoteIdentity = remoteAccount.getIdentityKeyPair().getPublicKey();

    final int version = 2; // use UUID
    final byte[] localId = localAccount.getSignalProtocolAddress().getName().getBytes();
    final byte[] remoteId = remoteAccount.getSignalProtocolAddress().getName().getBytes();

    NumericFingerprintGenerator numericFingerprintGenerator = new NumericFingerprintGenerator(5200);

    return numericFingerprintGenerator.createFor(version, localId, localIdentity, remoteId, remoteIdentity);
  }

  private void setActiveProtocolAccount(Account account, String username) {
    assertNotNull(account);
    Log.i(TAG, "Set account to " + username);
    SignalProtocolMain.getInstance().setAccount(account);
    assertEquals(account, SignalProtocolMain.getInstance().getAccount());
  }

  private static Account initializeAccount(final String username) {
    // setting test boolean
    SignalProtocolMain.testIsRunning = true;

    Account account;

    Log.i(TAG, "Initializing account " + username);
    SignalProtocolMain.initialize(null);
    account = SignalProtocolMain.getInstance().getAccount();
    assertNotNull(account);

    putAllInformationInMapSharedPreferences(account, username);

    return account;
  }

  private static Account loadAccount(final String username) throws IOException {
    final boolean isFirstTest = sharedPreferencesStoreAlice == null || sharedPreferencesStoreBob == null;

    // setting test boolean
    SignalProtocolMain.getInstance().testIsRunning = true;

    Account account = null;

    Log.i(TAG, "Reloading account " + username);
    if (username.equals(ALICE_USERNAME)) {
      account = alice;
    } else if (username.equals(BOB_USERNAME)) {
      account = bob;
    }

    loadAllInformationInMapSharedPreferencesToAccount(account, username);

    return account;
  }

  private static void putAllInformationInMapSharedPreferences(Account account, String accountName) {
    Map<ProtocolIdentifier, String> hashMap = null;
    if (accountName.equals(ALICE_USERNAME)) {
      hashMap = sharedPreferencesStoreAlice;
    } else if (accountName.equals(BOB_USERNAME)) {
      hashMap = sharedPreferencesStoreBob;
    }

    if (hashMap == null) {
      hashMap = new HashMap<>();
    }

    hashMap.put(ProtocolIdentifier.UNIQUE_USER_ID, JsonUtil.toJson(account.getName()));
    hashMap.put(ProtocolIdentifier.PROTOCOL_STORE, JsonUtil.toJson(account.getSignalProtocolStore()));
    hashMap.put(ProtocolIdentifier.PROTOCOL_ADDRESS, JsonUtil.toJson(account.getSignalProtocolAddress()));
    hashMap.put(ProtocolIdentifier.CONTACTS, JsonUtil.toJson(account.getContactList()));
    hashMap.put(ProtocolIdentifier.DEVICE_ID, JsonUtil.toJson(account.getDeviceId()));
    hashMap.put(ProtocolIdentifier.METADATA_STORE, JsonUtil.toJson(account.getMetadataStore()));
    hashMap.put(ProtocolIdentifier.UNENCRYPTED_MESSAGES, JsonUtil.toJson(account.getUnencryptedMessages()));

    if (accountName.equals(ALICE_USERNAME)) {
      sharedPreferencesStoreAlice = hashMap;
    } else if (accountName.equals(BOB_USERNAME)) {
      sharedPreferencesStoreBob = hashMap;
    }
  }

  private static void loadAllInformationInMapSharedPreferencesToAccount(Account account, String accountName) throws IOException {
    Map<ProtocolIdentifier, String> hashMap = null;

    if (accountName.equals(ALICE_USERNAME)) {
      hashMap = sharedPreferencesStoreAlice;
    } else if (accountName.equals(BOB_USERNAME)) {
      hashMap = sharedPreferencesStoreBob;
    }

    if (hashMap == null) {
      hashMap = new HashMap<>();
    }

    final String name = (String) loadClassFromSharedPreferencesMap(hashMap, ProtocolIdentifier.UNIQUE_USER_ID);
    final SignalProtocolStoreImpl signalProtocolStore = (SignalProtocolStoreImpl) loadClassFromSharedPreferencesMap(hashMap, ProtocolIdentifier.PROTOCOL_STORE);
    final IdentityKeyPair identityKeyPair = signalProtocolStore.getIdentityKeyPair();

    final PreKeyMetadataStore metadataStore = (PreKeyMetadataStore) loadClassFromSharedPreferencesMap(hashMap, ProtocolIdentifier.METADATA_STORE);
    final SignalProtocolAddress signalProtocolAddress = (SignalProtocolAddress) loadClassFromSharedPreferencesMap(hashMap, ProtocolIdentifier.PROTOCOL_ADDRESS);

    final ArrayList<StorageMessage> unencryptedMessages = (ArrayList<StorageMessage>) loadClassFromSharedPreferencesMap(hashMap, ProtocolIdentifier.UNENCRYPTED_MESSAGES);
    final ArrayList<Contact> contactList = (ArrayList<Contact>) loadClassFromSharedPreferencesMap(hashMap, ProtocolIdentifier.CONTACTS);

    account = new Account(name, signalProtocolAddress.getDeviceId(), identityKeyPair, metadataStore, signalProtocolStore, signalProtocolAddress); // deviceId is static
    account.setUnencryptedMessages(unencryptedMessages);
    account.setContactList(contactList);

    printHashMapForAccountName(accountName, hashMap);
  }

  private static void printHashMapForAccountName(String accountName, Map<ProtocolIdentifier, String> hashMap) {
    Log.i(TAG, "################################## Printing hashmap of " + accountName + " ##################################");
    hashMap.forEach((key, value) -> Log.i(TAG, key + ": " + value));
    Log.i(TAG, "########################################### END ###############################################");
  }

  private static Object loadClassFromSharedPreferencesMap(Map<ProtocolIdentifier, String> hashMap, ProtocolIdentifier protocolIdentifier) throws IOException {
    String json = String.valueOf(hashMap.get(protocolIdentifier));
    return JsonUtil.fromJson(json, protocolIdentifier.className);
  }

  private void logSizePreKeyStore() {
    Log.i(TAG, "alice: size pre key store: " + alice.getSignalProtocolStore().getPreKeyStore().getSize());
    Log.i(TAG, "bob: size pre key store: " + bob.getSignalProtocolStore().getPreKeyStore().getSize());
  }

  private void logSizeSessionStore() {
    Log.i(TAG, "alice: size session store: " + alice.getSignalProtocolStore().getSessionStore().getSize());
    Log.i(TAG, "bob: size session store: " + bob.getSignalProtocolStore().getSessionStore().getSize());
  }

  // old way of building/sending preKeyBundle
  @Deprecated
  private void buildSessionWithPreKeyBundle(final Account sender, final String senderUsername, final Account recipient, final String recipientUsername) throws IOException, InvalidMessageException, UntrustedIdentityException, DuplicateMessageException, InvalidVersionException, InvalidKeyIdException, LegacyMessageException, InvalidKeyException, NoSessionException {
    MessageEnvelope recipientsPreKeyMessage = createPreKeyBundleMessage(recipient, recipientUsername);
    assertNotNull(recipientsPreKeyMessage);

    setActiveProtocolAccount(sender, senderUsername);
    SignalProtocolMain.decryptMessage(recipientsPreKeyMessage, recipient.getSignalProtocolAddress());

    assertTrue(sender.getSignalProtocolStore().containsSession(recipient.getSignalProtocolAddress()));
    assertEquals(3, sender.getSignalProtocolStore().loadSession(recipient.getSignalProtocolAddress()).getSessionVersion());
  }

  @Deprecated
  private MessageEnvelope createPreKeyBundleMessage(final Account account, final String username) throws IOException {
    setActiveProtocolAccount(account, username);

    MessageEnvelope messageEnvelope = SignalProtocolMain.getPreKeyResponseMessage();
    assertNotNull(messageEnvelope);

    PreKeyBundle preKeyBundle = SignalProtocolMain.getInstance().createPreKeyBundle(messageEnvelope.getPreKeyResponse());
    assertNotNull(preKeyBundle);
    assertNotNull(preKeyBundle.getSignedPreKeySignature());
    assertNotNull(preKeyBundle.getPreKey());
    assertEquals(preKeyBundle.getPreKeyId(), 1);
    assertNotNull(preKeyBundle.getSignedPreKey());
    assertNotNull(preKeyBundle.getIdentityKey());
    assertNull(messageEnvelope.getCiphertextMessage());

    assertEquals(account.getSignalProtocolStore().getLocalRegistrationId(), preKeyBundle.getRegistrationId());
    return messageEnvelope;
  }

  @Deprecated
  // sample code as template for real implementation
  private void buildSessionWithPreKeyResponseMessageTemplateImplementation(final Account sender, final String senderUsername, final Account recipient, final String recipientUsername) throws IOException, InvalidKeyIdException, InvalidKeyException, UntrustedIdentityException {
    // recipient side
    PreKeyResponse preKeyResponseMessageGeneratedByRecipient = createPreKeyResponseMessage(recipient, recipientUsername);
    assertNotNull(preKeyResponseMessageGeneratedByRecipient);

    // test serializing/deserializing
    String outgoingPreKeyResponse = JsonUtil.toJson(preKeyResponseMessageGeneratedByRecipient);
    PreKeyResponse incomingPreKeyResponse = JsonUtil.fromJson(outgoingPreKeyResponse, PreKeyResponse.class);

    // sender side
    if (incomingPreKeyResponse.getDevices() == null || incomingPreKeyResponse.getDevices().size() < 1)
      throw new IOException("Empty prekey list");

    PreKeyResponseItem device = incomingPreKeyResponse.getDevices().get(0);
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

    PreKeyBundle preKeyBundle = new PreKeyBundle(device.getRegistrationId(), device.getDeviceId(), preKeyId, preKey, signedPreKeyId, signedPreKey, signedPreKeySignature, incomingPreKeyResponse.getIdentityKey());
    assertNotNull(preKeyBundle.getSignedPreKeySignature());
    assertNotNull(preKeyBundle.getPreKey());
    assertEquals(preKeyBundle.getPreKeyId(), 1);
    assertNotNull(preKeyBundle.getSignedPreKey());
    assertNotNull(preKeyBundle.getIdentityKey());
    assertNotNull(preKeyBundle);

    SessionBuilder sessionBuilder = new SessionBuilder(sender.getSignalProtocolStore(), recipient.getSignalProtocolAddress());
    sessionBuilder.process(preKeyBundle);
    assertTrue(sender.getSignalProtocolStore().containsSession(recipient.getSignalProtocolAddress()));
    assertEquals(3, sender.getSignalProtocolStore().loadSession(recipient.getSignalProtocolAddress()).getSessionVersion());
  }

  @Deprecated
  // new approach for pre key bundle message
  private PreKeyResponse createPreKeyResponseMessage(final Account account, final String username) throws InvalidKeyIdException, InvalidKeyException {
    setActiveProtocolAccount(account, username);

    final byte[] signedPreKeySignature = Curve.calculateSignature(account.getSignalProtocolStore().getIdentityKeyPair().getPrivateKey(), account.getSignalProtocolStore().loadSignedPreKey(account.getMetadataStore().getActiveSignedPreKeyId()).getKeyPair().getPublicKey().serialize());

    final int preKeyId = 1;
    final int mDeviceId = 1;
    final PreKeyBundle preKeyBundle = new PreKeyBundle(account.getSignalProtocolStore().getLocalRegistrationId(), mDeviceId, preKeyId, account.getSignalProtocolStore().loadPreKey(preKeyId).getKeyPair().getPublicKey(), account.getMetadataStore().getActiveSignedPreKeyId(), account.getSignalProtocolStore().loadSignedPreKey(account.getMetadataStore().getActiveSignedPreKeyId()).getKeyPair().getPublicKey(), signedPreKeySignature, account.getSignalProtocolStore().getIdentityKeyPair().getPublicKey());

    List<PreKeyResponseItem> responseItems = new LinkedList<>();
    responseItems.add(new PreKeyResponseItem(preKeyBundle.getDeviceId(), preKeyBundle.getRegistrationId(), new SignedPreKeyEntity(account.getMetadataStore().getActiveSignedPreKeyId(), preKeyBundle.getSignedPreKey(), signedPreKeySignature), new PreKeyEntity(preKeyId, preKeyBundle.getPreKey())));

    return new PreKeyResponse(account.getSignalProtocolStore().getIdentityKeyPair().getPublicKey(), responseItems);
  }
}