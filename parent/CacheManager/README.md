# Replication-on-Demand-Cache-Accelerator
This is an optional component associated with the "Replication on Demand" project that populates and maintains a Redis cache for use in increasing the performance of the overall application.  It's main use is caching pre-computed file hashes.  The software is compiled via Maven into a command-line-based JAR file. 

## Installing Redis
The Replication-on-Demand application is deployed to redundant Linux servers running RHEL6.
* Install the pre-requisites:
```
# yum install tcl.x86_64
# yum install tcl-devel.x86_64
```
* Download and install Redis from source:
```
# cd /usr/local/src
# wget http://download.redis.io/releases/redis-3.2.8.tar.gz
# tar -zxvf redis-3.2.8.tar.gz
# cd redis-3.2.8
# make 
# make test
# make install
```
* Run the server installation script to establish a Redis server bound to the local interface (127.0.0.1) and running on the default port (6379).
```
# cd utils
# ./install_server.sh
```

## Download and Build the Source
* Minimum requirements:
    * Java Development Kit (v1.8.0 or higher)
    * GIT (v1.7 or higher)
    * Maven (v3.3 or higher)
* Download source
```
# cd /var/local
# git clone https://github.com/carpenlc/Replication-on-Demand-Cache-Accelerator.git
```
* Configure the properties file for your environment.  The system.properties file must be updated with the database and cache connection information:
    * JDBC connection information:
        * db.driver  - The JDBC driver class (defaulted to oracle.jdbc.driver.OracleDriver)
        * db.connection_string  - The JDBC connection String 
        * db.user  - Database username
        * db.password  - Password associated with the database user
    * Redis cache connection data (only if running somewhere other than the local server and/or on a non-standard port):
        * redis.host - Host running the Redis cache
        * redis.port - Port on which the Redis server is listening
```
# cd /var/local/Replication-on-Demand-Cache-Accelerator/src/main/resources
# vim system.properties
```
* Build the output JAR file
```
# mvn clean install
```
## Execute the main CacheManager class
The output JAR file can be executed as a command-line tool.  In the production environment we execute the code via a cron job that runs once each hour at minute 0.  The relevant cron entry is as follows:
```
0 * * * * /var/local/Replication-on-Demand-Cache-Accelerator/bin/CacheManager.sh >> /var/log/applications/CacheManager.log 2>&1
```
