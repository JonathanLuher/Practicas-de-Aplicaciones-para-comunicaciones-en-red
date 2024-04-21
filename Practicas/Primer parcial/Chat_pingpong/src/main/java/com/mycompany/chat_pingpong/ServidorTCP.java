package com.mycompany.chat_pingpong;

import java.net.*;
import java.io.*;

public class ServidorTCP {
    public static void main(String[] args){
        try{
            ServerSocket s = new ServerSocket(1234);
            System.out.println("Esperando cliente...");

            while (true) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde " + cl.getInetAddress() + ":" + cl.getPort());

                // Configuramos para recibir mensajes del cliente
                BufferedReader brCliente = new BufferedReader(new InputStreamReader(cl.getInputStream()));

                // Configuramos para leer mensajes desde la consola del servidor
                BufferedReader brConsola = new BufferedReader(new InputStreamReader(System.in));

                // Configuramos para enviar mensajes al cliente
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()), true);

                // Loop para mantener la conversación
                while (true) {
                    // Leer mensaje del cliente
                    String mensajeCliente = brCliente.readLine();
                    if (mensajeCliente == null || mensajeCliente.equalsIgnoreCase("bye"))
                        break; // Si el cliente envía "bye", terminamos la conexión

                    System.out.println("Mensaje recibido del cliente: " + mensajeCliente);

                    // Leer mensaje desde la consola del servidor
                    System.out.println("Escribe tu mensaje para el cliente (presiona Enter para enviar):");
                    String mensajeConsola = brConsola.readLine();

                    // Enviar mensaje al cliente
                    pw.println(mensajeConsola);
                }

                // Cerrar recursos y conexión con el cliente
                brCliente.close();
                pw.close();
                cl.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
