# KryptEY

KryptEY was created by [mellitopia](https://github.com/mellitopia)
and [amnesica](https://github.com/amnesica).

We implemented a stand alone android keyboard, KryptEY, which enables E2EE encryption of single
messages on any Android messenger. It is based on
the [Simple Keyboard](https://github.com/rkkr/simple-keyboard). The Android version of
the [Signal Protocol](https://mvnrepository.com/artifact/org.signal/libsignal-android) is used for
the E2EE functionality.

The keyboard provides the functionality to encrypt and decrypt messages, is independent of a
messenger app and does not require a server for the key exchange. The keyboard includes a separate
text field for entering the message that is to be encrypted. After selecting the receiver, the
message is sent to the text field of the used messenger and then sent to the chat partner as usual.
The chat partner copies the message to the clipboard, the application recognises the KryptEY message
as well as the sender and offers the option to decrypt the ciphertext message. The decrypted message
is displayed in the KryptEY text field and saved in the message history. Further, chat partners can
be created and deleted through the application and their security number can be verified by
comparing it on both end devices. Encrypted and decrypted messages are stored in a message history
for later viewing and there is a Q&A section that helps with questions about the keyboard and its
functionalities.

The elliptic curve X25519 with SHA-512 is used in the X3DH Key Agreement Protocol from the applied
Signal library. The hash function SHA-256 is used for the various chains and AES-256 with CBC (
Pkcs#7) is used for the encryption of the messages. SHA-512 is also used to generate the
fingerprint, the representation of the public key used for encryption.

## Initialization

After installing the app, the Signal Protocol is initialised on the device. For this purpose,
a `SignalProtocolAddress` consisting of a randomised UUID and an arbitrary device id is created.
Further, an identity key, two one-time prekeys and a signed prekey are created. From this
information, various stores are created to man- age the protocol: the `IdentityKeyStore`
, `PreKeyMetadataStore`, `PreKeyStore`, `SenderKeyStore`, `SessionStore`, `SignalProtcolStore`, and
the `SignedPreKeyStore`. All protocol information is stored serialised in the application’s
SharedPreferences. The Jackson library is used for this purpose. All the information together forms
an account on the device, with the `SignalProtocolAddress` identifying the user.

In addition, there are four different message types in KryptEY for exchanging keys and ciphertext
messages between chat partners.

1. `PreKeyResponse`: to send the `PreKeyBundle` (Invite message)
2. `PreKeySignalMessage`: to send a ciphertext and `PreKeyBundle` after establishing the session on
   one’s side.
3. `SignalMessage`: to send a ciphertext
4. `PreKeyResponse`+`SignalMessage`: to send a ciphertext with new `PreKeyBundle` information to
   update the session

## Session Establishment and Key Management

To exchange messages, a session must first be initialised on both end devices. If Bob wants to
communicate with Alice, Bob needs Alice’s `PreKeyBundle` to establish the session on his side. Alice
sends this via an invite message, which contains her `PreKeyBundle` data (`PreKeyResponse`). This
allows Bob to add Alice as a contact within the keyboard and establish a session on his side. Bob
can now send an initial encrypted message to Alice. Since Alice has not yet initialised a session
with Bob, this message is a `PreKeySignalMessage` and contains additional information besides the
actual ciphertext message so that Alice can establish a session with Bob. After Alice has also added
Bob as a contact and created a session, she can now decrypt the message Bob sent. At this point,
both parties have established a session and can send messages to each other. These messages
are `SignalMessages`.

The one-time prekeys used to establish the sessions are renewed after each use, while the identity
key is never renewed. After 30 days the signed prekeys are rotated. The old signed prekey is deleted
after 2 days. After such rotation, an updated `PreKeyBundle` is sent together with an encrypted
message with which the receiver, e.g. Bob, can update his session with Alice (`PreKeyResponse`
+`SignalMessage`). This combination of messages eliminates the need for a separate reading of the
renewed KeyBundle information. For the user, the message appears like a normal `SignalMessage`. The
session management with the manually created `PreKeyResponse` is necessary, because no server is
used for the exchange of the `PreKeyBundles`.

In addition to the `PreKeySignalMessage` and `SignalMessage`, which are also used by the Signal app,
KryptEY uses the `PreKeyResponse` and `PreKeyResponse`+`SignalMessage`. As previously mentioned,
these are necessary to guarantee the Signal Protocol without the use of a server. Further, in the
Signal app 100 one-time prekeys are created, while in KryptEY only 2 one-time prekeys are created.
These one-time prekeys are replaced after each use, which eliminates the need for time-consuming key
management of 100 one-time prekeys. Also, unlike Signal, KryptEY does not use telephone numbers to
identify users. Instead, randomised UUIDs are used. This could contribute to an increase of the
privacy of the users, since no telephone numbers are used to identify the users.tween chat partners.

## MessageEnvelope and Message Encoding

To send a message, all information is collected in a `MessageEnvelope` and then sent as plain JSON
or hidden in a decoy message. Depending on the message type, this envelope contains
the `PreKeyResponse`, the `CipherTextMessage` (`PreKeySignalMessage` or `SignalMessage`) as a byte
array, the type of the `CipherTextMessage` (`PreKeySignalMessage` or `SignalMessage`), a timestamp
and the `SignalProtocolAddress`.

There are two different encoding modes in KryptEY, raw mode and fairytale mode. Messages can be sent
as a JSON array (raw mode) or hidden in a decoy message (fairytale mode) to make the conversation
look inconspicuous. In the latter, the encrypted message is hidden in invisible, non-printable
Unicode characters. To keep the message size as small as possible, the JSON is minified, i.e. all
spaces and paragraphs are removed and the key values of the JSON are replaced by abbreviated key
values, e.g. ”preKeyResponse” becomes ”pR”. After that, the string is compressed with GZIP and
converted into a binary string. When converting to invisible Unicode characters, 4 bits are always
mapped to an invisible Unicode character like U+200C (ZERO WIDTH NON-JOINER). There are 16 invisible
Unicode characters to choose from, covering all combinations from 0000-1111. The invisible Unicode
string is then placed after an arbitrary sentence from one of the two available
fairytales [Cinderella](https://www.cs.cmu.edu/∼spok/grimmtmp/016.txt)
and [Rapunzel](https://www.cs.cmu.edu/∼spok/grimmtmp/009.txt) and can be sent to the application.
After receiving, the invisible Unicode characters are extracted from the message, converted to a
binary string, decompressed, and deminified. Then the message can be read by the app. The invisible
characters are included in the transmitted messages, which can cause problems in some messengers,
unfortunately (see Limitations in the README).

## Additional Information

The keyboard only needs the ”VIBRATE” permission to enable vibration after key press. Unlike
the [Android Open Source Project keyboard](https://android.googlesource.com/platform/packages/inputmethods/LatinIME/+/refs/heads/master/java/AndroidManifest.xml)
, the application does not require any sensitive permissions such as access to external storage or
contacts. Internet access is also not needed. At least Android 8.0 (API 26) is required and the
application has been licensed with GPL-3.0.
