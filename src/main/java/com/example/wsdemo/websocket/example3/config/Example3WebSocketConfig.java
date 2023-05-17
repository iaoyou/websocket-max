package com.example.wsdemo.websocket.example3.config;

import com.example.wsdemo.websocket.example3.interceptor.Example3HandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;

@Configuration
@EnableWebSocketMessageBroker
public class Example3WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Resource
    private Example3HandshakeInterceptor example3HandshakeInterceptor;

    /**
     * 注册跺脚端点
     * 注册stomp站点
     *
     * @param registry 注册表
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/example3/ws")
                .addInterceptors(example3HandshakeInterceptor)
                .setAllowedOrigins("*")
                .withSockJS();

    }

    /**
     * 配置消息代理
     * 注册拦截"/topic","/queue"的消息
     *
     * @param registry 注册表
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置服务端推送消息给客户端的代理路径
        registry.enableSimpleBroker("/topic", "/queue");

        // 定义点对点推送时的前缀为/queue
        registry.setUserDestinationPrefix("/queue");

        // 定义客户端访问服务端消息接口时的前缀
        registry.setApplicationDestinationPrefixes("/app");
    }
}
