package org.example.furryfriendfund.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements INotificationService{

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Notification createAppointmentNotification(Notification notification) {


        return notificationRepository.save(notification);
    }
}
