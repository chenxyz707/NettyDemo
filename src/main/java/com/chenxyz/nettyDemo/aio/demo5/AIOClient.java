package com.chenxyz.nettyDemo.aio.demo5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * Description
 *
 * @author chenxyz
 * @version 1.0
 * @date 2018-09-10
 */
public class AIOClient {

    private final AsynchronousSocketChannel client;

    public AIOClient() throws IOException {
        //Asynchronous
        //BIO   Socket
        //NIO   SocketChannel
        //AIO   AsynchronousSocketChannel
        client = AsynchronousSocketChannel.open();
    }

    public static void main(String[] args) throws InterruptedException {
        int count = 3;
        final CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            latch.countDown();
            new Thread() {
                @Override
                public void run() {
                    try {
                        latch.await();
                        new AIOClient().connect("localhost", 8080);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }
        Thread.sleep(1000 * 60 * 60);
    }

    private void connect(String host, int port) {
        // 这里只做写操作
        client.connect(new InetSocketAddress(host, port), null, new CompletionHandler<Void, Void>() {

            /**
             * 实现IO操作完成的方法
             * @param result
             * @param attachment
             */
            @Override
            public void completed(Void result, Void attachment) {
                try {
                    client.write(ByteBuffer.wrap(("这是一条测试数据" + System.currentTimeMillis()).getBytes())).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 实现IO操作失败的方法
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });

        //只读数据
        final ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer, null, new CompletionHandler<Integer, Object>() {

            /**
             * 实现OP操作完成的方法
             * @param result
             * @param attachment
             */
            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("获取反馈结果" + new String(buffer.array()));
            }

            /**
             * 实现IO操作失败的方法
             * @param exc
             * @param attachment
             */
            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
