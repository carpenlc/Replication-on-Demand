#!/bin/bash

if [ -z `which java` ]; then
  if [ -z ${JAVA_HOME+x} ]; then 
  	export JAVA_HOME=/usr/java/jdk1.8.0
  	export PATH=PATH:${JAVA_HOME}/bin
  fi
fi
SCRIPT_DIR=$(dirname `which $0`)
LIB_DIR="${SCRIPT_DIR}/../CacheManager/target/lib/"

for i in ${LIB_DIR}*.jar; do
    CLASSPATH=$CLASSPATH:$i
done

java -cp ${CLASSPATH} mil.nga.rod.util.GetKey $1 $2 $3 $4