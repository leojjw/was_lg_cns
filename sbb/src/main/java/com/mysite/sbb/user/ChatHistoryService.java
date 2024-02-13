package com.mysite.sbb.user;

import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatHistoryService {

    private final UserRepository userRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    @Autowired
    public ChatHistoryService(UserRepository userRepository, ChatHistoryRepository chatHistoryRepository) {
        this.userRepository = userRepository;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public List<ChatHistoryEntry> getChatHistory(String username) {
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getChatHistory();
    }

    public void addChatHistory(String username, String content, MessageType messageType) {
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        ChatHistoryEntry chatHistoryEntry = new ChatHistoryEntry();
        chatHistoryEntry.setContent(content);
        chatHistoryEntry.setType(messageType);
        chatHistoryEntry.setUser(user);
        this.chatHistoryRepository.save(chatHistoryEntry);
    }

    public void addChatHistory(ChatHistoryEntry chatHistoryEntry, String username) {
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        chatHistoryEntry.setUser(user);
        this.chatHistoryRepository.save(chatHistoryEntry);
    }

    public void clearChatHistory() {
        // Implement logic to clear search history if needed
        // This might involve removing entries from the data store
    }
}
