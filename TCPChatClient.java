package com.programs.www;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

/*
@author: prachi.shah
@date: 8-27-2024
 */
// TCPChatClient class to manage individual client connections
public class TCPChatClient implements Runnable {
    private final Socket socket;
    private PrintWriter out;

    public TCPChatClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask the client for their username
            out.println("Enter your username: ");
            String username = in.readLine();

            // Add a client to the list
            TCPChatServer.addClient(this);

            // Broadcast that a new user has joined
            TCPChatServer.broadcastMessage(LocalDateTime.now() + ": @" + username + " has joined the chat!");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("BYE!")) {
                    break;
                }
                System.out.println(LocalDateTime.now() + ": @" + username + ": " + message);
                // Broadcast a message with username and timestamp
                TCPChatServer.broadcastMessage(LocalDateTime.now() + ": @" + username + ": " + message);
            }

            // Notify others that the user is leaving
            TCPChatServer.broadcastMessage(LocalDateTime.now() + ": @" + username + " has left the chat.");
        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
        } finally {
            // Clean up and close connection
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("IOException occurred: " + e.getMessage());
        }
        TCPChatServer.removeClient(this);
    }
}



