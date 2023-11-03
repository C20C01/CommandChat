package io.github.c20c01.commandchat.pkg;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Decoder {

    @Nullable
    public static IPackage decode(byte[] data) {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        DataInputStream dataStream = new DataInputStream(stream);
        IPackage pkg;
        try {
            byte id = dataStream.readByte();
            switch (id) {
                case IPackage.ID_MESSAGE -> pkg = PMessage.decode(dataStream);
                case IPackage.ID_USER_LIST -> pkg = PUserList.decode(dataStream);
                default -> pkg = null;
            }
            return pkg;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
