package com.example.wsdemo.websocket.example1.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;

/**
 * 网络套接字业务处理器
 *
 * @author lukou
 * @date 2023/05/05
 */
@Component
@Slf4j
public class Example1WebSocketHandler extends AbstractWebSocketHandler {

    /**
     * 连接建立之后
     *
     * @param session 会话
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("oneSearch websocket is connected! session id: [{}]", session.getId());
    }

    /**
     * 处理文字信息
     *
     * @param session 会话
     * @param message 消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("session [{}] received message is [{}]", session.getId(), message.getPayload());
    }

    /**
     * 处理异常错误
     *
     * @param session   会话
     * @param exception 异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws IOException {
        log.error("session [{}] error!", session.getId(), exception);
    }

    /**
     * 连接关闭后
     *
     * @param session 会话
     * @param status  状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("session [{}] is closed! closeStatus is [{}]", session.getId(), status.toString());
    }
}
