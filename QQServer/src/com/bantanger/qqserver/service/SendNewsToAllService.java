package com.bantanger.qqserver.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author bantanger 半糖
 * @version 1.0
 */
public class SendNewsToAllService implements Runnable{
    public boolean flag = true;
    public Scanner scanner = new Scanner(System.in);
    @Override
    public void run() {
        while(flag){
            System.out.println("请输入服务器要推送的消息/新闻(输入e退出)");
            String news = scanner.next();
            if (news.equals("e")){
                flag = false;
            }
            Message message = new Message();
            message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
            message.setSender("服务器");
            message.setContent(news);
            message.setSendTime(new Date().toString());
            System.out.println("服务器对所有人 说:" + news);
            // 群发逻辑实现 ， 拿到线程集合里面的所有线程
            HashMap<String, ServerConnectClientThread> hashMap = ManagerServerConnectClientThread.getHashMap();
            Iterator<String> iterator = hashMap.keySet().iterator();
            while (iterator.hasNext()) {
                String onLineUserId = iterator.next().toString();
                ServerConnectClientThread serverConnectClientThread = hashMap.get(onLineUserId);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
