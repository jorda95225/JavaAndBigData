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
   tar -vf mysql-5.7.28-1.el7.x86_64.rpm-bundle.tar
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

   ```sql
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

# 5.MySQL修改字符集

1. 常用命令

   ```sql
   create database test;//创建数据库
   use database;//使用数据库
   create table mytable(id int,name varchar(40));//创建表
   insert into mytable(id,name) values(1,'jordan');//插入数据
   ```

   ![image-20200501123855896](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501123855896.png)

   插入中文会报错，因为当前数据库编码不支持中文

   查看当前数据库的编码格式

   ```sql
   show create database test;
   ```

   ![image-20200501124119964](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501124119964.png)

   查看表的编码格式

   ```sql
   show create table mytable;
   ```

   ![image-20200501124226411](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501124226411.png)

   查询所有跟字符集相关的信息

   ```sql
   show variables like '%char%'
   ```

   ![image-20200501124631267](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501124631267.png)

2. 永久修改默认的编码字符集

   ```
   vim /etc/my.cnf
   添加以下配置：
   [client]
   default-character-set=utf8
   [mysqld]
   character_set_server=utf8
   collation-server=utf8_general_ci
   [mysql] 
   default-character-set=utf8
   ```

3. 重新启动MySQL服务

   ```
   service mysqld restart
   ```

4. 修改已有库和表的编码，因为参数创建只会对新建的数据库有效，不会对已有的设定产生变化

   ```sql
   alter database test character set 'utf8';
   alter table mytable convert to character set 'utf8';
   ```

# 6.MySQL的杂项配置

## 1.设置大小写不敏感

1. 查看大小写是否敏感

   ```sql
   show variables like '%lower_case_table_names%';
   ```

   ![image-20200501131654086](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501131654086.png)

   windows默认大小写不敏感，linux大小写敏感

2. 设置大小写不敏感

   ```
   vim /etc/my.cnf
   #追加如下内容，然后重启服务
   [mysqld]
   lower_case_table_names = 1
   # 0		大小写敏感
   # 1		大小写不敏感
   # 2		创建的表和DB依据语句上格式存放，凡是查找都是转换为小写进行
   
   ```

## 2.sql_mode

sql_mode中定义了MySQL中对sql语句语法的校验规则

如果该值为空值（非严格模式），这种情况下是可以允许一些非法操作的，比如允许一些非法数据的插入，生产环境必须将这个值设置为严格模式，所以开发测试环境应该也设置为严格模式，可以在这个阶段就发现问题。

1. sql_mode常用值

| ONLY_FULL_GROUP_BY         | 对于GROUP BY聚合操作，如果在SELECT中的列，没有在GROUP  BY中出现，那么这个SQL是不合法的，因为列不在GROUP BY从句中 |
| -------------------------- | ------------------------------------------------------------ |
| NO_AUTO_VALUE_ON_ZERO      | 该值影响自增长列的插入。默认设置下，插入0或NULL代表生成下一个自增长值。如果用户 希望插入的值为0，而该列又是自增长的，那么这个选项就有用了 |
| STRICT_TRANS_TABLES        | 在该模式下，如果一个值不能插入到一个事务表中，则中断当前的操作，对非事务表不做限制 |
| NO_ZERO_IN_DATE            | 在严格模式下，不允许日期和月份为零                           |
| NO_ZERO_DATE               | 设置该值，mysql数据库不允许插入零日期，插入零日期会抛出错误而不是警告 |
| ERROR_FOR_DIVISION_BY_ZERO | 在INSERT或UPDATE过程中，如果数据被零除，则产生错误而非警告。如 果未给出该模式，那么数据被零除时MySQL返回NULL |
| NO_AUTO_CREATE_USER        | 禁止GRANT创建密码为空的用户                                  |
| NO_ENGINE_SUBSTITUTION     | 如果需要的存储引擎被禁用或未编译，那么抛出错误。不设置此值时，用默认的存储引擎替代，并抛出一个异常 |
| PIPES_AS_CONCAT            | 将"\|\|"视为字符串的连接操作符而非或运算符，这和Oracle数据库是一样的，也和字符串的拼接函数Concat相类似 |
| ANSI_QUOTES                | 启用ANSI_QUOTES后，不能用双引号来引用字符串，因为它被解释为识别符 |
| ORACLE                     | 设置等同于PIPES_AS_CONCAT,  ANSI_QUOTES, IGNORE_SPACE, NO_KEY_OPTIONS, NO_TABLE_OPTIONS,  NO_FIELD_OPTIONS, NO_AUTO_CREATE_USER |

2.查看当前sql_mode

```sql
select @@sql_mode;
```

3.临时修改sql_mode

```sql
set @@sql_mode='';
```

4.永久修改

```
vim /etc/my.cnf
#添加下列配置，然后重启mysql即可
[mysqld]
sql_mode=''
```

# 7.MySQL用户管理

MySQL的用户管理在mysql库的user表中

1. 相关命令

   ```sql
   create user jordan identified by '123456';//创建名为jordan的用户，密码为123456
   select host,user,authentication_string,select_priv,insert_priv,drop_priv from mysql.user;//查看用户和权限的相关信息
   set password=password('123456');//修改当前用户的密码
   update mysql.user set authentication_string=password('123456') where user = 'jordan';//修改其他用户的密码,mysql5.7通过authentication_string表示密码列，所有通过user表的修改，必须用flush privileges命令才能生效
   update mysql.user set user='jordan' where user='lisi'//修改用户名
   drop user lisi;//删除用户，不要通过delete from user u where user='lisi'删除，系统会有残留信息保留
   ```

2. 示例

   ```sql
   select host,user,authentication_string,select_priv,insert_priv,drop_priv from mysql.user;
   ```

   ![image-20200501135844531](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501135844531.png)

   ```sql
   说明：
   host：表示连接类型
   	%：表示所有远程通过TCP连接的方式
   	IP地址：通过指定IP地址进行TCP方式的连接
   	机器名：通过指定网络中的机器名进行TCP方式的连接
   	::1：IPv6的本地IP地址，等同于IPv4的127.0.0.1
   	localhost：本地方式通过命令行方式连接
   user：表示用户名
   	同一用户通过不同方式连接的权限不一样
   authentication_string：密码
   	所有密码串通过authentication_string生成的密文字符串，加密算法为MYSQLSHA1，不可逆
   select_priv,insert_priv,drop_priv等：
   	该用户所拥有的权限
   ```

3. 远程连接MySQL
   当前root用户对应的host值为localhost，只能本机连接
   需要将host的值改为%，表示允许所有远程通过TCP方式的连接

   ```sql
   update user set host='%' where user='root';
   flush privileges;
   ```

# 8.MySQL权限管理

1. 授予权限

   ```sql
   grant 权限1,权限2,... on 数据库名称.表名 to 用户名@用户地址 identified by '连接口令';//该权限如果发现没有该用户，会直接新建一个用户
   grant all privileges on *.* to jordan@'%' identified by '123456';//授予通过网络方式登录的jordan用户，对所有库所有表的全部权限，密码设为123456
   ```

2. 收回权限

   ```sql
   show grants;//查看当前用户权限
   revoke  [权限1,权限2,…权限n] on 库名.表名 from 用户名@用户地址;//收回权限命令
   revoke all privileges on mysql.* from jordan@localhost;//收回mysql库下的所有权限
   revoke select,insert,update,delete on mysql.* from jordan@localhost;//收回mysql库下的增删改查权限
   权限收回后，用户必须重新登录才能生效
   ```

# 9.查看sql执行周期

```sql
1.查看profile是否开启
   show variables like '%profiling%';
