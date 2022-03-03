package com.bantanger.qqclient.service;

import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;

import java.io.*;
import java.util.Date;

/**
 * @author bantanger 半糖
 * @version 1.0
 */
public class FileClientSerive {
    public void sendFileToOne(String src,String dest,String senderId,String getterId){
        // 读取src文件 --> message
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);
        message.setSendTime(new Date().toString());

        // 需要将文件在本地里读取
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int) new File(src).length()];
        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes); // 将src文件读入到程序的字节数组
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关流
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 提示信息
        System.out.println("\n" + senderId + " 给 " + getterId + " 发送文件 " +
                src + " 到对方电脑目录 " + dest);

        // 发送
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(ManageClientConnectServerThread.getClientConnectServerThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
