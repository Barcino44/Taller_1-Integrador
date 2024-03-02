package org.example.Model;

import org.example.TypeMessage;

public class Message {
    private String author;
    private String typeMessage;

    public Message() {
    }
    public Message(String author, String typeMessage) {
        this.author = author;
        this.typeMessage = typeMessage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

}
