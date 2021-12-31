package io.kontur.layers.service;

import io.kontur.layers.controller.exceptions.Error;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.*;
import io.kontur.layers.repository.FeatureMapper;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.model.Feature;
import io.kontur.layers.repository.typehandler.FeatureCollectionResultHandler;
import io.kontur.layers.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public Optional<FeatureCollectionGeoJSON> getFeatureCollection(
            String collectionId,
            Integer limit,
            Integer offset,
            GeometryGeoJSON geometry,
            List<BigDecimal> bbox,
            DateTimeRange dateTimeRange,
            List<FeaturePropertiesFilter> propFilterList,
            boolean includeLinks) {

        final String title = layerMapper.getLayerName(collectionId).orElseThrow(
                () -> new WebApplicationException(NOT_FOUND, Error.errorFmt("Collection '%s' not found", collectionId)));

        List<FeaturePropertiesFilter> list = propFilterList.stream().map(c -> new FeaturePropertiesFilter(
                String.format("{%s}", c.getFieldName()),
                Arrays.stream(c.getPattern())
                        .map(p -> p.replace("%", "\\%")
                                .replace("_", "\\_").replace("*", "%")).toArray(String[]::new))
        ).collect(Collectors.toList());

        FeatureCollectionGeoJSON fc = getGeoJsonFeatureCollectionInParallel(collectionId, limit, offset,
                geometry != null ? JsonUtil.writeJson(geometry) : null, bbox,
                dateTimeRange, list, title, propFilterList, includeLinks);
        return Optional.of(fc).filter(collection -> collection.getNumberReturned() > 0);
    }

    public Optional<FeatureGeoJSON> getFeature(String collectionId, String featureId) {
        final String title = layerMapper.getLayerName(collectionId)
                .orElseThrow(
                        () -> new WebApplicationException(NOT_FOUND, Error.errorFmt("Collection '%s' not found", collectionId)));
        Optional<Feature> feature = featureMapper.getFeature(collectionId, featureId);

        return feature.map(f -> featureServiceHelper.toFeatureGeoJson(f, collectionId, title));
    }

    private FeatureCollectionGeoJSON getGeoJsonFeatureCollectionInParallel(String collectionId,
                                                                           Integer limit,
                                                                           Integer offset,
                                                                           String geometry,
                                                                           List<BigDecimal> bbox,
                                                                           DateTimeRange dateTimeRange,
                                                                           List<FeaturePropertiesFilter> list,
                                                                           String title,
                                                                           List<FeaturePropertiesFilter> propFilterList,
                                                                           boolean includeLinks) {
        Integer numberMatched = featureMapper.getFeaturesTotal(collectionId, geometry, bbox, dateTimeRange, list)
                .orElse(null);
        FeatureCollectionResultHandler resultHandler = new FeatureCollectionResultHandler(featureServiceHelper,
                collectionId, title);
        featureMapper.getFeatures(collectionId, limit, offset, geometry, bbox, dateTimeRange, list,
                resultHandler);
        FeatureCollectionGeoJSON fc = resultHandler.getResult();
        fc.setNumberMatched(numberMatched);
        if (includeLinks) {
            List<Link> links = featureServiceHelper.getCollectionLinks(collectionId, title, limit, offset,
                    numberMatched,
                    bbox, propFilterList);
            fc.setLinks(links);
        }

        return fc;
    }

}
