# 1.基础知识

## 1.1 数据模型

Zookeeper的数据模型是一种树形结构，有一个根文件夹，下面有一些子文件夹。每一层级用/分开，只能用绝对路径查询，不能使用相对路径。

### 1.1.1 znode节点类型

1. 持久节点：一直保存，需要调用delete函数去删除
2. 临时节点：客户端因会话超时或发生异常关闭时，节点也被删除
3. 有序持久节点：在创建有序节点的时候，使用一个单调递增的数字作为后缀
4. 有序临时节点

### 1.1.2 节点状态结构

执行stat /locks可以看到节点的状态信息

![image-20200510180520216](C:\Users\11579\AppData\Roaming\Typora\typora-user-images\image-20200510180520216.png)

|    状态属性    |                      说明                      |
| :------------: | :--------------------------------------------: |
|     cZxid      |             该节点被创建时的事务ID             |
|     cTime      |                该节点的创建时间                |
|     mZxid      |         该节点最后一次被更新时的事务ID         |
|     mtime      |           该节点最后一次被更新的时间           |
|     pZxid      |   该节点的子节点列表最后一次被修改时的事务ID   |
|    cversion    |                 子节点的版本号                 |
|  dataVersion   |                数据节点的版本号                |
|   aclVersion   |                节点的ACL版本号                 |
| ephemeralOwner | 创建该临时节点的SessionID，如果是持久节点，为0 |
|   dataLength   |                 数据内容的长度                 |
|  numChildren   |              当前节点的子节点个数              |

### 1.1.3 数据节点的版本

版本信息表示的是对节点数据内容、子节点信息或者ACL信息的修改次数

## 1.2 ACL权限控制

## 1.3 Watch监控
