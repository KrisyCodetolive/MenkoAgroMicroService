package com.menkoagro.api.modules.customer.domain.repository;

import com.menkoagro.api.modules.customer.domain.entity.Notification;
import com.menkoagro.api.modules.customer.domain.entity.StatutNotification;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository {

    List<Notification> findByClientIdOrderByIdDesc(UUID clientId);

    List<Notification> findByStatut(StatutNotification statut);

    Notification save(Notification notification);
}
