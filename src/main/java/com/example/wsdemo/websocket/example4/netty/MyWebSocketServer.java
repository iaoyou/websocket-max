package com.example.wsdemo.websocket.example4.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 服务段实例化
 *
 * @author lukou
 * @date 2023/05/17
 */
@Component
public class MyWebSocketServer extends BaseSocketServer {

    private static final Logger log = LoggerFactory.getLogger(MyWebSocketServer.class);

    @Override
    public void channelActive(ChannelHandlerContext context) {
        this.taskId = UUID.randomUUID().toString().replaceAll("-", "");
        this.context = context;
        log.info("taskId:[{}]有一个新请求进来了。。开始初始化上下文。。。", this.taskId);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) {
        log.info("taskId:[{}]识别服务触发关闭事件.", this.taskId);
        // 这边可以收尾处理
    }

    @Override
    protected void checkOpenInfo(ChannelHandlerContext context, FullHttpRequest fullHttpRequest) {
        log.info("taskId:[{}]识别服务中websocket握手协议正确。。开始校验其它。。", this.taskId);
    }

    @Override
    protected void handTextWebSocketFrame(ChannelHandlerContext context, WebSocketFrame webSocketFrame) {
        String text = ((TextWebSocketFrame) webSocketFrame).text();
        this.reply(this.taskId + " : " + text + System.currentTimeMillis());
    }
}
