package com.example.wsdemo.websocketserver.example4.config;

import com.example.wsdemo.websocketserver.example4.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * example4网络套接字服务端启动初始化
 *
 * @author lukou
 * @date 2023/05/17
 */
@Component
public class Example4WebSocketStartInit implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(Example4WebSocketStartInit.class);

    @Resource
    private NettyServer nettyServer;

    /**
     * 需要异步启动，不然会阻塞主线程
     * 这里自定义一个线程启动，也可以在方法上加上注解@Async，一样的效果
     *
     * @param args arg游戏
     */
    @Override
    public void run(String... args) {
        new Thread(() -> {
            try {
                nettyServer.start();
            } catch (Exception e) {
                log.error("识别服务中netty服务启动报错!", e);
            }
        }).start();
    }

    @PreDestroy
    public void destroy() {
        if (nettyServer != null) {
            try {
                nettyServer.destroy();
            } catch (Exception e) {
                log.error("停止netty服务发生异常!", e);
            }
        }
        log.info("netty识别服务已经销毁。。");
    }
}
