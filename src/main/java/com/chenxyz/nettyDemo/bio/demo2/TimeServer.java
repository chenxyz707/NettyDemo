package com.chenxyz.nettyDemo.bio.demo2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 同步阻塞IO模型实现时间服务器：
 * 缺陷：
 * 服务端接收到客户端的请求后，为每个客户端新创建一个线程。
 * 在高性能服务器应用领域，往往需要面对成千上万个客户端的并发链接，
 * 这种模型显然无法满足高性能、高并发的场景。
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
            while(true){
                socket = server.accept();
                new Thread(new TimeServerHandler(socket)).start();
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
