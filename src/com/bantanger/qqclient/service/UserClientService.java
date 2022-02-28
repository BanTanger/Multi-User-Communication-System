package com.bantanger.qqclient.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;
import com.bantanger.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author bantanger 半糖
 * @version 1.0
 */
public class UserClientService {

    // 可能在其他敌方需要使用到user信息，因此做成成员属性，使用get，set方法
    private User user = new User();
    // Socket要放在集合，所以做成属性，方便集合调用每一个socket
    private Socket socket;
    // IP地址封装成属性，方便后期维护
    private String LocalIP = "127.0.0.1";

    public boolean checkUser(String userId,String pwd){
        boolean flag = false;
        // 创建User对象
        user.setUseId(userId);
        user.setPasswd(pwd);

        // 连接服务器，发送user对象
        try {
            socket = new Socket(InetAddress.getByName(LocalIP),9999);
            // 创建对象流，传输User对象到服务端
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user); // 发送User对象
            
            // 读取从服务端回复的Message对象
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message) ois.readObject();

            if (ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){
                // System.out.println("登陆成功");
                // 创建一个和服务端保持通讯的线程 -> 创建类ClientConnectServerThread

                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                clientConnectServerThread.start();

                // 将线程放在集合里
                ManageClientConnectServerThread.addClientConnectServerThread(userId,clientConnectServerThread);

                flag = true;
            }else{
                // System.out.println("登陆失败");
                // 不能启动和服务器通讯的线程，但socket已经开启了，所以需要关闭socket
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

    // 向服务器端请求在线用户列表
    public void onlineFriendList(String userId){

        // 发送一个Message，类型MESSAGE_GET_ONLINE_FRIEND
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        message.setSender(userId);
        // 发送给服务器
        // 得到当前线程的Socket 对应的 ObjectOutputStream 对象
        try {
            // 获得当前连接的线程
            /*ClientConnectServerThread clientConnectServerThread =
                    ManageClientConnectServerThread.getClientConnectServerThread(user.getUseId());
            Socket socket = clientConnectServerThread.getSocket(); // 得到当前线程端口
            ObjectOutputStream oos =
                    new ObjectOutputStream(socket.getOutputStream()); // 通过当前线程，得到对象流*/
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(userId).getSocket().getOutputStream());
            oos.writeObject(message); // 发送拉取在线用户的信息的请求，服务端在线程的run方法里读取该信息
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
