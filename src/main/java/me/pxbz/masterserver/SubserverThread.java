package me.pxbz.masterserver;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class SubserverThread extends Thread {
    private final Socket socket;
    private final CentralServer server;
    private PrintWriter writer;
    private final Logger logger;
    private boolean closeThread = false;
    private String subserverName;

    public SubserverThread(Socket socket, CentralServer server, Logger logger) {
        this.socket = socket;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void run() {
        try {


            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            while (!closeThread) {

                String message = reader.readLine();
                handleMessage(message);
                logger.info((getSubserverName() == null ? "Unknown subserver" : getSubserverName())
                        + "'s incoming message: " + message);
                if (message == null) break;
            }

            socket.close();
            server.removeSubserver(this);
        } catch (IOException e) {e.printStackTrace();}
    }

    // Returns if the subserver should close as a result of the message parameter
    void handleMessage(String message) {
        if (message == null);
        else if (message.startsWith("setname:")) setSubserverName(message.substring(8));
        else if (message.startsWith("broadcastall:")) server.broadcastMessage(message.substring(13));
        else if (message.startsWith("globalchat:")) server.broadcast(message, this);
        else if (message.equals("disconnect")) closeConnection();
        else closeConnection();
    }

    void closeConnection() {
        try {
            sendMessage("disconnect");
            closeThread = true;
            socket.close();
            server.removeSubserver(this);
        } catch (IOException e) {logger.error(Arrays.toString(e.getStackTrace()));}
    }

    void sendMessage(String message) {
        writer.println(message);
    }

    String getSubserverName() {
        return this.subserverName;
    }

    void setSubserverName(String name) {
        subserverName = name;
    }
}
