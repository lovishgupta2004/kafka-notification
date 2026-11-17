package com.lovish.kafka_notification.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lovish.kafka_notification.Service.NotificationProducer;

@RestController 
@RequestMapping ("/notifications")
public class NotificationController {
    private final NotificationProducer NotificationProducer;
    public NotificationController(NotificationProducer NotificationProducer){
        this.NotificationProducer=NotificationProducer;
    }
    @PostMapping 
    public String sendNotification(@RequestParam String message){
        NotificationProducer.sendNotification(message);
        return "Notification sent successfully!";
    }
}
