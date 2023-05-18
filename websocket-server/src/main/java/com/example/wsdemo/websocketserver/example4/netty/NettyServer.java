package com.example.wsdemo.websocketserver.example4.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网状服务器
 *
 * @author lukou
 * @date 2023/05/17
 */
public class NettyServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private int port;
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ChannelInitializer<SocketChannel> channelInitializer;

    public NettyServer(int port, ChannelInitializer<SocketChannel> channelInitializer) {
        this.port = port;
        this.channelInitializer = channelInitializer;
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
    }

    /**
     * 开始
     *
     * @throws Exception 异常
     */
    public void start() throws Exception {
        try {
            ServerBootstrap sb = new ServerBootstrap();
            //绑定线程池
            sb.group(bossGroup, workGroup)
                    //指定使用的channel
                    .channel(NioServerSocketChannel.class)
                    //临时存放已完成三次握手的请求的队列的最大长度
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //禁用nagle算法，不等待，立即发送
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //当没有数据包过来时超过一定时间主动发送一个ack探测包
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //允许共用端口
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    //绑定监听端口
                    .localAddress(this.port)
                    //添加自定义处理器
                    .childHandler(this.channelInitializer);
            //服务器异步创建绑定
            ChannelFuture cf = sb.bind().sync();
            channel = cf.channel();
            log.info("netty服务启动。。正在监听:[{}]", channel.localAddress());
            //关闭服务器通道
            channel.closeFuture().sync();
        } catch (Exception e) {
            throw new Exception("启动netty服务发生异常，端口号:" + this.port, e);
        }
    }

    /**
     * 摧毁
     *
     * @throws Exception 异常
     */
    public void destroy() throws Exception {
        try {
            channel.close().sync();
            workGroup.shutdownGracefully().sync();
            bossGroup.shutdownGracefully().sync();
        } catch (Exception e) {
            throw new Exception("停止netty服务发生异常，端口号:" + this.port, e);
        }

    }

}
