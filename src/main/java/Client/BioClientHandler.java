package Client;

import java.io.IOException;
import java.util.Random;

/**
 * 测试方法
 */
public class BioClientHandler {
    //测试主方法
    public static void main(String[] args) throws InterruptedException {
        //算术表达式
        String expression = "21*4";
        BioClient.send(expression);
    }
}


