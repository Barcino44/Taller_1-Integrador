package org.example.Server;

import com.google.gson.Gson;
import org.example.Client.Cliente;
import org.example.Model.Message;
import org.example.TypeMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

public class Session {
    private static Socket socket;
    private static UUID id;


    public Session(Socket socket,UUID id) {
        Session.socket = socket;
        Session.id =id;
    }

    public void runSession() {
        //Recepcion
        new Thread(() -> {
            try {
                while (true) {
                    //Recibo mensaje
                    byte[] buffer = new byte[8192]; //Creo un buffer donde se va a alojar el msj
                    socket.getInputStream().read(buffer); //Leo el msj
                    String recibido = new String(buffer, StandardCharsets.UTF_8).trim();
                    if(recibido.length() == 0){//No hay sesion activa
                        System.out.println("Desconectado el cliente");
                        System.out.println("**********");
                        break;
                    }
                    //Caso RTT
                   if(recibido.length()==1024){
                       socket.getOutputStream().write(recibido.getBytes());
                       socket.getOutputStream().write("\nSesion Finalizada".getBytes());
                       Server.closeSesion(id);
                   }
                   //Caso speed
                   else if(recibido.length()==8192){
                       socket.getOutputStream().write(recibido.getBytes());
                       socket.getOutputStream().write("\nSesion Finalizada".getBytes());
                       Server.closeSesion(id);
                   }
                   //Los demás casos
                   else{
                        //Deserializar el tipo
                        Gson gson = new Gson();
                        Message msg = gson.fromJson(recibido, Message.class);
                        if (msg != null) {
                            switch (msg.getTypeMessage()) {
                                case TypeMessage.INTERFACES:
                                    serverInterfaces();
                                    break;
                                case TypeMessage.REMOTEIPCONFIG:
                                    ShowIP();
                                    break;
                                case TypeMessage.WHATTIMEISIT:
                                    theTime();
                                    break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
    public Socket getSocket() {
        return socket;
    }
    public UUID getId() {
        return id;
    }
    public void setSocket(Socket socket) {
        Session.socket = socket;
    }
    public static void theTime() { //Se puede acceder desde otras clase
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String actualTime = dateFormat.format(date);
            socket.getOutputStream().write(actualTime.getBytes());
            socket.getOutputStream().write("\nSesion Finalizada".getBytes());
            Server.closeSesion(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void ShowIP() { //Se puede acceder desde otras clase
        try {
            InetAddress ip = InetAddress.getLocalHost();
            socket.getOutputStream().write(ip.getHostAddress().getBytes());
            socket.getOutputStream().write("\nSesion Finalizada".getBytes());
            Server.closeSesion(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void serverInterfaces() {
        try {
            StringBuilder msg = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface actualInterface = interfaces.nextElement();
                msg.append("***").append(actualInterface.getDisplayName()).append("***\n");
            }
            socket.getOutputStream().write(msg.toString().getBytes());
            socket.getOutputStream().write("\nSesion Finalizada".getBytes());
            Server.closeSesion(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
