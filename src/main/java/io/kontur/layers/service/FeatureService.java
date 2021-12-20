package io.kontur.layers.service;

//import k2layers.api.common.Err;
//import k2layers.api.common.ServiceException;
//import k2layers.api.model.DateTimeRange;
//import k2layers.api.model.FeatureCollectionGeoJSON;
//import k2layers.api.model.FeatureGeoJSON;
//import k2layers.api.model.Link;
//import k2layers.api.persistence.Feature;
//import k2layers.api.persistence.FeatureCollectionResultHandler;
//import k2layers.api.persistence.mapper.FeatureMapper;
//import k2layers.api.persistence.mapper.LayerMapper;
import org.springframework.stereotype.Service;

//import javax.inject.Inject;
//import java.math.BigDecimal;
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Service
public class FeatureService {

//    private final FeatureMapper featureMapper;
//    private final LayerMapper layerMapper;
//    private final FeatureServiceHelper featureServiceHelper;
//    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
//
//    @Inject
//    public FeatureService(FeatureMapper featureMapper, final LayerMapper layerMapper, FeatureServiceHelper featureServiceHelper) {
//        this.featureMapper = featureMapper;
//        this.layerMapper = layerMapper;
//        this.featureServiceHelper = featureServiceHelper;
//    }
//
//    @Override
//    public Optional<FeatureCollectionGeoJSON> getFeatureCollection(
//            String collectionId,
//            Integer limit,
//            Integer offset,
//            List<BigDecimal> bbox,
//            DateTimeRange dateTimeRange,
//            List<PropFilter> propFilterList) {
//
//        final String title = layerMapper.getLayerName(collectionId).orElseThrow(
//                () -> new ServiceException(NOT_FOUND, Err.errorFmt("Collection '%s' not found", collectionId)));
//
//        var list = propFilterList.stream().map(c -> new PropFilter(
//                c.getFieldName(),
//                Arrays.stream(c.getPattern()).map(p->p.replace("%", "\\%")
//                        .replace("_", "\\_").replace("*", "%")).toArray(String[]::new))
//        ).collect(Collectors.toList());
//
//        FeatureCollectionGeoJSON fc = getGeoJsonFeatureCollectionInParallel(collectionId, limit, offset, bbox,
//                dateTimeRange, list, title, propFilterList);
//        return Optional.of(fc).filter(collection -> collection.getNumberReturned() > 0);
//    }
//
//    @Override
//    public Optional<FeatureCollectionGeoJSON> getFeatureCollectionByMultipoint(
//            String collectionId,
//            Integer limit,
//            Integer offset,
//            String geom,
//            DateTimeRange dateTimeRange,
//            List<PropFilter> propFilterList,
//            boolean excludeGeometry) {
//
//        final String title = layerMapper.getLayerName(collectionId).orElseThrow(
//                () -> new ServiceException(NOT_FOUND, Err.errorFmt("Collection '%s' not found", collectionId)));
//
//        var list = propFilterList.stream().map(c -> new PropFilter(
//                c.getFieldName(),
//                Arrays.stream(c.getPattern()).map(p->p.replace("%", "\\%")
//                        .replace("_", "\\_").replace("*", "%")).toArray(String[]::new))
//        ).collect(Collectors.toList());
//
//        List<Feature> features = featureMapper.getFeaturesByMultipoint(collectionId, limit, offset, geom, dateTimeRange, list, excludeGeometry);
//
//        if (features.isEmpty()) {
//            return Optional.empty();
//        }
//
//        Integer numberMatched = features.get(0).getNumberMatched();
//        FeatureCollectionGeoJSON fc = new FeatureCollectionGeoJSON()
//                .links(featureServiceHelper.getCollectionLinks(collectionId, title, limit, offset, numberMatched, new ArrayList<>(), propFilterList))
//                .timeStamp(OffsetDateTime.now(ZoneOffset.UTC).format(dateTimeFormatter))
//                .numberMatched(numberMatched)
//                .numberReturned(features.size());
//
//        fc.setFeatures(features.stream().map(f -> featureServiceHelper.toFeatureGeoJson(f, collectionId, title)).collect(Collectors.toList()));
//        return Optional.of(fc);
//    }
//
//    public Optional<FeatureGeoJSON> getFeature(String collectionId, String featureId) {
//        final String title = layerMapper.getLayerName(collectionId)
//                .orElseThrow(() -> new ServiceException(NOT_FOUND, Err.errorFmt("Collection '%s' not found", collectionId)));
//        Optional<Feature> feature = featureMapper.getFeature(collectionId, featureId);
//
//        return feature.map(f -> featureServiceHelper.toFeatureGeoJson(f, collectionId, title));
//    }
//
//    private FeatureCollectionGeoJSON getGeoJsonFeatureCollectionInParallel(String collectionId,
//                                                                           Integer limit,
//                                                                           Integer offset,
//                                                                           List<BigDecimal> bbox,
//                                                                           DateTimeRange dateTimeRange,
//                                                                           List<FeatureService.PropFilter> list,
//                                                                           String title,
//                                                                           List<PropFilter> propFilterList) {
//        Integer numberMatched = featureMapper.getFeaturesTotal(collectionId, bbox, dateTimeRange, list)
//                .orElse(null);
//        FeatureCollectionResultHandler resultHandler = new FeatureCollectionResultHandler(featureServiceHelper,
//                collectionId, title);
//        featureMapper.getFeatures(collectionId, limit, offset, bbox, dateTimeRange, list,
//                resultHandler);
//        FeatureCollectionGeoJSON fc = resultHandler.getResult();
//        fc.setNumberMatched(numberMatched);
//        List<Link> links = featureServiceHelper.getCollectionLinks(collectionId, title, limit, offset, numberMatched,
//                bbox, propFilterList);
//        fc.setLinks(links);
//
//        return fc;
//    }

    class PropFilter {
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
