package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

public class ClientHandler implements Runnable {
    private static String storeFileName = "stored_gks.txt";
    private static ArrayList<ClientHandler> handlers = new ArrayList<>();
    private Socket socket;
    private int userId;
    private int groupId = -1;
    private BufferedWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            String line = reader.readLine();
            if (line == null) {
                closeHanlder();
                return;
            }
            JSONObject initInfo = new JSONObject(line);
            userId = initInfo.getInt("user_id");
            if (initInfo.has("group_id")) {
                groupId = initInfo.getInt("group_id");

                if (new File(storeFileName).isFile()) {
                    JSONObject storedGks = loadStoredGK();
                    String keyMap = groupId + "_" + userId; 
                    if (storedGks.has(keyMap)) {
                        JSONObject inner = storedGks.getJSONObject(keyMap);
                        String gk = inner.getString("gk");
                        int sender = inner.getInt("sender");
                        JSONObject res = new JSONObject();
                        res.put("gk", gk);
                        res.put("group_id", groupId);
                        res.put("sender", sender);
                        this.writer.write(res.toString());
                        this.writer.newLine();
                        this.writer.flush();

                        storedGks.remove(keyMap);
                        saveGk(storedGks);
                    }
                } 
            }
            handlers.add(this);
            broadCastStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                String line = reader.readLine();
                if (line == null) {
                    closeHanlder();
                    break;
                }
                JSONObject msg = new JSONObject(line);
                if (msg.has("gk")) {
                    forwardGroupKey(msg);
                }
                else if (msg.getString("type").equals("single")) {
                    forwardMsg(msg);
                }
                else {
                    broadCastMsg(msg);
                }
                
            } catch (Exception e) {
                closeHanlder();
                break;
            }
        }
    }

    private void forwardMsg(JSONObject msg) {
        for (var handler: handlers) {
            try {
                int receiverId = msg.getInt("receiver_id");
                if (handler.userId == receiverId) {
                    JSONObject res = new JSONObject();
                    res.put("content", msg.getString("content"));
                    res.put("sender_id", msg.getInt("sender_id"));
                    res.put("msg_id", msg.getInt("msgId"));
                    handler.writer.write(res.toString());
                    handler.writer.newLine();
                    handler.writer.flush();
                }
            } catch (Exception e) {
                closeHanlder();
            }
        }
    }

    private void forwardGroupKey(JSONObject msg) {
        try {
            boolean found = false;
            int groupId = msg.getInt("group_id");
            int memId = msg.getInt("mem_id");
            int sender = msg.getInt("sender_id");
            String gk = msg.getString("gk");
            for (var handler: handlers) {
                if (handler.groupId == groupId && handler.userId == memId) {
                    JSONObject res = new JSONObject();
                    res.put("gk", gk);
                    res.put("group_id", groupId);
                    res.put("sender_id", sender);
                    handler.writer.write(res.toString());
                    handler.writer.newLine();
                    handler.writer.flush();
                    found = true;
                    break;
                }
            }

            if (!found) {
                JSONObject storedGks = null;
                if (new File(storeFileName).isFile()) {
                    storedGks = loadStoredGK();
                }
                else {
                    storedGks = new JSONObject();
                }
                JSONObject inner = new JSONObject();
                inner.put("gk", gk);
                inner.put("sender", sender);
                storedGks.put(groupId + "_" + memId, inner);
                saveGk(storedGks);
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeHanlder();
        }
    }

    private void broadCastMsg(JSONObject msg) {
        for (var handler: handlers) {
            try {
                int groupId = msg.getInt("receiver_id");
                if (handler.groupId == groupId && handler.userId != msg.getInt("sender_id")) {
                    JSONObject res = new JSONObject();
                    res.put("content", msg.getString("content"));
                    res.put("msg_id", msg.getInt("msgId"));
                    res.put("group_id", groupId);
                    res.put("sender_name", msg.getString("username"));
                    handler.writer.write(res.toString());
                    handler.writer.newLine();
                    handler.writer.flush();
                }
            } catch (Exception e) {
                closeHanlder();
            }
        }
    }

    private void broadCastStatus() {
        for (var handler: handlers) {
            try {
                if (handler.userId != this.userId) {
                    JSONObject statusBoardCast = new JSONObject();
                    statusBoardCast.put("id", this.userId);
                    statusBoardCast.put("status", "reload status");
                    handler.writer.write(statusBoardCast.toString());
                    handler.writer.newLine();
                    handler.writer.flush();
                }
            } catch (Exception e) {
                closeHanlder();
            }
        }
    }

    private void closeHanlder() {
        handlers.remove(this);
        broadCastStatus();
        try {
            if (socket != null) {
                socket.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private JSONObject loadStoredGK() {
        try (FileReader fr = new FileReader(storeFileName)) {
            JSONObject storedGk = new JSONObject(fr.readAllAsString());
            return storedGk;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private void saveGk(JSONObject gk) {
        try (FileWriter fw = new FileWriter(storeFileName)) {
            fw.write(gk.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
