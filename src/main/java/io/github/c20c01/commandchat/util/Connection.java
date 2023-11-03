package io.github.c20c01.commandchat.util;


import io.github.c20c01.commandchat.pkg.Decoder;
import io.github.c20c01.commandchat.pkg.IPackage;
import io.github.c20c01.commandchat.util.encryption.AESTool;
import io.github.c20c01.commandchat.util.encryption.RSATool;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.security.PrivateKey;
import java.util.Arrays;

public class Connection {
    private static final int BUFFER_SIZE = 2048;
    private final ByteBuffer RECEIVE_BUFFER = ByteBuffer.allocate(BUFFER_SIZE);
    private final SocketChannel SOCKET_CHANNEL;
    private final InetSocketAddress REMOTE_ADDRESS;
    private final AESTool AES_TOOL;

    public Connection(SocketChannel socketChannel, SecretKey secretKey) {
        SOCKET_CHANNEL = socketChannel;
        REMOTE_ADDRESS = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
        AES_TOOL = new AESTool(secretKey);
    }

    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return REMOTE_ADDRESS;
    }

    public void disconnect() {
        try {
            if (SOCKET_CHANNEL.isConnected()) {
                System.out.println("Disconnecting with " + SOCKET_CHANNEL.getRemoteAddress());
                SOCKET_CHANNEL.close();
            }
            AES_TOOL.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void send(byte[] data) {
        try {
            byte[] encrypted = AES_TOOL.encrypt(data);
            if (encrypted.length > BUFFER_SIZE) {
                throw new RuntimeException("Data too large");
            }
            SOCKET_CHANNEL.write(ByteBuffer.wrap(encrypted));
        } catch (IOException e) {
            disconnect();
        }
    }

    @Nullable
    public IPackage receive() {
        try {
            int len = SOCKET_CHANNEL.read(RECEIVE_BUFFER);
            if (len == -1) {
                disconnect();
                return null;
            }
            // remove empty bytes, or else the decryption will fail
            byte[] encrypted = Arrays.copyOf(RECEIVE_BUFFER.array(), len);
            RECEIVE_BUFFER.clear();
            return Decoder.decode(AES_TOOL.decrypt(encrypted));
        } catch (EOFException | ClosedByInterruptException e) {
            return null;
        } catch (IOException e) {
            disconnect();
            return null;
        }
    }

    public static class BuilderOnClient {
        private final SocketChannel SOCKET_CHANNEL;
        private final SecretKey SECRET_KEY;

        public BuilderOnClient(InetSocketAddress address, String publicKey) {
            try {
                SOCKET_CHANNEL = SocketChannel.open();
                SOCKET_CHANNEL.connect(address);
                SECRET_KEY = AESTool.generateSecretKey();
                sendSecretKey(publicKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void sendSecretKey(String publicKey) throws IOException {
            byte[] encryptedSecretKey = new RSATool().encrypt(SECRET_KEY.getEncoded(), RSATool.getPublicKey(publicKey));
            SOCKET_CHANNEL.write(ByteBuffer.wrap(encryptedSecretKey));
        }

        public Connection build() {
            return new Connection(SOCKET_CHANNEL, SECRET_KEY);
        }
    }

    public static class BuilderOnServer {
        private final SocketChannel SOCKET_CHANNEL;
        private final SecretKey SECRET_KEY;

        public BuilderOnServer(SocketChannel socketChannel, PrivateKey privateKey) {
            SOCKET_CHANNEL = socketChannel;
            SECRET_KEY = receiveSecretKey(privateKey);
        }

        private SecretKey receiveSecretKey(PrivateKey privateKey) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(256);
                SOCKET_CHANNEL.read(buffer);
                byte[] decryptedSecretKey = new RSATool().decrypt(buffer.array(), privateKey);
                return AESTool.getSecretKey(decryptedSecretKey);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Connection build() {
            return new Connection(SOCKET_CHANNEL, SECRET_KEY);
        }
    }
}
