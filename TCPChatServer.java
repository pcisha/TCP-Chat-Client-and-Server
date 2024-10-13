package com.programs.www;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/*
@author: prachi.shah
@date: 8-27-2024
 */
// TCPChatServer class to manage multiple clients
public class TCPChatServer {
    private static final int PORT = 7777;
    private static final Set<TCPChatClient> clients = new HashSet<>();
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        System.out.printf(LocalDateTime.now() + ": Chat server started on port %s...", PORT);

        try {
            serverSocket = new ServerSocket(PORT);
            Runtime.getRuntime().addShutdownHook(new Thread(TCPChatServer::shutdown));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(LocalDateTime.now() + ": New client connected at: " + clientSocket.getInetAddress());

                // Create and start a new TCPChatClient thread for the new client
                TCPChatClient client = new TCPChatClient(clientSocket);
                synchronized (clients) {
                    clients.add(client);
                }
                new Thread(client).start();
            }
        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
        }
    }

    // Broadcast a message to all connected clients
    public static synchronized void broadcastMessage(String message) {
        for (TCPChatClient client : clients) {
            client.sendMessage(message);
        }
    }

    // Add a client to the list
    public static synchronized void addClient(TCPChatClient client) {
        clients.add(client);
    }

    // Remove a client from the list
    public static synchronized void removeClient(TCPChatClient client) {
        clients.remove(client);
    }

    // Shutdown server and close all connections
    private static void shutdown() {
        System.out.println(LocalDateTime.now() + ": Shutting down server...");
        try {
            // Close server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            // Close all client connections
            synchronized (clients) {
                for (TCPChatClient client : clients) {
                    client.closeConnection();
                }
            }
        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
        }
    }
}

