package Server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by songyisong on 2018/2/26.
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];   //获取缓冲区可读的字节数，根据可读的字节数创建byte数组
        buf.readBytes(req);   //将缓冲区的字节数组复制到新建的byte数组中
        String body = new String(req, "UTF-8");   //获取请求消息
        System.out.println("TimeServer Receive order : " + body);
        String time = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(time.getBytes());
        ctx.write(resp);   //存入缓冲区
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();   //将缓冲区的全部消息写入SocketChannel中进行发送
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        ctx.close();   //发生异常时，释放资源
    }
}
