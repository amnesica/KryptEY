package com.amnesica.kryptey.inputmethod.signalprotocol.util;

import android.util.Log;

import com.amnesica.kryptey.inputmethod.signalprotocol.SenderKey;
import com.amnesica.kryptey.inputmethod.signalprotocol.SignalProtocolMain;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.Contact;
import com.amnesica.kryptey.inputmethod.signalprotocol.chat.StorageMessage;
import com.amnesica.kryptey.inputmethod.signalprotocol.exceptions.MalformedResponseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.protobuf.ByteString;

import org.signal.libsignal.protocol.IdentityKey;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.InvalidKeyException;
import org.signal.libsignal.protocol.SignalProtocolAddress;

import java.io.IOException;
import java.util.ArrayList;

public class JsonUtil {

  private static final String TAG = JsonUtil.class.getSimpleName();

  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(IdentityKeyPair.class, new IdentityKeyPairSerializer());
    module.addDeserializer(IdentityKeyPair.class, new IdentityKeyPairDeserializer());
    module.addSerializer(IdentityKey.class, new IdentityKeySerializer());
    module.addDeserializer(IdentityKey.class, new IdentityKeyDeserializer());
    module.addSerializer(SignalProtocolAddress.class, new SignalProtocolAddressSerializer());
    module.addDeserializer(SignalProtocolAddress.class, new SignalProtocolAddressDeserializer());
    module.addKeySerializer(SenderKey.class, new SenderKeySerializer());
    module.addKeyDeserializer(SenderKey.class, new SenderKeyDeserializer());
    objectMapper.registerModule(module);
    objectMapper.findAndRegisterModules(); // for Instant type
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // ignore null values
    // objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // for pretty json
  }

  public static String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      if (!SignalProtocolMain.testIsRunning) Log.w(TAG, e);
      e.printStackTrace();
      return null;
    }
  }

  public static ByteString toJsonByteString(Object object) {
    return ByteString.copyFrom(toJson(object).getBytes());
  }

  public static <T> T fromJson(String json, Class<T> clazz)
      throws IOException {
    return objectMapper.readValue(json, clazz);
  }

  public static <T> T fromJson(String json, TypeReference<T> typeRef)
      throws IOException {
    return objectMapper.readValue(json, typeRef);
  }

  public static <T> T fromJsonResponse(String json, TypeReference<T> typeRef)
      throws MalformedResponseException {
    try {
      return JsonUtil.fromJson(json, typeRef);
    } catch (IOException e) {
      throw new MalformedResponseException("Unable to parse entity", e);
    }
  }

  public static <T> T fromJsonResponse(String body, Class<T> clazz)
      throws MalformedResponseException {
    try {
      return JsonUtil.fromJson(body, clazz);
    } catch (IOException e) {
      throw new MalformedResponseException("Unable to parse entity", e);
    }
  }

  public static ArrayList<Contact> convertContactsList(ArrayList<Contact> classFromSharedPreferences) {
    return objectMapper.convertValue(classFromSharedPreferences, new TypeReference<ArrayList<Contact>>() {
    });
  }

  public static ArrayList<StorageMessage> convertUnencryptedMessagesList(ArrayList<StorageMessage> classFromSharedPreferences) {
    return objectMapper.convertValue(classFromSharedPreferences, new TypeReference<ArrayList<StorageMessage>>() {
    });
  }

  public static class IdentityKeySerializer extends JsonSerializer<IdentityKey> {
    @Override
    public void serialize(IdentityKey value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      Log.d(TAG, "IdentityKeySerializer used");
      gen.writeStartObject();
      gen.writeStringField("publicKey", Base64.encodeBytesWithoutPadding(value.getPublicKey().serialize()));
      gen.writeEndObject();
    }
  }

  public static class IdentityKeyDeserializer extends JsonDeserializer<IdentityKey> {
    @Override
    public IdentityKey deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      try {
        Log.d(TAG, "IdentityKeyDeserializer used");
        JsonNode node = p.getCodec().readTree(p);
        return new IdentityKey(Base64.decodeWithoutPadding(node.get("publicKey").asText()), 0);
      } catch (InvalidKeyException e) {
        throw new IOException(e);
      }
    }
  }

  public static class IdentityKeyPairSerializer extends JsonSerializer<IdentityKeyPair> {
    @Override
    public void serialize(IdentityKeyPair value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      Log.d(TAG, "IdentityKeyPairSerializer used");
      gen.writeString(Base64.encodeBytesWithoutPadding(value.serialize()));
    }
  }

  public static class IdentityKeyPairDeserializer extends JsonDeserializer<IdentityKeyPair> {
    @Override
    public IdentityKeyPair deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      Log.d(TAG, "IdentityKeyPairDeserializer used");
      return new IdentityKeyPair(Base64.decodeWithoutPadding(p.getValueAsString()));
    }
  }

  public static class SignalProtocolAddressSerializer extends JsonSerializer<SignalProtocolAddress> {
    @Override
    public void serialize(SignalProtocolAddress value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      Log.d(TAG, "SignalProtocolAddressKeySerializer used");
      gen.writeStartObject();
      gen.writeStringField("name", value.getName());
      gen.writeNumberField("deviceId", value.getDeviceId());
      gen.writeEndObject();
    }
  }

  public static class SignalProtocolAddressDeserializer extends JsonDeserializer<SignalProtocolAddress> {
    @Override
    public SignalProtocolAddress deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      Log.d(TAG, "SignalProtocolAddressDeserializer used");
      JsonNode node = p.getCodec().readTree(p);
      String name = node.get("name").asText();
      int deviceId = (Integer) ((IntNode) node.get("deviceId")).numberValue();
      return new SignalProtocolAddress(name, deviceId);
    }
  }

  public static class SenderKeyDeserializer extends KeyDeserializer {
    @Override
    public SenderKey deserializeKey(String key, DeserializationContext ctxt) {
      Log.d(TAG, "SenderKeyDeserializer used");
      String[] allThree = key.split("\\.");
      String name = allThree[0];
      int deviceId = Integer.parseInt(allThree[1]);
      String distributionId = allThree[2];
      return new SenderKey(name, deviceId, distributionId);
    }
  }

  public static class SenderKeySerializer extends JsonSerializer<SenderKey> {
    @Override
    public void serialize(SenderKey value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      Log.d(TAG, "SenderKeySerializer used");
      gen.writeStartObject();
      gen.writeStringField("name", value.getSignalProtocolAddressName());
      gen.writeNumberField("deviceId", value.getDeviceId());
      gen.writeStringField("distributionId", value.getDistributionId());
      gen.writeEndObject();
    }
  }
}
