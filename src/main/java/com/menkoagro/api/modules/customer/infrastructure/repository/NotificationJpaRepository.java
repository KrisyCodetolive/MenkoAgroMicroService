package com.menkoagro.api.modules.customer.infrastructure.repository;

import com.menkoagro.api.modules.customer.domain.entity.Notification;
import com.menkoagro.api.modules.customer.domain.entity.StatutNotification;
import com.menkoagro.api.modules.customer.domain.repository.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, UUID>, NotificationRepository {

    List<Notification> findByClientIdOrderByIdDesc(UUID clientId);

    List<Notification> findByStatut(StatutNotification statut);
}
