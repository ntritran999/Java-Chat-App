package server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(4321);) {
            while (!server.isClosed()) {
                try {
                    Socket socket = server.accept();

                    Thread thread = new Thread(new ClientHandler(socket));
                    thread.start();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }    
}
