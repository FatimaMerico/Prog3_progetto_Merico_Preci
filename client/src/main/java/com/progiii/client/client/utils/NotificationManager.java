package com.progiii.client.client.utils;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * Gestisce le notifiche dell'applicazione
 * Implementa il pattern Singleton per garantire una sola istanza
 */
public class NotificationManager {
    private static NotificationManager instance; //Istanza unica della classe
    private final StringProperty notificationMessage = new SimpleStringProperty(); // Messaggio della notifica
    private long notificationEndTime = 0; // Tempo di scadenza della notifica

    private NotificationManager() {} //Costruttore privato per impedire la creazione di istanze esterne
    /**
     * Restituisce l'istanza unica della classe
     * @return l'istanza unica di NotificationManager
     */
    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    /**
     * Mostra una notifica con il messaggio specificato
     * @param message il messaggio da mostrare
     */
    public void showNotification(String message) {
        Platform.runLater(() -> {
            notificationMessage.set(message); // Imposta il messaggio
            notificationEndTime = System.currentTimeMillis() + 3000; // 3 secondi

            // Nasconde la notifica dopo 3 secondi
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> notificationMessage.set(null));
                        }
                    },
                    3000 // Ritardo di 3 secondi
            );
        });
    }

    /**
     * Restituisce la proprietà del messaggio della notifica
     * @return la proprietà del messaggio della notifica
     */
    public StringProperty notificationMessageProperty() {
        return notificationMessage;
    }

    /**
     * Verifica se la notifica è attiva
     * @return true se la notifica è attiva, false altrimenti
     */
    public boolean isNotificationActive() {
        return notificationMessage.get() != null && System.currentTimeMillis() < notificationEndTime;
    }
}
