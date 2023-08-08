package me.pxbz.masterserver;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class CentralServer extends Thread {
    private final int port;
    private final Logger logger;
    private final Set<SubserverThread> subservers = new HashSet<>();

    public CentralServer(int port, Logger logger) {
        this.port = port;
        this.logger = logger;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getLocalHost())) {
            System.out.println(serverSocket.getInetAddress());


            // Accept new connections infinitely
            // Create a new thread for each connection

            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("Sub-server connected");

                SubserverThread subserver = new SubserverThread(socket, this, logger);
                addSubserver(subserver);
                subserver.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addSubserver(SubserverThread subserver) {
        subservers.add(subserver);
    }

    void removeSubserver(SubserverThread subserver) {
        subservers.remove(subserver);
    }

    void broadcast(String message, SubserverThread exclude) {
        logger.info(message);
        for (SubserverThread subserver : subservers) {
            if (subserver == exclude) continue;
            subserver.sendMessage(message);
        }
    }

    void broadcastMessage(String message) {
        logger.info(message);
        for (SubserverThread subserver : subservers) {
            subserver.sendMessage("broadcast:" + message);
        }
    }
}
