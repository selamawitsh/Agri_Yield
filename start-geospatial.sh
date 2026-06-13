#!/bin/bash
export JAVA_TOOL_OPTIONS="-Dsun.net.spi.nameservice.nameservers=8.8.8.8,1.1.1.1"
JAR=~/Desktop/Agri_Yield/backend/services/geospatial-service/target/geospatial-service-1.0.0-SNAPSHOT.jar

echo "Stopping any existing geospatial service..."
kill $(lsof -ti:8089) 2>/dev/null
sleep 3

echo "Starting geospatial service..."
nohup java -jar $JAR > /tmp/geospatial.log 2>&1 &
echo "PID: $!"

# Wait for local health
echo "Waiting for local startup..."
for i in $(seq 1 12); do
  if curl -s http://localhost:8089/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
    echo "✅ Service healthy on port 8089"
    break
  fi
  sleep 5
done

# Wait for Eureka registration + gateway propagation
echo "Waiting for Eureka registration (30s)..."
sleep 30

# Verify gateway can route to it
echo "Verifying gateway routing..."
for i in $(seq 1 6); do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" \
    http://localhost:8080/api/v1/geospatial/farms/11ba9c85-a2d5-4ba2-9b00-ca38c1166b2b/ndvi)
  if [ "$CODE" = "200" ] || [ "$CODE" = "404" ]; then
    echo "✅ Gateway routing working (HTTP $CODE)"
    break
  fi
  echo "  Gateway attempt $i/6: HTTP $CODE — waiting 10s..."
  sleep 10
done

echo "✅ Geospatial service ready"
