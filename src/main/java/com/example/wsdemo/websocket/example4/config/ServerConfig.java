package com.example.wsdemo.websocket.example4.config;

import com.example.wsdemo.websocket.example4.netty.Example4WebSocketChannelHandler;
import com.example.wsdemo.websocket.example4.netty.NettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Value("${netty.websocket.port:8081}")
    private int port;


    @Bean("example4WebSocketChannelHandler")
    public Example4WebSocketChannelHandler example4WebSocketChannelHandler() {
        return new Example4WebSocketChannelHandler();
    }

    @Bean("nettyServer")
    public NettyServer nettyServer(Example4WebSocketChannelHandler example4WebSocketChannelHandler) {
        return new NettyServer(this.port, example4WebSocketChannelHandler);
    }
}
