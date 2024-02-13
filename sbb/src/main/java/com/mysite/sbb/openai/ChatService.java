package com.mysite.sbb.openai;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mysite.sbb.user.ChatHistoryEntry;
import com.mysite.sbb.user.ChatHistoryService;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Service
public class ChatService {
    private List<Message> messages;
    private List<ChatHistoryEntry> chatHistoryEntries;
    private List<Message> messagesCache;
    private List<ChatHistoryEntry> chatHistoryEntriesCache;

    private final ChatHistoryService chatHistoryService;

    @Autowired
    public ChatService(ChatHistoryService chatHistoryService) {
        this.messages = new ArrayList<>();
        this.messagesCache = new ArrayList<>();
        this.chatHistoryEntries = new ArrayList<>();
        this.chatHistoryEntriesCache = new ArrayList<>();
        this.chatHistoryService = chatHistoryService;
    }

    public void saveCache() {
        this.setMessagesCache(this.getMessages());
        this.setChatHistoryEntriesCache(this.getChatHistoryEntries());
    }

    public void returnCache() {
        this.setMessages(this.getMessagesCache());
        this.setChatHistoryEntries(this.getChatHistoryEntriesCache());
    }

    public void historyUpdate(String content, String searchResult) {
        ChatHistoryEntry search = new ChatHistoryEntry();
        ChatHistoryEntry result = new ChatHistoryEntry();
        search.setType(MessageType.USER);
        search.setContent(content);
        result.setType(MessageType.ASSISTANT);
        result.setContent(searchResult);
        chatHistoryEntries.add(search);
        chatHistoryEntries.add(result);

        String currentPrincipalName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentPrincipalName != "anonymousUser") {
            this.chatHistoryService.addChatHistory(search, currentPrincipalName);
            this.chatHistoryService.addChatHistory(result, currentPrincipalName);
        }
    }
}