2.开启profiling
   set profiling = 1;
3.使用profile查看最近的几次查询
   show profiles;
4.根据Query_ID查看sql的具体执行步骤
   show profile cpu,block io for query 1;、
5.大致的查询流程
   mysql客户端通过协议与mysql服务器建立连接，发送查询语句，首先检查查询缓存，如果命中直接返回结果，否则进行语句解析，mysql通过关键字将sql语句进行解析，并且声称一颗对应的“解析树”，mysql解析器将使用mysql语法规则验证和解析查询，预处理器根据一些mysql规则进一步检查解析树是否合法，当解析树合法后，由优化器将其转换成Explain，一条查询可以有很多种执行方式，最后都返回相同的结果，优化器的作用就是找到其中最好的Explain
   mysql默认使用的BTREE索引，并且在目前来说，musql最多只用到表中的一个索引
6.SQL的书写顺序
   select-distinct-from-join on-where-group by-having-order by-limit
7.真正执行顺序
   经常出现的查询顺序：from-on-join-where-group by-having-select-distinct-order by-limit
```

# 10.查询缓存

1. 查看缓存相关设置

   ```sql
   show variables like '%query_cache%';
   ```

   ![image-20200501144100986](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501144100986.png)

   ```
   参数解释：
   query_cache_limit： 超过此大小的查询将不再缓存。
   query_cache_min_res_unit：缓存块的最小值。
   query_cache_size：缓存大小值
   query_cache_type：缓存类型，决定缓存什么样的查询。
   	0  表示关闭查询缓存OFF
   	1  表示开启查询缓存ON
   	2  表示SQL语句中有SQL_CACHE关键词时才缓存..
      例如: select  SQL_CACHE  name  from  t_user  where id = 1001;
   query_cache_wlock_invalidate：表示当有其它客户端正在对MyISAM表进行写操作时，读请求是要等write lock释放资源后再查询还是允许直接从query cache中读取结果
   单位是字节（8位，b）
   ```

2. 开启MySQL查询缓存

   ```
   vim /etc/my.cnf
   添加：
   [mysqld]
   query_cache_type=1
   重启mysql
   service mysqld restart
   ```

3. 使用查询缓存

   ```
   1.开启profiling
   set profiling=1;
   2.执行两条相同的sql
   select * from mytable;
   select * from mytable;
   3.查看最近执行的sql
   show profiles;
   4.查看两条相同SQL的执行周期
   show profile cpu,block io for query 7;
   show profile cpu,block io for query 8;
   可以看出第二次执行的sql结果是从缓存中查询的
   ```

4. 查询不使用缓存

   ```
   1.如果在开启了查询缓存的情况，SQL执行时不想使用缓存，可以在SQL中显式执行sql_no_cache
   select sql_no_cache * from mytable;
   ```

# 11.MySQL存储引擎

1. 查看支持的存储引擎

   ```
   show engines;
   ```

   ![image-20200501151241553](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501151241553.png)

2. 查看默认的存储引擎

   ```
   show variables like '%storage_engine%';
   ```

   ![image-20200501151353257](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501151353257.png)

3. 存储引擎介绍

   ```
   1.InnoDB
   默认事务型引擎，用来处理大量的短期事务，除非有特别的原因，否则优先考虑InnoDB
   2.MyISAM
   提供全文索引、压缩、空间函数等特性，但是不支持事务和行级锁，崩溃后无法安全恢复
   3.Archive
   档案引擎，只支持insert和select操作，适合日志和数据采集类的应用根据英文的测试结论来看，Archive表比MyISAM表要小大约75%，比支持事务处理的InnoDB表小大约83%
   4.Blackhole
   没有实现任何存储机制，会丢弃所有插入的数据，不做任何保存，服务器会记录Blackhole表的日志，可以用于复制数据到备用库，或者简单地记录到日志，但是会遇到很多问题，不推荐
   5.CSV
   将普通的CSV文件作文MySQL的表来处理，不支持索引，存储的数据可以直接在操作系统里面用文本编辑器或者Excel读取
   6.memory
   快速访问数据，并且数据不会被修改，重启以后丢失也没有关系，可以使用这种表，比MyISAM表至少快一个数量级
   7.Federated
   默认禁用，是访问其他MySQL服务器的一个代理
   ```

4. MyISAM和InnoDB

| 对比项         | MyISAM                                                   | InnoDB                                                       |
| -------------- | -------------------------------------------------------- | ------------------------------------------------------------ |
| 外键           | 不支持                                                   | 支持                                                         |
| 事务           | 不支持                                                   | 支持                                                         |
| 行表锁         | 表锁，即使操作一条记录也会锁住整个表，不适合高并发的操作 | 行锁,操作时只锁某一行，不对其它行有影响，  适合高并发的操作  |
| 缓存           | 只缓存索引，不缓存真实数据                               | 不仅缓存索引还要缓存真实数据，对内存要求较高，而且内存大小对性能有决定性的影响 |
| 关注点         | 读性能                                                   | 并发写、事务、资源                                           |
| 默认安装       | Y                                                        | Y                                                            |
| 默认使用       | N                                                        | Y                                                            |
| 自带系统表使用 | Y                                                        | N                                                            |

# 12.join

1. 常见的join说明

   ```
   内连接：inner join 交集
   外连接：left outer join,right outer join
   	左外连接左主右从，右外连接右主左从
   	主表取所有，从表取匹配，未匹配数据通过null来补全
   ```

# 13.索引

## 13.1 索引

索引是帮助MySQL高效获取数据的**数据结构**，可以理解为排好序的快速查找数据结构

一般来说索引本身也很大，不可能全部存储在内存中，因此索引往往以文件形式存储在磁盘上

索引在MySQL中分三类：B+树索引、Hash索引、全文索引

## 13.2 索引优劣势

优点：提高数据检索的效率，降低数据库的IO成本

劣势：会降低表的更新速度，更新表时不仅要保存数据，还要保存索引文件，更新添加了索引列的字段，要调整索引信息

实际上索引也是一张表，该表保存了主键与索引字段，并指向实体表的记录，所以索引列也是要占用空间

## 13.3 MySQL索引结构

### 13.3.1 B-tree（Balance Tree）

![image-20200501160401674](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501160401674.png)

一颗b树，浅蓝色的块称为一个磁盘块（页），每个磁盘块包含几个数据项（深蓝色）和指针（黄色），比如磁盘块1包含数据项17和35，指针P1、P2、P3，P1指向小于17的磁盘块，P2指向在17和35之间的磁盘块，P3指向大于35的磁盘块。

如果查找数据29，首先会把磁盘1由磁盘加载到内存，此时发生一次IO，在内存中用二分查找确定29在17和35之间，锁定磁盘块1的P2指针，内存中的时间很短可以忽略不计，通过磁盘块1的P2指针的磁盘地址把磁盘块3由磁盘加载到内存，发生第二次IO，29在26和30之间，锁定磁盘块3的P2指针，通过指针加载磁盘块8到内存中，发生第三次IO，同时内存中二分查找找到29，查询结束，总共三次IO

B树中结点可以有多个元素，有多个分支

为什么效率比二叉查找树高：

把磁盘里的数据加载到内存的时候，是以页为单位来加载的，节点与节点之间的数据是不连续的，不同节点可能分布在不同的磁盘页中，B树的磁盘寻址次数会比较少，虽然可能比较次数多，但是内存的运算速度很快，所以总体来说还是B树快的多

磁盘加载次数和树的高度相关联，高度越高加载次数越多，所以对于这种文件索引的存储，一般选择矮胖的树形结构

### 13.3.2 B+tree

![image-20200501161129802](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501161129802.png)

B+树的非叶子结点仅仅存储关键字信息和儿子的指针，数据都存储在叶子结点上，所有叶子结点的数据组合起来就是完整的数据，因此每个磁盘块包含的信息会更多，这样就决定了加载一个磁盘块可以获取到更多的关键字，可以减少IO操作

在B+树上增加了顺序访问指针，也就是每个叶子结点增加一个指向相邻叶子结点的指针

B+树的所有数据是按照顺序排列的，所以范围查找、排序查找、分组查找、去重查找都比较简单，各个页直接通过双向链表连接，叶子节点中的数据通过单向链表连接

### 13.3.3 B-Tree与B+Tree的区别

1. B树的关键字和记录是放在一起的，B+树的非叶子节点只存储关键字和下一个节点的索引，记录只放在叶子节点中
2. B树中，越靠近根节点的记录查找时间越快，B+树种每个记录的查找时间基本是一样的，都要从根节点走到叶子节点，还要在叶子节点中比较关键字，从这个角度看B树比B+树性能要好，但是实际应用中B+树每个节点可以容纳的关键字更多，比B树高度小，磁盘访问次数小

### 13.3.4 B+Tree更适合实际应用中操作系统的文件索引和数据库索引

1. B+树的磁盘读写代价更低
2. B+树的查询效率更加稳定

### 13.3.5 聚簇索引和非聚簇索引

聚簇索引表示数据行和相邻的键值聚簇的存储在一起，即数据行在磁盘的排列和索引的排序保持一致

好处：数据紧密相连，数据库不用从多个数据块中提取数据，节省大量io操作

限制：mysql数据库目前只有InnoDB支持聚簇索引，而且每个表只能有一个聚簇索引，一般是主键，主键之外的索引为非聚簇索引

非聚簇索引的叶子节点不存储表中的数据，存储该列对应的主键，想要查找数据需要根据主键取聚簇索引中查找，这个过程称为回表

![image-20200501165559529](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501165559529.png)

假设现在查找id>=18且id<40的数据，id为主键，查找过程如下：

- 一般根节点常驻内存，即页1已经在内存中，直接读取页1，首先查找id=18的键值，根据指针p2，定位页3
- 从磁盘读取页3后将页3放入内存中，然后进行查找，根据p1定位页8
- 从磁盘读取页8后将页8放入内存中，因为页中的数据是链表进行连接的，而且键值按照顺序存放，可以根据二分查找定位到键值18
- 此时因为是范围查找，而且所有数据都在叶子节点，并且有序排列，所以可以对页8中的键值依次遍历查找满足条件的数据，可以一直找到键值为22的数据，此时页8中没有数据，需要拿着页8中的指针p取读取页9中的数据
- 加载页9到内存中，和页8一样，一直到将页12加载到内从中，发现41大于40，不满足条件，查找终止，最终找到12条数据

![image-20200501170324715](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501170324715.png)

非聚簇索引叶子节点中不存储所有数据，存储键值和主键，比如1-1左边的1表示索引键值，右边1表示主键值，比如找键值为33的，最终找到主键值47，找到主键后再到聚簇索引中查找对应的具体数据

问题：为什么文件系统和数据库的索引用B树不用红黑树或者有序数组或者hash？

因为文件系统和数据库的索引都是存在硬盘上，数据量大的话不一定能一次性加载进内存，B树可以每次加载一个节点一步步往下找，并且数据库中经常会选择多条，B+树索引有序，又有链表相连，查询效率就比hash快很多了

## 13.4 MySQL索引分类

### 13.4.1 单值索引

一个索引只包含单个列，一个表可以有多个单列索引

```sql
1)	随表一起创建：
CREATE TABLE customer (
  id INT(10) AUTO_INCREMENT ,
  customer_no VARCHAR(200),
  customer_name VARCHAR(200),
  PRIMARY KEY(id),
  KEY (customer_name)
);
2)	单独建单值索引：
CREATE  INDEX idx_customer_name ON customer(customer_name);//index后面是索引名字
3)	查看某个表的索引
show index from 表名   
show keys from  表名
```

### 13.4.2 唯一索引

索引列的值必须唯一，但可以有空值

```sql
1)	随表一起创建：
CREATE TABLE customer (
  id INT(10)  AUTO_INCREMENT ,
  customer_no
  VARCHAR(200),
  customer_name VARCHAR(200),
  PRIMARY KEY(id),
  KEY (customer_name),
  UNIQUE (customer_no)
);
2)	单独建唯一索引：
CREATE UNIQUE INDEX idx_customer_no ON customer(customer_no);

