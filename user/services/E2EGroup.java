package user.services;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.crypto.KeyGenerator;

public class E2EGroup {
    private static Path saveDir = Paths.get(System.getProperty("user.home"), "java-chat-app");
    private E2EGroup() {}
    public static void saveGroupKey(String key, String username, int groupId) throws IOException {
        String fileName = groupId + "_group_key.txt";
        E2ESession.saveKeyToFile(key, username, fileName);
    }

    public static boolean isFirstMsg(String username, int groupId) {
        Path userDir = Paths.get(saveDir.toString(), username);
        String fileName = groupId + "_group_key.txt";
        String path = Paths.get(userDir.toString(), fileName).toString();
        if (new File(path).isFile()) {
            return false;
        }
        return true;
    }

    public static byte[] genNewGroupKey(String username, int groupId) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            byte[] sk = keyGen.generateKey().getEncoded();
            saveGroupKey(Base64.getEncoder().encodeToString(sk), username, groupId);
            return sk;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getGroupKey(String username, int groupId) {
        try {
            Path userDir = Paths.get(saveDir.toString(), username);
            String fileName = groupId + "_group_key.txt";
            String path = Paths.get(userDir.toString(), fileName).toString();
            FileReader reader = new FileReader(path);
            byte[] key = Base64.getDecoder().decode(reader.readAllAsString());
            reader.close();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encryptGroupKey(byte[] groupKey, byte[] secret) {
        try {
            String msg = Base64.getEncoder().encodeToString(groupKey);
            return E2ESession.encrypt(msg, E2ESession.getSecretKeySpec(secret));
        } catch (Exception e) {
            System.out.println(e);
        }
        return "";
    }

    public static byte[] decryptGroupKey(String msg, byte[] secret) {
        try {
            String groupKey = E2ESession.decrypt(msg, E2ESession.getSecretKeySpec(secret));
            return Base64.getDecoder().decode(groupKey);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
