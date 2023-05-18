# websocket-max
websocket-max包含了多种websocket实现方式，其中有基于spring-websocekt，也有基于netty框架，即下即用。

## 项目介绍

### 例子一：

代码位于example1中，使用的技术栈是spring-websocket。

项目启动后websocket访问地址：ws://localhost:9000/example1/ws

也可以通过资源文件目录下resources中前端页面example1.html测试。

### 例子二：

代码位于example2中，使用的技术栈是spring-websocket，开启了sockjs支持。

项目启动后websocket访问地址：http://localhost:9000/example2/ws

也可以通过资源文件目录下resources中前端页面example2.html测试。

### 例子三：

代码位于example3中，使用的技术栈是spring-websocket，开启了sockjs支持以及stomp。

项目启动后websocket访问地址：http://localhost:9000/example3/ws

也可以通过资源文件目录下resources中前端页面example3.html测试。

ps：这个例子不全，点对点消息发送未实现。

### 例子四：

代码位于example4中，使用的技术栈是netty框架。

项目启动后websocket访问地址：ws://localhost:9000/example4/ws

也可以通过资源文件目录下resources中前端页面example1.html测试。
