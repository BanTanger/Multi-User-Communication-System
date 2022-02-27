package com.bantanger.qqserver.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;
import com.bantanger.qqcommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bantanger 半糖
 * @version 1.0
 * 服务端，监听9999端口，等待客户端连接，并保持通讯
 */
public class QQServer{
    public ServerSocket serverSocket = null;

    /*private String DefaultUser = "100"; // default 默认
    private String DefaultPwd = "123456";*/ // 建立HashMap维护多用户，此代码废弃不用

    // 使用ConcurrentHashMap 线程安全，在并发时修改hashmap时安全的
    // HashMap没有处理线程安全，在多线程模式下不安全
    private static ConcurrentHashMap<String,User> validUsers = new ConcurrentHashMap<>(); // hashmap来维护多用户

    static { // 静态代码块，初始化validUsers

        validUsers.put("100",new User("100","123456"));
        validUsers.put("200",new User("200","123456"));
        validUsers.put("300",new User("300","123456"));
        validUsers.put("甲亢",new User("甲亢","123456"));
        validUsers.put("乙肝",new User("乙肝","123456"));
        validUsers.put("丙烯",new User("丙烯","123456"));

    }

    // 多用户验证是否有效
    private boolean checkUser(String userId,String passwd){
        User user = validUsers.get(userId);
        if (user == null){ // 没有此id
            return false;
        }
        if (!user.getPasswd().equals(passwd)){
            return false; // userId正确，但密码错误
        }
        return true;
    }
    public void UserVerify(User user, Socket socket){
        // verify 验证
        // 创建对象流向数据管道填写消息来回复客户端成功或失败
        Message message = new Message(); // 创建消息对象
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            if(/*user.getUseId().equals(DefaultUser) && user.getPasswd().equals(DefaultPwd)*/
                checkUser(user.getUseId(),user.getPasswd())){ // 登陆成功
                message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                // 将message对象回复给客户端
                oos.writeObject(message);
                // 创建线程，与客户端保持通信，该线程需要持有socket对象
                ServerConnectClientThread serverConnectClientThread =
                        new ServerConnectClientThread(socket, user.getUseId());
                serverConnectClientThread.start(); // 启动线程

                // 将线程放在hashmap集合进行维护
                ManagerServerConnectClientThread.addClientThread(user.getUseId(), serverConnectClientThread);

            } else { // 登陆失败
                System.out.println("用户 id= " + user.getUseId() + " 密码= " + user.getPasswd() + " 验证失败");
                message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                oos.writeObject(message);
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public QQServer(){
        // 端口可以写在配置文件中
        try {
            System.out.println("服务端在9999端口监听...");
            serverSocket = new ServerSocket(9999);
            
            while(true){ // 当与某个客户端连接时，就一直监听
                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                User user = (User) ois.readObject();// 读取客户端发送的User对象

                // 验证功能 本因该在数据库DB中测试
                UserVerify(user,socket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 如果服务器退出while。说明不在监听，就关闭ServerSocket
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
