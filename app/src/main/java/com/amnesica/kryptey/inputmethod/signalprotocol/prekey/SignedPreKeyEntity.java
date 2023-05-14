package com.amnesica.kryptey.inputmethod.signalprotocol.prekey;

import com.amnesica.kryptey.inputmethod.signalprotocol.util.Base64;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.signal.libsignal.protocol.ecc.ECPublicKey;

import java.io.IOException;
import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class SignedPreKeyEntity extends PreKeyEntity {

  @JsonProperty
  @JsonSerialize(using = ByteArraySerializer.class)
  @JsonDeserialize(using = ByteArrayDeserializer.class)
  private byte[] signature;

  public SignedPreKeyEntity() {
  }

  public SignedPreKeyEntity(int keyId, ECPublicKey publicKey, byte[] signature) {
    super(keyId, publicKey);
    this.signature = signature;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SignedPreKeyEntity that = (SignedPreKeyEntity) o;
    return Arrays.equals(signature, that.signature);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(signature);
    return result;
  }

  public byte[] getSignature() {
    return signature;
  }

  private static class ByteArraySerializer extends JsonSerializer<byte[]> {
    @Override
    public void serialize(byte[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      gen.writeString(Base64.encodeBytesWithoutPadding(value));
    }
  }

  private static class ByteArrayDeserializer extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return Base64.decodeWithoutPadding(p.getValueAsString());
    }
  }
}
