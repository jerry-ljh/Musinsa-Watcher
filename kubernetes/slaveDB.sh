#!/usr/bin/env bash

mysqldump -h $MASTER_HOST -u$MASTER_USER -p$MASTER_PASSWARD musinsa > musinsa.sql

mysql -uroot -p$SLAVE_PASSWARD  -e "create database musinsa"
mysql -uroot -p$SLAVE_PASSWARD musinsa < musinsa.sql

master_log_file=`mysql -h $MASTER_HOST -u$MASTER_USER -p$MASTER_PASSWARD -e "show master status\G" | grep mysql-bin`

re="[a-z]*-bin-[a-z]*.[0-9]*"

if [[ ${master_log_file} =~ $re ]];
then
    master_log_file=${BASH_REMATCH[0]}
fi

master_log_pos=`mysql -h $MASTER_HOST -u$MASTER_USER -p$MASTER_PASSWARD -e "show master status\G" | grep Position`

re="[0-9]+"

if [[ ${master_log_pos} =~ $re ]];
then
     master_log_pos=${BASH_REMATCH[0]}
 fi

mysql -uroot -pgurwns1346dl -e "change master to master_host='$MASTER_HOST',
master_user='$MASTER_USER',
master_password='$MASTER_PASSWARD',
master_log_file='$master_log_file',
master_log_pos=$master_log_pos"

mysql -uroot -p$SLAVE_PASSWARD -e "start slave"