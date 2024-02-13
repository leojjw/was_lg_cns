package com.mysite.sbb.user;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import org.springframework.ai.chat.messages.MessageType;

@Getter
@Setter
@Entity
public class ChatHistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MessageType type;

    @ManyToOne
    private SiteUser user;

    @Column(columnDefinition = "LONGTEXT")
    private String content;
}
