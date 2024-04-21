package com.mycompany.chat_pingpong;

import java.net.*;
import java.io.*;

public class ClienteTCP {
    public static void main(String[] args){
        try{
            // Dirección y puerto del servidor al que se conectará automáticamente el cliente
            String host = "192.168.0.01"; // Cambiar por la dirección IP del servidor
            int pto = 1234; // Puerto del servidor

            Socket cl = new Socket(host, pto);

            // Configuramos para enviar mensajes al servidor
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()), true);

            // Configuramos para recibir mensajes del servidor
            BufferedReader br = new BufferedReader(new InputStreamReader(cl.getInputStream()));

            // Hilo para recibir y mostrar respuestas del servidor en tiempo real
            Thread recibirRespuestas = new Thread(() -> {
                try {
                    while (true) {
                        String respuesta = br.readLine();
                        if (respuesta == null) {
                            break; // Si el servidor cierra la conexión, salimos del loop
                        }
                        System.out.println("Respuesta del servidor: " + respuesta);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recibirRespuestas.start(); // Iniciar el hilo para recibir respuestas

            // Loop principal para enviar mensajes al servidor
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("Escribe tu mensaje (escribe 'bye' para salir):");
                String mensaje = br1.readLine();

                // Enviar mensaje al servidor
                pw.println(mensaje);

                if (mensaje.equalsIgnoreCase("bye"))
                    break; // Si se envía "bye", terminamos la conexión
            }

            // Cerrar recursos y conexión con el servidor al salir del loop principal
            br1.close();
            pw.close();
            br.close();
            cl.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
