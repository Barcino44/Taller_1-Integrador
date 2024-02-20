package org.example.Server;

import com.google.gson.Gson;
import org.example.Client.Cliente;
import org.example.Model.Message;
import org.example.TypeMessage;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Session {
    private Socket socket;
    private UUID id;

    public Session(Socket socket,UUID id) {
        this.socket = socket;
        this.id=id;
    }

    public void runSession() {
        //Recepcion
        new Thread(() -> {
            try {
                while (true) {
                    //Recibo mensaje
                    byte[] buffer = new byte[3100]; //Creo un buffer donde se va a alojar el msj
                    socket.getInputStream().read(buffer); //Leo el msj
                    String recibido = new String(buffer, StandardCharsets.UTF_8).trim();
                    //Deserializar el tipo
                    Gson gson = new Gson();
                    Message msg= gson.fromJson(recibido,Message.class);
                    if(msg!=null) {
                        switch (msg.getTypeMessage()) {
                            case TypeMessage.INTERFACES:
                                Server.serverInterfaces(getId());
                                break;
                            case TypeMessage.REMOTEIPCONFIG:
                                Server.ShowIP(getId());
                                break;
                            case TypeMessage.WHATTIMEISIT:
                                Server.theTime(getId());
                                break;
                            case TypeMessage.RTT:
                                Server.RTT(getId());
                                break;
                            case TypeMessage.SPEED:
                                Server.speedOfTransmission(getId());
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }//Se ejecuta en segundo plano (proceso en segundo plano)
            catch (InterruptedException e) {
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
        this.socket = socket;
    }
}
