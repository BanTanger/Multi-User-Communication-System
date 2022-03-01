package com.bantanger.qqclient.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 * 该类/对象，提供对消息相关的服务方法
 */
public class MessageClientService {

    public void sendMessageToOne(String content,String senderId,String getterId){
        // 构建message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES); // 普通的聊天消息
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setContent(content);
        message.setSendTime(new Date().toString()); // 发送时间设置到message
        System.out.println(senderId + " 对 " + getterId + "说:" + content);

        // 发送给服务器
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
