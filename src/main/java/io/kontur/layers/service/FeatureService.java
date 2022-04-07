package io.kontur.layers.service;

import io.kontur.layers.controller.exceptions.Error;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.*;
import io.kontur.layers.repository.ApplicationMapper;
import io.kontur.layers.repository.FeatureMapper;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.repository.model.LayerFeature;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.util.AuthorizationUtils;
import io.kontur.layers.util.JsonUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wololo.geojson.Feature;
import org.wololo.geojson.FeatureCollection;
import org.wololo.geojson.Geometry;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class FeatureService {

    private final FeatureMapper featureMapper;
    private final LayerMapper layerMapper;
    private final ApplicationMapper applicationMapper;
    private final FeatureServiceHelper featureServiceHelper;

    public FeatureService(FeatureMapper featureMapper, final LayerMapper layerMapper,
                          ApplicationMapper applicationMapper,
                          FeatureServiceHelper featureServiceHelper) {
        this.featureMapper = featureMapper;
        this.layerMapper = layerMapper;
        this.applicationMapper = applicationMapper;
        this.featureServiceHelper = featureServiceHelper;
    }

    @Transactional(readOnly = true)
    public Optional<FeatureCollectionGeoJSON> getFeatureCollection(
            String collectionId,
            Integer limit,
            Integer offset,
            Geometry geometry,
            List<BigDecimal> bbox,
            DateTimeRange dateTimeRange,
            List<FeaturePropertiesFilter> propFilterList,
            boolean includeLinks,
            UUID appId) {
        List<FeaturePropertiesFilter> list = convertAdditionalPropertiesIntoFilterList(propFilterList);
        String geometryString = geometry != null ? JsonUtil.writeJson(geometry) : null;

        String title;
        if (appId != null) {
            String userName = AuthorizationUtils.getAuthenticatedUserName();
            Application app = applicationMapper.getApplicationOwnedOrPublic(appId, userName)
                    .orElseThrow(() -> new WebApplicationException(HttpStatus.NOT_FOUND, "Application is not found"));

            title = getLayerTitle(getLayerIfAvailable(collectionId, app));
        } else {
            title = getLayerTitle(getLayerIfAvailable(collectionId, null));
        }

        Integer numberMatched = featureMapper.getFeaturesTotal(collectionId, geometryString, bbox, dateTimeRange, list)
                .orElse(null);

        List<LayerFeature> features = featureMapper.getFeatures(collectionId, limit, offset, geometryString, bbox,
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
                                                                 List<LayerFeature> features) {
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
        String title = getLayerTitle(getLayerIfAvailable(collectionId, null));

        Optional<LayerFeature> feature = featureMapper.getFeature(collectionId, featureId);

        return feature.map(f -> featureServiceHelper.toFeatureGeoJson(f, collectionId, title));
    }

    @Transactional
    public FeatureCollectionGeoJSON upsertFeatures(String collectionId, FeatureCollection fc) {
        String userName = AuthorizationUtils.getAuthenticatedUserName();
        final Layer layer = layerMapper.getOwnedLayer(collectionId, userName)
                .orElseThrow(
                        () -> new WebApplicationException(NOT_FOUND, Error.errorFmt("Collection '%s' not found", collectionId)));

        List<LayerFeature> features = Arrays.stream(fc.getFeatures())
                .map(f -> new LayerFeature(layer.getId(),
                        getOrGenerateFeatureId(f),
                        f.getGeometry(),
                        f.getProperties() != null ? JsonUtil.writeObjectNode(f.getProperties()) : null,
                        OffsetDateTime.now()))
                .collect(Collectors.toList());

        List<LayerFeature> result = featureMapper.upsertFeatures(features);

        List<String> resultedIds = result.stream()
                .map(LayerFeature::getFeatureId)
                .toList();

        featureMapper.deleteFeaturesNotInList(userName, collectionId, resultedIds);

        return convertFeatures(collectionId, layer.getName(), result);
    }

    private String getOrGenerateFeatureId(Feature feature) {
        if (feature.getId() == null || StringUtils.isBlank(String.valueOf(feature.getId()))) {
            return RandomStringUtils.randomAlphanumeric(8);
        }
        return String.valueOf(feature.getId());
    }

    @Transactional
    public void deleteItem(String collectionId, String featureId) {
        featureMapper.deleteFeature(AuthorizationUtils.getAuthenticatedUserName(), collectionId,
                        featureId)
                .orElseThrow(() -> new WebApplicationException(HttpStatus.NOT_FOUND, "Feature can not be found"));
    }

    private Layer getLayerIfAvailable(String collectionId, Application app) {
        String userName = AuthorizationUtils.getAuthenticatedUserName();
        Optional<Layer> layer;
        if (app == null) {
            layer = layerMapper.getLayer(collectionId, userName);
        } else {
            layer = layerMapper.getLayer(collectionId, userName, app.getId(), app.getShowAllPublicLayers());
        }
        return layer
                .orElseThrow(() -> new WebApplicationException(NOT_FOUND,
                        Error.errorFmt("Collection '%s' not found", collectionId)));
    }

    private String getLayerTitle(Layer layer) {
        String title = layer.getName();
        if (StringUtils.isEmpty(title)) {
            title = layer.getPublicId();
        }
        return title;
    }
}
