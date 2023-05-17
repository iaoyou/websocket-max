package com.example.wsdemo.websocket.example4.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.TimeUnit;

/**
 * example4网络套接字通道处理程序
 *
 * @author lukou
 * @date 2023/05/17
 */
public class Example4WebSocketChannelHandler extends ChannelInitializer<SocketChannel> {

    private static final EventExecutorGroup EVENT_EXECUTOR_GROUP = new DefaultEventExecutorGroup(100);

    @Override
    protected void initChannel(SocketChannel ch) {
        // 设置30秒没有读到数据，则触发一个READER_IDLE事件。
        ch.pipeline().addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        // websocket协议本身就是基于http协议的，所以这边也要使用http编解码器
        ch.pipeline().addLast(new HttpServerCodec());
        // 以块的方式来写处理器
        ch.pipeline().addLast(new ChunkedWriteHandler());
        // netty是基于分段请求的，HttpObjectAggregator的作用是将请求分段再聚合,参数是聚合字节的最大长度
        ch.pipeline().addLast(new HttpObjectAggregator(8192));
        // 在管道中添加我们自己的接收数据实现方法
        ch.pipeline().addLast(EVENT_EXECUTOR_GROUP, new MyWebSocketServer());
        ch.pipeline().addLast(new WebSocketServerProtocolHandler(BaseSocketServer.ENDPOINT, null, true, 65536 * 10));
    }

}