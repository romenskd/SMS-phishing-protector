package org.example.user_service.repository;

import org.example.user_service.entity.SmsMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsMessageJpaRepository extends JpaRepository<SmsMessage, String> {

}
