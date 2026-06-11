#!/bin/bash
export JAVA_TOOL_OPTIONS="-Dsun.net.spi.nameservice.nameservers=8.8.8.8,1.1.1.1"
JAR=~/Desktop/Agri_Yield/backend/services/geospatial-service/target/geospatial-service-1.0.0-SNAPSHOT.jar
kill $(lsof -ti:8089) 2>/dev/null
sleep 2
nohup java -jar $JAR > /tmp/geospatial.log 2>&1 &
echo "Geospatial service started — PID: $! — logs: /tmp/geospatial.log"
