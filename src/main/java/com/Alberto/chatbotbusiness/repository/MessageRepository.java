package com.Alberto.chatbotbusiness.repository;

import com.Alberto.chatbotbusiness.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}
