package com.zimpel.mtalk.message;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class CommandMessageCodec implements MessageCodec<Command, Command> {

    @Override
    public void encodeToWire(Buffer buffer, Command command) {
        var jsonToEncode = new JsonObject();
        jsonToEncode.put("code", command.getCode());
        jsonToEncode.put("description", command.getDescription());
        jsonToEncode.put("mid", command.getMid());
        var jsonString = jsonToEncode.encode();
        var length = jsonString.getBytes().length;
        buffer.appendInt(length);
        buffer.appendString(jsonString);
    }

    @Override
    public Command decodeFromWire(int pos, Buffer buffer) {
        var position = pos;
        var length = buffer.getInt(position);
        var jsonString = buffer.getString(position += 4, position + length);
        var jsonContent = new JsonObject(jsonString);
        return new Command(jsonContent.getInteger("code"), jsonContent.getString("description"), jsonContent.getString("mid"));
    }

    @Override
    public Command transform(Command command) {
        return command;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
