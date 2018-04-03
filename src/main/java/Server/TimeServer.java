package Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by songyisong on 2018/2/26.
 */
public class TimeServer {

    private int port;
    private SocketChannel socketChannel;

    public TimeServer(int port) throws Exception {
        this.port = port;
        bind(port);
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void bind(int port) throws Exception {
        //配置服务端NIO线程组（Reactor线程组，将IO操作和非IO操作分开）
        EventLoopGroup bossGroup = new NioEventLoopGroup();   //处理客户端的连接请求
        EventLoopGroup workerGrop = new NioEventLoopGroup();   //进行SocketChannel的网络读写
            ServerBootstrap b = new ServerBootstrap();   //netty用于启动NIO服务端的启动类
            b.group(bossGroup, workerGrop)
                    .channel(NioServerSocketChannel.class)   //创建NioServerSocketChannel，为非阻塞模式
                    .option(ChannelOption.SO_BACKLOG, 1024)   //配置TCP参数,设置backlog参数为1024,
                    .option(ChannelOption.TCP_NODELAY, true) //有数据立即发送
                    .childOption(ChannelOption.SO_KEEPALIVE, false)  //客户端存活检测
                    .childHandler(new ChildChannelHandle());   //绑定事件处理类
            ChannelFuture f = b.bind(port).sync();   //绑定端口，等待同步成功
            if (f.isSuccess()) {
                System.out.println("long connection started success");
            } else {
                System.out.println("long connection started fail");
            }
    }

    private class ChildChannelHandle extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            arg0.pipeline().addLast(new TimeServerHandler());   //实际业务逻辑的类
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new TimeServer(port);
    }
}
