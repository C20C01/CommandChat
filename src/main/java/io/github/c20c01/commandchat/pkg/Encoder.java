package io.github.c20c01.commandchat.pkg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Encoder {
    private static final ByteArrayOutputStream BOS = new ByteArrayOutputStream();
    private static final DataOutputStream DOS = new DataOutputStream(BOS);

    public static byte[] encode(IPackage pkg) {
        try {
            DOS.writeByte(pkg.id());
            pkg.encode(DOS);
            byte[] data = BOS.toByteArray();
            BOS.reset();
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
