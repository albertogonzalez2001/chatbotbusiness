package com.Alberto.chatbotbusiness.repository;

import com.Alberto.chatbotbusiness.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    // Recuperacion de los ultimos mensajes de una sesion, N, orden de mas antiguos a mas recientes
    @Query("""
    SELECT m FROM Message m
    WHERE m.sessionId = :sessionId 
    AND m.id IN (
        SELECT m2.id FROM Message m2
        WHERE m2.sessionId = :sessionId
        ORDER BY m2.createdAt DESC
        ) 
        ORDER BY m.createdAt ASC   
        """)
    List<Message> findLastNBySessionId(@Param("sessionId") String sessionId,
                                       // Implementa paginacion y orden con la interfaz Pageable.
                                       Pageable pageable);
}
