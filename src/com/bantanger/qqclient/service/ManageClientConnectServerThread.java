package com.bantanger.qqclient.service;

import java.util.HashMap;

/**
 * @author bantanger 半糖
 * @version 1.0
 * 管理客户端连接到服务器端的线程
 */
public class ManageClientConnectServerThread {

    // 使用HashMap集合 存放线程，key 就是 用户id， value 就是 线程
    private static HashMap<String,ClientConnectServerThread> hashMap= new HashMap<>();

    // 将线程加入集合
    public static void addClientConnectServerThread(String useId,ClientConnectServerThread clientConnectServerThread) {
        hashMap.put(useId,clientConnectServerThread);
    }

    // 通过userId 可以得到对应线程
    public static ClientConnectServerThread getClientConnectServerThread(String userId){
        return hashMap.get(userId);
    }
}
