package io.kontur.layers.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.*;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.repository.model.LayerFeature;
import io.kontur.layers.util.JsonUtil;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.wololo.geojson.Geometry;
import org.wololo.jts2geojson.GeoJSONWriter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.UUID;

public class TestDataHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static WKTReader wktReader = new WKTReader();
    private static GeoJSONWriter geoJSONWriter = new GeoJSONWriter();

    public static Layer buildLayerN(int n) {
        final ObjectNode props = objectMapper.createObjectNode();
        props.put("prop1", "propValue1_" + n);
        props.put("prop2", "propValue2_" + n);

        final ObjectNode featureProps = objectMapper.createObjectNode();
        featureProps.put("featureProp1", "featureProperty_" + n);

        return new Layer(null, "pubId_" + n, "name_" + n, "description_" + n, null, null,
                String.format("SRID=4326;POLYGON((0 0, %1$d 0, %1$d %1$d, 0 %1$d, 0 0))", n),
                "copyrights_" + n, props, null, null, featureProps, null, null,
                OffsetDateTime.of(2020, 4, 15, 15, 30, 0, 0, offset()).plusSeconds(n),
                OffsetDateTime.of(2020, 4, 15, 15, 0, 0, 0, offset()).plusSeconds(n),
                null, null, true, true, "owner_" + n);
    }

    public static LayerFeature buildPointN(int n) {
        return buildFeatureN(n, String.format("POINT(0 %1$d)", n));
    }

    public static LayerFeature buildMultipointN(int n) {
        return buildFeatureN(n, String.format("MULTIPOINT((%1$d 0),(0 %1$d))", n));
    }

    public static LayerFeature buildLineStringN(int n) {
        return buildFeatureN(n, String.format("LINESTRING(0 0, %1$d 0, %1$d %1$d)", n));
    }

    public static LayerFeature buildMultiLineStringN(int n) {
        return buildFeatureN(n, String.format("MULTILINESTRING((0 0, %1$d 0, %1$d %1$d),(%1$d 0, 0 0))", n));
    }

    public static LayerFeature buildMultiPolygonN(int n) {
        return buildFeatureN(n, String.format(
                "MULTIPOLYGON(((0 0, %1$d 0, %1$d %1$d, 0 %1$d, 0 0)),((0 0, %2$d 0, %2$d %2$d, 0 %2$d, 0 0)))",
                n, n + 10));
    }

    public static LayerFeature buildGeometryCollectionN(int n) {
        return buildFeatureN(n,
                String.format("GEOMETRYCOLLECTION(POINT(0 %1$d),LINESTRING(0 0, %1$d 0, %1$d %1$d))", n));
    }

    public static LayerFeature buildPolygonN(int n) {
        return buildFeatureN(n, String.format("POLYGON((0 0, %1$d 0, %1$d %1$d, 0 %1$d, 0 0))", n));
    }

    public static LayerFeature buildFeatureN(int n, String wkt) {
        final ObjectNode props = objectMapper.createObjectNode();
        props.put("prop1", "propValue1_" + n);
        props.put("prop2", "propValue2_" + n);
        final OffsetDateTime lastUpdated = OffsetDateTime.of(2020, 4, 15, 15, 30, 0, 0, offset()).plusMinutes(n);
        try {
            return new LayerFeature(null, "featureId_" + n, wkt == null ? null : geoJSONWriter.write(wktReader.read(wkt)), props, lastUpdated);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static ZoneOffset offset() {
        Instant instant = Instant.now();
        ZoneId systemZone = ZoneId.systemDefault();
        return systemZone.getRules().getOffset(instant);
    }

    public static CollectionCreateDto buildCollectionCreateDtoN(int n) {
        final ObjectNode props = objectMapper.createObjectNode();
        props.put("prop2", "propValue2_" + n);
        props.put("prop1", "propValue1_" + n);

        final ObjectNode featureProps = objectMapper.createObjectNode();
        featureProps.put("featureProp1", "featureProperty_" + n);

        CollectionCreateDto dto = new CollectionCreateDto();
        dto.setId("pubId_" + n);
        dto.setTitle("name_" + n);
        dto.setDescription("description_" + n);
        dto.setLink(new Link().rel("tiles").href("https://www.example.com"));
        dto.setProperties(props);
        dto.setItemType(CollectionUpdateDto.Type.tiles);
        dto.setFeatureProperties(featureProps);
        dto.setGeometry(JsonUtil.readJson(String.format("{\"type\":\"Point\",\"coordinates\":[0,%1$d]}", n), Geometry.class));
        dto.setCopyrights("copyrights_" + n);
        return dto;
    }

    public static Application buildApplication(int n) {
        Application dto = new Application();
        dto.setId(UUID.randomUUID());
        dto.setIsPublic(true);
        dto.setShowAllPublicLayers(true);
        dto.setOwner("owner_" + n);
        return dto;
    }

    public static ApplicationUpdateDto buildApplicationCreateDto() {
        ApplicationUpdateDto dto = new ApplicationUpdateDto();
        dto.setPublic(true);
        dto.setShowAllPublicLayers(true);
        dto.setLayers(new ArrayList<>());
        return dto;
    }

    public static ApplicationLayerDto buildApplicationLayerDto(String layerId, int n) {
        ApplicationLayerDto dto = new ApplicationLayerDto();
        dto.setLayerId(layerId);
        dto.setIsDefault(true);

        final ObjectNode displayRules = objectMapper.createObjectNode();
        displayRules.put("displayRule1", "propValue1_" + n);
        displayRules.put("displayRule2", "propValue2_" + n);
        dto.setDisplayRule(displayRules);

        final ObjectNode legend = objectMapper.createObjectNode();
        legend.put("legend1", "legendValue1_" + n);
        legend.put("legend2", "legendValue2_" + n);
        dto.setStyleRule(legend);
        return dto;
    }
}
