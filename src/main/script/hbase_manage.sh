#!/usr/bin/env bash

#关闭一个regionserver。尽量不要使用rolling-restart.sh，时间太长

#之前需要进到shell中，执行balance_switch false，关闭成功之后再执行balance_switch true
$HBASE_HOME/bin/hbase-daemon.sh stop regionserver

#带有关闭和启动balance_switch步骤，
$HBASE_HOME/bin/graceful_stop.sh $HOSTNAME



#Rolling Restarts
$HBASE_HOME/bin/hbase hbck
$HBASE_HOME/bin/hbase-daemon.sh stop master; $HBASE_HOME/bin/hbase-daemon.sh start master
$ echo "balance_switch false" | $HBASE_HOME/bin/hbase shell
for i in `cat $HBASE_HOME/conf/regionservers|sort`; do $HBASE_HOME/bin/graceful_stop.sh \
--restart --reload --debug $i; done &> /tmp/log.txt &
$HBASE_HOME/bin/hbase-daemon.sh stop master; $HBASE_HOME/bin/hbase-daemon.sh start master
$ echo "balance_switch true" | $HBASE_HOME/bin/hbase shell
$HBASE_HOME/bin/hbase hbck


#启动备份master

#创建文件backup-masters，加入备份服务器
touch $HBASE_HOME/conf/backup-masters
echo '10.0.0.248' > $HBASE_HOME/conf/backup-masters
#启动hbase，此时248上以backup模式启动一个master
$HBASE_HOME/bin/start-hbase.sh
#当主master挂掉之后，hbase会启动认为248的master为主的


#启动regionserver
$HBASE_HOME/bin/hbase-daemon.sh start regionserver



#检查集群的状态
$HBASE_HOME/bin/hbase hbck
#Assign .META. to a single new server if it is unassigned.
#Reassign .META. to a single new server if it is assigned to multiple servers.
#Assign a user table region to a new server if it is unassigned.
#Reassign a user table region to a single new server if it is assigned to multiple servers.
#Reassign a user table region to a new server if the current server does not match
#what the .META. table refers to.
$HBASE_HOME/bin/hbase hbck -fix

