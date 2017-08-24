# Replication-on-Demand
Repository used to exchange the "front-end" code associated with the replication on demand project.  This project contains an EJB business rules tier and a Web tier (based on PrimeFaces) delivered in a single Enterprise ARchive (EAR) file.

## Pre-requisites
* Java (1.8 or higher) 
* git (v1.7 or higher)
* Maven (v3.3.8 or higher)

## Download the Source and Build the EAR File
* Download source
```
# cd /var/local/src
# git clone https://github.com/carpenlc/Replication-on-Demand.git
```
* Execute the Maven targets to build the output EAR
```
# cd /var/local/src/Replication-on-Demand/parent
# mvn clean package 
```
* The deployable EAR file will reside at the following location
```
/var/local/src/Replication-on-Demand/parent/ReplicationOnDemand/target/ReplicationOnDemand.ear
