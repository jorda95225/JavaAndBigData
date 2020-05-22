第一题：
需求：
有如下访问数据
userId  visitDate   visitCount
u01 2017/01/21   5
u02 2017/01/23   6
u03 2017/01/22   8
u04 2017/01/20   3
u01 2017/01/23   6
u01 2017/02/21   8
U02 2017/01/23   6
U01 2017/02/22   4
统计每个用户每个月的累计访问次数，如下：
用户id    月份  小计  累积
u01 2017-01 11  11
u01 2017-02 12  23
u02 2017-01 12  12
u03 2017-01 8   8
u04 2017-01 3   3
实现：
1.数据准备
CREATE TABLE test1 (
    userId string,
    visitDate string,
    visitCount INT )
ROW format delimited FIELDS TERMINATED BY "\t";
INSERT INTO TABLE test1
VALUES
( 'u01', '2017/01/21', 5 ),
( 'u02', '2017/01/23', 6 ),
( 'u03', '2017/01/22', 8 ),
( 'u04', '2017/01/20', 3 ),
( 'u01', '2017/01/23', 6 ),
( 'u01', '2017/02/21', 8 ),
( 'u02', '2017/01/23', 6 ),
( 'u01', '2017/02/22', 4 );
2.查询SQL
set hive.cli.print.header=true;
-----------------------------------------
select
a.userId,
a.month,
a.sub_total,
sum(a.sub_total) over(partition by a.userId order by a.month) as total
from
(
select
userId,
substring(regexp_replace(visitDate,'/','-'),1,7) as month,
sum(visitCount) as sub_total
from
test1
group by userId,substring(regexp_replace(visitDate,'/','-'),1,7)
) a;
第二题：
需求：
表结构为user_id shop
求：
1.每个店铺的uv
2.每个店铺访问次数top3的访客信息，输出店铺名称、访客id、访问次数
实现：
1.数据准备
CREATE TABLE test2 (
     user_id string,
     shop string )
ROW format delimited FIELDS TERMINATED BY '\t';
INSERT INTO TABLE test2 VALUES
( 'u1', 'a' ),
( 'u2', 'b' ),
( 'u1', 'b' ),
( 'u1', 'a' ),
( 'u3', 'c' ),
( 'u4', 'b' ),
( 'u1', 'a' ),
( 'u2', 'c' ),
( 'u5', 'b' ),
( 'u4', 'b' ),
( 'u6', 'c' ),
( 'u2', 'c' ),
( 'u1', 'b' ),
( 'u2', 'a' ),
( 'u2', 'a' ),
( 'u3', 'a' ),
( 'u5', 'a' ),
( 'u5', 'a' ),
( 'u5', 'a' );
2.查询SQL
1)
select
shop,
count(distinct user_id) as uv
from test2
group by shop;
2)
select
b.shop,
b.user_id,
b.visit_count
from
(
select
a.shop,
a.user_id,
a.visit_count,
row_number() over(partition by a.shop order by a.visit_count desc) as rank
from
(
select
shop,
user_id,
count(*) as visit_count
from
test2
group by shop,user_id
) a
) b
where b.rank <= 3;
第三题：
需求：
表字段：Date,Order_id，User_id，amount
数据样例:2017-01-01,10029028,1000003251,33.57
求：
1)给出2017年每个月的订单数、用户数、总成交金额
2)给出2017年11月的新客数(在11月有第一笔订单)
实现：
1.数据准备
CREATE TABLE test3 (
    dt string,
    order_id string,
    user_id string,
    amount DECIMAL ( 10, 2 ) )
