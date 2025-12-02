package user.services;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.*;

public class E2ESession {
    private static Path saveDir = Paths.get(System.getProperty("user.home"), "java-chat-app");
    private E2ESession() {}

    public static KeyPair generateX25519KeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("X25519");
        return kpg.generateKeyPair();
    }

    public static byte[] generateSharedSecret(PrivateKey senderPrivateKey, PublicKey receiverPublicKey) throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement ka = KeyAgreement.getInstance("X25519");
        ka.init(senderPrivateKey);
        ka.doPhase(receiverPublicKey, true);
        return ka.generateSecret();
    }

    public static SecretKeySpec getSecretKeySpec(byte[] secret) {
        return new SecretKeySpec(secret, 0, 16, "AES");
    }

    public static String encrypt(String msg, SecretKey secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(128, iv));
        byte[] output = cipher.doFinal(msg.getBytes());

        return Base64.getEncoder().encodeToString(output) + "?" + Base64.getEncoder().encodeToString(iv);
    }

    public static String decrypt(String input, SecretKey secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        String[] parts = input.split("\\?");
        byte[] cipherText = Base64.getDecoder().decode(parts[0]);
        byte[] iv = Base64.getDecoder().decode(parts[1]);
        
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(128, iv));
        byte[] msg = cipher.doFinal(cipherText);
        return new String(msg);
    }

    public static void saveKeyToFile(String key, String username, String fileName) throws IOException {
        Path userDir = Paths.get(saveDir.toString(), username);
        Files.createDirectories(userDir);
        try (FileWriter fw = new FileWriter(Paths.get(userDir.toString(), fileName).toString())) {
            fw.write(key);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static PrivateKey loadPrivateKey(String username) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Path userDir = Paths.get(saveDir.toString(), username);
        FileReader reader = new FileReader(Paths.get(userDir.toString(), "private_key.txt").toString());
        
        byte[] key = Base64.getDecoder().decode(reader.readAllAsString());
        PKCS8EncodedKeySpec PKCS8key = new PKCS8EncodedKeySpec(key);
        KeyFactory kf = KeyFactory.getInstance("X25519");
        PrivateKey privateKey = kf.generatePrivate(PKCS8key);
        
        reader.close();
        return privateKey;
    }

    public static PublicKey loadPublicKey(String input) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] key = Base64.getDecoder().decode(input);
        X509EncodedKeySpec X509key = new X509EncodedKeySpec(key);
        KeyFactory kf = KeyFactory.getInstance("X25519");
        PublicKey publicKey = kf.generatePublic(X509key);
        return publicKey;
    }

    public static byte[] getSharedSecret(String username, int recipentId, String publicKey) {
        try {
            Path userDir = Paths.get(saveDir.toString(), username);
            String fileName = recipentId + "_shared_key.txt";
            String path = Paths.get(userDir.toString(), fileName).toString();
            if (new File(path).isFile()) {
                FileReader reader = new FileReader(path);
                byte[] key = Base64.getDecoder().decode(reader.readAllAsString());
                reader.close();
                return key;
            }
            else {
                PrivateKey privKey = loadPrivateKey(username);
                PublicKey pubKey = loadPublicKey(publicKey);
                byte[] sharedKey = generateSharedSecret(privKey, pubKey);
                saveKeyToFile(Base64.getEncoder().encodeToString(sharedKey), username, fileName); 
                return sharedKey;
            }
        } catch (Exception e) {
            System.out.println();
        }
        return null;
    }
}
