package org.example.Server;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server {

    public static HashMap<UUID,Session> sessions = new HashMap<>();

    public static void main(String[] args) {
        //Esperar solicitudes de handshake
        //Socket = Puerta de entrada y salida
        new Thread(()->{
            try{
                ServerSocket server = new ServerSocket(6000);//El puerto de conexión es unico y recibe la solicitud de handshek
                while(true) {
                    //Va creando sockets para cada usuario
                    Socket socket=server.accept();
                    Session session = new Session(socket,UUID.randomUUID());
                    //Socket establece conexion con el cliente
                    System.out.println("Conectado");
                    //Crear proceso de recepción
                    session.runSession();
                    sessions.put(session.getId(),session);
                    System.out.println("Las sesiones activas son:");
                    System.out.println(sessions.toString());
                    System.out.println("El numero de sesiones existentes son:");
                    System.out.println(sessions.size());
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }
    public static void closeSesion(UUID id){
        sessions.remove(id);
    }
}