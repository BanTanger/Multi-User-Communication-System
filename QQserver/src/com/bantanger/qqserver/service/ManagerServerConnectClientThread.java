package com.bantanger.qqserver.service;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author bantanger 半糖
 * @version 1.0
 * 用于管理和客户端通讯的线程
 */
public class ManagerServerConnectClientThread {
    private static HashMap<String,ServerConnectClientThread> hashMap = new HashMap<>();

    // 添加线程到集合的函数
    public static void addClientThread(String userId,ServerConnectClientThread serverConnectClientThread){
        hashMap.put(userId,serverConnectClientThread);
    }
    // 根据userId 返回ServerConnectClientThread线程
    public static ServerConnectClientThread getServerConnectClientThread(String userId){
        return hashMap.get(userId);
    }

    public static String getOnlineUser(){
        // 集合遍历， 遍历HashMap的key
        Iterator<String> iterator = hashMap.keySet().iterator();
        String onlineUserList = "";
        while(iterator.hasNext()){
            onlineUserList += iterator.next().toString() + " ";
        }
        return onlineUserList;
    }
}
