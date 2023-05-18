package com.example.wsdemo.websocketserver.example1.config;

import com.example.wsdemo.websocketserver.example1.interceptor.Example1HandshakeInterceptor;
import com.example.wsdemo.websocketserver.example1.service.Example1WebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * 网络套接字配置
 *
 * @author lukou
 * @date 2023/04/12
 */
@Configuration
@EnableWebSocket
public class Example1WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private Example1WebSocketHandler example1WebSocketHandler;

    @Resource
    private Example1HandshakeInterceptor example1HandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 访问地址：ws://localhost:9000/example1/ws
        registry.addHandler(example1WebSocketHandler, "/example1/ws")
                // 注册拦截器
                .addInterceptors(example1HandshakeInterceptor)
                // 设置跨域
                .setAllowedOrigins("*");
    }

}
