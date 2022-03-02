package com.bantanger.qqclient.view;

import com.bantanger.qqclient.service.FileClientSerive;
import com.bantanger.qqclient.service.MessageClientService;
import com.bantanger.qqclient.service.UserClientService;
import com.bantanger.qqclient.utils.Utility;
import com.bantanger.qqcommon.Message;
import com.bantanger.qqcommon.MessageType;

import java.io.File;

/**
 * @author bantanger 半糖
 * @version 1.0
 * 菜单管理模块
 */
public class QQView {
    private boolean loop = true; // 控制是否显示菜单
    private String key = ""; // 接收用户的键盘输入
    private UserClientService userClientService = new UserClientService(); // 对象时用于登陆服务器/注册用户
    private MessageClientService messageClientService = new MessageClientService(); // 对象用户私聊
    private FileClientSerive fileClientSerive = new FileClientSerive(); // 对象用于传输文件

    public static void main(String[] args) {
        new QQView().mainMenu();
    }

    // 二级菜单
    private void SecondaryMenu(String userId) {

        while (loop) {
            System.out.println("\n======网络通信系统二级菜单(用户 " + userId + " ) ======");
            System.out.println("\t\t 1 显示在线用户列表");
            System.out.println("\t\t 2 群发消息");
            System.out.println("\t\t 3 私聊消息");
            System.out.println("\t\t 4 发送文件");
            System.out.println("\t\t 5 退出系统");
            System.out.print("请输入你的选择: ");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    userClientService.onlineFriendList(userId);
                    break;
                case "2":
                    System.out.println("请输入想对大家说的话:");
                    String content = Utility.readString(100);
                    messageClientService.sendMessageToAll(content,userId);
                    break;
                case "3":
                    System.out.print("请输入想聊天的用户号(在线):");
                    String getterId = Utility.readString(50);
                    System.out.print("请输入想说的话:");
                    content = Utility.readString(100);
                    // 方法：将消息发送给服务器端
                    messageClientService.sendMessageToOne(content,userId,getterId);
                    break;
                case "4":
                    System.out.print("请输入想把文件发送给的用户号(在线):");
                    getterId = Utility.readString(50);
                    System.out.print("请输入发送文件的路径(形式 d:\\xx.jpg):");
                    String src = Utility.readString(100);
                    System.out.print("请输入把文件发送到对应的路径(形式 d:\\yy.jpg):");
                    String dest = Utility.readString(100);
                    fileClientSerive.sendFileToOne(src,dest,userId,getterId);
                    break;
                case "5":
                    userClientService.logout(userId);
                    loop = false;
                    break;
            }
        }
    }

    // 主菜单
    private void mainMenu() {
        while (loop) {
            System.out.println("======欢迎进入网络通信系统======");
            System.out.println("\t\t 1.登陆系统");
            System.out.println("\t\t 5.退出系统");
            System.out.print("用户请输入选择:");

            key = Utility.readString(1);
            // 根据用户输入，来处理不同的逻辑

            switch (key) {
                case "1":
                    System.out.print("请输入用户号:");
                    String userId = Utility.readString(50);
                    System.out.print("请输入密  码:");
                    String pwd = Utility.readString(50);
                    // 需要到服务器验证用户是否合法
                    // 简单实现
                    if (userClientService.checkUser(userId, pwd)) { // 登陆服务器成功（默认成功）
                        System.out.println("======欢迎 (用户 " + userId + " 登陆成功) ======");
                        new QQView().SecondaryMenu(userId);
                    } else { // 登陆服务器失败
                        System.out.println("=======登陆失败======");
                    }
                    break;
                case "5":
                    // userClientService.logout(/*userId*/"100");
                    loop = false;
                    break;
            }
        }

    }


}
