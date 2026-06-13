#!/bin/bash
echo "=== Agri-Yield NDVI Sync ==="

# Wait for gateway to be ready
echo "Checking gateway on port 8080..."
for i in $(seq 1 10); do
  GW=$(curl -s --max-time 5 http://localhost:8080/actuator/health 2>/dev/null | python3 -c "import sys,json; print(json.load(sys.stdin).get('status',''))" 2>/dev/null)
  if [ "$GW" = "UP" ]; then
    echo "✅ Gateway is UP"
    break
  fi
  echo "⏳ Gateway not ready ($i/10) — waiting 10s..."
  sleep 10
done

if [ "$GW" != "UP" ]; then
  echo "❌ Gateway is DOWN on port 8080. Start it first."
  echo "Running services:"
  for P in 8080 8081 8083 8084 8085 8086 8087 8089 8090 8761; do
    S=$(curl -s --max-time 2 http://localhost:$P/actuator/health 2>/dev/null | python3 -c "import sys,json; print(json.load(sys.stdin).get('status',''))" 2>/dev/null)
    [ -n "$S" ] && echo "  Port $P: $S"
  done
  exit 1
fi

# Get admin token
ADMIN_TOKEN=$(curl -s --max-time 10 -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"+251911111111","password":"admin123"}' \
  | python3 -c "import sys,json; r=json.load(sys.stdin); print(r['data']['accessToken'])" 2>/dev/null)

if [ -z "$ADMIN_TOKEN" ]; then
  echo "❌ Could not get admin token"
  exit 1
fi
echo "✅ Admin token obtained"

# Get all investment farm IDs
FARMS=$(psql "postgresql://agriyield:agriyield123@localhost:5432/agriyield_investment_db" -t \
  -c "SELECT DISTINCT farm_id FROM investments;" 2>/dev/null | tr -d ' ' | grep -v '^$')

FARM_COUNT=$(echo "$FARMS" | grep -c .)
echo "Found $FARM_COUNT farms to sync..."

SUCCESS=0; FAILED=0
for FARM_ID in $FARMS; do
  [ -z "$FARM_ID" ] && continue
  RESULT=$(curl -s --max-time 30 -X POST \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    "http://localhost:8089/api/v1/geospatial/farms/${FARM_ID}/ndvi/sync" \
    | python3 -c "
import sys,json
try:
    r=json.load(sys.stdin)
    d=r.get('data',{})
    if d.get('ndviValue'):
        print('✅', round(float(d['ndviValue']),4), d.get('healthStatus',''))
    else:
        print('❌', r.get('message','failed'))
except Exception as e:
    print('❌ error:', str(e)[:40])
" 2>/dev/null)
  echo "  ${FARM_ID:0:8}: $RESULT"
  if echo "$RESULT" | grep -q "✅"; then SUCCESS=$((SUCCESS+1)); else FAILED=$((FAILED+1)); fi
  sleep 1
done

echo ""
echo "=== Done: ✅ $SUCCESS synced  ❌ $FAILED failed ==="
