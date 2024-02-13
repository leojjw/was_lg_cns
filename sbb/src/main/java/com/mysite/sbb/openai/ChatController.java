package com.mysite.sbb.openai;

import java.util.List;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    private final ChatClient chatClient;
    private final ChatService chatService;
    
    @Autowired
    public ChatController(ChatClient chatClient, ChatService chatService) {
        this.chatClient = chatClient;
        this.chatService = chatService;
    }

    @RequestMapping("/searchResult")
    public String generate(Model model, 
            @RequestParam(value = "content", required = false) String content) {

        if (content != null){
            // System.out.println("empty!");
            List<Message> messages = chatService.getMessages();
            messages.add(new UserMessage(content));
            String searchResult = chatClient.call(new Prompt(messages)).getResult().getOutput().getContent();
            messages.add(new AssistantMessage(searchResult));
            chatService.setMessages(messages);
            chatService.historyUpdate(content, searchResult);
        }
        model.addAttribute("chatHistoryEntries", chatService.getChatHistoryEntries());

        return "search_result";
    }
}