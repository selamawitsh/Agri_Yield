package com.agriyield.geospatialservice.domain.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * GeoJSON Polygon parsing, validation, centroid and area (approximate hectares).
 */
public final class GeoJsonPolygonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final double MIN_AREA_HECTARES = 0.01;
    private static final double MAX_AREA_HECTARES = 5000.0;

    private GeoJsonPolygonUtils() {}

    public record PolygonData(
        List<double[]> ring,
        double centroidLat,
        double centroidLng,
        double areaHectares
    ) {}

    public record ValidationResult(
        boolean valid,
        String message,
        PolygonData polygonData
    ) {}

    public static ValidationResult validate(String geoJsonPolygon) {
        if (geoJsonPolygon == null || geoJsonPolygon.isBlank()) {
            return new ValidationResult(false, "Polygon is required", null);
        }
        try {
            List<double[]> ring = normalizeRing(parseExteriorRing(geoJsonPolygon));
            if (ring.size() < 4) {
                return new ValidationResult(false,
                    "Polygon must have at least 3 distinct vertices", null);
            }
            if (!isClosedRing(ring)) {
                return new ValidationResult(false,
                    "Polygon ring must be closed (first point equals last)", null);
            }
            if (hasSelfIntersection(ring)) {
                return new ValidationResult(false,
                    "Polygon has self-intersecting edges", null);
            }
            double areaHa = calculateAreaHectares(ring);
            if (areaHa < MIN_AREA_HECTARES) {
                return new ValidationResult(false,
                    "Farm area is too small (min 0.01 ha)", null);
            }
            if (areaHa > MAX_AREA_HECTARES) {
                return new ValidationResult(false,
                    "Farm area exceeds maximum allowed (5000 ha)", null);
            }
            double[] centroid = calculateCentroid(ring);
            PolygonData data = new PolygonData(ring, centroid[0], centroid[1], areaHa);
            return new ValidationResult(true, "Valid polygon", data);
        } catch (IllegalArgumentException e) {
            return new ValidationResult(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ValidationResult(false, "Invalid GeoJSON: " + e.getMessage(), null);
        }
    }

    public static List<double[]> parseExteriorRing(String geoJsonPolygon) throws Exception {
        JsonNode root = MAPPER.readTree(geoJsonPolygon);
        if (!"Polygon".equalsIgnoreCase(root.path("type").asText())) {
            throw new IllegalArgumentException("GeoJSON type must be Polygon");
        }
        JsonNode coordinates = root.path("coordinates");
        if (!coordinates.isArray() || coordinates.isEmpty()) {
            throw new IllegalArgumentException("Missing polygon coordinates");
        }
        JsonNode ringNode = coordinates.get(0);
        List<double[]> ring = new ArrayList<>();
        for (JsonNode point : ringNode) {
            if (point.size() < 2) {
                throw new IllegalArgumentException("Each coordinate must be [lng, lat]");
            }
            ring.add(new double[]{point.get(0).asDouble(), point.get(1).asDouble()});
        }
        return ring;
    }

    public static boolean isClosedRing(List<double[]> ring) {
        if (ring.size() < 2) return false;
        double[] first = ring.get(0);
        double[] last = ring.get(ring.size() - 1);
        return Math.abs(first[0] - last[0]) < 1e-9
            && Math.abs(first[1] - last[1]) < 1e-9;
    }

    /**
     * Removes duplicate closing vertex and consecutive duplicate points.
     */
    public static List<double[]> normalizeRing(List<double[]> ring) {
        if (ring.isEmpty()) return ring;
        List<double[]> out = new ArrayList<>();
        for (double[] p : ring) {
            if (!out.isEmpty()) {
                double[] last = out.get(out.size() - 1);
                if (Math.abs(last[0] - p[0]) < 1e-9
                    && Math.abs(last[1] - p[1]) < 1e-9) {
                    continue;
                }
            }
            out.add(p);
        }
        if (out.size() >= 2) {
            double[] first = out.get(0);
            double[] last = out.get(out.size() - 1);
            if (Math.abs(first[0] - last[0]) < 1e-9
                && Math.abs(first[1] - last[1]) < 1e-9) {
                out.remove(out.size() - 1);
            }
        }
        if (!out.isEmpty()) {
            double[] first = out.get(0);
            out.add(new double[]{first[0], first[1]});
        }
        return out;
    }

    public static boolean hasSelfIntersection(List<double[]> ring) {
        int vertexCount = ring.size() - 1;
        if (vertexCount < 3) return false;

        for (int i = 0; i < vertexCount; i++) {
            int i2 = (i + 1) % vertexCount;
            for (int j = i + 1; j < vertexCount; j++) {
                int j2 = (j + 1) % vertexCount;
                if (i == j || i == j2 || i2 == j || i2 == j2) continue;
                if (segmentsIntersect(
                    ring.get(i), ring.get(i2),
                    ring.get(j), ring.get(j2))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean segmentsIntersect(double[] a1, double[] a2,
                                              double[] b1, double[] b2) {
        double d1 = direction(b1, b2, a1);
        double d2 = direction(b1, b2, a2);
        double d3 = direction(a1, a2, b1);
        double d4 = direction(a1, a2, b2);
        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0))
            && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
            return true;
        }
        return false;
    }

    private static double direction(double[] p, double[] q, double[] r) {
        return (q[0] - p[0]) * (r[1] - p[1]) - (q[1] - p[1]) * (r[0] - p[0]);
    }

    public static double[] calculateCentroid(List<double[]> ring) {
        int n = ring.size() - 1;
        double sumLat = 0, sumLng = 0;
        for (int i = 0; i < n; i++) {
            sumLng += ring.get(i)[0];
            sumLat += ring.get(i)[1];
        }
        return new double[]{sumLat / n, sumLng / n};
    }

    public static double calculateAreaHectares(List<double[]> ring) {
        int n = ring.size() - 1;
        if (n < 3) return 0;
        double avgLat = 0;
        for (int i = 0; i < n; i++) {
            avgLat += ring.get(i)[1];
        }
        avgLat /= n;
        double metersPerDegreeLat = 111_320.0;
        double metersPerDegreeLng = 111_320.0 * Math.cos(Math.toRadians(avgLat));

        double areaSqM = 0;
        for (int i = 0; i < n; i++) {
            double x1 = ring.get(i)[0] * metersPerDegreeLng;
            double y1 = ring.get(i)[1] * metersPerDegreeLat;
            double x2 = ring.get((i + 1) % n)[0] * metersPerDegreeLng;
            double y2 = ring.get((i + 1) % n)[1] * metersPerDegreeLat;
            areaSqM += x1 * y2 - x2 * y1;
        }
        return Math.abs(areaSqM / 2.0) / 10_000.0;
    }

    public static boolean isPointInPolygon(double lat, double lng, List<double[]> ring) {
        int n = ring.size() - 1;
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double yi = ring.get(i)[0];
            double xi = ring.get(i)[1];
            double yj = ring.get(j)[0];
            double xj = ring.get(j)[1];
            boolean intersect = ((yi > lng) != (yj > lng))
                && (lat < (xj - xi) * (lng - yi) / (yj - yi + 1e-12) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }
}
