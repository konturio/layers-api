package io.kontur.layers.service;

import io.kontur.layers.controller.exceptions.Error;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.*;
import io.kontur.layers.repository.FeatureMapper;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.model.Feature;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.util.AuthorizationUtils;
import io.kontur.layers.util.JsonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wololo.geojson.FeatureCollection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class FeatureService {

    private final FeatureMapper featureMapper;
    private final LayerMapper layerMapper;
    private final FeatureServiceHelper featureServiceHelper;

    public FeatureService(FeatureMapper featureMapper, final LayerMapper layerMapper,
                          FeatureServiceHelper featureServiceHelper) {
        this.featureMapper = featureMapper;
        this.layerMapper = layerMapper;
        this.featureServiceHelper = featureServiceHelper;
    }

    @Transactional(readOnly = true)
    public Optional<FeatureCollectionGeoJSON> getFeatureCollection(
            String collectionId,
            Integer limit,
            Integer offset,
            GeometryGeoJSON geometry,
            List<BigDecimal> bbox,
            DateTimeRange dateTimeRange,
            List<FeaturePropertiesFilter> propFilterList,
            boolean includeLinks) {

        final String title = layerMapper.getLayerName(collectionId, AuthorizationUtils.getAuthenticatedUserName())
                .orElseThrow(() -> new WebApplicationException(NOT_FOUND, Error.errorFmt("Collection '%s' not found", collectionId)));

        List<FeaturePropertiesFilter> list = convertAdditionalPropertiesIntoFilterList(propFilterList);
        String geometryString = geometry != null ? JsonUtil.writeJson(geometry) : null;

        Integer numberMatched = featureMapper.getFeaturesTotal(collectionId, geometryString, bbox, dateTimeRange, list)
                .orElse(null);

        List<Feature> features = featureMapper.getFeatures(collectionId, limit, offset, geometryString, bbox,
                dateTimeRange, list);
        FeatureCollectionGeoJSON fc = convertFeatures(collectionId, title, features)
                .numberMatched(numberMatched);

        if (includeLinks) {
            List<Link> links = featureServiceHelper.getCollectionLinks(collectionId, title, limit, offset,
                    numberMatched,
                    bbox, propFilterList);
            fc.setLinks(links);
        }

        return Optional.of(fc).filter(collection -> collection.getNumberReturned() > 0);
    }

    private FeatureCollectionGeoJSON convertFeatures(String collectionId, String title,
                                                                 List<Feature> features) {
        return new FeatureCollectionGeoJSON()
                .timeStamp(OffsetDateTime.now(ZoneOffset.UTC))
                .features(featureServiceHelper.toFeatureGeoJson(features, collectionId, title))
                .numberReturned(features.size());
    }

    private List<FeaturePropertiesFilter> convertAdditionalPropertiesIntoFilterList(List<FeaturePropertiesFilter> propFilterList) {
        return propFilterList.stream().map(c -> new FeaturePropertiesFilter(
                String.format("{%s}", c.getFieldName()),
                Arrays.stream(c.getPattern())
                        .map(p -> p.replace("%", "\\%")
                                .replace("_", "\\_").replace("*", "%")).toArray(String[]::new))
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FeatureGeoJSON> getFeature(String collectionId, String featureId) {
        final String title = layerMapper.getLayerName(collectionId, AuthorizationUtils.getAuthenticatedUserName())
                .orElseThrow(
                        () -> new WebApplicationException(NOT_FOUND, Error.errorFmt("Collection '%s' not found", collectionId)));
        Optional<Feature> feature = featureMapper.getFeature(collectionId, featureId);

        return feature.map(f -> featureServiceHelper.toFeatureGeoJson(f, collectionId, title));
    }

    @Transactional
    public FeatureCollectionGeoJSON upsertFeatures(String collectionId, FeatureCollection fc) {
        final Layer layer = layerMapper.getOwnedLayer(collectionId, AuthorizationUtils.getAuthenticatedUserName())
                .orElseThrow(
                        () -> new WebApplicationException(NOT_FOUND, Error.errorFmt("Collection '%s' not found", collectionId)));

        List<Feature> features = Arrays.stream(fc.getFeatures())
                .map(f -> new Feature(layer.getId(),
                        String.valueOf(f.getId()),
                        f.getGeometry(),
                        f.getProperties() != null ? JsonUtil.writeObjectNode(f.getProperties()) : null,
                        OffsetDateTime.now()))
                .collect(Collectors.toList());

        List<Feature> featureList = featureMapper.upsertFeatures(features);

        return convertFeatures(collectionId, layer.getName(), featureList);
    }

    @Transactional
    public void deleteItem(String collectionId, String featureId) {
        featureMapper.deleteFeature(AuthorizationUtils.getAuthenticatedUserName(), collectionId,
                        featureId)
                .orElseThrow(() -> new WebApplicationException(HttpStatus.NOT_FOUND, "Feature can not be found"));
    }
}