```

### 13.4.3 主键索引

设定为主键后数据库会自动建立索引，innodb为聚簇索引

```sql
1)	随表一起建索引
CREATE TABLE customer (
 id INT(10) AUTO_INCREMENT ,
 customer_no
 VARCHAR(200),
 customer_name VARCHAR(200),
 PRIMARY KEY(id) 
);
2)	单独建主键索引：
ALTER TABLE customer add PRIMARY KEY customer(customer_no);
3)	删除建主键索引：
ALTER TABLE customer drop PRIMARY KEY ;
4)	需要注意的问题：
设置为自增的主键上的索引不能删除.
```

### 13.4.4 复合索引

一个索引包含多个列

```sql
1)	随表一起建索引：
CREATE TABLE customer (
  id INT(10)  AUTO_INCREMENT ,
  customer_no VARCHAR(200),
  customer_name VARCHAR(200),
  PRIMARY KEY(id),
  KEY (customer_name),
  UNIQUE (customer_name),
  KEY (customer_no,customer_name)
);
2)	单独建索引：
CREATE  INDEX idx_no_name ON customer(customer_no,customer_name);

```

### 13.4.5 基本语法

| 创建                                                         | CREATE [UNIQUE  ] INDEX [indexName] ON  table_name(column))  |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 删除                                                         | DROP INDEX [indexName] ON mytable;                           |
| 查看                                                         | SHOW INDEX FROM table_name\G                                 |
| 使用Alter命令                                                | ALTER TABLE tbl_name ADD  PRIMARY KEY (column_list) : 该语句添加一个主键，这意味着索引值必须是唯一的，且不能为NULL。 |
| ALTER TABLE tbl_name ADD INDEX index_name  (column_list): 添加普通索引，索引值可出现多次。 |                                                              |
| ALTER  TABLE tbl_name ADD FULLTEXT index_name (column_list):该语句指定了索引为 FULLTEXT ，用于全文索引。 |                                                              |

## 13.5 索引创建时机

### 13.5.1 适合创建索引的情况

1. 主键自动建立唯一索引
2. 频繁作为查询条件的字段应该创建索引
3. 查询中与其他表关联的字段，外键关系建立索引
4. 组合索引性价比更高
5. 查询中排序的字段，排序字段通过索引取访问会大大提高排序速度
6. 查询中统计或者分组的字段

### 13.5.2 不适合创建索引的情况

1. 表记录太少
2. 经常增删改的表或者字段、
3. where条件里用不到的字段
4. 过滤性不好（列）的不适合建索引

# 14.Explain

可以模拟优化器执行SQL查询语句，从而知道MySQL是如何处理SQL语句的，分析查询语句或者表结构的性能瓶颈

## 14.1 字段解释：

- id：select查询序列号，表示查询中执行select子句或者操作表的顺序
- select_type：区别普通查询、联合查询、子查询等复杂查询
- table：数据基于哪张表
- **type**：查询的访问类型，比较重要
- **possible_keys**：可能应用在这个表中的索引，一个或者多个
- **key**：实际使用的索引，为null则没有使用索引
- **key_len**：索引中使用的字节数
- **ref**：显示索引的哪一列被使用了
- **rows**：显示MySQL认为它执行查询时必须检查的行数，不精确
- filtered：返回结果的行占需要读到的行的百分比
- **Extra**：其他额外的重要信息，比如分组、排序

## 14.2 字段分析：

1. 创建测试数据

   ```sql
   CREATE TABLE t1(id INT(10) AUTO_INCREMENT,content  VARCHAR(100) NULL ,  PRIMARY KEY (id));
    CREATE TABLE t2(id INT(10) AUTO_INCREMENT,content  VARCHAR(100) NULL ,  PRIMARY KEY (id));
    CREATE TABLE t3(id INT(10) AUTO_INCREMENT,content  VARCHAR(100) NULL ,  PRIMARY KEY (id));
    CREATE TABLE t4(id INT(10) AUTO_INCREMENT,content  VARCHAR(100) NULL ,  PRIMARY KEY (id));
   
    INSERT INTO t1(content) VALUES(CONCAT('t1_',FLOOR(1+RAND()*1000)));
    INSERT INTO t2(content) VALUES(CONCAT('t2_',FLOOR(1+RAND()*1000)));
    INSERT INTO t3(content) VALUES(CONCAT('t3_',FLOOR(1+RAND()*1000)));
    INSERT INTO t4(content) VALUES(CONCAT('t4_',FLOOR(1+RAND()*1000)));
   ```

2. id
   id相同，执行顺序由上至下

   ```
   EXPLAIN select * from t1,t2,t3 where t1.id = t2.id and t2.id = t3.id ;
   ```

   ![image-20200501183709001](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200501183709001.png)

   id不同，如果是子查询，id序号会递增，id值越大优先级越高

   id每个号码表示一次独立的查询，一个sql的查询次数越少越好

3. select_type
   SIMPLE：单表查询

   PRIMARY：查询中包含任何复杂的子部分，最外层查询被标记为Primary
   DERIVED： 在FROM查询中的子查询，结果存放在临时表中

   SUBQUERY：在WHERE列表中包含的子查询

   DEPENDENT SUBQUERY：在SELECT或WHERE列表中包含了子查询,子查询基于外层（一组值）

   UNCACHEABLE SUBQUREY：当使用了@@来引用系统变量的时候，不会使用缓存

   UNION：若第二个SELECT出现在UNION之后，则被标记为UNION；若UNION包含在FROM子句的子查询中,外层SELECT将被标记为：DERIVED

   UNION RESULT：从UNION表获取结果的SELECT

4. type

   从最好到最坏依次是：

   system > const > **eq_ref** > **ref** > fulltext > ref_or_null > index_merge > unique_subquery > index_subquery > **range** > index > **ALL** 

   一般来说保证查询至少到range级别，最好能达到ref

   ```
   1.system
   表只有一行记录（等于系统表），const类型的特列，平时不会出现，可以忽略不计
   2.const
   表示通过索引一次就找到了，const用于比较primary key或者unique索引。因为只匹配一行数据，所以很快，比如将主键置于where列表中，MySQL就能将该查询转换为一个常量
   3.eq_ref
   唯一性索引扫描，对于每个索引键，表中只有一条记录与之匹配。常见于主键或唯一索引扫描
   4.ref
   非唯一性索引扫描，返回匹配某个单独值的所有行.本质上也是一种索引访问，它返回所有匹配某个单独值的行，然而，它可能会找到多个符合条件的行，所以他应该属于查找和扫描的混合体
   5.range
   只检索给定范围的行,使用一个索引来选择行。key 列显示使用了哪个索引一般就是在你的where语句中出现了between、<、>、in等的查询这种范围扫描索引扫描比全表扫描要好，因为它只需要开始于索引的某一点，而结束于另一点，不用扫描全部索引
   6.index
   出现index是sql使用了索引但是没有通过索引进行过滤，一般是使用了覆盖索引或者是利用索引进行了排序分组
   7.all
   Full Table Scan，将遍历全表以找匹配的行（需要优化）
   8.index_merge
   在查询过程中需要多个索引组合使用，通常出现在有 or 的关键字的sql中
   9.ref_or_null
   对于某个字段既需要关联条件，也需要null值得情况下。查询优化器会选择用ref_or_null连接查询
   10.index_subquery
   利用索引来关联子查询，不再全表扫描
   11.unique_subquery
   该联接类型类似于index_subquery。 子查询中的唯一索引
   ```

5. possible_keys
   不一定被查询实际使用

6. key

7. key_len
   越长说明索引使用的越充分

   计算：

   ```
   1） 先看索引上字段的类型+长度比如 int=4 ;  varchar(20) =20 ; char(20) =20  
   2） 如果是varchar或者char这种字符串字段，视字符集要乘不同的值，比如utf-8  要乘 3,GBK要乘2
   3） varchar这种动态字符串要加2个字节
   4） 允许为空的字段要加1个字节  
   ```

8. ref

9. rows
   越少越好

10. Extra

    ```
    1.Using filesort
    说明mysql会对数据使用一个外部的索引排序，而不是按照表内的索引顺序进行读取，MySQL中无法利用索引完成的排序操作称为“文件排序”，该情况下测试表数据不能太小
    2.Using temporary
    使用临时表保存中间结果,MySQL在对查询结果排序时使用临时表。常见于排序 order by 和分组查询 group by
    3.Using index
    Using index表示相应的select操作中使用了覆盖索引(Covering Index)，避免访问了表的数据行，效率不错！
    如果同时出现using where，表明索引被用来执行索引键值的查找;如果没有同时出现using where，表明索引只是用来读取数据而非利用索引执行查找
    4.Using where
    使用了where过滤
    5.Using join buffer
    使用了连接缓存
    6.impossible where
    where子句的值总是false，不能用来获取任何元素
    7.select tables optimized away
    在没有GROUP BY子句的情况下，基于索引优化MIN/MAX操作或者对于MyISAM存储引擎优化COUNT(*)操作，不必等到执行阶段再进行计算，查询Explian生成的阶段即完成优化
    ```

# 15.单表使用索引常见的索引失效

## 15.1 全职匹配我最爱

查询的字段按照顺序在索引中都能匹配到（能使用到的索引都使用），查询字段的顺序，跟使用索引中字段的顺序无关

## 15.2 最佳左前缀法则

复合索引需要遵循最佳左前缀法则，如果索引了多列，查询必须从索引的最左前列开始并且不跳过索引中的列，过滤条件要使用索引必须按照索引建立时的顺序，依次满足，一旦跳过某个字段，索引后面的字段都无法被使用

原因：在建立复合索引时，最前面的索引的节点上面维护了后面的索引的树，所以必须从开头去找

## 15.3 索引列上做计算

不要在索引列上做任何操作（计算、函数、自动或手动类型转换），会导致索引失效而转向全表扫描

## 15.4 索引列上进行范围查询

使用了范围查询，后面的索引在条件里面会失效，所以将可能做范围查询的字段的索引顺序放在最后

## 15.5 使用覆盖索引

不要写select *

## 15.6 使用不等于（!=或者<>）

使用不等于时，有时会无法使用索引导致全表扫描

## 15.7 is not null和is null

is not null用不到索引,is null可以用到索引

## 15.8 like的前后模糊匹配

前缀模糊查询会导致索引失效('%开头')，前面不固定，但是如果后面是不固定，前面固定可以使用索引

## 15.9 使用or

or查询会导致索引失效（不确定）

可以使用union all或者union来替代

## 15.10 口诀

```
全值匹配我最爱，最左前缀要遵守；
带头大哥不能死，中间兄弟不能断；
索引列上少计算，范围之后全失效；
LIKE百分写最右，覆盖索引不写*；
不等空值还有OR，索引影响要注意。
```



