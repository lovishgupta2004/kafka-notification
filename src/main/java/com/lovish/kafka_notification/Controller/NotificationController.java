package com.lovish.kafka_notification.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lovish.kafka_notification.Service.NotificationProducer;

@RestController 
@RequestMapping ("/notifications")
public class NotificationController {
    private final NotificationProducer producer;
    public NotificationController(NotificationProducer producer) {
        this.producer = producer;
    }
    @PostMapping
    public String sendNotification(
            @RequestParam String type,
            @RequestParam String message) {
        producer.sendNotification(type, message);
        return "Notification sent successfully!";
    }
}