ROW format delimited FIELDS TERMINATED BY '\t';
INSERT INTO TABLE test3 VALUES ('2017-01-01','10029028','1000003251',33.57);
INSERT INTO TABLE test3 VALUES ('2017-01-01','10029029','1000003251',33.57);
INSERT INTO TABLE test3 VALUES ('2017-01-01','100290288','1000003252',33.57);
INSERT INTO TABLE test3 VALUES ('2017-02-02','10029088','1000003251',33.57);
INSERT INTO TABLE test3 VALUES ('2017-02-02','100290281','1000003251',33.57);
INSERT INTO TABLE test3 VALUES ('2017-02-02','100290282','1000003253',33.57);
INSERT INTO TABLE test3 VALUES ('2017-11-02','10290282','100003253',234);
INSERT INTO TABLE test3 VALUES ('2018-11-02','10290284','100003243',234);
2.查询SQL
1)
select
substring(dt,1,7) as month,
count(order_id) as order_count,
count(distinct user_id) as user_count,
sum(amount) as total_amount
from
test3
group by substring(dt,1,7)
having substring(month,1,4) = '2017';
2)
select
count(*) as new_count
from
test3
group by user_id
having substring(min(dt),1,7) = '2017-11';
第四题：
需求：
用户表：user_id,name,age
看电影的记录表：user_id,url
根据年龄段观看电影的次数进行排序
实现：
1.数据准备
CREATE TABLE test4user
   (user_id string,
    name string,
    age int);
CREATE TABLE test4log
    (user_id string,
    url string);
