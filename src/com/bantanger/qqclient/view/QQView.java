package com.bantanger.qqclient.view;

import com.bantanger.qqclient.service.UserClientService;
import com.bantanger.qqclient.utils.Utility;

/**
 * @author bantanger 半糖
 * @version 1.0
 * 菜单管理模块
 */
public class QQView {

    private boolean loop = true; // 控制是否显示菜单
    private String key = ""; // 接收用户的键盘输入
    private UserClientService userClientService = new UserClientService(); // 对象时用于登陆服务器/注册用户

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
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择: ");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    userClientService.onlineFriendList(userId);
                    break;
                case "2":
                    System.out.println("群发消息");
                case "3":
                    System.out.println("私聊消息");
                    break;
                case "4":
                    System.out.println("发送文件");
                    break;
                case "9":
                    System.out.println("退出系统");
                    loop = false;
                default:
                    System.out.println("输入不正确");
            }
        }
    }

    // 主菜单
    private void mainMenu() {

        while (loop) {
            System.out.println("======欢迎进入网络通信系统======");
            System.out.println("\t\t 1.登陆系统");
            System.out.println("\t\t 9.退出系统");
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
                case "9":
                    System.out.println("退出系统");
                    loop = false;
                default:
                    System.out.println("输入不正确");
            }
        }

    }
}
