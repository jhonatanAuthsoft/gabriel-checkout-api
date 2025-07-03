package com.projeto.modelo.repository;

import com.projeto.modelo.model.entity.ConfigWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigWebhookRepository extends JpaRepository<ConfigWebhook, Long> {
}
