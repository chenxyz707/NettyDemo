package com.chenxyz.nettyDemo.nio.demo4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Description
 *
 * @author chenxyz
 * @version 1.0
 * @date 2018-09-10
 */
public class TimeClientHandler implements Runnable {

    private String host;
    private int port;
    private SocketChannel socketChannel;
    private Selector selector;
    private volatile boolean stop;

    public TimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            // 打开SocketChannel
            socketChannel = SocketChannel.open();
            // 创建Selector
            selector = Selector.open();
            // 设置为非阻塞模式
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while(!stop) {
           // 轮询通道的状态
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey selectionKey = null;
                while (iterator.hasNext()) {
                    selectionKey = iterator.next();
                    iterator.remove();
                    handleInput(selectionKey);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isValid()) {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            if (selectionKey.isConnectable()) {
                if (client.finishConnect()) {
                    client.register(selector, SelectionKey.OP_READ);
                    doWrite(client);
                } else {
                    System.exit(1);
                }
            }
            if (selectionKey.isReadable()) {
                ByteBuffer receivebuffer = ByteBuffer.allocate(1024);
                int count = client.read(receivebuffer);
                if (count > 0) {
                    receivebuffer.flip();
                    byte[] bytes = new byte[receivebuffer.remaining()];
                    receivebuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("Now is : " + body);
                    this.stop = true;
                } else if (count < 0) {
                    selectionKey.channel();
                    client.close();
                }
            }
        }
    }

    private void doConnect() throws IOException {
        // 连接服务端
        boolean connect = socketChannel.connect(new InetSocketAddress(host, port));
        // 判断是否连接成功，如果连接成功，则监听Channel的状态
        if (connect) {
            socketChannel.register(selector, SelectionKey.OP_READ);
            // 写数据，写给服务端
            doWrite(socketChannel);
        } else {
            // 如果没有连接成功，则向多路复用器注册Connect状态
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    private void doWrite(SocketChannel socketChannel) throws IOException {
        ByteBuffer sendbuffer = ByteBuffer.allocate(1024);
        sendbuffer.put("QUERY TIME ORDER".getBytes());
        sendbuffer.flip();
        // 向Channel中写入客户端的指令 写到服务端
        socketChannel.write(sendbuffer);
        if (!sendbuffer.hasRemaining()) {
            System.out.println("Send order to server succeed.");
        }
    }
}
