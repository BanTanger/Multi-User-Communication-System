package com.bantanger.qqcommon;

/**
 * @author bantanger 半糖
 * @version 1.0
 */
public interface MessageType {
    String MESSAGE_LOGIN_SUCCEED = "1"; // 表示登陆成功
    String MESSAGE_LOGIN_FAIL = "2"; // 表示登陆失败
    String MESSAGE_COMM_MES = "3"; // 普通消息包
    String MESSAGE_GET_ONLINE_FRIEND = "4"; // 请求返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5"; // 返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6"; // 客户端退出请求
    String MESSAGE_TO_ALL_MES = "7"; // 群发消息包
    String MESSAGE_FILE_MES = "8"; // 文件消息（发送文件）
}
