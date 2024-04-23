package com.mycompany.chatmulticast;

import java.io.*;
import java.net.*;

public class MulticastChatClient {
    private static final String MULTICAST_ADDRESS = "230.0.0.1";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            MulticastSocket socket = new MulticastSocket(PORT);
            socket.joinGroup(group);

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // Enviar el nombre del usuario al servidor
            System.out.print("<inicio>");
            String userName = userInput.readLine();
            byte[] buffer = userName.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
            socket.send(packet);

            Thread receiverThread = new Thread(() -> {
                try {
                    byte[] buffer1 = new byte[1024];
                    while (true) {
                        DatagramPacket packet1 = new DatagramPacket(buffer1, buffer1.length);
                        socket.receive(packet1);
                        String message = new String(packet1.getData(), 0, packet1.getLength());
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();

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

            socket.leaveGroup(group);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
