package com.lovish.kafka_notification.Service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service 
public class NotificationConsumer {
    @KafkaListener (
        topics = "notifications",
        groupId = "notification-group"
    )
    public void consumeNotification(String message){
        System.out.println("Recieved Notification: "+message);
    }
}
