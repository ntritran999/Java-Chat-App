package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONObject;

public class ChatClient {
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Thread listenThread;
    private MessageListener msgListener;
    private MessageHandler msgHandler;
    private int userId = -1, receiverId = -1;
    private String msgType = "";

    public interface MessageHandler {
        public void handleMsg(JSONObject msg);    
    }

    class MessageListener implements Runnable {
        private volatile boolean running = true;
        
        public void terminate() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    JSONObject msg = new JSONObject(reader.readLine());
                    if (msgHandler != null)
                        msgHandler.handleMsg(msg);
                } catch (Exception e) {
                    System.out.println(e);
                    closeClient();
                    break;
                }
            }
        }
        
    }
    public ChatClient() {
        try {
            socket = new Socket("localhost", 4321);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            listenThread = null;
            msgListener = null;
        } catch (Exception e) {
            closeClient();
        }
    }

    public void initClient(int userId, int receiverId, String msgType) {
        this.userId = userId;
        this.receiverId = receiverId;
        this.msgType = msgType;
        try {
            writer.write(String.valueOf(this.userId));
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            closeClient();
        }
    }

    public boolean isInitialized() {
        return userId > 0 && receiverId > 0 && !msgType.isEmpty();
    }

    public void sendMessage(String msg, int msgId) {
        try {
            JSONObject data = new JSONObject();
            data.put("content", msg);
            data.put("sender_id", userId);
            data.put("receiver_id", receiverId);
            data.put("type", msgType);
            data.put("msgId", msgId);

            writer.write(data.toString());
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            closeClient();
        }
    }

    public void listen() {
        System.out.println("Listening...");
        msgListener = new MessageListener();
        listenThread = new Thread(msgListener);
        listenThread.start();
    }

    public void disconnectClient() {
        closeClient();
        if (listenThread != null) {
            msgListener.terminate();
            try {
                listenThread.join();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void closeClient() {
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
    
    public void addMsgHandler(MessageHandler handler) {
        msgHandler = handler;
    }

    public int getReceiver() {
        return receiverId;
    }

    public String getMsgType() {
        return msgType;
    }
}
