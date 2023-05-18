package com.example.wsdemo.websocketserver.example3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class Example3WsController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/topic")
    @SendTo("/topic/chat")
    public String handleMessage(String message) {
        return message;
    }

}
