package com.abevriens;

import com.abevriens.jda.DiscordIdEnum;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class DiscordIdCodec implements Codec<DiscordIdEnum> {
    @Override
    public DiscordIdEnum decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return DiscordIdEnum.valueOf(bsonReader.readString());
    }

    @Override
    public void encode(BsonWriter bsonWriter, DiscordIdEnum discordIdEnum, EncoderContext encoderContext) {
        if(discordIdEnum != null) {
            bsonWriter.writeString(discordIdEnum.toString());
        }
    }

    @Override
    public Class<DiscordIdEnum> getEncoderClass() {
        return DiscordIdEnum.class;
    }
}
