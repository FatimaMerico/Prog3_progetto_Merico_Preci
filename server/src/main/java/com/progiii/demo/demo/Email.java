package com.progiii.demo.demo;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * Rappresenta un'email con i suoi dettagli.
 */
public class Email implements Serializable {
    private String id;
    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private LocalDateTime timestamp;

    /**
     * Costruttore della classe Email
     * @param id l'ID dell'email
     * @param sender il mittente dell'email
     * @param receiver il destinatario dell'email
     * @param subject l'oggetto dell'email
     * @param body il corpo dell'email
     */
    public Email(String id, String sender, String receiver, String subject, String body) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.body = body;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Da: " + sender + "\nA: " + receiver + "\nOggetto: " + subject + "\n" + body + "\n" + timestamp;
    }
}
