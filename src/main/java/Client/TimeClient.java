package Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * Created by songyisong on 2018/2/26.
 */
public class TimeClient {
    public void connect(int port, String host) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();   //客户端处理I/O读写的线程组
        try {
            Bootstrap b = new Bootstrap();   //客户端辅助启动类
            b.group(group).channel(NioSocketChannel.class)   //设置为非阻塞模式
                    .option(ChannelOption.TCP_NODELAY, true)//禁用nagle算法（试图减少TCP包的数量和结构性开销, 将多个较小的包组合成较大的包进行发送）,达到低延迟效果
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new TimeClientHandler());   //客户端业务逻辑
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();   //发起异步连接操作
            f.channel().closeFuture().sync();   //等待客户端链路关闭，主函数才退出
        } finally {
            group.shutdownGracefully();   //释放NIO线程组
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new TimeClient().connect(port, "192.168.132.61");
    }
}
