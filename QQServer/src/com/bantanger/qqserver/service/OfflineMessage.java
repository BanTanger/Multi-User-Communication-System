package com.bantanger.qqserver.service;

import com.bantanger.qqcommon.Message;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bantanger 半糖
 * @version 1.0
 */
public class OfflineMessage {
    private static ConcurrentHashMap<String,Message> concurrentHashMap = new ConcurrentHashMap();

    public static ConcurrentHashMap<String, Message> getConcurrentHashMap() {
        return concurrentHashMap;
    }

    public static void setConcurrentHashMap(ConcurrentHashMap<String, Message> concurrentHashMap) {
        OfflineMessage.concurrentHashMap = concurrentHashMap;
    }

    /**
     * 判断离线用户是否上线
     * @param onlineUser 每一个在线用户，通过字符串分割 for循环的方式得到
     * @param getterId 离线消息的发送对象
     * @return 如果离线用户上线就返回true，否则返回false
     */
    public static boolean isOnline(String onlineUser,String getterId){
        return onlineUser.equals(getterId);
    }

    /**
     * 离线消息保存在一个hashMap中，之后等用户上线再取出
     * @param getterId 离线用户名
     * @param message 消息
     */
    public static void addConcurrentHashMap(String getterId,Message message){
        concurrentHashMap.put(getterId,message);
    }

    /**
     * 用户上线，取出集合里的消息
     * @param getterId 离线用户名
     */
    public static void removeConcurrentHashMap(String getterId){
        concurrentHashMap.remove(getterId);
    }
}
