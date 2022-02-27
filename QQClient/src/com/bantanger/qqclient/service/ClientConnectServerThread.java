package com.bantanger.qqclient.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * @author bantanger 半糖
 * @version 1.0
 */
public class ClientConnectServerThread extends Thread{
    // 该线程需要持有Socket
    private Socket socket;

    // 提供接收socket的构造器
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        // 线程Thread需要在后台一直与服务器保持通信，因此需要while循环
        while(true){

            try {
                System.out.println("客户端线程，等待读取冲服务端发送的消息");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                // 如果服务端没有发送Message对象，线程会阻塞在这
                Message message = (Message) ois.readObject();
                // 判断message类型，做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){
                    // 取出在线列表信息，并显示
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("1 显示在线用户列表");
                    System.out.println("======当前在线用户列表======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户: " + onlineUsers[i]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
