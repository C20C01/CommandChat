package io.github.c20c01.commandchat.util.encryption;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Encryption {
    private final ByteArrayOutputStream BOS;

    public Encryption() {
        BOS = new ByteArrayOutputStream();
    }

    protected byte[] handle(byte[] data, Cipher cipher) {
        try {
            CipherOutputStream stream = new CipherOutputStream(BOS, cipher);
            stream.write(data);
            stream.close();
            byte[] encrypted = BOS.toByteArray();
            BOS.reset();
            return encrypted;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            BOS.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
