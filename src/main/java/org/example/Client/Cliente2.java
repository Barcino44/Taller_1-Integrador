package org.example.Client;

import com.google.gson.Gson;
import org.example.Model.Message;
import org.example.Server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Cliente2 {
    static Socket socket =null;


    public static void main(String[] args) throws IOException {
        System.out.println("*************");
        System.out.println(msg());
        System.out.println("*************");
        //Puerto de entrada y salida
        Scanner reader = new Scanner(System.in);
        //Enviar
        while (true) {
            String msjcliente = reader.nextLine();
            if(socket==null){
                socket=new Socket("127.0.0.1", 6000);
            }
            new Thread(() -> {
                Message message = new Message("Cliente 2", msjcliente);
                Gson gson = new Gson();
                String json = gson.toJson(message);
                try {
                    socket.getOutputStream().write(json.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            //Recibir respuesta del server
            new Thread(() -> {
                while (true) {
                    try {
                        if(socket==null){
                            break;
                        }
                        else {
                            byte[] buffer = new byte[8192]; //Creo un buffer donde se va a alojar el msj
                            socket.getInputStream().read(buffer); //Leo el msj
                            String recibido = new String(buffer, StandardCharsets.UTF_8).trim();
                            if (recibido.equalsIgnoreCase("Sesion Finalizada")) {
                                System.out.println("Mensaje de server: " + "\n" + recibido);
                                System.out.println("*************");
                                System.out.println(msg());
                                System.out.println("*************");
                                socket=null;
                                break;
                            } else {
                                System.out.println("Mensaje de server: " + "\n" + recibido);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }//Se ejecuta en segundo plano (proceso en segundo plano)
            }).start();
        }
    }
    public static String msg(){
        return """
                Escriba un comando:
                interfaces
                remoteipconfig
                whattimeisit
                rtt
                speed""";
    }
}
