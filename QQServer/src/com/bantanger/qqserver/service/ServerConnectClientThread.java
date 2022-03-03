package com.bantanger.qqserver.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bantanger 半糖
 * @version 1.0
 * 该类的一个对象和某个客户端保持通讯
 */
public class ServerConnectClientThread extends Thread {

    private Socket socket;
    private String userId; // 连接到服务器的用户id

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("服务端和客户端 " + userId + " 保持通讯，读取数据...");
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();
                // 根据message类型做相应的业务处理
                if (message.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)) {
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
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(returnMessage);
                } else if (message.getMesType().equals(MessageType.MESSAGE_COMM_MES)) {
                    // 如果线程没有启动
                    HashMap<String, ServerConnectClientThread> hashMap = ManagerServerConnectClientThread.getHashMap();
                    if (hashMap.get(message.getGetter()) == null) {
                        OfflineMessage.addConcurrentHashMap(message.getGetter(), message);
                    } else { // 如果线程启动
                        // 根据message获取到getterid，然后得到对应线程
                        ServerConnectClientThread serverConnectClientThread =
                                ManagerServerConnectClientThread.getServerConnectClientThread(message.getGetter());
                        // 得到对应socket的对象输出流，将message对象转发给指定的客户端
                        ObjectOutputStream oos = new
                                ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        oos.writeObject(message); // 如果用户不在线，可以保存到数据库，实现离线留言
                    }
                    // 实时检测在线用户列表状态
                    String onlineUsers = ManagerServerConnectClientThread.getOnlineUser();
                    String[] onlineUser = onlineUsers.split(" ");
                    ConcurrentHashMap<String, Message> concurrentHashMap = OfflineMessage.getConcurrentHashMap();
                    Iterator<String> iterator = concurrentHashMap.keySet().iterator();
                    int i = 0; // i 变量用来遍历在线用户列表
                    while (iterator.hasNext() && i < onlineUser.length) {
                        String OfflineUser = iterator.next();
                        if (OfflineMessage.isOnline(onlineUser[i++], OfflineUser)) {
                            ObjectOutputStream oos =
                                    new ObjectOutputStream(ManagerServerConnectClientThread.getServerConnectClientThread(OfflineUser).getSocket().getOutputStream());
                            Message offlineMessage = concurrentHashMap.get(OfflineUser);
                            offlineMessage.setMesType(MessageType.MESSAGE_OFFLINE_MES);
                            oos.writeObject(offlineMessage);
                            OfflineMessage.removeConcurrentHashMap(OfflineUser);
                        }
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)) {
                    System.out.println(message.getSender() + "退出");
                    // 将客户端对应的线程从集合中删除
                    ManagerServerConnectClientThread.removeServerConnectClientThread(message.getSender());
                    socket.close(); // 关闭连接
                    break; // 关闭线程
                } else if (message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)) {
                    // 遍历管理线程的集合，排除消息发送者本身线程。
                    HashMap<String, ServerConnectClientThread> hashMap = ManagerServerConnectClientThread.getHashMap();
                    Iterator<String> iterator = hashMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        String onLineUserId = iterator.next();
                        if (!onLineUserId.equals(message.getSender())) { // 排除群发消息者本身
                            // 进行转发
                            ObjectOutputStream oos =
                                    new ObjectOutputStream(hashMap.get(onLineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                } else if (message.getMesType().equals(MessageType.MESSAGE_FILE_MES)) {
                    // 根据getterid 获取到集合中对应的线程，将message对象转发
                    ObjectOutputStream oos =
                            new ObjectOutputStream(ManagerServerConnectClientThread.getServerConnectClientThread(message.getGetter()).getSocket().getOutputStream());
                    oos.writeObject(message);
                } else {
                    System.out.println("其他类型的Message ， 暂时不去处理");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
