package com.chenxyz.nettyDemo.aio.demo5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description
 *
 * @author chenxyz
 * @version 1.0
 * @date 2018-09-10
 */
public class AIOServer {

    private final int port;

    /**
     * 注册一个端口给客户端连接
     * @param port
     */
    public AIOServer(int port) {
        this.port = port;
        listen();
    }

    public static void main(String[] args) {
        int port = 8080;
        new AIOServer(port);
    }

    /**
     * 监听方法
     */
    private void listen() {

        try {
            // 线程缓冲池，实现异步
            ExecutorService executor = Executors.newCachedThreadPool();
            // 线程池初始化一个线程
            AsynchronousChannelGroup threadGroup =
                    AsynchronousChannelGroup.withThreadPool(executor);

            final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(threadGroup);

            server.bind(new InetSocketAddress(port));
            System.out.println("服务已启动，监听端口" + port);

            final Map<String, Integer> count = new ConcurrentHashMap<String, Integer>();
            count.put("count", 0);

            // 开始等待客户端连接
            // 实现一个CompletionHandler的接口，匿名的实现类
            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

                final ByteBuffer buffer = ByteBuffer.allocate(1024);

                // 实现IO操作完成的方法
                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    count.put("count", count.get("count") + 1);

                    System.out.println(count.get("count"));

                    try {
                        buffer.clear();
                        result.read(buffer).get();
                        result.write(buffer);
                        buffer.flip();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            result.close();
                            server.accept(null, this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("IO操作时失败: " + exc);
                }
            });

            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
