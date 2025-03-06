package com.progiii.demo.demo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Email implements Serializable {
    private String id;
    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private LocalDateTime timestamp;

    public Email(String id, String sender, String receiver, String subject, String body) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.timestamp = LocalDateTime.now();
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Da: " + sender + "\nA: " + receiver + "\nOggetto: " + subject + "\n" + body + "\n" + timestamp;
    }
}
