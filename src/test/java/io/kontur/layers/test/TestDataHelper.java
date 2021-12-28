package io.kontur.layers.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.DateTimeRange;
import io.kontur.layers.repository.model.Feature;
import io.kontur.layers.repository.model.Layer;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TestDataHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Layer buildLayerN(int n) {
        final ObjectNode props = objectMapper.createObjectNode();
        props.put("prop1", "propValue1_" + n);
        props.put("prop2", "propValue2_" + n);
//        final ObjectNode legend = objectMapper.createObjectNode();
//        props.put("legend1", "legendValue1_" + n);
//        props.put("legend2", "legendValue2_" + n);
//        final ObjectNode group = objectMapper.createObjectNode();
//        props.put("group1", "groupValue1_" + n);
//        props.put("group2", "groupValue2_" + n);
//        final ObjectNode category = objectMapper.createObjectNode();
//        props.put("category1", "categoryValue1_" + n);
//        props.put("category2", "categoryValue2_" + n);

        return new Layer("pubId_" + n, "name_" + n, "description_" + n,
                String.format("SRID=4326;POLYGON((0 0, %1$d 0, %1$d %1$d, 0 %1$d, 0 0))", n),
                "copyrights_" + n, props, null, null, null,
                OffsetDateTime.of(2020, 4, 15, 15, 30, 0, 0, offset()).plusSeconds(n),
                OffsetDateTime.of(2020, 4, 15, 15, 0, 0, 0, offset()).plusSeconds(n), null, null, null);
    }

    public static Feature buildPointN(int n) {
        return buildFeatureN(n, String.format("SRID=4326;POINT(0 %1$d)", n));
    }

    public static Feature buildMultipointN(int n) {
        return buildFeatureN(n, String.format("SRID=4326;MULTIPOINT((%1$d 0),(0 %1$d))", n));
    }

    public static Feature buildLineStringN(int n) {
        return buildFeatureN(n, String.format("SRID=4326;LINESTRING(0 0, %1$d 0, %1$d %1$d)", n));
    }

    public static Feature buildMultiLineStringN(int n) {
        return buildFeatureN(n, String.format("SRID=4326;MULTILINESTRING((0 0, %1$d 0, %1$d %1$d),(%1$d 0, 0 0))", n));
    }

    public static Feature buildMultiPolygonN(int n) {
        return buildFeatureN(n, String.format(
                "SRID=4326;MULTIPOLYGON(((0 0, %1$d 0, %1$d %1$d, 0 %1$d, 0 0)),((0 0, %2$d 0, %2$d %2$d, 0 %2$d, 0 0)))",
                n, n + 10));
    }

    public static Feature buildGeometryCollectionN(int n) {
        return buildFeatureN(n,
                String.format("SRID=4326;GEOMETRYCOLLECTION(POINT(0 %1$d),LINESTRING(0 0, %1$d 0, %1$d %1$d))", n));
    }

    public static Feature buildPolygonN(int n) {
        return buildFeatureN(n, String.format("SRID=4326;POLYGON((0 0, %1$d 0, %1$d %1$d, 0 %1$d, 0 0))", n));
    }

    public static Feature buildFeatureN(int n, String featureWkt) {
        final ObjectNode props = objectMapper.createObjectNode();
        props.put("prop1", "propValue1_" + n);
        props.put("prop2", "propValue2_" + n);
        final OffsetDateTime lastUpdated = OffsetDateTime.of(2020, 4, 15, 15, 30, 0, 0, offset()).plusMinutes(n);
        return new Feature(null, "featureId_" + n, featureWkt, props, lastUpdated);
    }

    public static ZoneOffset offset() {
        Instant instant = Instant.now();
        ZoneId systemZone = ZoneId.systemDefault();
        return systemZone.getRules().getOffset(instant);
    }
}
