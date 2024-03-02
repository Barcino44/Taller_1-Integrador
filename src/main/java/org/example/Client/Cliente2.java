package org.example.Client;

import com.google.gson.Gson;
import org.example.Model.Message;
import org.example.TypeMessage;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class Cliente2 {
    static long startingTime;
    static long endingTime;
    static Socket socket = null;

    public static void main(String[] args) throws IOException {
        System.out.println("*************");
        System.out.println(msg());
        System.out.println("*************");
        //Puerto de entrada y salida
        Scanner reader = new Scanner(System.in);
        //Enviar
        while (true) {
            String msjcliente = reader.nextLine();
            if(isTypeMessage(msjcliente)) {
                socket = new Socket("127.0.0.1", 6000);
                new Thread(() -> {
                    Message message = new Message("Cliente 1", msjcliente);
                    if (msjcliente.equals(TypeMessage.RTT)) {
                        byte[] buffer = new byte[1024];
                        Arrays.fill(buffer, (byte) 'a');
                        startingTime = System.nanoTime();
                        try {
                            socket.getOutputStream().write(buffer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (msjcliente.equals(TypeMessage.SPEED)) {
                        byte[] buffer = new byte[8192];
                        Arrays.fill(buffer, (byte) 'b');
                        startingTime = System.nanoTime();
                        try {
                            socket.getOutputStream().write(buffer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Gson gson = new Gson();
                        String json = gson.toJson(message);
                        try {
                            socket.getOutputStream().write(json.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            //Recibir respuesta del server
            new Thread(() -> {
                while (true) {
                    try {
                        if (socket == null||socket.isClosed()) break;
                        else {
                            byte[] buffer = new byte[8192]; //Creo un buffer donde se va a alojar el msj
                            socket.getInputStream().read(buffer); //Leo el msj
                            String recibido = new String(buffer, StandardCharsets.UTF_8).trim();
                            if (recibido.length() == 1024) {
                                endingTime = System.nanoTime();
                                long time = endingTime - startingTime;
                                System.out.println("El mensaje tardo " + time * Math.pow(10, -9) + " segundos en ir y volver");
                            } else if (recibido.length() == 8192) {
                                endingTime = System.nanoTime();
                                long time = endingTime - startingTime;
                                double timeInseconds = time * Math.pow(10, -9);
                                double speed = (8192 * 2) / timeInseconds;
                                System.out.println("El mensaje tiene una velocidad de transmision de " + speed + " bytes/segundo");
                            } else if (recibido.equalsIgnoreCase("Sesion Finalizada")) {
                                System.out.println("Mensaje de server: " + "\n" + recibido);
                                socket.close();
                                System.out.println("*************");
                                System.out.println(msg());
                                System.out.println("*************");
                                break;
                            } else {
                                System.out.println("Mensaje de server: " + "\n" + recibido);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }//Se ejecuta en segundo plano (proceso en segundo plano)
            }).start();
        }
    }

    public static String msg() {
        return """
                    Escriba un comando:
                    interfaces
                    remoteipconfig
                    whattimeisit
                    rtt
                    speed""";
    }
    public static boolean isTypeMessage(String msg){
        return msg.equals(TypeMessage.INTERFACES) || msg.equals(TypeMessage.WHATTIMEISIT)
                || msg.equals(TypeMessage.REMOTEIPCONFIG) || msg.equals(TypeMessage.RTT)
                || msg.equals(TypeMessage.SPEED);
    }
}
