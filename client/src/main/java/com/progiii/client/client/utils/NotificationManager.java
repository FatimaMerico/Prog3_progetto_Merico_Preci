package com.progiii.client.client.utils;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NotificationManager {
    private static NotificationManager instance; // Istanza Singleton
    private final StringProperty notificationMessage = new SimpleStringProperty(); // Messaggio della notifica
    private long notificationEndTime = 0; // Tempo di scadenza della notifica

    // Costruttore privato per impedire la creazione di istanze esterne
    private NotificationManager() {}

    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    // Metodo per mostrare una notifica
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

    // Metodo per ottenere il messaggio della notifica
    public StringProperty notificationMessageProperty() {
        return notificationMessage;
    }

    // Metodo per verificare se la notifica è attiva
    public boolean isNotificationActive() {
        return notificationMessage.get() != null && System.currentTimeMillis() < notificationEndTime;
    }
}
