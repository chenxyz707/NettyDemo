package com.chenxyz.nettyDemo.bio.demo3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Description
 *
 * @author chenxyz
 * @version 1.0
 * @date 2018-09-10
 */
public class TimeServer {

    public static void main(String[] args) {
        int port=8080; //服务端默认端口
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port:"+port);
            Socket socket = null;
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50, 10000);

            while(true){
                socket = server.accept();
//				new Thread(new TimeServerHandler(socket)).start();
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(server!=null){
                System.out.println("The time server is close.");
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
