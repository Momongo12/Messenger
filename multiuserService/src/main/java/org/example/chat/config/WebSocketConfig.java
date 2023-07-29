package org.example.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.username:quest}")
    private String username;

    @Value("${spring.rabbitmq.password:quest}")
    private String password;

    @Value("${spring.rabbitmq.port:61613}")
    private Integer port;

    public static final String TOPIC_DESTINATION_PREFIX = "/topic/";
    public static final String REGISTRY = "/ws";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(REGISTRY)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay(TOPIC_DESTINATION_PREFIX)
                .setRelayHost(host)
                .setRelayPort(port)
                .setClientLogin(username)
                .setClientPasscode(password)
                .setSystemLogin(username)
                .setSystemPasscode(password);
    }
}
