package com.mycompany.chatmulticast;

import java.io.*;
import java.net.*;

public class MulticastChatServer {
    private static final String MULTICAST_ADDRESS = "230.0.0.1";
    private static final int PORT = 5000;

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
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();

            System.out.println("Multicast chat server started. Type 'exit' to stop.");

            while (true) {
                System.out.println("<inicio>");
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String userName = new String(packet.getData(), 0, packet.getLength());

                while (true) {
                    String message = "<msj><Server> ";
                    BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                    message += userInput.readLine();
                    if (message.equalsIgnoreCase("<msj><" + userName + "> exit")) {
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
}
