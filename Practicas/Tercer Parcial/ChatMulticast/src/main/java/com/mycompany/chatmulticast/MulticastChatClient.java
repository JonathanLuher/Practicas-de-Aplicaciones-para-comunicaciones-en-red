package com.mycompany.chatmulticast;

import java.io.*;
import java.net.*;

public class MulticastChatClient {
    private static final String MULTICAST_ADDRESS = "230.1.1.1";
    private static final int PORT = 4000;

    public static void main(String[] args) {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket(PORT);
            socket.joinGroup(group);

            Thread receiverThread = new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());

                        if (message.startsWith("<users>")) {
                            String userListStr = message.substring(7);
                            String[] userList = userListStr.split(",");
                            System.out.println("Connected users:");
                            for (String user : userList) {
                                System.out.println("- " + user);
                            }
                        } else {
                            System.out.println(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();

            System.out.print("<inicio>");
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            String userName = userInput.readLine();
            byte[] buffer = ("<inicio> " + userName).getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            System.out.println("Multicast chat client started. Type 'exit' to leave.");

            String message;
            while (true) {
                message = userInput.readLine();
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                message = "<msj><" + userName + "> " + message;
                buffer = message.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
            }

            byte[] exitMessage = ("<fin> " + userName).getBytes();
            DatagramPacket exitPacket = new DatagramPacket(exitMessage, exitMessage.length, group, PORT);
            socket.send(exitPacket);

            socket.leaveGroup(group);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
