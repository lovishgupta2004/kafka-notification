package com.lovish.kafka_notification.Service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service 
public class NotificationProducer {
    private final KafkaTemplate<String,String> kafkaTemplate;
    public NotificationProducer(KafkaTemplate<String,String> kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
    }
    public void sendNotification(String type, String message){
        if (type.equalsIgnoreCase("EMAIL")) {
            kafkaTemplate.send("notifications", 0, null, message);
            System.out.println("Email sent to Partition 0");
        }
        else if (type.equalsIgnoreCase("WHATSAPP")) {
            kafkaTemplate.send("notifications", 1, null, message);
            System.out.println("WhatsApp sent to Partition 1");
        }
    }
}