INSERT INTO TABLE test4user VALUES
('001','u1',10),
('002','u2',15),
('003','u3',15),
('004','u4',20),
('005','u5',25),
('006','u6',35),
('007','u7',40),
('008','u8',45),
('009','u9',50),
('0010','u10',65);
INSERT INTO TABLE test4log VALUES
('001','url1'),
('002','url1'),
('003','url2'),
('004','url3'),
('005','url3'),
('006','url1'),
('007','url5'),
('008','url7'),
('009','url5'),
('0010','url1');
2.查询SQL
--先按照年龄范围划分年龄段，然后查询每个用户观看的次数，关联两个表，按照年龄段分组并求和排序
select
t2.age_phase,
sum(t1.cnt) as view_cnt
from
(
select
user_id,
count(*) as cnt
from
test4log
group by user_id
) t1
join
(
select
user_id,
(case when age <= 10 and age > 0 then '0-10'
when age <= 20 and age > 10 then '10-20'
when age <= 30 and age > 20 then '20-30'
when age <= 40 and age > 30 then '30-40'
when age <= 50 and age > 40 then '40-50'
when age <= 60 and age > 50 then '50-60'
else '70以上' end
) as age_phase
from
test4user
) t2
on t1.user_id = t2.user_id
group by t2.age_phase
order by view_cnt;
第五题：
需求：
表结构：日期 用户 年龄
求所有用户和活跃用户的总数及平均年龄(活跃用户：连续两天都有访问记录)
实现：
1.数据准备
CREATE TABLE test5(
dt string,
user_id string,
age int)
ROW format delimited fields terminated BY ',';
INSERT INTO TABLE test5 VALUES
('2019-02-11','test_1',23),
('2019-02-11','test_2',19),
('2019-02-11','test_3',39),
('2019-02-11','test_1',23),
('2019-02-11','test_3',39),
('2019-02-11','test_1',23),
('2019-02-12','test_2',19),
('2019-02-13','test_1',23),
('2019-02-15','test_2',19),
('2019-02-16','test_2',19);
2.查询SQL
--按时间和用户分组并对每个用户按时间排序
t1:
SELECT dt,
user_id,
max(age) age,
row_number() over(PARTITION BY user_id ORDER BY dt) rank
FROM test5
GROUP BY dt,
user_id
--如果用户连续几天登录，则dt-rank应该相等
t2:
SELECT
user_id,
age,
date_sub(dt,rank) flag
FROM
(
SELECT dt,
user_id,
max(age) age,
row_number() over(PARTITION BY user_id ORDER BY dt) rank
FROM test5
GROUP BY dt,
user_id
) t1
--判断连续登录天数大于等于两天
t3:
select
user_id,
max(age) age
from
(
SELECT
user_id,
age,
date_sub(dt,rank) flag
FROM
(
SELECT dt,
user_id,
max(age) age,
row_number() over(PARTITION BY user_id ORDER BY dt) rank
FROM test5
GROUP BY dt,
user_id
) t1
) t2
group by user_id,flag
having count(*) >= 2
--去重
t4:
select
user_id,
max(age) age
from
(
select
user_id,
max(age) age
from
(
SELECT
user_id,
age,
date_sub(dt,rank) flag
FROM
(
SELECT dt,
user_id,
max(age) age,
row_number() over(PARTITION BY user_id ORDER BY dt) rank
FROM test5
GROUP BY dt,
user_id
) t1
) t2
group by user_id,flag
having count(*) >= 2
) t3
group by user_id
--求所有用户的id和年龄
t5:
select
user_id,
max(age) age
from test5
group by user_id
--求活跃用户、所有用户的总数，平均年龄
t6:
select
0 total_user_cnt,
0 total_user_avg_age,
count(*) AS two_days_cnt,
cast(sum(age) / count(*) AS decimal(5,2)) AS avg_age
from
(
select
user_id,
max(age) age
from
(
select
user_id,
max(age) age
from
(
SELECT
user_id,
age,
date_sub(dt,rank) flag
FROM
(
SELECT dt,
user_id,
max(age) age,
row_number() over(PARTITION BY user_id ORDER BY dt) rank
FROM test5
GROUP BY dt,
user_id
) t1
) t2
group by user_id,flag
having count(*) >= 2
) t3
group by user_id
) t4
union all
select
count(*) total_user_cnt,
cast(sum(age) /count(*) AS decimal(5,2)) total_user_avg_age,
0 two_days_cnt,
0 avg_age
from
(
select
user_id,
max(age) age
from test5
group by user_id
) t5
--最终结果
select
sum(total_user_cnt) total_user_cnt,
sum(total_user_avg_age) total_user_avg_age,
sum(two_days_cnt) two_days_cnt,
sum(avg_age) avg_age
from
(
select
0 total_user_cnt,
0 total_user_avg_age,
count(*) AS two_days_cnt,
cast(sum(age) / count(*) AS decimal(5,2)) AS avg_age
from
(
select
user_id,
max(age) age
from
(
select
user_id,
max(age) age
from
(
SELECT
user_id,
age,
date_sub(dt,rank) flag
FROM
(
SELECT dt,
user_id,
max(age) age,
row_number() over(PARTITION BY user_id ORDER BY dt) rank
FROM test5
GROUP BY dt,
user_id
) t1
) t2
group by user_id,flag
having count(*) >= 2
) t3
group by user_id
) t4
union all
select
count(*) total_user_cnt,
cast(sum(age) /count(*) AS decimal(5,2)) total_user_avg_age,
0 two_days_cnt,
0 avg_age
from
(
select
user_id,
max(age) age
from test5
group by user_id
) t5
) t6;
第六题：
需求：
表结构：(购买用户：userid，金额：money，购买时间：paymenttime(格式：2017-10-01)，订单id：orderid)
求所有用户中在今年10月份第一次购买商品的金额
实现：
1.数据准备
CREATE TABLE test6 (
userid string,
money decimal(10,2),
paymenttime string,
orderid string);
INSERT INTO TABLE test6 VALUES('001',100,'2017-10-01','123');
INSERT INTO TABLE test6 VALUES('001',200,'2017-10-02','124');
INSERT INTO TABLE test6 VALUES('002',500,'2017-10-01','125');
INSERT INTO TABLE test6 VALUES('001',100,'2017-11-01','126');
2.查询SQL
select
userid,
money,
paymenttime,
orderid
from
(
select
userid,
money,
paymenttime,
orderid,
row_number() over(partition by userid order by paymenttime) rank
from
test6
where substring(paymenttime,1,7) = '2017-10'
) t
where rank = 1;
第七题：
需求：
现有图书管理数据库的三个数据模型如下：
图书（数据表名：BOOK）
    序号      字段名称    字段描述    字段类型
    1       BOOK_ID     总编号         文本
    2       SORT        分类号         文本
    3       BOOK_NAME   书名          文本
    4       WRITER      作者          文本
    5       OUTPUT      出版单位    文本
    6       PRICE       单价          数值（保留小数点后2位）
读者（数据表名：READER）
    序号      字段名称    字段描述    字段类型
    1       READER_ID   借书证号    文本
    2       COMPANY     单位          文本
    3       NAME        姓名          文本
    4       SEX         性别          文本
    5       GRADE       职称          文本
    6       ADDR        地址          文本
借阅记录（数据表名：BORROW LOG）
    序号      字段名称        字段描述    字段类型
    1       READER_ID       借书证号    文本
    2       BOOK_ID         总编号         文本
    3       BORROW_DATE     借书日期    日期
