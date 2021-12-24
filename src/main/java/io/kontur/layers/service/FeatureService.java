package io.kontur.layers.service;

import io.kontur.layers.controller.exceptions.Err;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.DateTimeRange;
import io.kontur.layers.dto.FeatureCollectionGeoJSON;
import io.kontur.layers.dto.FeatureGeoJSON;
import io.kontur.layers.dto.Link;
import io.kontur.layers.repository.FeatureMapper;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.model.Feature;
import io.kontur.layers.repository.typehandler.FeatureCollectionResultHandler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

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
            List<BigDecimal> bbox,
            DateTimeRange dateTimeRange,
            List<PropFilter> propFilterList) {

        final String title = layerMapper.getLayerName(collectionId).orElseThrow(
                () -> new WebApplicationException(NOT_FOUND, Err.errorFmt("Collection '%s' not found", collectionId)));

        List<PropFilter> list = propFilterList.stream().map(c -> new PropFilter(
                c.getFieldName(),
                Arrays.stream(c.getPattern()).map(p -> p.replace("%", "\\%")
                        .replace("_", "\\_").replace("*", "%")).toArray(String[]::new))
        ).collect(Collectors.toList());

        FeatureCollectionGeoJSON fc = getGeoJsonFeatureCollectionInParallel(collectionId, limit, offset, bbox,
                dateTimeRange, list, title, propFilterList);
        return Optional.of(fc).filter(collection -> collection.getNumberReturned() > 0);
    }

    public Optional<FeatureGeoJSON> getFeature(String collectionId, String featureId) {
        final String title = layerMapper.getLayerName(collectionId)
                .orElseThrow(
                        () -> new WebApplicationException(NOT_FOUND, Err.errorFmt("Collection '%s' not found", collectionId)));
        Optional<Feature> feature = featureMapper.getFeature(collectionId, featureId);

        return feature.map(f -> featureServiceHelper.toFeatureGeoJson(f, collectionId, title));
    }

    private FeatureCollectionGeoJSON getGeoJsonFeatureCollectionInParallel(String collectionId,
                                                                           Integer limit,
                                                                           Integer offset,
                                                                           List<BigDecimal> bbox,
                                                                           DateTimeRange dateTimeRange,
                                                                           List<FeatureService.PropFilter> list,
                                                                           String title,
                                                                           List<PropFilter> propFilterList) {
        Integer numberMatched = featureMapper.getFeaturesTotal(collectionId, bbox, dateTimeRange, list)
                .orElse(null);
        FeatureCollectionResultHandler resultHandler = new FeatureCollectionResultHandler(featureServiceHelper,
                collectionId, title);
        featureMapper.getFeatures(collectionId, limit, offset, bbox, dateTimeRange, list,
                resultHandler);
        FeatureCollectionGeoJSON fc = resultHandler.getResult();
        fc.setNumberMatched(numberMatched);
        List<Link> links = featureServiceHelper.getCollectionLinks(collectionId, title, limit, offset, numberMatched,
                bbox, propFilterList);
        fc.setLinks(links);

        return fc;
    }

    public static class PropFilter {

        private String fieldName;
        private String[] pattern;

        public PropFilter(final String fieldName, final String[] pattern) {
            this.fieldName = fieldName;
            this.pattern = pattern;
        }

        public String getFieldName() {
            return fieldName;
        }

        public String[] getPattern() {
            return pattern;
        }
    }

}
