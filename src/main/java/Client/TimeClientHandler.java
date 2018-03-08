package Client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by songyisong on 2018/2/26.
 */
public class TimeClientHandler extends ChannelHandlerAdapter {
    private byte[] req;
    private int counter;

    public TimeClientHandler() {
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {  //客户端和服务器端建立连接后，netty的NIO线程调用此方法
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);//将消息发送给服务端
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object mes) throws Exception {   //服务器返回消息时被调用
        ByteBuf buf = (ByteBuf) mes;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("Now is : " + body + " ; the counter is : " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Unexcepected from downstream : " + cause.getMessage());
        ctx.close();
    }
}

