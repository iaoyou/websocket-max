package com.example.wsdemo.websocketserver.example2.config;

import com.example.wsdemo.websocketserver.example2.interceptor.Example2HandshakeInterceptor;
import com.example.wsdemo.websocketserver.example2.service.Example2WebSocketHandler;
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
public class Example2WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private Example2WebSocketHandler example2WebSocketHandler;

    @Resource
    private Example2HandshakeInterceptor example2HandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 访问地址：ws://localhost:9000/example1/ws
        registry.addHandler(example2WebSocketHandler, "/example2/ws")
                // 注册拦截器
                .addInterceptors(example2HandshakeInterceptor)
                // 设置跨域
                .setAllowedOrigins("*")
                // 启用sockjs
                .withSockJS();
    }

}
