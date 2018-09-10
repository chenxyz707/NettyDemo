package com.chenxyz.nettyDemo.nio.demo4;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Description
 *
 * @author chenxyz
 * @version 1.0
 * @date 2018-09-10
 */
public class BufferDemo {

    public static void main(String[] args) {
        try {
            System.out.println(BufferDemo.class.getClassLoader().getResource("BufferDemo.txt").getPath());
            RandomAccessFile file = new RandomAccessFile(BufferDemo.class.getClassLoader().getResource("BufferDemo.txt").getPath(), "r");
            FileChannel channel = file.getChannel();

            // 定义一个buffer并初始化大小
            ByteBuffer buf = ByteBuffer.allocate(10);
            System.out.println("position:"+buf.position()+"; limit:"+buf.limit()+"; capacity:"+buf.capacity());
            // 将Channel中的数据读到buffer中
            channel.read(buf);
            System.out.println("position:"+buf.position()+"; limit:"+buf.limit()+"; capacity:"+buf.capacity());

            //将buffer写模式切换为读模式
            buf.flip();
            System.out.println("position:"+buf.position()+"; limit:"+buf.limit()+"; capacity:"+buf.capacity());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
