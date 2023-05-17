package com.example.wsdemo.websocket.example4.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import static io.netty.handler.codec.http.HttpMethod.GET;

/**
 * 基本套接字服务器
 * 抽象了一层.<br>
 *
 * @author lukou
 * @date 2023/05/17
 */
public abstract class BaseSocketServer extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(BaseSocketServer.class);

    /**websocket协议内容*/
    public static final String WEBSOCKET = "websocket";
    public static final String UPGRADE = "Upgrade";

    /**
     * 客户端连接地址
     */
    public static final String ENDPOINT = "/example4/ws";

    /**
     * 连接唯一id，方便链路追踪
     */
    protected String taskId;

    /**
     * 上下文
     */
    protected ChannelHandlerContext context;

    /**
     * websocket握手处理器
     */
    private WebSocketServerHandshaker webSocketServerHandshaker;

    /**
     * 通道活性
     * 客户端与服务端创建链接的时候调用.<br>
     *
     * @param context 上下文
     */
    @Override
    public abstract void channelActive(ChannelHandlerContext context);

    /**
     * 频道不活跃
     * 客户端与服务端断开连接的时候调用.<br>
     *
     * @param context 上下文
     */
    @Override
    public abstract void channelInactive(ChannelHandlerContext context);

    /**
     * 通道读完整
     * 服务端接收客户端发送过来的数据结束之后调用.<br>
     *
     * @param context 上下文
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    /**
     * 例外了
     * 工程出现异常的时候调用.<br>
     *
     * @param context   上下文
     * @param throwable throwable
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable throwable) {
        context.close();
        log.info("taskId:[{}]中发生错误，原因：[{}]", this.taskId, throwable.toString(), throwable);
    }

    /**
     * 通道read0
     * 连接和帧信息.<br>
     *
     * @param ctx ctx
     * @param msg 味精
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof WebSocketFrame) {
            this.handWebSocketFrame(ctx, (WebSocketFrame) msg);
            return;
        }
        if (msg instanceof FullHttpRequest) {
            log.info("taskId:[{}]开始处理websocket握手请求。。", taskId);
            this.httpRequestHandler(ctx, (FullHttpRequest) msg);
            log.info("taskId:[{}]处理websocket握手请求结束。。", taskId);
        }
    }

    /**
     * 用户事件触发
     * 这里设置了一个读超时事件，可以参考{@link Example4WebSocketChannelHandler}中设置
     *
     * @param ctx ctx
     * @param evt evt
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
                log.info("taskId:[{}]读操作超时。。断开连接。。", this.taskId);
            }
        }
    }

    /**
     * 处理客户端与服务端之间的websocket业务.<br>
     *
     * @param context        上下文
     * @param webSocketFrame 网络套接字框架
     */
    public void handWebSocketFrame(ChannelHandlerContext context, WebSocketFrame webSocketFrame) {
        //判断是否是关闭websocket的指令
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(context.channel(), (CloseWebSocketFrame) webSocketFrame.retain());
            log.info("taskId:[{}]接收到关闭帧。。断开连接。。", this.taskId);
            return;
        }
        //判断是否是ping消息
        if (webSocketFrame instanceof PingWebSocketFrame) {
            context.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            log.info("taskId:[{}]接收到心跳帧。。", this.taskId);
            return;
        }
        //判断是否是二进制消息
        if (webSocketFrame instanceof TextWebSocketFrame) {
            this.handTextWebSocketFrame(context, webSocketFrame);
        }
    }

    /**
     * http请求处理程序
     * http握手请求校验.<br>
     *
     * @param context         上下文
     * @param fullHttpRequest 完整http请求
     */
    private void httpRequestHandler(ChannelHandlerContext context, FullHttpRequest fullHttpRequest) {
        //判断是否http握手请求
        if (!fullHttpRequest.decoderResult().isSuccess() || !(WEBSOCKET.equals(fullHttpRequest.headers().get(UPGRADE)))
                || !GET.equals(fullHttpRequest.method())) {
            sendHttpResponse(context, new DefaultFullHttpResponse(fullHttpRequest.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
            log.error("taskId:{{}}websocket握手内容不正确。。响应并关闭。。", taskId);
            return;
        }
        String uri = fullHttpRequest.uri();
        log.info("taskId:{{}}websocket握手uri[{}]", taskId, uri);
        if (!ENDPOINT.equals(getBasePath(uri))) {
            sendHttpResponse(context, new DefaultFullHttpResponse(fullHttpRequest.protocolVersion(), HttpResponseStatus.NOT_FOUND));
            log.info("taskId:[{}]websocket握手协议不正确。。响应并关闭。。", taskId);
            return;
        }
        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = new WebSocketServerHandshakerFactory("", null, false);
        webSocketServerHandshaker = webSocketServerHandshakerFactory.newHandshaker(fullHttpRequest);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
            log.info("taskId:[{}]websocket握手协议版本不正确。。响应并关闭。。", taskId);
            return;
        }
        webSocketServerHandshaker.handshake(context.channel(), fullHttpRequest);
        this.checkOpenInfo(context, fullHttpRequest);
    }

    /**
     * 得到基本路径
     *
     * @param url url
     * @return {@link String}
     */
    public static String getBasePath(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        int idx = url.indexOf("?");
        if (idx == -1) {
            return url;
        }
        return url.substring(0, idx);
    }

    /**
     * 发送http响应
     * 服务端发送响应消息.<br>
     *
     * @param context                 上下文
     * @param defaultFullHttpResponse 默认完整http响应
     */
    private void sendHttpResponse(ChannelHandlerContext context, DefaultFullHttpResponse defaultFullHttpResponse) {
        if (defaultFullHttpResponse.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(defaultFullHttpResponse.status().toString(), CharsetUtil.UTF_8);
            defaultFullHttpResponse.content().writeBytes(buf);
            buf.release();
        }
        //服务端向客户端发送数据
        ChannelFuture future = context.channel().writeAndFlush(defaultFullHttpResponse);
        if (defaultFullHttpResponse.status().code() != 200) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 回复消息给客户端.<br>
     *
     * @param message 消息
     * @return {@link ChannelFuture}
     */
    protected ChannelFuture reply( String message) {
        ChannelFuture channelFuture = context.writeAndFlush(new TextWebSocketFrame(message));
        log.info("taskId:[{}]回复给客户端消息完成：[{}]", this.taskId, message);
        return channelFuture;
    }

    /**
     * 检查打开信息
     * 检验连接打开时的信息.<br>
     *
     * @param context         上下文
     * @param fullHttpRequest 完整http请求
     */
    protected abstract void checkOpenInfo(ChannelHandlerContext context, FullHttpRequest fullHttpRequest);


    /**
     * 手文本框架网络套接字
     * 文本帧处理.<br>
     *
     * @param context        上下文
     * @param webSocketFrame 网络套接字框架
     */
    protected abstract void handTextWebSocketFrame(ChannelHandlerContext context, WebSocketFrame webSocketFrame);

}
