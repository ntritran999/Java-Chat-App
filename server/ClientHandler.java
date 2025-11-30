package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONObject;

public class ClientHandler implements Runnable {
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
                if (msg.getString("type").equals("single")) {
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
}
