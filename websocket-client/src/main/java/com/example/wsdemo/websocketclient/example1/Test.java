package com.example.wsdemo.websocketclient.example1;

import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;

@Slf4j
public class Test {

    public static void main(String[] args) {
        try (WebsocketClient websocketClient = new WebsocketClient("ws://localhost:8081/example4/ws")) {
            // 连接
            websocketClient.connect();
            // 发送消息
            websocketClient.send("xxxxxxxxxxxxxxxxx");
            // 阻塞一下，否则这里客户端会调用close方法
            Thread.sleep(10);
        } catch (URISyntaxException | MyException | InterruptedException e) {
            log.error("发生异常,原因:{}", e.getMessage(), e);
        }

    }
}
