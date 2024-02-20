package org.example.Server;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {

    public static HashMap<UUID,Session> sessions = new HashMap<>();

    public static void main(String[] args) throws IOException {
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
                    System.out.println("Las sesiones activas son");
                    System.out.println(sessions.toString());
                    System.out.println(sessions.size());
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    public static void serverInterfaces(UUID id) {
            Session session=sessions.get(id);
            try {
                StringBuilder msg = new StringBuilder();
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface actualInterface = interfaces.nextElement();
                    msg.append("\n").append(actualInterface.getName())
                            .append(" -- ").append(actualInterface.getDisplayName());
                }
                session.getSocket().getOutputStream().write(msg.toString().getBytes());
                session.getSocket().getOutputStream().write("\nSesion Finalizada".getBytes());
                System.out.println("Sesion Finalizada");
                sessions.remove(id);

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void ShowIP(UUID id) { //Se puede acceder desde otras clase
        Session session=sessions.get(id);
            try {
                InetAddress ip = InetAddress.getLocalHost();
                session.getSocket().getOutputStream().write(ip.getHostAddress().getBytes());
                session.getSocket().getOutputStream().write("\nSesion Finalizada".getBytes());
                System.out.println("Sesion Finalizada");
                sessions.remove(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void theTime(UUID id) { //Se puede acceder desde otras clase
        Session session =sessions.get(id);
            try {
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                String actualTime = dateFormat.format(date);
                session.getSocket().getOutputStream().write(actualTime.getBytes());
                session.getSocket().getOutputStream().write("\nSesion Finalizada".getBytes());
                System.out.println("Sesion Finalizada");
                sessions.remove(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void RTT(UUID id) throws InterruptedException, IOException {
        Session session = sessions.get(id);
        try {
            session.getSocket().getOutputStream().write("Escriba un mensaje de 1024 bits".getBytes());
            byte[] buffer = new byte[1024]; //Creo un buffer donde se va a alojar el msj
            session.getSocket().getInputStream().read(buffer); //Leo el mensaje del cliente
            long mnStart = System.nanoTime(); //Inicio contador
            String answer = new String(buffer, StandardCharsets.UTF_8); //Transformo msg a String
            session.getSocket().getOutputStream().write(answer.getBytes()); //Lo envio devuelta
            long msFinish = System.nanoTime();
            double time = (msFinish - mnStart) * Math.pow(10, -9);
            //Conversion a formato decimal
            BigDecimal timedecimal = new BigDecimal(time);
            timedecimal = timedecimal.setScale(6, RoundingMode.HALF_UP);
            String rtt = "El mensaje tardó " + timedecimal + " segundos en ir y volver";
            session.getSocket().getOutputStream().write(rtt.getBytes());
            session.getSocket().getOutputStream().write("\nSesion Finalizada".getBytes());
            System.out.println("Sesion Finalizada");
            sessions.remove(id);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void speedOfTransmission(UUID id) throws InterruptedException {
        Session session = sessions.get(id);
            try {
                session.getSocket().getOutputStream().write("Escriba un mensaje de 8192 bits".getBytes());
                byte[] buffer = new byte[8192]; //Creo un buffer donde se va a alojar el msj
                session.getSocket().getInputStream().read(buffer); //Leo el mensaje del cliente
                long mnStart = System.nanoTime(); //Inicio contador
                String answer = new String(buffer, StandardCharsets.UTF_8); //Transformo msg a String
                session.getSocket().getOutputStream().write(answer.getBytes()); //Lo envio devuelta
                long msFinish = System.nanoTime();
                double time = (msFinish - mnStart) * Math.pow(10, -9);

                //Conversion a formato decimal
                double speed = 8.192 / time;
                BigDecimal speedDecimal = new BigDecimal(speed);
                speedDecimal = speedDecimal.setScale(6, RoundingMode.HALF_UP);
                String speedToString = "La velocidad de transmisíon viene drada por: " + speedDecimal + " KB/s";
                session.getSocket().getOutputStream().write(speedToString.getBytes());
                session.getSocket().getOutputStream().write("\nSesion Finalizada".getBytes());
                System.out.println("Sesion Finalizada");
                sessions.remove(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}