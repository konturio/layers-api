package io.kontur.layers.repository.typehandler;

import io.kontur.layers.dto.FeatureCollectionGeoJSON;
import io.kontur.layers.dto.FeatureGeoJSON;
import io.kontur.layers.repository.model.Feature;
import io.kontur.layers.service.FeatureServiceHelper;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FeatureCollectionResultHandler implements ResultHandler<Feature> {

    private FeatureServiceHelper featureServiceHelper;
    private String collectionId;
    private String title;
    private FeatureCollectionGeoJSON result;
    private List<FeatureGeoJSON> features;

    public FeatureCollectionResultHandler(FeatureServiceHelper featureServiceHelper, String collectionId, String title) {
        super();
        this.featureServiceHelper = featureServiceHelper;
        this.collectionId = collectionId;
        this.title = title;
        this.result = new FeatureCollectionGeoJSON();
        this.features = new ArrayList<>();
    }

    @Override
    public void handleResult(ResultContext resultContext) {
        Feature feature = (Feature) resultContext.getResultObject();
        features.add(featureServiceHelper.toFeatureGeoJson(feature, collectionId, title));
    }

    public FeatureCollectionGeoJSON getResult() {
        result
                .timeStamp(OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")))
                .features(features)
                .numberReturned(features.size());
        return result;
    }
}
