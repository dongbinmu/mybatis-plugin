#!/bin/bash

DB_PREFIX="demo"
COUPON_TABLE="user"

COUPON_CONTENT="(
  \`id\` bigint(20) NOT NULL AUTO_INCREMENT,
  \`name\` varchar(100) DEFAULT NULL,
  \`gender\` varchar(20) DEFAULT NULL,
  \`age\` int(11) DEFAULT NULL,
  PRIMARY KEY (\`id\`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;"

persist_file=`pwd`/schema.sql

for ((i=0; i<=99; i++))
do
    dbIndex=`printf "%02d\n" ${i}`
    dbName="${DB_PREFIX}_${dbIndex}"
    echo "create database if not exists ${dbName};" >> ${persist_file}
    for ((j=0; j<=9; j++))
    do
        tableIndex="${j}${dbIndex}"
        couponTable="${dbName}.${COUPON_TABLE}_${tableIndex}"
        echo "create table if not exists ${couponTable} ${COUPON_CONTENT}" >> ${persist_file}
    done
done