（1）创建图书管理库的图书、读者和借阅三个基本表的表结构。请写出建表语句。
（2）找出姓李的读者姓名（NAME）和所在单位（COMPANY）。
（3）查找“高等教育出版社”的所有图书名称（BOOK_NAME）及单价（PRICE），结果按单价降序排序。
（4）查找价格介于10元和20元之间的图书种类(SORT）出版单位（OUTPUT）和单价（PRICE），结果按出版单位（OUTPUT）和单价（PRICE）升序排序。
（5）查找所有借了书的读者的姓名（NAME）及所在单位（COMPANY）。
（6）求”科学出版社”图书的最高单价、最低单价、平均单价。
（7）找出当前至少借阅了2本图书（大于等于2本）的读者姓名及其所在单位。
（8）考虑到数据安全的需要，需定时将“借阅记录”中数据进行备份，请使用一条SQL语句，在备份用户bak下创建与“借阅记录”表结构完全一致的数据表BORROW_LOG_BAK.井且将“借阅记录”中现有数据全部复制到BORROW_L0G_ BAK中。
（9）现在需要将原Oracle数据库中数据迁移至Hive仓库，请写出“图书”在Hive中的建表语句（Hive实现，提示：列分隔符|；数据表数据需要外部导入：分区分别以month＿part、day＿part 命名）
（10）Hive中有表A，现在需要将表A的月分区　201505　中　user＿id为20000的user＿dinner字段更新为bonc8920，其他用户user＿dinner字段数据不变，请列出更新的方法步骤。（Hive实现，提示：Hlive中无update语法，请通过其他办法进行数据更新）
实现：
1)
-- 创建图书表book
CREATE TABLE book(book_id string,
`SORT` string,
book_name string,
writer string,
OUTPUT string,
price decimal(10,2));
INSERT INTO TABLE book VALUES ('001','TP391','信息处理','author1','机械工业出版社','20');
INSERT INTO TABLE book VALUES ('002','TP392','数据库','author12','科学出版社','15');
INSERT INTO TABLE book VALUES ('003','TP393','计算机网络','author3','机械工业出版社','29');
INSERT INTO TABLE book VALUES ('004','TP399','微机原理','author4','科学出版社','39');
INSERT INTO TABLE book VALUES ('005','C931','管理信息系统','author5','机械工业出版社','40');
INSERT INTO TABLE book VALUES ('006','C932','运筹学','author6','科学出版社','55');
-- 创建读者表reader
CREATE TABLE reader (reader_id string,
company string,
name string,
sex string,
grade string,
addr string);
INSERT INTO TABLE reader VALUES ('0001','阿里巴巴','jack','男','vp','addr1');
INSERT INTO TABLE reader VALUES ('0002','百度','robin','男','vp','addr2');
INSERT INTO TABLE reader VALUES ('0003','腾讯','tony','男','vp','addr3');
INSERT INTO TABLE reader VALUES ('0004','京东','jasper','男','cfo','addr4');
INSERT INTO TABLE reader VALUES ('0005','网易','zhangsan','女','ceo','addr5');
INSERT INTO TABLE reader VALUES ('0006','搜狐','lisi','女','ceo','addr6');
-- 创建借阅记录表borrow_log
CREATE TABLE borrow_log(reader_id string,
book_id string,
borrow_date string);
INSERT INTO TABLE borrow_log VALUES ('0001','002','2019-10-14');
INSERT INTO TABLE borrow_log VALUES ('0002','001','2019-10-13');
INSERT INTO TABLE borrow_log VALUES ('0003','005','2019-09-14');
INSERT INTO TABLE borrow_log VALUES ('0004','006','2019-08-15');
INSERT INTO TABLE borrow_log VALUES ('0005','003','2019-10-10');
INSERT INTO TABLE borrow_log VALUES ('0006','004','2019-17-13');
2)
SELECT
name,
company
FROM
reader
WHERE name LIKE '李%';
3)
SELECT book_name,
       price
FROM book
WHERE OUTPUT = "高等教育出版社"
ORDER BY price DESC;
4)
SELECT sort,
       output,
       price
FROM book
WHERE price >= 10 and price <= 20
ORDER BY output,price ;
5)
SELECT b.name,
       b.company
FROM borrow_log a
JOIN reader b ON a.reader_id = b.reader_id;
6)
SELECT max(price),
       min(price),
       avg(price)
