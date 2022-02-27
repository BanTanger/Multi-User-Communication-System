package com.bantanger.qqserver.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author bantanger 半糖
 * @version 1.0
 * 该类的一个对象和某个客户端保持通讯
 */
public class ServerConnectClientThread extends Thread{

    private Socket socket;
    private String userId; // 连接到服务器的用户id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {
        while(true){
            System.out.println("服务端和客户端 " + userId + " 保持通讯，读取数据...");
            try {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                // 根据message类型做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){
                    // 客户端要拉取在线用户列表
                    System.out.println(message.getSender() + " 要拉取在线用户列表");
                    String onlineUser = ManagerServerConnectClientThread.getOnlineUser();
                    // 返回message
                    // 构建一个Message对象，返回给客户端,设置消息类型
                    Message returnMessage = new Message();
                    returnMessage.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    returnMessage.setContent(onlineUser);
                    // 返回之前请求的对象名，此时Sender变成Getter
                    returnMessage.setGetter(message.getSender());
                    // 返回给客户端
                    oos.writeObject(returnMessage);
                } else {
                    System.out.println("其他类型的Message ， 暂时不去处理");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
