package com.mysite.sbb.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistoryEntry, Long> {
    
}