FROM book
WHERE OUTPUT = '科学出版社';
7)
SELECT b.name,
       b.company
FROM
  (SELECT reader_id
   FROM borrow_log
   GROUP BY reader_id
   HAVING count(*) >= 2) a
JOIN reader b ON a.reader_id = b.reader_id;
8)
CREATE TABLE borrow_log_bak AS
SELECT *
FROM borrow_log;
9)
CREATE TABLE book_hive (
book_id string,
SORT string,
book_name string,
writer string,
OUTPUT string,
price DECIMAL ( 10, 2 ) )
partitioned BY ( month_part string, day_part string )
ROW format delimited FIELDS TERMINATED BY '\\|' stored AS textfile;
10)
方式1：配置hive支持事务操作，分桶表，orc存储格式
方式2：第一步找到要更新的数据，将要更改的字段替换为新的值，第二步找到不需要更新的数据，第三步将上两步的数据插入一张新表中。
第八题：
需求：
日志格式如下：
时间                    接口                         ip地址
2016-11-09 14:22:05        /api/user/login             110.23.5.33
2016-11-09 14:23:10        /api/user/detail            57.3.2.16
2016-11-09 15:59:40        /api/user/login             200.6.5.166
求：11月9号下午14点-15点，访问/api/user/login接口的top10的ip地址
实现：
1.数据准备
CREATE TABLE test8(`date` string,
interface string,
ip string);
INSERT INTO TABLE test8 VALUES ('2016-11-09 11:22:05','/api/user/login','110.23.5.23');
INSERT INTO TABLE test8 VALUES ('2016-11-09 11:23:10','/api/user/detail','57.3.2.16');
INSERT INTO TABLE test8 VALUES ('2016-11-09 23:59:40','/api/user/login','200.6.5.166');
INSERT INTO TABLE test8 VALUES ('2016-11-09 11:14:23','/api/user/login','136.79.47.70');
INSERT INTO TABLE test8 VALUES ('2016-11-09 11:15:23','/api/user/detail','94.144.143.141');
INSERT INTO TABLE test8 VALUES ('2016-11-09 11:16:23','/api/user/login','197.161.8.206');
INSERT INTO TABLE test8 VALUES ('2016-11-09 12:14:23','/api/user/detail','240.227.107.145');
INSERT INTO TABLE test8 VALUES ('2016-11-09 13:14:23','/api/user/login','79.130.122.205');
INSERT INTO TABLE test8 VALUES ('2016-11-09 14:14:23','/api/user/detail','65.228.251.189');
INSERT INTO TABLE test8 VALUES ('2016-11-09 14:15:23','/api/user/detail','245.23.122.44');
INSERT INTO TABLE test8 VALUES ('2016-11-09 14:17:23','/api/user/detail','22.74.142.137');
INSERT INTO TABLE test8 VALUES ('2016-11-09 14:19:23','/api/user/detail','54.93.212.87');
INSERT INTO TABLE test8 VALUES ('2016-11-09 14:20:23','/api/user/detail','218.15.167.248');
INSERT INTO TABLE test8 VALUES ('2016-11-09 14:24:23','/api/user/detail','20.117.19.75');
INSERT INTO TABLE test8 VALUES ('2016-11-09 15:14:23','/api/user/login','183.162.66.97');
INSERT INTO TABLE test8 VALUES ('2016-11-09 16:14:23','/api/user/login','108.181.245.147');
INSERT INTO TABLE test8 VALUES ('2016-11-09 14:17:23','/api/user/login','22.74.142.137');
INSERT INTO TABLE test8 VALUES ('2016-11-09 14:19:23','/api/user/login','22.74.142.137');
2.查询SQL
select
ip,
count(*) as cnt
from
test8
where substring(`date`,1,13) >= '2016-11-09 14' and substring(`date`,1,13) < '2016-11-09 15' and interface = '/api/user/login'
group by ip
order by cnt desc
limit 10;
第九题：
需求：
有一个充值日志表credit_log，字段如下：
`dist_id` int  '区组id',
`account` string  '账号',
`money` int   '充值金额',
`create_time` string  '订单时间'
请写出SQL语句，查询充值日志表2019年01月02号每个区组下充值额最大的账号，要求结果：
区组id，账号，金额，充值时间
实现：
1.数据准备
CREATE TABLE test9(
dist_id string COMMENT '区组id',
account string COMMENT '账号',
`money` decimal(10,2) COMMENT '充值金额',
create_time string COMMENT '订单时间');
INSERT INTO TABLE test9 VALUES ('1','11',100006,'2019-01-02 13:00:01');
INSERT INTO TABLE test9 VALUES ('1','22',110000,'2019-01-02 13:00:02');
INSERT INTO TABLE test9 VALUES ('1','33',102000,'2019-01-02 13:00:03');
INSERT INTO TABLE test9 VALUES ('1','44',100300,'2019-01-02 13:00:04');
INSERT INTO TABLE test9 VALUES ('1','55',100040,'2019-01-02 13:00:05');
INSERT INTO TABLE test9 VALUES ('1','66',100005,'2019-01-02 13:00:06');
INSERT INTO TABLE test9 VALUES ('1','77',180000,'2019-01-03 13:00:07');
INSERT INTO TABLE test9 VALUES ('1','88',106000,'2019-01-02 13:00:08');
INSERT INTO TABLE test9 VALUES ('1','99',100400,'2019-01-02 13:00:09');
INSERT INTO TABLE test9 VALUES ('1','12',100030,'2019-01-02 13:00:10');
INSERT INTO TABLE test9 VALUES ('1','13',100003,'2019-01-02 13:00:20');
INSERT INTO TABLE test9 VALUES ('1','14',100020,'2019-01-02 13:00:30');
INSERT INTO TABLE test9 VALUES ('1','15',100500,'2019-01-02 13:00:40');
INSERT INTO TABLE test9 VALUES ('1','16',106000,'2019-01-02 13:00:50');
INSERT INTO TABLE test9 VALUES ('1','17',100800,'2019-01-02 13:00:59');
INSERT INTO TABLE test9 VALUES ('2','18',100800,'2019-01-02 13:00:11');
INSERT INTO TABLE test9 VALUES ('2','19',100030,'2019-01-02 13:00:12');
INSERT INTO TABLE test9 VALUES ('2','10',100000,'2019-01-02 13:00:13');
INSERT INTO TABLE test9 VALUES ('2','45',100010,'2019-01-02 13:00:14');
INSERT INTO TABLE test9 VALUES ('2','78',100070,'2019-01-02 13:00:15');
2.查询SQL
select
b.dist_id,
b.account,
b.sum_money
from
(
select
a.dist_id,
a.account,
a.sum_money,
rank() over(partition by a.dist_id order by a.sum_money desc) rank
from
(
select
dist_id,
account,
sum(`money`) sum_money
from
test9
where substring(create_time,1,10) = '2019-01-02'
group by dist_id,account
) a
) b
where b.rank = 1
;
第十题：
需求：
有一个账号表如下，请写出SQL语句，查询各自区组的gold排名前十的账号（分组取前10）
dist_id string  '区组id',
account string  '账号',
gold     int    '金币'
实现：
1.数据准备
CREATE TABLE test10(
    `dist_id` string COMMENT '区组id',
    `account` string COMMENT '账号',
    `gold` int COMMENT '金币'
);
INSERT INTO TABLE test10 VALUES ('1','77',18);
INSERT INTO TABLE test10 VALUES ('1','88',106);
INSERT INTO TABLE test10 VALUES ('1','99',10);
INSERT INTO TABLE test10 VALUES ('1','12',13);
INSERT INTO TABLE test10 VALUES ('1','13',14);
INSERT INTO TABLE test10 VALUES ('1','14',25);
INSERT INTO TABLE test10 VALUES ('1','15',36);
INSERT INTO TABLE test10 VALUES ('1','16',12);
INSERT INTO TABLE test10 VALUES ('1','17',158);
INSERT INTO TABLE test10 VALUES ('2','18',12);
INSERT INTO TABLE test10 VALUES ('2','19',44);
INSERT INTO TABLE test10 VALUES ('2','10',66);
INSERT INTO TABLE test10 VALUES ('2','45',80);
INSERT INTO TABLE test10 VALUES ('2','78',98);
2.查询SQL
SELECT
dist_id,
account,
gold
FROM
(SELECT
dist_id,
account,
gold,
row_number () over (PARTITION BY dist_id ORDER BY gold DESC) rank
FROM test10) t
WHERE rank <= 10;