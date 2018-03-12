package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO服务端源码
 */
public final class BioServer {
    //默认的端口号
    private static int DEFAULT_PORT = 8081;
    //单例的ServerSocket
    private static ServerSocket server;

    //根据传入参数设置监听端口，如果没有参数调用以下方法并使用默认值
    public static void start() throws IOException {
        //使用默认值
        start(DEFAULT_PORT);
    }

    public synchronized static void start(int port) throws IOException {
        if (server != null) return;
        try {
            //如果端口合法且空闲，服务端就监听成功
            server = new ServerSocket(port);
            System.out.println("服务器已启动，端口号：" + port);
            //通过无限循环监听客户端连接，如果没有客户端接入，将阻塞在accept操作上。
            while (true) {
                Socket socket = server.accept();
                //当有新的客户端接入时，会创建一个新的线程处理这条Socket链路
                new Thread(new BioServerHandler(socket)).start();
            }
        } finally {
            //一些必要的清理工作
            if (server != null) {
                System.out.println("服务器已关闭。");
                server.close();
                server = null;
            }
        }
    }

    public static void main(String[] args) {
        try {
            BioServer.start();
        } catch (Exception e) {
            System.out.println("服务器启动异常");
        }
    }
}
