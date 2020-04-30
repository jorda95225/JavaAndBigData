# 1.MySQL整体架构

1. 连接层：最上层是一些客户端和连接服务，主要完成一些连接处理、授权认证以及一些相关的安全方案
2. 服务层：管理控制工具、SQL接口、解析器、查询优化器、查询缓存
3. 引擎层：存储引擎，负责MySQL中数据的存储和提取，服务器通过API与存储引擎进行通信
4. 存储层：数据存储，将数据存储于裸设备的文件系统之上，并且完成与存储引擎的交互

# 2.MySQL的安装

环境：Centos7

下载地址：

http://dev.mysql.com/downloads/mysql/（官网）

http://mirrors.sohu.com/mysql/MySQL-5.7/（搜狐镜像）

安装过程：

1. 检查当前系统是否安装过MySQL，Centos7默认安装mariadb，需要检查

   ```
   rpm -qa|grep mariadb
   ```

   检查到如下结果：

   ```
   mariadb-libs-5.5.56-2.el7.x86_64
   ```

   使用以下命令卸载：

   ```
   rpm -e --nodeps mariadb-libs
   ```

2. 将MySQL安装包解压

   ```
   tar -zxvf mysql-5.7.28-1.el7.x86_64.rpm-bundle.tar
   ```

3. 按照以下顺序依次安装

   ```
   rpm -ivh mysql-community-common-5.7.28-1.el7.x86_64.rpm
   ```

   ```
   rpm -ivh mysql-community-libs-5.7.28-1.el7.x86_64.rpm
   ```

   ```
   rpm -ivh mysql-community-client-5.7.28-1.el7.x86_64.rpm
   ```

   ```
   rpm -ivh mysql-community-server-5.7.28-1.el7.x86_64.rpm
   ```

4. 删除/etc/my.cnf文件中datadir指向的目录下的内容

5. 初始化数据库

   ```
   mysqld --initialize --user=mysql
   ```

6. 查看临时生成的密码

   ```
   cat /var/log/mysqld.log
   ```

   ![image-20200430202026220](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200430202026220.png)

7. 启动MySQL服务

   ```
   service mysqld start
   ```

8. 使用临时密码登录

   ```
   mysql -uroot -p
   ```

9. 修改root用户密码

   ```
   set password = password("新密码")
   ```

# 3.MySQL安装位置

| 参数         | 路径                       | 解释                         | 备注                       |
| ------------ | -------------------------- | ---------------------------- | -------------------------- |
| --datadir    | /var/lib/mysql/            | mysql数据库文件的存放路径    |                            |
| --basedir    | /usr/bin                   | 相关命令目录                 | mysqladmin mysqldump等命令 |
| --plugin-dir | /usr/lib64/mysql/plugin    | mysql插件存放路径            |                            |
| --log-error  | /var/log/mysqld.log        | mysql错误日志路径            |                            |
| --pid-file   | /var/run/mysqld/mysqld.pid | 进程pid文件                  |                            |
| --socket     | /var/lib/mysql/mysql.sock  | 本地连接时用的unix套接字文件 |                            |
|              | /usr/share/mysql           | 配置文件目录                 | mysql脚本及配置文件        |
|              | /etc/init.d/mysql          | 服务启停相关脚本             |                            |

# 4.MySQL自启动

使用systemctl list-unit-files | grep mysqld.service命令查看，默认是自启动的

如果要取消自启动：systemctl disable mysqld.service

