package com.mysite.sbb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mysite.sbb.openai.ChatService;
import com.mysite.sbb.user.ChatHistoryEntry;
import com.mysite.sbb.user.ChatHistoryService;

import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ChatService chatService;
    private final ChatHistoryService chatHistoryService;

    @Autowired
    public SecurityConfig(ChatService chatService, ChatHistoryService chatHistoryService) {
        this.chatService = chatService;
        this.chatHistoryService = chatHistoryService;       
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
            .csrf((csrf) -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
            .headers((headers) -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
            .formLogin((formLogin) -> formLogin
                .loginPage("/user/login")
                .successHandler((request, response, authentication) -> {
                    String username = authentication.getName();
                    List<ChatHistoryEntry> chatHistory = chatHistoryService.getChatHistory(username);
                    chatService.saveCache();
                    chatService.setChatHistoryEntries(chatHistory);

                    List<Message> messages = new ArrayList<>();
                    for (ChatHistoryEntry chatHistoryEntry : chatHistory) {
                        if (chatHistoryEntry.getType() == MessageType.USER){
                            messages.add(new UserMessage(chatHistoryEntry.getContent()));
                        } else if (chatHistoryEntry.getType() == MessageType.ASSISTANT){
                            messages.add(new AssistantMessage(chatHistoryEntry.getContent()));
                        } else {
                            messages.add(new SystemMessage(chatHistoryEntry.getContent()));
                        }
                    }
                    chatService.setMessages(messages);

                    response.sendRedirect("/searchResult");
                }))
                //.defaultSuccessUrl("/"))
            .logout((logout) -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessHandler((request, response, authentication) -> {
                    chatService.returnCache();
                    response.sendRedirect("/searchResult");
                })
                .invalidateHttpSession(true))
        ;
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
