package com.bantanger.qqclient.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author bantanger 半糖
 * @version 1.0
 */
public class ClientConnectServerThread extends Thread {
    // 该线程需要持有Socket
    private Socket socket;

    // 提供接收socket的构造器
    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        // 线程Thread需要在后台一直与服务器保持通信，因此需要while循环
        while (true) {

            try {
                System.out.println("客户端线程，等待读取从服务端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果服务端没有发送Message对象，线程会阻塞在这
                Message message = (Message) ois.readObject();
                // 判断message类型，做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)) {
                    // 取出在线列表信息，并显示
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("1 显示在线用户列表");
                    System.out.println("======当前在线用户列表======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户: " + onlineUsers[i]);
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES) || message.getMesType().equals(MessageType.MESSAGE_OFFLINE_MES)) {
                    // 把服务端的消息显示到控制台上
                    System.out.println("\n" + message.getSender()
                            + " 对 " + message.getGetter() + "说:" + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    System.out.println("\n" + message.getSender() + " 对大家说 " + message.getContent());
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    System.out.println("\n" + message.getSender() + " 给 " + message.getGetter()
                    + " 发送文件 " + message.getSrc() + " 到我的电脑目录 " + message.getDest());

                    // 取出message的文件字节数组，通过文件输出流写出到磁盘
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("\n 保存文件成功~");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
