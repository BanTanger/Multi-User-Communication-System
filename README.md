# 项目开发流程简介

![img](https://api2.mubu.com/v3/document_image/05059ff0-99b1-47c7-a36f-02221b72b35f-7239137.jpg)

## 需求分析
1. 用户登陆
2. 拉取在线用户列表
3. 无异常退出（客户端，服务端）
4. 私聊
5. 群聊
6. 发送文件
7. 服务器推送新闻

## 界面设计



### 用户登陆

![img](https://api2.mubu.com/v3/document_image/e487e434-1582-4e65-8f8e-68ce4c3d5ba8-7239137.jpg)

功能：

+ 校验功能
  + `checkUser(String userId, String pwd)`

### 查看在线用户列表

![img](https://api2.mubu.com/v3/document_image/b7caa3ab-cdfd-46d6-bf8f-620e93933cad-7239137.jpg)



### 私聊

![img](https://api2.mubu.com/v3/document_image/3261fd86-cf91-4d39-ac9f-ddfcb2845d62-7239137.jpg)



### 群聊

![img](https://api2.mubu.com/v3/document_image/5d7368e2-cf85-4738-af0b-a5e6b2c13948-7239137.jpg)



### 发文件

![img](https://api2.mubu.com/v3/document_image/48af7407-4d1e-4b3d-a472-48351b6fc0cc-7239137.jpg)



### 服务器推送新闻

![img](https://api2.mubu.com/v3/document_image/99c6e082-086c-4399-8e78-40bce738e489-7239137.jpg)



## 功能涉及知识点，大概轮廓

### 网络编程TCP方式

#### socket

#### 数据管道传入Message对象和User对象

- ？为什么要传入对象？还要串行化（不就是简单发一个消息嘛）
  对象传输，解决文本传输不知道什么时候结尾的问题，message有不同的类型来控制服务器返回什么样式，传入对象方便做各种逻辑

#### 创建端口，所有客户端通过这一个端口启动服务器的多个socket对象

#### 多线程

- 服务器出现了多个socket

  - 为了达到实时通讯，要求实现多线程？

  - 并且将socket放在一个集合中

    - 管理线程的集合（vector），线程池？

    - 将连接好的通道放在了一个容器里，需要的时候直接调用连接

- ？客户端也要实现线程？

  - 客户端也要求实现多线程

  - 每个线程分别承担不同的功能

    - 视频

    - 文件发送

    - 消息传递

  - 也需要用Vector管理线程

#### 代码实现思路

- 服务端
  - 思路
    有客户端连接到服务端时，会得到一个socket
    启动线程，该线程持有该socket对象（socket是该线程属性）
    使用集合来管理线程

- 客户端

  - 思路
    和服务端通信时，使用对象方式，可以使用对象流将对象读写入数据通道
    当客户端连接到服务端后，也会得到socket
    启动线程，该线程持有socket对象
    使用集合管理线程

  - 模块
    - QQView
      - 菜单界面（管理一级菜单和二级菜单）
        布尔量 loop 用来控制是否显示菜单

- 数据库
  - 账户涉及
    - 没有学习数据库
      暂时使用人为规定的：用户名/id = 100，密码 123456
      只有这个用户可以登陆，其它用户不能登陆
      扩展，使用HashMap模拟数据库实现多用户登录

- 两个对象

  - Message对象
    发送者 sender
    接收者 getter
    内容 content
    发送时间 sendtime
    消息类型 mesType
  - User对象
    ID
    密码
  - 细节
    使用对象流读取
    需要在两个对象里实现序列化Serializable
    ` private static final long serialVersionUID = 1L`;增强兼容性
    因为开了两个IDEA（服务端和客户端）
    应该要将这两个对象共享到两个端口中
  
- 退出进程

  + 客户端：
    1. 在main线程中调用方法，给服务器端发送一个退出系统的message对象
    2. 调用` System.exit（0）`// 正常退出
  + 服务器端：
    1. 服务器端收到退出系统的message对象后
    2. 把这个线程持有的socket关闭
    3. 在将线程从集合中移除

- 私聊

  - 客户端：

    1. 创建用户（客户端A）希望给某个其他在线用户（客户端B）聊天的内容

       > 代码实现部分在` MessageClinentService`

       > 说明：1. 构建消息的属性，消息类型，发送者，接收者，内容，发送时间` new Date().toString()`

       > 2.通过编写对象输入流，从线程集合中拿到发送者的线程，通过线程拿到socket端口

    2. 将消息构建成Message对象，通过对应的socket发送给服务器

       >  通过对象输入流将消息message写入数据通道 ` oos.writeObject(message);` 

    3. 在客户端B的线程中读取发送到的Message对象，并显示（此步先要等待服务器的数据）

  - 服务器端：

    1. 可以读取到客户端A发送给客户端B的消息，（原理是客户端A先将数据写入服务器中A用户对应的socket，然后服务器找到私聊对应用户的socket传入。

       > 在` ServerConnectClientThread`类里创建新的else-if分支

       >  新建` getSocket`方法`(ps: 如果是单个线程的原路返回，直接引用socket就行，但如果要得到服务器客户端B的socket，就需要用getSocket的方式提前保存上一个socket，不知道理解的对不对)` 

    2. 从管理线程的集合中，通过Message对象的getId方式获取到对应线程的socket

       > 在线程集合中拿到对应的线程：

       ```java
       ServerConnectClientThread serverConnectClientThread =
               ManagerServerConnectClientThread.getServerConnectClientThread(message.getSender());
       ```

       > 创建对象输出流

    3. 将message对象转发个指定用户

       写入消息`(ps：实现私聊逻辑，将content先保存到数据库里，等待用户上线时，通过服务器实时检测用户在线情况，调用数据库里的信息直接返回给上线用户)`

- 群聊

  - 与私聊思路类似，重点是在服务端中，拿到线程集合hashMap，遍历除了群发者的所有线程，拿到线程之后再创建对象流，将消息写入给所有对象`(这里有点不太理解为什么循环创建了多个对象流却没有报StreamCorruptedException错误)`


#### BUG

- 序列化问题
  - 使用对象流来读写数据，读写的对象需要序列化
    否则会报未序列化异常和读写错误

- 空指针
  查找之后是因为拉取在线用户那个模块时使用user.getId 有可能返回为空，暂时不知道原因，只能通过传参的方式实现该功能

- 运行关闭Server | Client报错

  - 主线程退出，子线程并没有退出，子线程一直等待主线程的数据传输，因此报错

    解决：

    1. 通过设置守护线程的方式（不推荐，守护线程不可控，没有十足的把握尽量少使用）
    2. 通过向服务端发送一个退出类型的消息，只要接收到该消息关闭进程

- `StreamCorruptedException`

  - 未序列化或者创建了一个以上的对象流导致

  - 虽然不知道为什么，我主菜单和二级菜单在退出界面放了同一个退出方法，通过向服务端发送退出类型的消息来关闭两个端口和对应进程，然后在二级菜单退出后不知道为什么loop没有变成false，在主菜单又走了一遍，导致产生了两个对象流

  - 产生错误原因有

    1. 客户端少写了将消息传入数据通道里 ` oos.writeObject(message);`

    2. 客户端少写了关闭进程的命令 

       ```
       System.out.println(u.getUserId() + " 退出系统 ");
       System.exit(0);//结束进程
       ```

    3. 情况9 case 9 退出分支debug没有执行，不知原因，改成情况5 case 5



# 程序结构框架

## 客户端 QQClient

### service

#### UserClientService[🔞创建线程]

+ 两个核心属性

  + User （自定义类：在qqcommon类里） 
  + Socket

+ 方法：

  + 校验用户数据：`boolean checkUser(String userId, String pwd) `

    + 先创建客户端与服务端的连接，通过Socket数据通道传入User对象，在服务端`QQServer/QQServer/QQServer()`里校验用户合法性

      ```java
      QQClient/UserClientService
      private String LocalIP = "127.0.0.1";
      public boolean checkUser(String userId, String pwd) {
          boolean flag = false;
          // 创建User对象
          user.setUseId(userId);
          user.setPasswd(pwd);
          socket = new Socket(InetAddress.getByName(LocalIP), 9999);
                      // 创建对象流，传输User对象到服务端
                      ObjectOutputStream oos = new 			ObjectOutputStream(socket.getOutputStream());
                      oos.writeObject(user); // 发送User对象
      ```

    + 读取从服务器`QQServer/QQServer/UserVerify`回复的Message对象: (**消息类型：【成功 & 失败】**)
    + 条件分支处理：
      1. 验证消息类型为成功：创建一个和服务器`ServerConnectClientThread`线程对应的客户端`ClientConnectServerThread`线程
      2. 把线程放在集合里维护**（实际上并没有用到这个客户端线程集合，但在后期可能会在一个客户端开启多个Socket端口，所以需要用集合维护：例如实现同时发送文件和聊天功能，就需要多个线程多个Socket端口）**
      3. 验证消息类型为失败：关闭端口

  + 拉取在线用户数据：`void onlineFriendList(String userId)`:

    + 核心逻辑：向服务端`ServerConnectClientThread`发送消息类型为请求需要获得在线用户数据的Message和当前客户端用户名

    + 创建对象流，从管理线程集合中拿取当前发送请求的线程，`ManageClientConnectServerThread.getClientConnectServerThread(userId).getSocket()`，发送拉取在线用户信息的请求，服务端`ServerConnectClientThread`通过与客户端`UserClientService`对应的数据通道读取该信息

      **(实际上因为没有实现一个客户端多个线程的逻辑，这个也可以直接从当前端口中获取对象流)**

    + 服务端接收到消息后从管理线程的集合中拉取出所有在线用户的线程，通过内部封装的方法返回客户端`ClientConnectServerThread`在线用户列表。

#### ClientConnectServerThread（核心）：

+ 构造器封装一个Socket对象，保持服务端和客户端的数据通讯

+ 线程部分：

  + 创建对象流，如果服务端没有发送Message对象，线程会阻塞在接收Message对象的语句中；

  + 通过对象流读取Message消息类型的结果，条件分支处理Message类型的业务

### utils

### view：

### QQView：

+ 程序入口

+ 存放主菜单和二级菜单

  + 主菜单：

    + 用户校验函数：`userClientService.checkUser(String userId,String pwd)`

  + 二级菜单：

    1. 显示在线用户：`userClientService.onlineFriendList(String userId)`

    2. 群发消息：`messageClientService.sendMessageToAll(String content,String userId)`
    3. 私聊消息：`messageClientService.sendMessageToOne(String content,String userId,String getterId)`

### qqcommon

#### Message

+ 实现序列化接口`Serializable`

+ 属性：

  ```java
  private String sender; // 发送者
  private String getter; // 接收者
  private String content; // 消息内容
  private String sendTime; // 发送时间
  private String mesType; // 消息类型[成功 & 失败]
  
  private byte[] fileBytes;
  private int fileLen = 0;
  private String dest; // 目标路径
  private String src; // 源文件路径
  ```

  + 消息类型实现了MessageType方法：

    ```java
    String MESSAGE_LOGIN_SUCCEED = "1"; // 表示登陆成功
    String MESSAGE_LOGIN_FAIL = "2"; // 表示登陆失败
    String MESSAGE_COMM_MES = "3"; // 普通消息包
    String MESSAGE_GET_ONLINE_FRIEND = "4"; // 请求返回在线用户列表
    String MESSAGE_RET_ONLINE_FRIEND = "5"; // 返回在线用户列表
    String MESSAGE_CLIENT_EXIT = "6"; // 客户端退出请求
    String MESSAGE_TO_ALL_MES = "7"; // 群发消息包
    String MESSAGE_OFFLINE_MES = "9"; // 离线消息
    String MESSAGE_FILE_MES = "8"; // 文件消息（发送文件）
    ```





## 服务端 QQServer

### service

#### QQServer[🔞创建线程]

+ 创建服务端发送新闻线程`new Thread(new SendNewsToAllService()).start();`
+ 死循环监听客户端`Socket socket = serverSocket.accept();`
  + 创建对象流读取客户端`QQClient/UserClientService/checkUser`传入的user对象

+ 使用静态代码块（使其在类加载时便被创建）初始化在`ConcurrentHashMap`集合里的`validUsers`（用户数据）
  + HashMap没有处理线程安全，所以在多线程下尽量使用`ConcurrentHashMap`集合
  + 初期是创建User，pwd两个私有属性来实现用户校验功能，现代码使用集合维护多用户信息，后期还可以放在数据库里
+ 多用户验证功能：`boolean checkUser(String userId,String passwd)`
  + 有无id
  + 密码pwd是否正确
  + 返回boolean值
+ 多用户校验功能(核心):`void UserVerify(User user, Socket socket)`
  + 创建Message对象（qqcommon包中的类）
  + 创建对象流传输Message对象
  + 创建线程`ServerConnectClientThread`，线程里持有socket对象，维持与客户端QQClient的数据通道
  + 将线程放在`ManagerServerConnectClientThread`里维护：为了后续拉取在线用户做准备，每一次监听客户端的数据通道，就会创建一个线程。**客户端登陆用户时会自动调用用户校验，这时候就会发送消息向服务端，服务端通过accpect监听客户端的数据通道**

  

#### `ServerConnectClientThread`（核心）:

+ 作用：创建线程，将用户名和端口Socket绑定，维持客户端和服务端的数据通道连接

+ 创建对象流，准备读写对象

  ```java
  ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
  Message message = (Message) ois.readObject();// 向下转型对象流，使其变成消息类型
  ```

+ 条件处理Message类型

  + 拉取在线用户：从`ManagerServerConnectClientThread.getOnlineUser()`调用hashMap集合中所有的用户







#### ManagerServerConnectClientThread

+ 作用：存放服务器端口线程的集合
+ 使用HashMap进行客户端用户名和端口Socket的绑定**（HashMap里面的<k-v>:key保存String userId，value保存对应用户名的线程）**
+ 方法：
  + 添加线程到集合`void addClientThread(String userId,ServerConnectClientThread serverConnectClientThread)`
    + 在QQserver类里被调用，每一次监听客户端数据通道就会开辟一个新的线程，并将该线程放到集合中。



