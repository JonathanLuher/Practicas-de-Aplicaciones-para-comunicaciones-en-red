package com.mycompany.chatmulticast;

import java.io.*;
import java.net.*;
import java.util.*;

public class MulticastChatServer {
    private static final String MULTICAST_ADDRESS = "230.1.1.1";
    private static final int PORT = 4000;

    public static void main(String[] args) {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket(PORT);
            socket.joinGroup(group);

            List<String> connectedUsers = new ArrayList<>();

            Thread receiverThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());

                        if (message.startsWith("<inicio>")) {
                            String userName = message.substring(8).trim();
                            connectedUsers.add(userName);
                            sendUserList(socket, group, connectedUsers);
                        } else if (message.startsWith("<fin>")) {
                            String userName = message.substring(5).trim();
                            connectedUsers.remove(userName);
                            sendUserList(socket, group, connectedUsers);
                        } else {
                            System.out.println(message);
                            // Aquí puedes procesar otros tipos de mensajes si es necesario
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();

            System.out.println("Multicast chat server started. Type 'exit' to stop.");

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("<inicio>");
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String userName = new String(packet.getData(), 0, packet.getLength()).trim();

                while (true) {
                    String message = "<msj><Server> ";
                    message += userInput.readLine();
                    if (message.equalsIgnoreCase("<msj><Server> exit")) {
                        break;
                    }
                    buffer = message.getBytes();
                    packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                    socket.send(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendUserList(MulticastSocket socket, InetAddress group, List<String> connectedUsers) throws IOException {
        StringBuilder userListMessage = new StringBuilder("<users>");
        for (String user : connectedUsers) {
            userListMessage.append(user).append(",");
        }
        byte[] userListBuffer = userListMessage.toString().getBytes();
        DatagramPacket userListPacket = new DatagramPacket(userListBuffer, userListBuffer.length, group, PORT);
        socket.send(userListPacket);
    }
}

