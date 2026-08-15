package com.fitnessmedical.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fitnessmedical.entity.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
            select m from ChatMessage m
            join fetch m.sender
            join fetch m.receiver
            where (m.sender.id = :a and m.receiver.id = :b)
               or (m.sender.id = :b and m.receiver.id = :a)
            order by m.createdAt asc, m.id asc
            """)
    List<ChatMessage> findConversation(@Param("a") Long accountId, @Param("b") Long peerAccountId);
}
