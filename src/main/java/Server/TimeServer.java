package Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by songyisong on 2018/2/26.
 */
public class TimeServer {
    public void bind(int port) throws Exception {
        //配置服务端NIO线程组（Reactor线程组，将IO操作和非IO操作分开）
        EventLoopGroup bossGroup = new NioEventLoopGroup();   //处理客户端的连接请求
        EventLoopGroup workerGrop = new NioEventLoopGroup();   //进行SocketChannel的网络读写
        try {
            ServerBootstrap b = new ServerBootstrap();   //netty用于启动NIO服务端的启动类
            b.group(bossGroup, workerGrop)
                    .channel(NioServerSocketChannel.class)   //创建NioServerSocketChannel，为非阻塞模式
                    .option(ChannelOption.SO_BACKLOG, 1024)   //配置TCP参数,设置backlog参数为1024,
                    .childHandler(new ChildChannelHandle());   //绑定事件处理类
            ChannelFuture f = b.bind(port).sync();   //绑定端口，等待同步成功
            f.channel().closeFuture().sync();   //阻塞，等待服务端监听端口关闭，main函数退出
        } finally {
            //释放线程资源
            bossGroup.shutdownGracefully();
            workerGrop.shutdownGracefully();
        }
    }
    private class ChildChannelHandle extends ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel arg0) throws Exception{
            arg0.pipeline().addLast(new LineBasedFrameDecoder(1024));
            arg0.pipeline().addLast(new StringDecoder());
            arg0.pipeline().addLast(new TimeServerHandler());   //实际业务逻辑的类
        }
    }
    public static void main(String[] args) throws  Exception{
        int port=8080;
        new TimeServer().bind(port);
    }
}
